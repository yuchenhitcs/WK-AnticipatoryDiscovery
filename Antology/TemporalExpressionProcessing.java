package Antology;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.PTBTokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.time.SUTime.Temporal;
import edu.stanford.nlp.time.SUTime.Time;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;

public class TemporalExpressionProcessing {
	ArrayList<String> initiateList = new ArrayList<String>();
	public TemporalExpressionProcessing() {
		initiateList.add("say");
		initiateList.add("says");
		initiateList.add("said");
	}
	public void getTitleTemporalResults(ArrayList<Documents> docList) {
		Properties props = new Properties();
	    AnnotationPipeline pipeline = new AnnotationPipeline();
	    pipeline.addAnnotator(new PTBTokenizerAnnotator(false));
	    pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
	    pipeline.addAnnotator(new POSTaggerAnnotator(false));
	    pipeline.addAnnotator(new TimeAnnotator("sutime", props));

	    for (Documents doc : docList) {
	      Annotation annotation    = new Annotation(doc.Title);

	      annotation.set(CoreAnnotations.DocDateAnnotation.class, doc.calendarString);
	      pipeline.annotate(annotation);
//	      System.out.println(annotation.get(CoreAnnotations.TextAnnotation.class));
	      List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
	      for (CoreMap cm : timexAnnsAll) {
	        List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
	        String temporal = cm + " [from char offset " +
		            tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) +
		            " to " + tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class) + ']' +
		            " --> " + cm.get(TimeExpression.Annotation.class).getTemporal();
	        doc.titleTemporal.add(temporal);
	      }
//	      System.out.println("--");
	    }
	}
	
	private void getTitleTemporalResults(String str, AnnotationPipeline pipeline, TemporalExpressionElment tee) {
		Annotation annotation    = new Annotation(str);
		
      	annotation.set(CoreAnnotations.DocDateAnnotation.class, tee.calendarString);
      	pipeline.annotate(annotation);
      	List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
      	for (CoreMap cm : timexAnnsAll) {
//	        List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
//	        String temporal = cm + " [from char offset " +
//		            tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) +
//		            " to " + tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class) + ']' +
//		            " --> " + cm.get(TimeExpression.Annotation.class).getTemporal();
      		Temporal t = cm.get(TimeExpression.Annotation.class).getTemporal();
	        if(t.getTimexType().toString().equals("DATE")) {
	        	tee.dates.add(t.getTime());
	        	List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
	        	int[] temp = new int[2];
	        	temp[0] = tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
	        	temp[1] = tokens.get(tokens.size()-1).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
	        	tee.temporalOffset.add(temp);
	        }
      	}
	}
	
	private void temporalOffsetTransfer(Sentence s, ArrayList<int[]> offsets, ArrayList<int[]> indices) {
		int size = s.size();
		for(int[] offsetArray : offsets) {
			int[] wordInd = new int[2];
			for(int i=0; i<2; i++) {
				int offset = offsetArray[i];
				int off = 0;
				for(int j=1; j<size; j++) {
					if(off==offset) {
						wordInd[i] = j;
						if(off>offset) {
							System.out.println("transfer may wrong:"+s.get(j-1).getForm());
							wordInd[i] = j-1;
//							System.exit(-1);
						}
						break;
					}
					off += s.get(j).getForm().length()+1;
				}
			}
			indices.add(wordInd);	
		}
	}
	
	private Word nearestVerbChild(Word parent, ArrayList<Word> words) {
		ArrayList<Word> list = new ArrayList<Word>();
		list.add(parent);
		Word nearest = null;
		Word   Verb = null;  
		while(list.size()>0&&(nearest==null||Verb==null)) {
			Word         top = list.get(0);
			list.remove(0);
			if(words.contains(top)) {
				if(nearest==null)
					nearest = top;
				if(Verb==null&&top.getPOS().charAt(0)=='V') 
					Verb    = top;
			}
			list.addAll(top.getChildren());
		}
		if(Verb!=null)
			return Verb; 
		return nearest;
	}
	
	private Word findRootNearestVerbPredicate(Sentence s, List<Predicate> preds) {
		Word root = null;
		for(Word word : s) {
			if(word.getDeprel().equals("ROOT")) {
				root = word;
				break;
			}
		}
		ArrayList<Word> words = new ArrayList<Word>();
		words.addAll(preds);
		return nearestVerbChild(root, words);
	}
	
	private boolean IsPredicate(List<Predicate> preds, Word word) {
		for(Predicate pred : preds) {
			if(pred.getIdx()==word.getIdx()&&
					(pred.getPOS().contains("VB")||pred.getDeprel().equals("APPO"))) {
				return true;
			}
		}
		return false;
	}
	
	private Word findTemporalNearestVerbPredicate(int[] temporalInd, Sentence s, List<Predicate> preds) {
		Word temporal = s.get(temporalInd[0]);
		Word    start = temporal;
		while(start.getIdx()>0) {
			if(IsPredicate(preds, start)) {
				return start;
			}
			start = start.getHead();
		}
		return s.get(0);
	}
	
	private Word nearestTemporalChild(Word parent, ArrayList<Word> words) {
		ArrayList<Word>         list = new ArrayList<Word>();
		list.add(parent);
		ArrayList<Word> prepositions = new ArrayList<Word>();
		while(list.size()>0) {
			Word         top = list.get(0);
			list.remove(0);
			if(top.getPOS().equals("IN"))
				prepositions.add(top);
			list.addAll(top.getChildren());
		}
		for(Word w:prepositions) {
			list.clear();
			list.add(w);
			while(list.size()>0) {
				Word         top = list.get(0);
				list.remove(0);
				if(words.contains(top))
					return top;
				list.addAll(top.getChildren());
			}
		}
		return null;
	}
	
	private void temporal_filter(Sentence s, TemporalExpressionElment tee) {
		ArrayList<int[]>  temporalIndex = tee.temporalIndex;
		ArrayList<Time>           dates = tee.dates;
		for(int i=0; i<temporalIndex.size(); i++) {
			int[]         indices = temporalIndex.get(i);
			if(indices[0]==1) {
				temporalIndex.remove(i);
				dates.remove(i);
				i--;
				continue;
			}
			if(indices[0]>1) {
				Word word = s.get(indices[0]-1);
				if(!word.getPOS().equals("IN")) {
					temporalIndex.remove(i);
					dates.remove(i);
					i--;
					continue;
				}
			}
		}
	}
	
	private void temporal_TitlePredicate(TemporalExpressionElment tee,  Sentence s) {
		int size = tee.dates.size();
    	if(size>0) {
	    	temporalOffsetTransfer(s, tee.temporalOffset, tee.temporalIndex);
//	    	Word     w = null;
	    	ArrayList<Word[]> words = new ArrayList<Word[]>();
	    	for(int[] ind : tee.temporalIndex) {
	    		Word[] temp = new Word[2];
	    		temp[0]     = s.get(ind[0]);
	    		temp[1]     = s.get(ind[1]);
	    		words.add(temp);
	    	}
		    List<Predicate> preList = s.getPredicates();
		    if(preList.size()==0)
		    	return;
		    ArrayList<Word> nearestPres = new ArrayList<Word>();
		    for(int[] temporalInd : tee.temporalIndex) {
		    	Word pred = findTemporalNearestVerbPredicate(temporalInd, s, preList);
		    	tee.preList.add(pred);
		    }
    	}
	}
	
	private String getStringFromSentence(Sentence s) {
		String str = "";
		for(Word w : s) {
			str += w.getForm()+" ";
		}
		return str.trim();
	}
	
	private void linkPredicateAndTemporalExpression(ArrayList<Triplet>  titleTripletList, TemporalExpressionElment tee) {
		for(Triplet tri : titleTripletList) {
			for(int i=0; i<tee.preList.size(); i++) {
				Word w = tee.preList.get(i);
				if(tri.ActionIndex==w.getIdx()) {
					tri.temporalIndex[0] = tee.temporalIndex.get(i)[0];
					tri.temporalIndex[1] = tee.temporalIndex.get(i)[1];
					tri.calendarString   = tee.calendarString;
				}
				
			}
		}
	}
	
	private int getPathLength(Word word, int index) {
		int length = 0;
		while(word.getIdx()!=index) {
			if(word.getIdx()==0)
				return -1;
			if(word.getIdx()!=index) {
				length++;
				word = word.getHead();
			}
		}
		return length;
	}
	
	private int initiateNearestPredicate(Sentence s, Word word) {
		Word w = word.getHead();
		int t = 0;
		while(w.getIdx()!=0) {
			t++;
			w = w.getHead();
			if(initiateList.contains(w.getForm())) {
				return t;
			}
		}
		return t;
	}
	
	private void linkTemporalExpressionToNearestTriples(Sentence s, ArrayList<Triplet>  TripletList, TemporalExpressionElment tee) {
		for(int i=0; i<tee.temporalIndex.size(); i++) {
			int[] temporalIndex = tee.temporalIndex.get(i);
			Word  word = s.get(temporalIndex[0]);
			int length = initiateNearestPredicate(s, word);
			int index  = -1;
			for(int j=0; j<TripletList.size(); j++) {
				Triplet tri = TripletList.get(j);
				int plength = getPathLength(word, tri.ActionIndex);
				if(plength>0&&length>plength) {
					length = plength;
					index  = j;
				}
			}
			if(index>-1) {
				Triplet tri = TripletList.get(index);
				tri.temporalIndex[0] = tee.temporalIndex.get(i)[0];
				tri.temporalIndex[1] = tee.temporalIndex.get(i)[1];
				tri.calendarString   = tee.calendarString;
				tri.realTemporal     = tee.dates.get(i);
			}
		}
	}
	
//	private void temporal_ContextPredicate(eventElement ee, GetTriplet GT, String title, 
//			String str, Lemmatizer lema) {
//		int size = ee.dates.size();
//    	if(size>0) {
//	    	Sentence S_context = GT.quickGetTriplet(str, lema);
//	    	temporalOffsetTransfer(S_context, ee.temporalOffset, ee.temporalIndex);
//	    	Word             w = null;
//	    	ArrayList<Word> words = new ArrayList<Word>();
//	    	for(int ind : ee.temporalIndex) {
//	    		words.add(S_context.get(ind));
//	    	}
////		    List<Predicate> preList     = S_title.getPredicates();
//		    List<Predicate> pre_context = S_context.getPredicates();
////		    Word samePre                = null;
////		    Word pred = findRootNearestPredicate(S_title, preList);
////		    for(int i=0; i<pre_context.size(); i++) {
////		    	Word word = pre_context.get(i);
////		    	if(preList.size()==0)
////			    	return;
////		    	if(word.getLemma().toLowerCase().equals(pred.getLemma().toLowerCase())) {	    			
////		    		samePre = word;
////		    		break;
////		    	}
////		   	}
////		    Word samePre                = null;
//		    Word samePre = findRootNearestVerbPredicate(S_context, pre_context);
//		    if(samePre!=null)
//		    	w = nearestTemporalChild(samePre, words);
//	    	if(w!=null) {
//	    		for(int i=0; i<size; i++) {
//		    		if((w.getIdx()-ee.temporalIndex.get(i))==0) {
//		    			ee.realTemporal = ee.dates.get(i);
//		    			break;
//		    		}
//	    		}
//	    	}
//    	}
//	}
	
	public void getEventTemporalResults(ArrayList<Documents> docList, ArrayList<ArrayList<Sentence>> sentences, 
			ArrayList<ArrayList<Triplet>>  titleTripletList) {
		Properties            props = new Properties();
	    AnnotationPipeline pipeline = new AnnotationPipeline();
	    pipeline.addAnnotator(new PTBTokenizerAnnotator(false));
	    pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
	    pipeline.addAnnotator(new POSTaggerAnnotator(false));
	    pipeline.addAnnotator(new TimeAnnotator("sutime", props));
	    int                    size = titleTripletList.size();
//	    GetTriplet               GT = new GetTriplet();
//	    Lemmatizer            lemma = new Lemmatizer();
//	    MySentenceDetector      MSD = new MySentenceDetector();
	    ReadData                 RD = new ReadData();
//	    Set<String>       actionSet = RD.ReadSet("data/list/actionList");
	    for (int i=0; i<size; i++) {
	    	System.out.println(i);
	    	if(i==346)
	    		System.out.println();
	    	ArrayList<Sentence> subSenList = sentences.get(i);
	    	ArrayList<Triplet> titleTriplets = titleTripletList.get(i);
	    	if(titleTriplets.size()==0)
	    		continue;
	    	for(int j=0; j<subSenList.size(); j++) {
			    Sentence s = subSenList.get(j);
			    TemporalExpressionElment tee = new TemporalExpressionElment();
				Documents     doc = docList.get(i);
				tee.calendarString = doc.calendarString;
				String str = getStringFromSentence(s);
				getTitleTemporalResults(str, pipeline, tee);
				temporalOffsetTransfer(s, tee.temporalOffset, tee.temporalIndex);
				temporal_filter(s, tee);
			    if(tee.dates.size()>0) {
//	//		    		temporalRanking(ee, GT, doc.Title, pairs.get(i)[j+2]);
//			    	temporal_TitlePredicate(tee, s);
//			    	linkPredicateAndTemporalExpression(titleTriplets, tee);
			    	linkTemporalExpressionToNearestTriples(s, titleTriplets, tee);
			    }
//			    else if(tee.realTemporal==null) {
//			    	tee.temporalIndex.clear();
//			    	tee.temporalOffset.clear();
//			    	if(doc.Context==null||doc.Context.length()==0)
//			    		continue;
//			    		
//			    	String contexts = MSD.sdetector.sentDetect(doc.Context.split("\n")[0])[0];
//			    	String     cont = lemma.myTokenizeToString(contexts);
//			    	if(contexts.length()>0) {
//			    		getTitleTemporalResults(cont, pipeline, tee);
//			    		if(tee.dates.size()>0) {
//	//				    	temporalRanking(ee, GT, contexts[0], -1);
//			    			Sentence sen = null;
//			    			GT.getTriplet(cont.trim().split(" "), sen);
//			    			ArrayList<Triplet> triplets = new ArrayList<Triplet>();
//			    			GT.getTripletListFromAction_singleSentence(sen, triplets, actionSet, lemma);
//			    			
//			    			TemporalExpressionElment tee_cont = new TemporalExpressionElment();
//			    			temporal_TitlePredicate(tee_cont, sen);
//			    			
//			    			linkPredicateAndTemporalExpression(triplets, tee);
//					    }
//			    	}
//		    	}
	    	}
	    }
	}
	
	public void println(Sentence s) {
		int size = s.size();
		List<Predicate> preList = s.getPredicates();
		for(int i=1; i<size; i++) {
			Word word = s.get(i);
			System.out.printf("%-2s\t",String.valueOf(word.getIdx()));
			System.out.printf("%-17s\t",word.getForm());
			System.out.printf("%-17s\t",word.getLemma());
			System.out.printf("%-5s\t",word.getPOS());
			System.out.printf("%-8s\t",word.getHeadId());
			System.out.printf("%-8s\t",word.getDeprel());
			if(preList.contains(word))
				System.out.printf("%-17s\t",word.getForm());
			else
				System.out.printf("%-17s\t","_");
			for(int j=0; j<preList.size(); j++) {
				Predicate pre = preList.get(j);
				String ss = pre.getArgumentTag(word);
				if(ss==null)
					System.out.printf("%-6s\t", "_");
				else
					System.out.printf("%-6s\t", ss);
			}
			System.out.println();
		}
		System.out.println("--------------\n");
	}
	
	public void getEventTemporalResults_firstSentence(ArrayList<Documents> docList, ArrayList<Sentence> sentences, ArrayList<ArrayList<Triplet>>  TripletList) {
		Properties            props = new Properties();
	    AnnotationPipeline pipeline = new AnnotationPipeline();
	    pipeline.addAnnotator(new PTBTokenizerAnnotator(false));
	    pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
	    pipeline.addAnnotator(new POSTaggerAnnotator(false));
	    pipeline.addAnnotator(new TimeAnnotator("sutime", props));
	    int                    size = TripletList.size();
	    for (int i=0; i<size; i++) {
	    	Sentence s = sentences.get(i);
//	    	if(i==400)
//	    		System.out.println(i);
	    		
//	    	println(s);
	    	ArrayList<Triplet> Triplets = TripletList.get(i);
	    	if(Triplets.size()==0)
	    		continue;
		    TemporalExpressionElment tee = new TemporalExpressionElment();
			Documents     doc = docList.get(i);
			tee.calendarString = doc.calendarString;
			String str = getStringFromSentence(s);
			getTitleTemporalResults(str, pipeline, tee);
			temporalOffsetTransfer(s, tee.temporalOffset, tee.temporalIndex);
			temporal_filter(s, tee);
			if(tee.dates.size()>0) {
//			    temporal_TitlePredicate(tee, s);
//			    linkPredicateAndTemporalExpression(Triplets, tee);
				linkTemporalExpressionToNearestTriples(s, Triplets, tee);
	    	}
	    }
	}
}

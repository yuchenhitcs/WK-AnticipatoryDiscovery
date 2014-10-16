package Antology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

import se.lth.cs.srl.SemanticRoleLabeler;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.languages.Language;
import se.lth.cs.srl.options.CompletePipelineCMDLineOptions;
import se.lth.cs.srl.pipeline.Pipeline;
import se.lth.cs.srl.preprocessor.Preprocessor;
import se.lth.cs.srl.util.FileExistenceVerifier;
public class GetTriplet {
	public SemanticRoleLabeler srl = null;
	String[]                   args = new String[13];
	Preprocessor       pp           = null;
//	public Preprocessor         ppp = null;
	public ArrayList<String> verbBeforeTo = new ArrayList<String>();
	public ArrayList<String> NoneBehindTo = new ArrayList<String>();
	public Set<String>          statusSet = new HashSet<String>();
	public Set<String>         entityList = new HashSet<String>(); 
	public Set<String> 		 entityMDList = new HashSet<String>();
	public Set<String>    preModification = new HashSet<String>();
	GetTriplet() {
		verbBeforeTo.add("relating");
		
		NoneBehindTo.add("bonus");
		
		statusSet.add("available");
		statusSet.add("updated");
		
		entityList.add("explanation");
		entityList.add("updated information");
		entityList.add("report");
		entityList.add("information");
		entityList.add("bill");
		entityList.add("bill text");
		entityList.add("insight");
		entityList.add("text of bill");
		entityList.add("regulations");
		entityList.add("definition");
		entityList.add("updates");
		entityList.add("briefing");
		
		entityMDList.add("the");
		entityMDList.add("additional");
		entityMDList.add("the additional");
		entityMDList.add("updated");
		entityMDList.add("the updated");
		entityMDList.add("following");
		entityMDList.add("the following");
		entityMDList.add("proposed");
		entityMDList.add("the proposed");
		entityMDList.add("tax");
		entityMDList.add("the tax");
		entityMDList.add("CCH tax");
		entityMDList.add("the CCH tax");
		
		preModification.add("attempt");
		preModification.add("not");
		args[0]  = "eng";
		args[1]  = "-lemma"; args[2]  = "models/CoNLL2009-ST-English-ALL.anna-3.3.lemmatizer.model";
		args[3]  = "-tagger";args[4]  = "models/CoNLL2009-ST-English-ALL.anna-3.3.postagger.model";
		args[5]  = "-parser";args[6]  = "models/CoNLL2009-ST-English-ALL.anna-3.3.parser.model";
		args[7]  = "-srl";   args[8]  = "models/CoNLL2009-ST-English-ALL.anna-3.3.srl-4.1.srl.model";
		args[9]  = "-test";  args[10] = "inputsample"; 
		args[11] = "-out";   args[12] = "outputsample";
		
		CompletePipelineCMDLineOptions options = new CompletePipelineCMDLineOptions();
	    options.parseCmdLineArgs(args);
	    String error=FileExistenceVerifier.verifyCompletePipelineAllNecessaryModelFiles(options);
	    if(error!=null){
			System.err.println(error);
			System.err.println();
			System.err.println("Aborting.");
			System.exit(1);
		}
	    try {
	    	pp = Language.getLanguage().getPreprocessor(options); 
	    	
	    	ZipFile zipFile=new ZipFile(args[8]);
			srl = Pipeline.fromZipFile(zipFile);
	    } catch (Exception ie) {
        	System.out.println(ie.getMessage());
        }
	   
	}
    
    	
    
	public ArrayList<Sentence> getTriplet(ArrayList<String[]> stringArray, ArrayList<Documents> data) {
//		Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
//		String[] args = new String[13];
//		args[0]  = "eng";
//		args[1]  = "-lemma"; args[2]  = "models/CoNLL2009-ST-English-ALL.anna-3.3.lemmatizer.model";
//		args[3]  = "-tagger";args[4]  = "models/CoNLL2009-ST-English-ALL.anna-3.3.postagger.model";
//		args[5]  = "-parser";args[6]  = "models/CoNLL2009-ST-English-ALL.anna-3.3.parser.model";
//		args[7]  = "-srl";   args[8]  = "models/CoNLL2009-ST-English-ALL.anna-3.3.srl-4.1.srl.model";
//		args[9]  = "-test";  args[10] = "inputsample"; 
//		args[11] = "-out";   args[12] = "outputsample";
//		CompletePipelineCMDLineOptions options = new CompletePipelineCMDLineOptions();
//        options.parseCmdLineArgs(args);
//        String error=FileExistenceVerifier.verifyCompletePipelineAllNecessaryModelFiles(options);
        ArrayList<Sentence> senList = new ArrayList<Sentence>();
//        if(error!=null){
//			System.err.println(error);
//			System.err.println();
//			System.err.println("Aborting.");
//			System.exit(1);
//		}
        try {
//	        Preprocessor       pp = Language.getLanguage().getPreprocessor(options); 
	        ReadData           rd = new ReadData();
			Set<String> Locations = rd.ReadSet("data/list/States");
			Set<String>  taxtypes = rd.ReadSet("data/list/taxType"); 
	        int           docSize = stringArray.size();    
			
	        for(int ii=0; ii<docSize; ii++) {
	        	String[] str = stringArray.get(ii);
	        	List<String> forms=new ArrayList<String>();
				forms.add("<root>");
				if(str.length>0){
				//	String[] tokens = WHITESPACE_PATTERN.split(str);
					int[]     index = IsMatchPattern(str, Locations, taxtypes);
					String location = locationTermMatch(str, Locations);
					String  taxtype = taxTermMatch(str, taxtypes);
					data.get(ii).TitleLocation = location;
					data.get(ii).TitleTaxType  = taxtype;
					for(int i=index[0]; i<index[1]; i++) {
						if(str[i].equals(".")) {
							forms.add("MYDOT");
							continue;
						}
						forms.add(str[i]);
					}
				}
				if(forms.size()>1){ //We have the root token too, remember!
					senList.add(getPreprocessing(forms, pp));
				}
			}
//			SemanticRoleLabeler srl = null; //= Pipeline.fromZipFile(zipFile);
//			ZipFile zipFile=new ZipFile(args[8]);
//			srl = Pipeline.fromZipFile(zipFile);
//			List<Boolean> isPred=new ArrayList<Boolean>();
//			isPred.add(false);
			
			for(int i=0; i<senList.size(); i++) {
				Sentence s = senList.get(i);
				srl.parseSentence(s);
//				if(i==11)
//					System.out.println(i);
//				println(s);
			}
        } catch (Exception ie) {
        	System.out.println(ie.getMessage());
        }
        return senList;
	}
	
	public void getTriplet_semicolon(ArrayList<ArrayList<String[]>> stringArray, 
			ArrayList<Documents> data, ArrayList<ArrayList<Sentence>> senList) {
//		Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
//		String[] args = new String[13];
//		args[0]  = "eng";
//		args[1]  = "-lemma"; args[2]  = "models/CoNLL2009-ST-English-ALL.anna-3.3.lemmatizer.model";
//		args[3]  = "-tagger";args[4]  = "models/CoNLL2009-ST-English-ALL.anna-3.3.postagger.model";
//		args[5]  = "-parser";args[6]  = "models/CoNLL2009-ST-English-ALL.anna-3.3.parser.model";
//		args[7]  = "-srl";   args[8]  = "models/CoNLL2009-ST-English-ALL.anna-3.3.srl-4.1.srl.model";
//		args[9]  = "-test";  args[10] = "inputsample"; 
//		args[11] = "-out";   args[12] = "outputsample";
//		CompletePipelineCMDLineOptions options = new CompletePipelineCMDLineOptions();
//        options.parseCmdLineArgs(args);
//        String error=FileExistenceVerifier.verifyCompletePipelineAllNecessaryModelFiles(options);
////        ArrayList<ArrayList<Sentence>> senList = new ArrayList<ArrayList<Sentence>>();
//        if(error!=null){
//			System.err.println(error);
//			System.err.println();
//			System.err.println("Aborting.");
//			System.exit(1);
//		}
        try {
//	        Preprocessor       pp = Language.getLanguage().getPreprocessor(options); 
	        ReadData           rd = new ReadData();
			Set<String> Locations = rd.ReadSet("data/list/States");
			Set<String>  taxtypes = rd.ReadSet("data/list/taxType"); 
	        int           docSize = stringArray.size();    
			
	        for(int ii=0; ii<docSize; ii++) {
//	        	if(ii>500)
//	        		return;
	        	ArrayList<String[]> subStringArray = stringArray.get(ii);
	        	ArrayList<Sentence>     subSenList = new ArrayList<Sentence>();
	        	for(int jj=0; jj<subStringArray.size(); jj++) {
		        	String[] str = subStringArray.get(jj);
		        	List<String> forms=new ArrayList<String>();
					forms.add("<root>");
					if(str.length>0){
					//	String[] tokens = WHITESPACE_PATTERN.split(str);
						int[]     index = IsMatchPattern(str, Locations, taxtypes);
						String location = locationTermMatch(str, Locations);
						String  taxtype = taxTermMatch(str, taxtypes);
						data.get(ii).TitleLocation = location;
						data.get(ii).TitleTaxType  = taxtype;
						for(int i=index[0]; i<index[1]; i++) {
							if(str[i].equals(".")) {
								forms.add("MYDOT");
								continue;
							}
							forms.add(str[i]);
						}
					}
					if(forms.size()>1){ //We have the root token too, remember!
						subSenList.add(getPreprocessing(forms, pp));
					}
	        	}
	        	senList.add(subSenList);
			}
	        
//			SemanticRoleLabeler srl = null; //= Pipeline.fromZipFile(zipFile);
//			ZipFile zipFile=new ZipFile(args[8]);
//			srl = Pipeline.fromZipFile(zipFile);
			
			for(int i=0; i<senList.size(); i++) {
				ArrayList<Sentence> subSenList =senList.get(i);
				for(int j=0; j<subSenList.size(); j++) {
					Sentence s = subSenList.get(j);
					srl.parseSentence(s);
				}
			}
        } catch (Exception ie) {
        	System.out.println(ie.getMessage());
        }
//        return senList;
	}
	
	public void getTriplet_firstSentence(ArrayList<String[]> stringArray, ArrayList<Sentence> senList) {
        try {
//	        ReadData           rd = new ReadData();
//	        Set<String> Locations = rd.ReadSet("data/list/States");
//			Set<String>  taxtypes = rd.ReadSet("data/list/taxType"); 
	        for(int jj=0; jj<stringArray.size(); jj++) {
//	        	System.out.println("preprocessing:"+jj);
		        String[] str = stringArray.get(jj);
		        List<String> forms=new ArrayList<String>();
		        System.out.println(jj);
				forms.add("<root>");
				if(str.length>0){
					//	String[] tokens = WHITESPACE_PATTERN.split(str);
//					String location = locationTermMatch(str, Locations);
//					String  taxtype = taxTermMatch(str, taxtypes);
					for(int i=0; i<str.length; i++) {
						if(i==str.length-1&&str[i].equals(".")) {
							continue;
						}
						if(str[i].equals(".")) {
							forms.add("MYDOT");
							continue;
						}
						forms.add(str[i]);
					}
				}
				if(forms.size()>1){ //We have the root token too, remember!
					senList.add(getPreprocessing(forms, pp));
				} else 
					senList.add(null);
			}
	        
			for(int i=0; i<senList.size(); i++) {
//				System.out.println("SRLing:"+i);
				Sentence s = senList.get(i);
				if(s!=null)
					srl.parseSentence(s);
			}
        } catch (Exception ie) {
        	System.out.println(ie.getMessage());
        }
//        return senList;
	}
	
	public void getTriplet(String[] stringArray, Sentence sen) {
        try {
        	List<String> forms=new ArrayList<String>();
			forms.add("<root>");
			if(stringArray.length>0){
//				int[]     index = IsMatchPattern(stringArray, Locations, taxtypes);
//				String location = locationTermMatch(str, Locations);
//				String  taxtype = taxTermMatch(str, taxtypes);
//				data.get(ii).TitleLocation = location;
//				data.get(ii).TitleTaxType  = taxtype;
				for(int i=0; i<stringArray.length; i++) {
					if(stringArray[i].equals(".")) {
						forms.add("MYDOT");
						continue;
					}
					forms.add(stringArray[i]);
				}
			}
			if(forms.size()>1){ //We have the root token too, remember!
					sen = getPreprocessing(forms, pp);
			}
	        
//			SemanticRoleLabeler srl = null; //= Pipeline.fromZipFile(zipFile);
//			ZipFile zipFile=new ZipFile(args[8]);
//			srl = Pipeline.fromZipFile(zipFile);
			
			srl.parseSentence(sen);
        } catch (Exception ie) {
        	System.out.println(ie.getMessage());
        }
//        return senList;
	}
	
	private int[] IsMatchPattern(String[] tokens, Set<String> locations, Set<String> taxTypes) {
		int[]   indices = new int[2];
		indices[0]      = 0;
		indices[1]      = tokens.length;
		String        s = "";
		String location = "";
		String  taxtype = "";
		boolean   flag1 = true;
		boolean   flag2 = false;
		int       index = 0;
		int       ind   = -1;
		for(int i=0; i<tokens.length; i++) {
			String token = tokens[i];
			s += token;
			if(token.equals("--")) {
				flag1 = false;
			}
			if(index == 0&&token.equals(":")) {
				flag2 = false;
				index = i+1;
			}
			if(flag1)
				location += token+" ";
			if(flag2)
				taxtype  += token+" ";
			if(token.equals("--")) {
				flag2 = true;
			}
			if(token.equals("("))
				ind = i;
		}
		if(Pattern.matches(".+--.+:.+", s)) {
			indices[0] = index;
		}
		if(Pattern.matches("codesec.[0-9]+.*:.+", s)) {
			indices[0] = index;
		}
		if(Pattern.matches(".+(.+)", s)&&ind!=-1) {
			indices[1] = ind;
		}
		return indices;
	}
	
	private String locationTermMatch(String[] s, Set<String> set) {
		String ss = "";
		for(String w : s) {
			ss += w+" ";
		}
		ss           = ss.toLowerCase().trim();
		String match = "";
		for(String str : set) {
			str      = str.toLowerCase();
			if(ss.contains(str.toLowerCase())) {
				if(match.length()==0)
					match = str;
				else {
					match += ", "+str;
				}
			}
		}
		return match;
	}
	
	private String taxTermMatch(String[] s, Set<String> set) {
		boolean flag = false; 
		String match = "";
		for(String str : s) {
			boolean f = false;
			str = str.toLowerCase();
			if(flag&&(str.equals("and")||str.equals(","))) {
				match += ",";
				continue;
			}
			for(String ss : set) {
				if(ss.contains(str)) {
					flag = true;
					f    = true;
					if(match.length()==0)
						match = str;
					else {
						match += " "+str;
					}
					break;
				}
			}
			if(flag&&!f&&!str.contains("tax")) {
				match = "";
				flag  = false;
				break;
			} else if(flag&&!f&&str.contains("tax")) {
					return match;
			}
		}
		return match;
	}
	
	public Sentence getPreprocessing(List<String> words, Preprocessor pp) {
		Sentence s = null;
		try{ 
			String[] array = words.toArray(new String[words.size()]);
			s              = new Sentence(pp.preprocess(array),false);
		} catch(Exception ie) {
			System.out.println();
		}
		return s;
	}
//	
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
	
//	private Word setNewWord(Word oldWord, Sentence s, int index, String POS) {
//		String  form = oldWord.getForm();
//		String lemma = oldWord.getLemma();
//		String feats = oldWord.getFeats();
//		Word word = new Word(form, lemma, POS, feats, s, index);
//		word.setChildren((HashSet<Word>)oldWord.getChildren());
////		s.remove(index+1);
//		return word;
//	}
	
	private String myLemma(String wordLemma) {
		String  le_extra = wordLemma;
		if(wordLemma.charAt(wordLemma.length()-1)=='s'){
			le_extra = le_extra.substring(0, wordLemma.length()-1);
		}
		return le_extra;
	}
	
	private void exceptionProcessing(Sentence s, ArrayList<String> actionLemma, Lemmatizer lemma) {
		List<Predicate>     preList = s.getPredicates();
//		ArrayList<Triplet> triplets = new ArrayList<Triplet>();
		for(Predicate pre : preList) {
			if(!IsActionListContainPredicate(actionLemma, pre.getForm(), lemma))
				continue;
			if((pre.getDeprel().equals("ROOT")||pre.getDeprel().equals("APPO"))&&pre.getPOS().contains("VBN")) {
				int rootInd = pre.getIdx();
				if(rootInd<s.size()-1) {
					Word nextWord = s.get(rootInd+1);
					String   wordLemma = nextWord.getLemma(); 
					String        le = lemma.tokenLemmatize(nextWord.getLemma());
					String  le_extra = wordLemma;
					if(wordLemma.charAt(wordLemma.length()-1)=='s'){
						le_extra = le_extra.substring(0, wordLemma.length()-1);
					}
					if((actionLemma.contains(nextWord.getLemma())||actionLemma.contains(le)||actionLemma.contains(le_extra))&&!nextWord.getDeprel().equals("APPO")&&!nextWord.getDeprel().equals("ROOT")) {
//						Word newWord = setNewWord(s.get(rootInd+1), s, rootInd+1, "VB");
						
						Predicate nextPredicate = new Predicate(s.get(rootInd+1));
						nextPredicate.setPOS("VB");
						s.remove(rootInd+1);
						s.add(rootInd+1, nextPredicate);
						nextPredicate.setDeprel("ROOT");
						pre.getChildren().remove(s.get(rootInd+1));
						for(int i=rootInd-1; i>0; i--) {
							if(s.get(i).getHead().getIdx()!=rootInd) {
								pre.setHead(s.get(i));
								break;
							}
						}
						for(int i=1; i<s.size();i++) {
							if(s.get(i).getHead().getIdx()==rootInd&&!s.get(i).getPOS().equals("RB")) {
								pre.getChildren().remove(s.get(i));
								if(s.get(i).getIdx()==rootInd+1)
									continue;
								s.get(i).setHead(nextPredicate);
							}
						}
						if(rootInd==s.size()-2) {
							Map<Word, String> attr = pre.getArgMap();
							Set<Word> words        = attr.keySet();
							for(Word ww :words) {
		//						System.out.println(i);
//								if(ww.getHead().getIdx()==rootInd&&!ww.getPOS().equals("RB")) {
//									if(ww.getIdx()==rootInd+1)
//										continue;
//									ww.setHead(nextPredicate);
//								}
								nextPredicate.setHead(s.get(0));
								if(pre.getArgumentTag(ww).equals("A0")) {
									Map<Word, String> argmap = new HashMap<Word, String>();
									argmap.put(ww, "A1");
//									nextPredicate.setArgMap(argmap);
									pre.setDeprel("NMOD");
//									pre.getChildren().clear();
									s.makePredicate(rootInd+1);
									List<Predicate> listP = s.getPredicates();
									for(Predicate pp : listP) {
										if(pp.getIdx()==rootInd+1)
											pp.addArgMap(ww, "A1");
									}
//									for(Word wws:s) {
//										Set<Word> wordss = wws.getChildren();
//										System.out.println(wws.getForm()+": ");
//										for(Word ssss:wordss) {
//											System.out.println(ssss.getForm());
//										}
//										System.out.println("-----------------");
//									}
									return;
								}
							}
						}
					}
				}
			}
		}
	}
	
	private boolean Isloop(Sentence s, Word word1, Word word2) {
		Stack<Word> stack = new Stack<Word>();
		stack.push(word1);
		while(stack.size()>0) {
			Word w = stack.pop();
			Set<Word> children = w.getChildren();
			if(children.contains(word2))
				return true;
			stack.addAll(children);
		}
		return false;
	}
	
	private void exceptionProcessing_RootAfterPredicate(Sentence s, ArrayList<String> actionLemma, Lemmatizer lemma) {
		List<Predicate>     preList = s.getPredicates();
		int predicateInd = -1;
		Predicate predicate = null;
		Predicate   preRoot = null;
		for(Predicate pre : preList) {
			if(pre.getDeprel().equals("ROOT"))
				preRoot = pre;
			if(!IsActionListContainPredicate(actionLemma, pre.getForm(), lemma))
				continue;
			if((pre.getDeprel().equals("ROOT")||pre.getDeprel().equals("APPO"))&&pre.getPOS().contains("VB")) {
				predicateInd = pre.getIdx();
				predicate    = pre;
			}
		}
		for(Predicate pre : preList) {
			if(!IsActionListContainPredicate(actionLemma, pre.getForm(), lemma))
				continue;
			if(preRoot!=null&&pre.getHead().getIdx()!=preRoot.getIdx())
				continue;
			if(predicate==null) {
				predicateInd = pre.getIdx();
				predicate    = pre;
			}
		}
		int rootInd = -1;
		for(Word word : s) {
			if(word.getDeprel().equals("ROOT")&&word.getPOS().contains("NN")) {
				rootInd = word.getIdx();
			}
		}
		if(predicateInd==-1||rootInd==-1)
			return;
//		println(s);
		Word root    = s.get(rootInd);
		root.setDeprel("NMOD");
		Word newRoot = s.get(predicateInd);
		newRoot.setDeprel("ROOT");
		newRoot.setHead(s.get(0));
		newRoot.setPOS("VB");
		
		for(int i=1; i<s.size();i++) {
//			if(s.get(i).getChildren().size()>0) {
//				Set<Word> children = s.get(i).getChildren();
//				for(Word w : children) {
//					if(w.getIdx()==newRoot.getIdx()) {
//						children.remove(newRoot);
//					}
//				}
//			}
			if(s.get(i).getChildren().contains(newRoot)) {
				s.get(i).getChildren().remove(newRoot);
			}
			if(s.get(i).getHead().getIdx()==rootInd) {
				if(s.get(i).getDeprel().equals("NMOD"))
					continue;
//				if(predicate!=null){
//					Map<Word, String> attr = preRoot.getArgMap();
//					if(attr.containsKey(s.get(i))&&(preRoot.getArgumentTag(s.get(i)).equals("A0")||
//						preRoot.getArgumentTag(s.get(i)).equals("A1")||
//						preRoot.getArgumentTag(s.get(i)).equals("A2"))) {
//						newRoot.getChildren().add(s.get(i));
//						s.get(i).getHead().getChildren().remove(s.get(i));
//					}
//				}
				if(predicate!=null){
					Map<Word, String> attr = predicate.getArgMap();
					if(attr.containsKey(s.get(i))&&(predicate.getArgumentTag(s.get(i)).equals("A0")||
							predicate.getArgumentTag(s.get(i)).equals("A1")||
							predicate.getArgumentTag(s.get(i)).equals("A2"))) {
						newRoot.getChildren().add(s.get(i));
						s.get(i).getHead().getChildren().remove(s.get(i));
						s.get(i).setHead(newRoot);
					}
				}
				if(preRoot==null||preRoot.getArgumentTag(s.get(i))==null) {
					s.get(i).setHead(newRoot);
					newRoot.getChildren().add(s.get(i));
					root.getChildren().remove(s.get(i));
				} 
			}
			if(s.get(i).getChildren().contains(newRoot)) {
				s.get(i).getChildren().remove(newRoot);
			}
		}
		boolean setRootParent = false;
		if(rootInd>predicateInd+1) {
			for(int i=rootInd-1; i>0; i--) {
				if(!Isloop(s, root, s.get(i))&&s.get(i).getHead().getIdx()!=root.getIdx()) {
					root.setHead(s.get(i));
					setRootParent = true;
					break;
				}
			}
		}
		if(rootInd==1||!setRootParent) {
			for(int i=rootInd+1; i<s.size(); i++) {
				if(!Isloop(s, root, s.get(i))&&s.get(i).getHead().getIdx()!=root.getIdx()) {
					root.setHead(s.get(i));
					break;
				}
			}
		}
		if(predicateInd>0) {
			for(int i=1; i<s.size();i++) {
				adjustParentofWord(s, s.get(i), predicate);
			}
		}
	}
	
	private boolean IsMDCondition(Sentence s, int i) {
		if(s.get(i).getPOS().equals("MD")&&s.get(i).getDeprel().equals("ROOT")&&s.get(i+1).getPOS().contains("VB"))
			return true;
		if(i<(s.size()-2)&&s.get(i).getPOS().equals("MD")&&s.get(i).getDeprel().equals("ROOT")&&s.get(i+1).getForm().toLowerCase().equals("not")&&s.get(i+2).getPOS().contains("VB"))
			return true;
		if(s.get(i).getLemma().equals("be")&&s.get(i).getDeprel().equals("ROOT")&&s.get(i+1).getPOS().contains("VB"))
			return true;
		if(i<(s.size()-2)&&s.get(i).getLemma().equals("be")&&s.get(i).getDeprel().equals("ROOT")&&s.get(i+1).getForm().toLowerCase().equals("not")&&s.get(i+2).getPOS().contains("VB"))
			return true;
		return false;
	}
	
	private void exceptionProcessing_RootAtMD(Sentence s) {
		for(int i=0; i<s.size(); i++) {
//			if(s.get(i).getForm().equals("could")&&s.get(i).getDeprel().equals("ROOT")&&s.get(i+1).getPOS().contains("VB")) {
//			if(s.get(i).getPOS().equals("MD")&&s.get(i).getDeprel().equals("ROOT")&&s.get(i+1).getPOS().contains("VB")) {		
//				println(s);
//				s.get(i+1).getChildren().addAll(s.get(i).getChildren());
			if(IsMDCondition(s, i)) {
				int jj = i+1;
				for(Word w:s.get(i).getChildren()) {
					if(w.getIdx()==s.get(i+1).getIdx())
						continue;
					if(s.size()>(i+1)&&s.get(i+1).getForm().toLowerCase().equals("not"))
						jj++;
					s.get(jj).getChildren().add(s.get(i));
					break;
				}
//				s.get(jj).getChildren().addAll(s.get(i).getChildren());
				s.get(i).getChildren().clear();
				s.get(i).setDeprel("MOD");
				s.get(jj).setDeprel("ROOT");
				s.get(i).setHead(s.get(jj));
				s.get(jj).setHead(s.get(0));
				for(int j=0; j<s.size(); j++) {
					if(j==jj)
						continue;
					if(s.get(j).getHeadId()==i) {
						s.get(j).setHead(s.get(jj));
//						s.get(jj).getChildren().add(s.get(j));
					}
				}
//				println(s);
			}
		}
	}
	
	private boolean IsAllInCapital(String s) {
		char[] ches = s.toCharArray();
		for(char ch : ches) {
			if(ch>='A'&&ch<='Z')
				continue;
			return false;
		}
		return true;
	}
	
	private void exceptionProcessing_AllInCapital(Sentence s) {
		for(int i=2; i<s.size(); i++) {
			if(IsAllInCapital(s.get(i).getForm())) {
				if(s.get(i-1).getForm().equals("to")) {
					if(s.get(i).getPOS().contains("VB")) {
						s.get(i).setPOS("NNP");
						s.get(i).setDeprel("NMOD");
					}
				}
			}
		}
	}
	
	private int exceptionProcessing_lastWordIsStatus(Sentence s, Set<String> statusList) {
		Word status = s.get(s.size()-1);
		int index = -1;
		if(statusList.contains(status.getForm().toLowerCase())) {
			for(int i=0; i<s.size(); i++) {
				if(s.get(i).getDeprel().equals("ROOT")) {
					index = i;
//					status.getChildren().addAll(s.get(i).getChildren());
					s.get(i).getChildren().remove(status);
					s.get(i).setDeprel("NMOD");
					status.setDeprel("ROOT");
					s.get(i).setHead(status);
					status.getHead().getChildren().remove(status);
					status.setHead(s.get(0));
//					Set<Word> children = status.getChildren();
// 					for(int j=0; j<children.size(); j++) {
//						if(s.get(j).getHeadId()==i) {
//							s.get(j).setHead(status);
//						}
//					}
 					return index;
				}
			}
		}
		return index;
	}
	
	private int IsSiblingtoPredicate(Sentence s, int i) {
		Set<Word> words = s.get(i).getHead().getChildren();
		for(Word word : words) {
			if(verbBeforeTo.contains(word.getForm()))
				return word.getIdx();
		}
		return -1;
	}
	
	private int exceptionProcessing_Relating(Sentence s, ArrayList<String> actionLemma, Lemmatizer lemma) {
		List<Predicate>     preList = s.getPredicates();
		int index = -1;
		Word root = s.get(1);
		while(!root.getDeprel().equals("ROOT")) {
			if(root.getHeadId()<0)
				return -1;
			root = s.get(root.getHeadId());
		}
		for(Predicate pre : preList) {
			if(IsActionListContainPredicate(actionLemma, pre.getForm(), lemma)) {
				if(pre.getIdx()<s.size()-1&&s.get(pre.getIdx()+1).getForm().equals("relating")) { //||
//						IsSiblingtoPredicate(s, pre.getIdx())) {
					pre.getHead().getChildren().remove(pre);
					root.setDeprel("NMOD");
					pre.setDeprel("ROOT");
					pre.setHead(s.get(0));
					if(root.getIdx()>pre.getIdx()+1) {
						for(int i=root.getIdx()-1; i>0; i--) {
							if(!Isloop(s, root, s.get(i))&&s.get(i).getHead().getIdx()!=root.getIdx()) {
								root.setHead(s.get(i));
								break;
							}
						}
					}
					else {
						for(int i=root.getIdx()+1; i<s.size(); i++) {
							if(!Isloop(s, root, s.get(i))&&s.get(i).getHead().getIdx()!=root.getIdx()) {
								root.setHead(s.get(i));
								break;
							}
						}
					}
					return root.getIdx();
				}
			}
		}
		return -1;
	}
	
	private void setAttributeAsChildToNewRoot(Sentence s) {
		List<Predicate>     preList = s.getPredicates();
		Word root = s.get(1);
		while(!root.getDeprel().equals("ROOT")) {
			if(root.getHeadId()<0)
				return;
			root = s.get(root.getHeadId());
		}
		for(Predicate pre : preList) {
			if(root.getIdx()==pre.getIdx()) {
				Map<Word, String> attrMap = pre.getArgMap();
				Set<Word> keys = attrMap.keySet();
				for(Word word:keys) {
					if(word.getHeadId()!=root.getIdx()&&word.getIdx()!=root.getIdx()
							&&(pre.getArgumentTag(word).equals("A0")||
								pre.getArgumentTag(word).equals("A1")||
								pre.getArgumentTag(word).equals("A2"))) {
						word.getHead().getChildren().remove(word);
						word.setHead(root);
						root.getChildren().add(word);
					}
				}
			}
		}
	} 
	
	private void adjustParentofWord(Sentence s, Word word, Predicate pre) {
		if(word.getIdx()==pre.getIdx())
			return;
		if((pre.getArgumentTag(word)!=null&&(pre.getArgumentTag(word).contains("A0")||pre.getArgumentTag(word).contains("A1")))
				&&!pre.getChildren().contains(word)&&!word.getPOS().equals("WDT")) {
			Word head = word.getHead();
			if(s.isPredicate(head))
				return;
			head.getChildren().remove(word);
			word.setHead(pre);
			pre.getChildren().add(word);
		}
	}
	
//	private void adjustWordsSurroundObject(Sentence s, Predicate pre) {
//		Map<Word, String> arg = pre.getArgMap();
//		Set<Word>       words = arg.keySet();
//		for(Word word : words) {
//			if(pre.getArgumentTag(word).equals("OBJ")) {
//				int index = word.getIdx();
//				for(int i=index+1;i<s.size();i++) {
//					Word nextWord = s.get(i);
//					if(nextWord.getPOS().contains("NN")) {
//						
//					}
//				}
//			}
//		}
//	}
	
	private boolean IsActionListContainPredicate(ArrayList<String> actionLemma, String action, Lemmatizer lemma) {
		String        le = lemma.tokenLemmatize(action);
		String  le_extra = myLemma(action);
		if(actionLemma.contains(action)||actionLemma.contains(le)||actionLemma.contains(le_extra))
			return true;
		return false;
	}
	
//	private boolean getTopicAfterPreAndTo(Sentence s, Triplet tri, Predicate pre, 
//			ArrayList<String> actionLemma, Lemmatizer lemma) {
//		List<Predicate> preList = s.getPredicates();
//		boolean subflag = false;
//		if(pre.getIdx()<s.size()-2) {
//			if(s.get(pre.getIdx()+1).getForm().equals("to")) {
//				Word    verb = s.get(pre.getIdx()+2);
//				if(IsActionListContainPredicate(actionLemma, verb.getForm(), lemma))
//					return false;
//				Predicate pp = null;
//				for(Predicate p : preList) {
//					boolean flag = false;
//					if(p.getIdx()==verb.getIdx()) {
//						flag = true;
//						pp = p;
//					}
//					if(flag) {
//						subflag = getTripletListofPre(s, pp, tri)||subflag;
//					}
//				}
//				return subflag;
//			}
//		}
//		return subflag;
//	}
	
	private void getPreModification(Sentence s, Triplet tri, Predicate pre) {
//		tri.Action             = appendString(tri.Action, pre.getForm());
//		tri.ActionLemma        = appendString(tri.ActionLemma, pre.getLemma());
		tri.Action             = pre.getForm();
		tri.ActionLemma        = pre.getLemma();
		tri.ActionIndex        = pre.getIdx();
		
		if(pre.getIdx()>2) {
			if(s.get(pre.getIdx()-1).getForm().equals("not")) {
				tri.Action             = "not" + " " + pre.getForm();
				tri.ActionLemma        = "not" + " " + pre.getLemma();
			}
 			
			if(s.get(pre.getIdx()-1).getForm().equals("to")) {
	 			if(preModification.contains(s.get(pre.getIdx()-2).getLemma()))
		 		tri.Action             = s.get(pre.getIdx()-2).getForm() + " " + "to" + pre.getForm();
				tri.ActionLemma        = s.get(pre.getIdx()-2).getLemma() + " " + "to " + pre.getLemma();
			}
		}
	}
	
	private boolean getTripletListofPre(Sentence s, Predicate pre, Triplet tri) {
		Map<Word, String> attr = pre.getArgMap();
		Set<Word> words        = attr.keySet();
		
		getPreModification(s, tri, pre);
		
		boolean subflag = false;
		for(Word word : words) {
			if(pre.getArgumentTag(word).equals("A0")) {
				setPathString(word, s, tri, pre, "Subject");
				subflag = true;
			}

			if(pre.getArgumentTag(word).equals("A1")) {
				setPathString(word, s, tri, pre, "DirectObject");
//				if(i==44)
//					System.out.println(tri.object+"\n"+tri.Topic);
				subflag = true;
			}
			if(pre.getArgumentTag(word).equals("A2")) {
				setPathString(word, s, tri, pre, "InDirectObject");
				subflag = true;
			}
		}
		for(Word word : s) {
			if(pre.getDeprel().equals("ROOT")&&IsObjwithoutA1(s, pre, word)) {
				setPathString(word, s, tri, pre, "DirectObject");
			}
			if(word.getDeprel().equals("COORD")
					&&(pre.getArgumentTag(s.get(word.getIdx()-1))!=null&&pre.getArgumentTag(s.get(word.getIdx()-1)).equals("A1"))
					&&(word.getIdx()<s.size()-1&&!s.get(word.getIdx()+1).getPOS().contains("VB"))
					&&word.getHeadId()!=word.getIdx()-1) {
				setPathString(word, s, tri, pre, "DirectObject");
				subflag = true;
			}
		}
		return subflag;
	}
	
	public void getTripletListFromAction(ArrayList<Sentence> senList, ArrayList<ArrayList<Triplet>> triList, Set<String> actionSet) {
		int               size = senList.size();
		System.out.println("Start to get triplet!");
		Lemmatizer lemma = new Lemmatizer();
		ArrayList<String> actionLemma = lemma.lemmatizeTokens(actionSet);
		for(int i=0; i<size; i++) {
			Sentence                  s = senList.get(i);
			System.out.println(i);
			if(i==1581) 
				println(s);
			exceptionProcessing(s, actionLemma, lemma);
			exceptionProcessing_RootAfterPredicate(s, actionLemma, lemma);
			List<Predicate>     preList = s.getPredicates();
			ArrayList<Triplet> triplets = new ArrayList<Triplet>();
//			String             location = locationTermMatch(s, statesSet);
//			String              taxtype = taxTermMatch(s, taxType);
			for(Predicate pre : preList) {
				if(!IsActionListContainPredicate(actionLemma, pre.getForm(), lemma))
					continue;
				if((pre.getDeprel().equals("ROOT")||pre.getDeprel().equals("APPO"))&&pre.getPOS().contains("VB")) {
					if(i==2162) 
						println(s);
					Triplet tri1 = new Triplet();
					boolean subflag = getTripletListofPre(s, pre, tri1);
					if(subflag) {
						triplets.add(tri1);
					}
//					Triplet tri2 = new Triplet();
//					subflag = getTopicAfterPreAndTo(s, tri2, pre, actionLemma, lemma);
//					if(subflag) {
//						triplets.add(tri2);
//					}
				}
			}
			for(Word w:s) {
				if(w.getForm().equals("to")) {
					if(s.size()>w.getIdx()+1) {
						if(s.get(w.getIdx()+1).getPOS().contains("VB")) {
							for(Predicate pre2 : preList) {
								if(pre2.getIdx()==s.get(w.getIdx()+1).getIdx()) {
									Triplet tri2 = new Triplet();
									boolean subflag = getTripletListofPre(s, pre2, tri2);
									if(subflag) {
										triplets.add(tri2);
									}
								}
							}
						}
					}
				}
			}
			triList.add(triplets);
		}
	}
	
	public void getTripletCoord(Sentence s, ArrayList<Triplet> triplets, Set<Predicate> preSet, Predicate pre) {
		List<Predicate> preList = s.getPredicates();
		for(int iii=pre.getIdx()+2; iii<s.size(); iii++) {
			Word word = s.get(iii);
			if(word.getDeprel().equals("COORD")) {
				for(Predicate pre2 : preList) {	
					if(preSet.contains(pre2))
						continue;
					if(pre2.getHeadId()==word.getIdx()&&word.getHeadId()==pre.getIdx()) {
						Triplet tri3 = new Triplet();
						boolean subflag = getTripletListofPre(s, pre2, tri3);
						if(subflag) {
							triplets.add(tri3);
							preSet.add(pre2);
						}
					}
				}
			}
		}
	}
	
	public void getTripletAfterTo(Sentence s, ArrayList<Triplet> triplets, Set<Predicate> preSet, int sentenceIndex) {
		List<Predicate> preList = s.getPredicates();
		for(Word w:s) {
			if(w.getForm().equals("to")) {
				if(s.size()>w.getIdx()+3) {
					if(s.get(w.getIdx()+1).getPOS().contains("VB")) {
						for(Predicate pre : preList) {
							if(pre.getIdx()==s.get(w.getIdx()+1).getIdx()) {
								if(preSet.contains(pre))
									continue;
								if(s.get(w.getIdx()+2).getForm().equals(",")||s.get(w.getIdx()+2).getForm().equals("and"))
									continue;
								if(NoneBehindTo.contains(pre.getForm()))
									continue;
//								if(ii==30) 
//									println(s);
								Triplet tri2 = new Triplet();
								boolean subflag = false;
//								if(s.get(w.getIdx()+3).getForm().equals(",")||s.get(w.getIdx()+3).getForm().equals("to")) {
//									subflag = false;
//									break;
//								}
								if(s.get(w.getIdx()+3).getForm().equals("to")) {
									subflag = false;
									break;
								}
								subflag = getTripletListofPre(s, pre, tri2);
								if(subflag) {
									getTripletFromRelatingBehindPrediacte(s, tri2, pre.getIdx());
									tri2.sentenceIndex = sentenceIndex;
									triplets.add(tri2);
									preSet.add(pre);
									getTripletCoord(s, triplets, preSet, pre);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void getTripletAfterAuxiliaries(Sentence s, Set<Predicate> preSet, ArrayList<Triplet> triplets, int sentenceIndex) {
		List<Predicate>     preList = s.getPredicates();
		for(int j=0; j<s.size()-1; j++) {
//			if(s.get(j).getForm().equals("could")&&s.get(j+1).getDeprel().equals("ROOT")&&s.get(j+1).getPOS().contains("VB")) {
			if(s.get(j).getPOS().equals("MD")&&s.get(j+1).getDeprel().equals("ROOT")&&s.get(j+1).getPOS().contains("VB")) {
				for(Predicate pre2 : preList) {
					if(pre2.getIdx()==j+1) {
						if(preSet.contains(pre2))
							continue;
						Triplet tri2 = new Triplet();
						boolean subflag = getTripletListofPre(s, pre2, tri2);
						if(subflag) {
							triplets.add(tri2);
							tri2.sentenceIndex = sentenceIndex;
							preSet.add(pre2);
						}
					}
				}
			}
		}
	}
	
	public void getTripletFromstatus(Sentence s, ArrayList<Triplet> triplets, int preRoot, int sentenceIndex) {
		Word                status = s.get(s.size()-1);
		Triplet                tri = new Triplet();
		Stack<Word>          stack = new Stack<Word>();
		ArrayList<Integer> wordNum = new ArrayList<Integer>();
		boolean          firstFlag = false;
//		boolean          firstChild = true;
		for(int i=0; i<s.size(); i++) {
			if(firstFlag)
				break;
			if(s.get(i).getHeadId()==status.getIdx()&&s.get(i).getIdx()<=preRoot) {
				stack.add(s.get(i));
				firstFlag = true;
				while(stack.size()>0) {
					Word    wordInd = stack.pop();
					ArrayList<Word> child = wordInd.getSortedChildrenDescending();
					if(child.size()>0) {
						for(int j=0; j<child.size(); j++) {
							Word w = child.get(j);
							if(w.getForm().equals(",")&&
									(s.get(w.getIdx()+1).getDeprel().equals("APPO")||(w.getIdx()+1)==preRoot)) {
//								flag = true;
								continue;
							}
							if(w.getForm().equals("to")) {
								if(s.size()>w.getIdx()+1) {
									if(s.get(w.getIdx()+1).getPOS().contains("VB")) {
										continue;
									}
								}
							}
//							if(flag) {
								if(w.getDeprel().equals("APPO")&&
										(w.getForm().toLowerCase().equals("and")||s.get(w.getIdx()-1).getForm().equals(","))) {
									Set<Word> nextSet = new HashSet<Word>();
									nextSet.add(w);
									setSubTreeString_NOIN(nextSet, s, status, tri);
									continue;
								}
//							}
							if(w.getDeprel().equals("COORD")) {
								Set<Word> nextSet = w.getChildren();
								if(!IsCoordlink(w)) {
									setSubTreeString_NOIN(nextSet, s, status, tri);
									continue;
								}
							}
							if(wordInd.getLemma().equals("be")&&w.getIdx()>wordInd.getIdx())
								continue;
							stack.push(w);
						}
					}
//					firstChild= false;
					if(wordInd.getLemma().equals("be")){
						continue;
					}
					boolean flag = true;
					for(int j=0; j<wordNum.size(); j++) {
						if(wordInd.getIdx()<wordNum.get(j)) {
							wordNum.add(j, wordInd.getIdx());
							flag = false;
							break;
						}
					}
					if(flag) {
						wordNum.add(wordInd.getIdx());
					}
				}
			}
		}
		String chunker_object = "";
		for(int i:wordNum) {
			if(s.get(i).getForm().equals("MYDOT")) {
				chunker_object +=".";
				continue;
			}
			chunker_object += s.get(i).getForm()+" ";
		}
		tri.Action        = status.getForm().toLowerCase();
		tri.ActionLemma   = status.getForm().toLowerCase();
		tri.ActionIndex   = status.getIdx();
		if(IsEntityListContains(chunker_object)) {
			tri.entity = appendString(tri.entity, chunker_object);
		} else {
			tri.Topic         = appendString(tri.Topic, chunker_object);
		}
		tri.sentenceIndex = sentenceIndex;
		if(firstFlag)
			triplets.add(tri);
	}
	
	public void getTripletFromRelatingBehindPrediacte(Sentence s, Triplet tri, int preInd) {
		ArrayList<ArrayList<Integer>> wordNum = new ArrayList<ArrayList<Integer>>();
//		boolean          firstFlag = false;
//		boolean          firstChild = true;	
		int index = -1;
		if(preInd<s.size()-2) {
			if((verbBeforeTo.contains(s.get(preInd+1).getForm())&&
					s.get(preInd+2).getForm().equals("to")&&
					s.get(preInd+2).getHeadId()==preInd+1)||(index=IsSiblingtoPredicate(s, preInd))>0) {
				int  m = preInd+3;
				int mm = preInd+3;
				if(index>0) {
					m  = index+2;
					mm = index+2;
				}
				while(m<s.size()) {
					ArrayList<Integer> topicNum = new ArrayList<Integer>();
					for(int j=m; j<s.size(); j++) {
						m=j+1;
						if(s.get(j).getForm().equals(",")||s.get(j).getForm().equals("and")) {
							break;
						}
						topicNum.add(j);
					}
					if(topicNum.size()>0)
						wordNum.add(topicNum);
				}
				if(wordNum.size()>1)
					tri.wordNumList.addAll(wordNum);
				if(wordNum.size()==1) {
					setSubTreeString(s.get(mm-1).getChildren(), s, s.get(preInd), tri);
				}
//				for(int j=0; j<wordNum.size(); j++) {
//					ArrayList<Integer> topicNum = wordNum.get(j);
//					String chunker_object = "";
//					for(int ii:topicNum) {
//						if(s.get(ii).getForm().equals("MYDOT")) {
//							chunker_object +=".";
//							continue;
//						}
//						chunker_object += s.get(ii).getForm()+" ";
//					}
//					tri.Topic      = appendString(tri.Topic, chunker_object);
//				}
			}
		}
	}
	
	public void getTripletListFromAction_semiColon(ArrayList<ArrayList<Sentence>> senList, 
			ArrayList<ArrayList<Triplet>> triList, Set<String> actionSet) {
		int               size = senList.size();
		System.out.println("Start to get triplet!");
		Lemmatizer lemma = new Lemmatizer();
		ArrayList<String> actionLemma = lemma.lemmatizeTokens(actionSet);
		
//		ReadData RD = new ReadData();
//		Set<String> statusSet = RD.ReadSet("data/list/statusList");
		for(int ii=0; ii<size; ii++) {
			ArrayList<Sentence> subSenList = senList.get(ii);
			ArrayList<Triplet>    triplets = new ArrayList<Triplet>();
			System.out.println(ii);
			for(int i=0; i<subSenList.size(); i++) {
				Sentence                 s = subSenList.get(i);
				int             statusFlag = -1;
				int           relatingFlag = -1;
				if(ii==77) 
					println(s);
				exceptionProcessing(s, actionLemma, lemma);
				exceptionProcessing_RootAfterPredicate(s, actionLemma, lemma);
				exceptionProcessing_RootAtMD(s);
				exceptionProcessing_AllInCapital(s);
				statusFlag = exceptionProcessing_lastWordIsStatus(s, statusSet);
				
				List<Predicate>     preList = s.getPredicates();
				if(statusFlag>0)
					getTripletFromstatus(s, triplets, statusFlag, i);
				
				relatingFlag = exceptionProcessing_Relating(s, actionLemma, lemma);
//				if(relatingFlag>0) {
//					
//				}
	//			String             location = locationTermMatch(s, statesSet);
	//			String              taxtype = taxTermMatch(s, taxType);
				setAttributeAsChildToNewRoot(s);
				Set<Predicate> preSet = new HashSet<Predicate>();
				for(Predicate pre : preList) {
					if(!IsActionListContainPredicate(actionLemma, pre.getForm(), lemma))
						continue;
					if((pre.getDeprel().equals("ROOT")||pre.getDeprel().equals("APPO"))&&pre.getPOS().contains("VB")) {
						if(ii==77) 
							println(s);
						Triplet tri = new Triplet();
						
						boolean subflag = getTripletListofPre(s, pre, tri);
						
						if(subflag) {
							if(pre.getIdx()<s.size()-1&&verbBeforeTo.contains(s.get(pre.getIdx()+1).getForm())) {
								tri.Topic = "";
							}
							getTripletFromRelatingBehindPrediacte(s, tri, pre.getIdx());
							tri.sentenceIndex = i;
							triplets.add(tri);
							preSet.add(pre);
						}
					}
				}
				
				getTripletAfterTo(s, triplets, preSet, i);
				getTripletAfterAuxiliaries(s, preSet, triplets, i);
			}
			triList.add(triplets);
		}
	}
	
	public void getTripletListFromAction_firstSentence(ArrayList<Sentence> senList, 
			ArrayList<ArrayList<Triplet>> triList, Set<String> actionSet) {
		System.out.println("Start to get triplet!");
		Lemmatizer lemma = new Lemmatizer();
		ArrayList<String> actionLemma = lemma.lemmatizeTokens(actionSet);
		
		ReadData RD = new ReadData();
		Set<String> statusSet = RD.ReadSet("data/list/statusList");
//		for(int ii=0; ii<triList.size(); ii++) {
//			ArrayList<Sentence> subSenList = senList.get(ii);
//			ArrayList<Triplet>    triplets = new ArrayList<Triplet>();
//			System.out.println(ii);
		for(int i=0; i<senList.size(); i++) {
			Sentence                  s = senList.get(i);
			if(i==6)
				System.out.println(i);
			ArrayList<Triplet> triplets = new ArrayList<Triplet>();
			if(s==null) {
				triList.add(triplets);
				continue;
			}
			int              statusFlag = -1;
			int            relatingFlag = -1;

			exceptionProcessing(s, actionLemma, lemma);
			exceptionProcessing_RootAfterPredicate(s, actionLemma, lemma);
			exceptionProcessing_RootAtMD(s);
			exceptionProcessing_AllInCapital(s);
			statusFlag = exceptionProcessing_lastWordIsStatus(s, statusSet);
				
			List<Predicate>     preList = s.getPredicates();
			if(statusFlag>0)
				getTripletFromstatus(s, triplets, statusFlag, -1);
				
			relatingFlag = exceptionProcessing_Relating(s, actionLemma, lemma);
			setAttributeAsChildToNewRoot(s);
			Set<Predicate> preSet = new HashSet<Predicate>();
			for(Predicate pre : preList) {
				if(!IsActionListContainPredicate(actionLemma, pre.getForm(), lemma))
					continue;
				if((pre.getDeprel().equals("ROOT")||pre.getDeprel().equals("APPO"))&&pre.getPOS().contains("VB")) {
					Triplet tri = new Triplet();
						
					boolean subflag = getTripletListofPre(s, pre, tri);
						
					if(subflag) {
						if(pre.getIdx()<s.size()-1&&verbBeforeTo.contains(s.get(pre.getIdx()+1).getForm())) {
							tri.Topic = "";
						}
						getTripletFromRelatingBehindPrediacte(s, tri, pre.getIdx());
						tri.sentenceIndex = i;
						triplets.add(tri);
						preSet.add(pre);
					}
				}
				
				getTripletAfterTo(s, triplets, preSet, -1);
				getTripletAfterAuxiliaries(s, preSet, triplets, -1);
			}
			triList.add(triplets);
		}
	}
	
	public void getTripletListFromAction_singleSentence(Sentence s, 
			ArrayList<Triplet> triplets, Set<String> actionSet, Lemmatizer lemma) {
		ArrayList<String> actionLemma = lemma.lemmatizeTokens(actionSet);
		int             statusFlag = -1;
		int           relatingFlag = -1;
		exceptionProcessing(s, actionLemma, lemma);
		exceptionProcessing_RootAfterPredicate(s, actionLemma, lemma);
		exceptionProcessing_RootAtMD(s);
		exceptionProcessing_AllInCapital(s);
		statusFlag = exceptionProcessing_lastWordIsStatus(s, statusSet);
				
		List<Predicate>     preList = s.getPredicates();
		if(statusFlag>0)
			getTripletFromstatus(s, triplets, statusFlag, -1);
				
		relatingFlag = exceptionProcessing_Relating(s, actionLemma, lemma);
		setAttributeAsChildToNewRoot(s);
		Set<Predicate> preSet = new HashSet<Predicate>();
		for(Predicate pre : preList) {
			if(!IsActionListContainPredicate(actionLemma, pre.getForm(), lemma))
				continue;
			if((pre.getDeprel().equals("ROOT")||pre.getDeprel().equals("APPO"))&&pre.getPOS().contains("VB")) {
				Triplet tri = new Triplet();
				boolean subflag = getTripletListofPre(s, pre, tri);
			
				if(subflag) {
					if(pre.getIdx()<s.size()-1&&verbBeforeTo.contains(s.get(pre.getIdx()+1).getForm())) {
						tri.Topic = "";
					}
					getTripletFromRelatingBehindPrediacte(s, tri, pre.getIdx());
					triplets.add(tri);
					preSet.add(pre);
				}
			}
		}
				
		getTripletAfterTo(s, triplets, preSet, -1);
		getTripletAfterAuxiliaries(s, preSet, triplets, -1);
	}
	
	private boolean IsObjwithoutA1(Sentence s, Predicate pre, Word word) {
		if(word.getHeadId()==pre.getIdx()) {
			if(word.getDeprel().equals("OBJ")&&
					(pre.getArgumentTag(word)==null||(pre.getArgumentTag(word)!=null&&!pre.getArgumentTag(word).equals("A1"))))
				return true;
		}
		return false;
	}
	
	
	private void setPathString(Word word, Sentence s, Triplet tri, Word pre, String element) {
		try {
			if(word.getPOS().equals("IN")||word.getPOS().equals("TO")||word.getDeprel().equals("COORD")) {
//				if(tri.Subject==null) {
				if(Triplet.class.getField(element).get(tri)==null) {
					//tri.Subject = getSubTreeString(word.getChildren(), s, pre);
					String ss = getSubTreeString(word.getChildren(), s, pre);
					if(element.equals("DirectObject")||element.equals("InDirectObject")) {
						setSubTreeString(word.getChildren(), s, pre, tri);
					}
					tri.setValue(element, ss);
				}
				else {
					String ss = getSubTreeString(word.getChildren(), s, pre);
					if(element.equals("DirectObject")||element.equals("InDirectObject")) {
						setSubTreeString(word.getChildren(), s, pre, tri);
					}
					tri.addValue(element, ss);
				}
			} else {
				Set<Word> set = new HashSet<Word>();
				set.add(word);
				if(element.equals("DirectObject")||element.equals("InDirectObject")) {
					setSubTreeString(set, s, pre, tri);
				}
				if(Triplet.class.getField(element).get(tri)==null) {
					String ss = getSubTreeString(set, s, pre);
					tri.setValue(element, ss);
//					if(element.equals("DirectObject")) {
//						setSubTreeString(set, s, pre, tri);
//					}
				}
				else {
					String ss = getSubTreeString(set, s, pre);
					tri.addValue(element, ss);
//					if(element.equals("DirectObject")) {
//						setSubTreeString(set, s, pre, tri);
//					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException
				| NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getSubTreeString(Set<Word> word, Sentence s, Word exp) {
		Stack<Word> stack = new Stack<Word>();
		stack.addAll(word);
		ArrayList<Integer> wordNum = new ArrayList<Integer>();
		while(stack.size()>0) {
			Word wordInd = stack.pop();
			Set<Word> child = wordInd.getChildren();
			if(child.size()>0) {
				for(Word w : child) {
					if(w.equals(exp))
						continue;
					stack.add(w);
				}
			}
			boolean flag = true;
			for(int i=0; i<wordNum.size(); i++) {
				if(wordInd.getIdx()<wordNum.get(i)) {
					wordNum.add(i, wordInd.getIdx());
					flag = false;
					break;
				}
			}
			if(flag)
				wordNum.add(wordInd.getIdx());
		}
		String chunker = "";
		for(int i:wordNum) {
			chunker += s.get(i).getForm()+" ";
		}
		return chunker.trim();
	}
	private String appendString(String str1, String str2) {
		if(str1!=null&&str1.length()>0) {
			str1 = str1+" // "+str2;
			return str1;
		} 
		return str2;
	}
	
	public boolean IsCoordlink(Word word) {
		Word      head = word.getHead();
		int ancestorId = head.getHeadId();
		if(ancestorId>0&&ancestorId>word.getIdx()) {
			return true;
		}
		return false;
	}
	
	public void setSubTreeString(Set<Word> word, Sentence s, Word exp, Triplet tri) {
		Stack<Word>                       stack = new Stack<Word>();
		stack.addAll(word);
//		if(word.size()==1) {
//			for(Word w : word) {
//				if(verbBeforeTo.contains(w.getForm())){
//					if(w.getIdx()<s.size()-2&&s.get(w.getIdx()+1).getForm().equals("to")) {
//						stack.pop();
//						stack.addAll(s.get(w.getIdx()+1).getChildren());
//						break;
//					}
//				}
//			}
//		}
		ArrayList<Integer>       wordNum        = new ArrayList<Integer>();
		ArrayList<Integer>       wordNum_object = new ArrayList<Integer>();
		ArrayList<Integer> wordNum_Modification = new ArrayList<Integer>();
		
//		ArrayList<ArrayList<Integer>>              wordNumList = new ArrayList<ArrayList<Integer>>();
//		ArrayList<ArrayList<Integer>>       wordNum_objectList = new ArrayList<ArrayList<Integer>>();
//		ArrayList<ArrayList<Integer>> wordNum_ModificationList = new ArrayList<ArrayList<Integer>>();
		
		boolean                         flag_IN = false;
//		boolean                       flag_APPO = false;
		while(stack.size()>0) {
			Word    wordInd = stack.pop();
			ArrayList<Word> child = wordInd.getSortedChildrenDescending();
			if(verbBeforeTo.contains(wordInd.getForm()))
				continue;
//			System.out.println("parent is: "+wordInd.getForm());
			if(child.size()>0) {
//				boolean flag = false;
				for(int i=0; i<child.size(); i++) {
					Word w = child.get(i);
					if(w.equals(exp))
						continue;
//					System.out.println("children is: "+w.getForm());
					if(w.getForm().equals(",")&&
							(s.get(w.getIdx()+1).getDeprel().equals("APPO")||s.get(w.getIdx()+1).getDeprel().equals("COORD"))) {
//						flag = true;
						continue;
					}
					if(w.getForm().equals("to")) {
						if(s.size()>w.getIdx()+1) {
							if(s.get(w.getIdx()+1).getPOS().contains("VB")) {
								if(s.size()>(w.getIdx()+2)&&
										(!s.get(w.getIdx()+2).getForm().equals(",")||!s.get(w.getIdx()+2).getForm().equals("and")))
								continue;
							}
						}
					}
//					if(flag) {
						if(w.getDeprel().equals("APPO")&&(w.getForm().toLowerCase().equals("and")||s.get(w.getIdx()-1).getForm().equals(","))) {
							Set<Word> nextSet = new HashSet<Word>();
							nextSet.add(w);
//							tri.wordNumList.add(setSubTreeString_NOIN(nextSet, s, exp, tri));
							setSubTreeString_NOIN(nextSet, s, exp, tri);
							continue;
						}
						
						if(w.getForm().equals("on")||w.getForm().equals("for")) {
							Set<Word> nextSet = w.getChildren();
//							tri.wordNumList.add(setSubTreeString_NOIN(nextSet, s, exp, tri));
							setSubTreeString_NOIN(nextSet, s, exp, tri);
							continue;
						}
//					}
					if(w.getDeprel().equals("COORD")) {
						Set<Word> nextSet = null;
						if(w.getForm().toLowerCase().equals("and")||w.getForm().equals(","))
							nextSet = w.getChildren();
						else {
							nextSet = new HashSet<Word>();
							nextSet.add(w);
						}
						if(!IsCoordlink(w)) {
//							tri.wordNumList.add(setSubTreeString_NOIN(nextSet, s, exp, tri));
							setSubTreeString_NOIN(nextSet, s, exp, tri);
							continue;
						}
					}
					stack.push(w);
				}
			}
			boolean flag = true;
			for(int i=0; i<wordNum.size(); i++) {
				if(wordInd.getIdx()<wordNum.get(i)) {
					wordNum.add(i, wordInd.getIdx());
					flag = false;
					break;
				}
			}
			if(flag) {
				wordNum.add(wordInd.getIdx());
			}
			if(!flag_IN&&!wordInd.getPOS().equals("IN")) {
				flag = true;
				for(int i=0; i<wordNum_object.size(); i++) {
					if(wordInd.getIdx()<wordNum_object.get(i)) {
						wordNum_object.add(i, wordInd.getIdx());
						flag = false;
						break;
					}
				}
				if(flag)
					wordNum_object.add(wordInd.getIdx());
			}
			if(wordInd.getPOS().equals("IN")||flag_IN) {
				if(!flag_IN) {
					flag_IN = true;
					continue;
				}
				flag = true;
				for(int i=0; i<wordNum_Modification.size(); i++) {
					if(wordInd.getIdx()<wordNum_Modification.get(i)) {
						wordNum_Modification.add(i, wordInd.getIdx());
						flag = false;
						break;
					}
				}
				if(flag)
					wordNum_Modification.add(wordInd.getIdx());
			}
			
		}
//		String chunker_object = "";
//		String chunker_topic  = "";
		if(wordNum_object.size()==1&&wordNum_Modification.size()==1) {
//			int index = wordNum_object.get(0)+1;
			if(wordNum_Modification.get(0)-wordNum_object.get(0)==2) {
				wordNum_object.add(wordNum_object.get(0)+1);
				wordNum_object.add(wordNum_object.get(0)+2);
				wordNum_Modification.remove(0);
			}
		}
		if(wordNum_object.size()>0)
			tri.wordNumList.add(wordNum_object);
		if(wordNum_Modification.size()>0)
			tri.wordNumList.add(wordNum_Modification);
		for(ArrayList<Integer> subList : tri.wordNumList) {
			removeLastVerb(s, subList);
		}
//		for(ArrayList<Integer> subList : tri.wordNumList) {
//			String chunker_object = "";
//			
//			for(int ii=0; ii<subList.size(); ii++) {
//				int i = subList.get(ii);
//				if(i==0||i==subList.size()-1) {
//					if(s.get(i).getForm().equals(","))
//						continue;
//				}
//				if(s.get(i).getForm().equals("MYDOT")) {
//					chunker_object +=".";
//					continue;
//				}
//				chunker_object += s.get(i).getForm()+" ";
//			}
//			if(IsEntityListContains(chunker_object))
//				tri.entity = appendString(tri.entity, chunker_object);
//			else
//				tri.Topic = appendString(tri.Topic, chunker_object);
//		}
		
//		for(int i:wordNum_object) {
//			if(s.get(i).getForm().equals("MYDOT")) {
//				chunker_object +=".";
//				continue;
//			}
//			chunker_object += s.get(i).getForm()+" ";
//		}
//		for(int i:wordNum_Modification) {
//			if(s.get(i).getForm().equals("MYDOT")) {
//				chunker_topic +=".";
//				continue;
//			}
//			chunker_topic  += s.get(i).getForm()+" ";
//		}
//		if(flag_IN) {
//			if(wordNum_object.size()==1&&wordNum_Modification.size()==1) {
//				int index = wordNum_object.get(0)+1;
//				if(index<s.size()) {
//					String ss = chunker_object.trim()+" "+s.get(index).getForm()+" "+chunker_topic;
//					tri.object = appendString(tri.object, ss);
//				}
//			}
//			else {	
//				tri.object = appendString(tri.object, chunker_object); 
//				tri.Topic  = appendString(tri.Topic, chunker_topic);
//			}
//		} else {
//			tri.Topic  = appendString(tri.Topic, chunker_object);
//			if(wordNum_object.size()>0&&wordNum_object.get(0)>0&&s.get(wordNum_object.get(0)-1).getPOS().equals("IN")) {
//				Word ww = s.get(wordNum_object.get(0)-1);
//				ArrayList<Integer> used = new ArrayList<Integer>();
//				used.addAll(wordNum_Modification);
//				used.addAll(wordNum_object);
//				used.add(exp.getIdx());
//				if(ww.getHead().getIdx()>0) {
//					tri.object = getSubTreeStringBefore(ww.getHead(), s, used);
//				}
//			}
//		}
//		return chunker.trim();
	}
	
	private void removeLastVerb(Sentence s, ArrayList<Integer> wordNums) {
		if(wordNums.size()>0&&s.size()>(wordNums.get(wordNums.size()-1)+1)) {
			int i = wordNums.get(wordNums.size()-1);
			Word word = s.get(i); 
			if(word.getPOS().equals("VBN")&&s.get(i+1).getPOS().equals("IN")) {
				wordNums.remove(wordNums.size()-1);
			}
		}
	}
	
	private boolean IsEntityListContains(String str) {
		if(str==null&&str.length()==0) {
			return false;
		}
		if(entityList.contains(str))
			return true;
		String[] strs = str.trim().split(" ");
		String s = "";
		for(String ss : strs) {
			if(ss.length()<1)
				continue;
			if(ss.charAt(ss.length()-1)=='s'){
				s += ss.substring(0, ss.length()-1);
				continue;
			}
			s += ss +" ";
		}
		if(entityList.contains(s.trim()))
			return true;
		for(String ss:entityMDList){
			for(String sss: entityList) {
				String ssss = ss+" "+sss;
				if(ssss.equals(s.trim()))
					return true;
			}
		}
		return false;
	}
	
	public String getSubTreeStringBefore(Word word, Sentence s, ArrayList<Integer> usedList) {
		Word root = word;
		while(root.getHead().getIdx()!=0&&!usedList.contains(root.getHead().getIdx())) {
				root = root.getHead();
		}
		Stack<Word> stack = new Stack<Word>();
		stack.push(word);
		ArrayList<Integer> numberList = new ArrayList<Integer>();
		while(stack.size()>0) {
			Word             w = stack.pop();
			Set<Word> children = w.getChildren();
			for(Word child : children) {
				if(child.getIdx()==word.getHeadId()||usedList.contains(child.getIdx()))
					continue;
				if(child.getIdx()>word.getIdx())
					continue;
				stack.add(child);
			}
			addIntegerSorted(numberList, w.getIdx());
		}
		String str = "";
		for(int i:numberList) {
			str += s.get(i).getForm()+" ";
		}
		return str.trim();
	}
	
	private void addIntegerSorted(ArrayList<Integer> list, int value) {
		if(list.size()>0) {
			boolean flag = true;
			for(int i=0; i<list.size(); i++) {
				if(list.get(i)>value) {
					list.add(i, value);
					flag=false;
					break;
				}
			}
			if(flag)
				list.add(value);
		} else 
			list.add(value);
	}
	
	public void setSubTreeString_NOIN(Set<Word> word, Sentence s, Word exp, Triplet tri) {
		Stack<Word>                       stack = new Stack<Word>();
		stack.addAll(word);
		ArrayList<Integer>       wordNum        = new ArrayList<Integer>();
		while(stack.size()>0) {
			Word wordInd = stack.pop();
			ArrayList<Word> child = wordInd.getSortedChildrenDescending();
			if(child.size()>0) {
//				boolean flag = false;
				for(Word w : child) {
					if(w.equals(exp))
						continue;
					if(w.getForm().equals(",")&&
							(s.get(w.getIdx()+1).getDeprel().equals("APPO")||s.get(w.getIdx()+1).getDeprel().equals("COORD"))) {
//						flag = true;
						continue;
					}
//					if(flag) {
						if(w.getDeprel().equals("APPO")) {
							Set<Word> nextSet = new HashSet<Word>();
							nextSet.add(w);
							setSubTreeString_NOIN(nextSet, s, exp, tri);
							continue;
						}
//					}
						if(w.getForm().equals("on")) {
							Set<Word> nextSet = w.getChildren();
//							tri.wordNumList.add(setSubTreeString_NOIN(nextSet, s, exp, tri));
							setSubTreeString_NOIN(nextSet, s, exp, tri);
							continue;
						}
					if(w.getDeprel().equals("COORD")) {
						Set<Word> nextSet = null;
						if(w.getForm().toLowerCase().equals("and")||w.getForm().equals(","))
							nextSet = w.getChildren();
						else {
							nextSet = new HashSet<Word>();
							nextSet.add(w);
						}
						if(!IsCoordlink(w)) {
							setSubTreeString_NOIN(nextSet, s, exp, tri);
							continue;
						}
//						Set<Word> nextSet = w.getChildren();
//						setSubTreeString_NOIN(nextSet, s, exp, tri);
//						continue;
					}
					stack.push(w);
				}
			}
			boolean flag = true;
			for(int i=0; i<wordNum.size(); i++) {
				if(wordInd.getIdx()<wordNum.get(i)) {
					wordNum.add(i, wordInd.getIdx());
					flag = false;
					break;
				}
			}
			if(flag) {
				wordNum.add(wordInd.getIdx());
			}
			
		}
		if(wordNum.size()>0)
			tri.wordNumList.add(wordNum);
//		return wordNum;
//		String chunker = "";
//		for(int i:wordNum) {
//			if(s.get(i).getForm().equals("MYDOT")) {
//				chunker +=".";
//				continue;
//			}
//			chunker += s.get(i).getForm()+" ";
//		}
//		tri.Topic  = appendString(tri.Topic, chunker);
//		return chunker.trim();
	}
	
	public void modifiedTopicbyTemporalExpression(Triplet tri, ArrayList<int[]> temporalIndices) {
		for(int i=0; i<tri.wordNumList.size(); i++) {
			ArrayList<Integer> list = tri.wordNumList.get(i);
			for(int value : list) {
				boolean flag = false;
				for(int[] temporalIndex : temporalIndices) {
					if(value==temporalIndex[0]) {
						tri.wordNumList.remove(i);
						i--;
						flag = true;
						break;
					}
				}
				if(flag)
					break;
			}
		}
	}

	public void modifiedTopicbyTemporalExpression(ArrayList<ArrayList<Triplet>> triList) {
		for(ArrayList<Triplet> subList:triList) {
			ArrayList<int[]> temporalIndices = new ArrayList<int[]>();
			for(Triplet tri : subList) {
				if(tri.temporalIndex[0]>-1) {
					temporalIndices.add(tri.temporalIndex);
				}
			}
			for(Triplet tri : subList) {
				modifiedTopicbyTemporalExpression(tri, temporalIndices);
			}
		}
	}
	
	private boolean IsInvalid(int index, Sentence s) {
		if(s.get(index).getForm().equals(",")) {
			return true;
		}
		if(s.get(index).getForm().equals("and")) {
			return true;
		}
		if(s.get(index).getPOS().equals("IN")) {
			return true;
		}
		return false;
	}
	
	private boolean splitWord(Triplet tri, ArrayList<Sentence> sentence) {
		boolean flag = false;
		for(int j=0; j<tri.wordNumList.size(); j++) {
			ArrayList<Integer> numList = tri.wordNumList.get(j);
			Sentence s = sentence.get(tri.sentenceIndex);
			for(int i=1; i<numList.size(); i++) {
				if(numList.get(i)-numList.get(i-1)!=1) {
					if(IsInvalid(numList.get(i), s)&&i==numList.size()-1) {
						break;
					}
					if(IsInvalid(numList.get(0), s)&&i==1) {
						break;
					}
					ArrayList<Integer> newList = new ArrayList<Integer>();
					for(int ii=i; i!=numList.size(); ii++) {
						newList.add(numList.get(i));
						numList.remove(i);
					}
					tri.wordNumList.add(newList);
					flag = true;
				}
			}
		}
		return flag;
	}
	
	private boolean removeInvalidCharacter(Triplet tri, ArrayList<Sentence> sentence) {
		boolean flag = false;
		for(int j=0; j<tri.wordNumList.size(); j++) {
			ArrayList<Integer> numList = tri.wordNumList.get(j);
			Sentence s = sentence.get(tri.sentenceIndex);
			while(IsInvalid(numList.get(0), s)) {
				numList.remove(0);
				flag = true;
			}
			while(IsInvalid(numList.get(numList.size()-1), s)) {
				numList.remove(numList.size()-1);
				flag = true;
			}
		}
		return flag;
	}
	private void LinkTopic(Triplet tri) {
		for(int j=0; j<tri.wordNumList.size(); j++) {
			boolean flag = false;
			ArrayList<Integer> numList1 = tri.wordNumList.get(j);
			for(int k=j+1; k<tri.wordNumList.size(); k++) {
				ArrayList<Integer> numList2 = tri.wordNumList.get(k);
				if((numList1.get(numList1.size()-1)+1)-numList2.get(0)==0) {
					numList1.addAll(numList2);
					tri.wordNumList.remove(k);
					k--;
					continue;
				}
				if((numList2.get(numList2.size()-1)+1)-numList1.get(0)==0) {
					numList2.addAll(numList1);
					tri.wordNumList.remove(j);
					k--;
					flag = true;
					break;
				}
				if(flag) {
					j--;
				}
			}
		}
	}
	
	private ArrayList<String> CodeSecSplit(String str) {
		ArrayList<String> newTopic = new ArrayList<String>();
		if(Pattern.matches(".+ and codesec \\..+", str)) {
			int begin = str.indexOf("and codesec .");
			newTopic.add(str.substring(0, begin));
			newTopic.add(str.substring(begin+4, str.length()));
			return newTopic;
		}
		if(Pattern.matches(".+ codesec \\..+ and \\.+", str)) {
			int begin = str.indexOf("codesec .");
			begin     = str.indexOf("and", begin);
			newTopic.add(str.substring(0, begin));
			newTopic.add(str.substring(begin-4, str.length()));
			return newTopic;
		}
		return null;
	}
	
	private void removeOverlap(Triplet tri) {
		for(int i=0; i<tri.wordNumList.size(); i++) {
			boolean flag = false;
			ArrayList<Integer> numList1 = tri.wordNumList.get(i);
			for(int j=i+1; j<tri.wordNumList.size(); j++) {
				ArrayList<Integer> numList2 = tri.wordNumList.get(j);
				if(numList2.containsAll(numList1)&&numList1.size()>1) {
					tri.wordNumList.remove(j);
					j--;
					continue;
				}
				if(numList1.containsAll(numList2)&&numList2.size()>1) {
					tri.wordNumList.remove(i);
					flag = true;
					break;
				}
				if(flag) {
					i--;
				}
			}
		}
	}
	
	public void getTopic(ArrayList<ArrayList<Sentence>> senList, ArrayList<ArrayList<Triplet>> triList) {
		int size = senList.size();
		for(int i=0; i<size; i++){
			ArrayList<Sentence>  subSenList = senList.get(i);
			ArrayList<Triplet>   subTriList = triList.get(i);
			if(i==77)
				System.out.println();
			for(Triplet tri : subTriList) {
				boolean flag1 = true; boolean flag2 = true;
				while(flag1||flag2) {
					flag1 = splitWord(tri, subSenList);
					flag2 = removeInvalidCharacter(tri, subSenList);
				}
				LinkTopic(tri);
				
				removeOverlap(tri);
				for(ArrayList<Integer> subList : tri.wordNumList) {
					String chunker_object = "";
					Sentence s = subSenList.get(tri.sentenceIndex);
					for(int ii=0; ii<subList.size(); ii++) {
						int value = subList.get(ii);
						if(value==0||value==subList.size()-1) {
							if(s.get(value).getForm().equals(","))
								continue;
							if(s.get(value).getForm().equals("("))
								continue;
							if(chunker_object.indexOf("(")==-1&&s.get(value).getForm().equals(")"))
								continue;
						}
						if(s.get(value).getForm().equals("MYDOT")) {
							chunker_object +=".";
							continue;
						}
						chunker_object += s.get(value).getForm()+" ";
					}
					chunker_object = chunker_object.trim();
					if(IsEntityListContains(chunker_object))
						tri.entity = appendString(tri.entity, chunker_object);
					else {
						ArrayList<String> newTopics = CodeSecSplit(chunker_object);
						if(newTopics==null)
							tri.Topic = appendString(tri.Topic, chunker_object);
						else {
							for(String newTopic : newTopics) {
								tri.Topic = appendString(tri.Topic, newTopic);
							}
						}
					}
				}
			}
		}
	}
	
	public void getTopicSingle(ArrayList<Sentence> senList, ArrayList<ArrayList<Triplet>> triList) {
		int size = senList.size();
		for(int i=0; i<size; i++){
			Sentence s = senList.get(i);
			ArrayList<Triplet>  subTriList = triList.get(i);
			for(Triplet tri : subTriList) {
				for(ArrayList<Integer> subList : tri.wordNumList) {
					String chunker_object = "";
//					Sentence s = subSenList.get(tri.sentenceIndex);
					for(int ii=0; ii<subList.size(); ii++) {
						int value = subList.get(ii);
						if(value==0||value==subList.size()-1) {
							if(s.get(value).getForm().equals(","))
								continue;
							if(s.get(value).getForm().equals("("))
								continue;
							if(chunker_object.indexOf("(")==-1&&s.get(value).getForm().equals(")"))
								continue;
						}
						if(s.get(value).getForm().equals("MYDOT")) {
							chunker_object +=".";
							continue;
						}
						chunker_object += s.get(value).getForm()+" ";
					}
					chunker_object = chunker_object.trim();
					if(IsEntityListContains(chunker_object))
						tri.entity = appendString(tri.entity, chunker_object);
					else {
						ArrayList<String> newTopics = CodeSecSplit(chunker_object);
						if(newTopics==null)
							tri.Topic = appendString(tri.Topic, chunker_object);
						else {
							for(String newTopic : newTopics) {
								tri.Topic = appendString(tri.Topic, newTopic);
							}
						}
					}
				}
			}
		}
	}
}

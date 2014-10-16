package Antology;

import java.util.ArrayList;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

import se.lth.cs.srl.corpus.Sentence;

public class OntologyBuilding {
	public ArrayList<ArrayList<Triplet>> getTripletList(ArrayList<Documents> data,
			Set<String> actionSet, ArrayList<ArrayList<Sentence>> senList, GetTriplet GT) {
//		String path  = "data/raw/ftd01_WK-US_DOCUMENTS.xml";
		ReadData  RD = new ReadData();
//		ArrayList<Documents>          data = RD.read(path);
//		ArrayList<String[]> titleTokenList = RD.checkLetterCases(data);
		ArrayList<ArrayList<String[]>> titleTokenList = RD.checkLetterCases_semiColon(data);
//		GetTriplet                          GT = new GetTriplet();
//		ArrayList<Sentence>            senList = GT.getTriplet(titleTokenList, data);
		GT.getTriplet_semicolon(titleTokenList, data, senList);
		ArrayList<ArrayList<Triplet>>  triList = new ArrayList<ArrayList<Triplet>>();
//		Set<String>                 actionSet = RD.ReadSet("data/list/actionList");
//		GT.getTripletListFromAction(senList, triList, actionSet);
		GT.getTripletListFromAction_semiColon(senList, triList, actionSet);
		
		
//		WriteData WD = new WriteData();
//		WD.writeTriple(data, triList, "results/triList");
		
		return triList;
	}
	
	public ArrayList<ArrayList<Triplet>> getTripletList_FirstSentence(ArrayList<Documents> data, ArrayList<String> strList,
			Set<String> actionSet, ArrayList<Sentence> senList, GetTriplet GT) {
		
		ArrayList<String[]> TokenList = new ArrayList<String[]>();
		for(int i=0;i<strList.size();i++) {
			String str = strList.get(i);
			System.out.println(i);
			TokenList.add(str.split(" "));
		}
		GT.getTriplet_firstSentence(TokenList, senList);
		ArrayList<ArrayList<Triplet>>  triList = new ArrayList<ArrayList<Triplet>>();
		GT.getTripletListFromAction_firstSentence(senList, triList, actionSet);
		
		
//		WriteData WD = new WriteData();
//		WD.writeTriple(data, triList, "results/triList");
//		
		return triList;
	}
	
	public ArrayList<String> getTitleSentence(ArrayList<Documents> data) {
		ArrayList<String> sentences = new ArrayList<String>();
		for(Documents doc: data) {
			sentences.add(doc.Title);
		}
		return sentences;
	}
	
	public ArrayList<String> getFirstSentence(ArrayList<Documents> data, Lemmatizer lemma) {
		MySentenceDetector      MSD = new MySentenceDetector();
		ArrayList<String> sentences = new ArrayList<String>();
		for(Documents doc: data) {
//			System.out.println(doc.Title+"\n*******\n"+doc.Context.trim().split("\n")[0]+"\n----------------");
			String contexts = "";
			if(doc.Context.trim().split("\n")[0].length()>0)
				contexts = MSD.sdetector.sentDetect(doc.Context.trim().split("\n")[0])[0];
//			Lemmatizer lemma = new Lemmatizer();
//			if(contexts.length()<2) {
//				System.out.println();
//			}
	    	String     cont = lemma.myTokenizeToString(contexts).trim();
	    	if(cont!=null&&cont.length()>1&&cont.charAt(cont.length()-1)=='.') {
	    		cont = cont.substring(0, cont.length()-1).trim();
	    	}
	    	if(cont!=null&&cont.length()>1&&cont.charAt(cont.length()-1)==')') {
	    		int index = cont.lastIndexOf("(");
	    		if(index>0)
	    			cont = cont.substring(0, index);
	    	}
			sentences.add(cont);
		}
		return sentences;
	}
	
	public void buildOntology_govRelevant(ArrayList<Documents> data, ArrayList<ArrayList<Triplet>> triList, 
			Model mymodel_gov, Model model_gov, String mynamespace, Property property) {
//		Model mymodel_gov = ModelFactory.createDefaultModel();
		int           size = data.size();
		ReadOntology    RO = new ReadOntology();
		String   namespace = "http://www.w3.org/2004/02/skos/core#";
		String       field = "prefLabel";
		for(int i=0; i<size; i++) {
			ArrayList<Triplet> triplets = triList.get(i);
			for(Triplet tri : triplets) {
				String Gov_URI = RO.getSourceFromRDF_USGov(namespace, field, model_gov, tri.Subject);
				if(Gov_URI!=null) {
					mymodel_gov.createProperty(mynamespace, tri.ActionLemma);
				}
			}
		}
	}
	
//	public void buildOntology_Triples(ArrayList<Documents> data, ArrayList<ArrayList<Triplet>> triList, 
//			Model mymodel_Triples, String mynamespace, Property property) {
////		Model mymodel_gov = ModelFactory.createDefaultModel();
//		int           size = data.size();
//		ReadOntology    RO = new ReadOntology();
//		String   namespace = "http://www.w3.org/2004/02/skos/core#";
//		String       field = "prefLabel";
//		for(int i=0; i<size; i++) {
//			ArrayList<Triplet> triplets = triList.get(i);
//			for(Triplet tri : triplets) {
//				String Gov_URI = RO.getSourceFromRDF_USGov(namespace, field, model_gov, tri.Subject);
//				if(Gov_URI!=null) {
//					mymodel_gov.createProperty(mynamespace, tri.ActionLemma);
//				}
//			}
//		}
//	}
}

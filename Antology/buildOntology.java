package Antology;

import java.util.ArrayList;
import java.util.Set;

import se.lth.cs.srl.corpus.Sentence;

//import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.rdf.model.ModelFactory;

public class buildOntology {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		ReadOntology RO = new ReadOntology();
//		Model model_gov = ModelFactory.createDefaultModel();
//		RO.ReadRDFFile("data/RDF/government/NA-IssuingBodies.skos.rdf", model_gov);
////		String namespace_sko = "http://www.w3.org/2004/02/skos/core#";
////		String namespace_cc  = "http://wolterskluwer.com/ceres/concept-v1.0/";
////		RO.ListSourceContentFromRDF_US(namespace_sko, "prefLabel", model_gov);
////		RO.MapSourceContentFromRDF_US(namespace_sko, "prefLabel", "altLabel.abbreviation", model_gov);
//		String namespace_ltr = "http://wolterskluwer.com/ceres/ltr-v1.0/";
//		String      field_US = "geographicArea";
//		RO.ListSourceFromRDF_US(namespace_ltr, field_US, model_gov);
		
		OntologyBuilding ontoBuil = new OntologyBuilding();
		ReadData               RD = new ReadData();
		ArrayList<Documents> docs = RD.readDoc();
		Set<String>     actionSet = RD.ReadSet("data/list/actionList");
		
		Lemmatizer lemma = new Lemmatizer();
//		ArrayList<String> strList = ontoBuil.getFirstSentence(docs, lemma);
		
		GetTriplet             GT = new GetTriplet(); 
//		ArrayList<String>  titles = ontoBuil.getTitleSentence(docs);
		ArrayList<ArrayList<Sentence>> titleSentences = new ArrayList<ArrayList<Sentence>>();
		ArrayList<ArrayList<Triplet>>         triList = ontoBuil.getTripletList(docs, actionSet, titleSentences, GT);
//		Lemmatizer lemma = new Lemmatizer();
//		ArrayList<String> strList = ontoBuil.getFirstSentence(docs, lemma);
		ArrayList<Sentence>  FirstSentences = new ArrayList<Sentence>();
//		System.out.println("-123");
//		ArrayList<ArrayList<Triplet>> triList_firstSen = ontoBuil.getTripletList_FirstSentence(docs, strList, actionSet, FirstSentences, GT);
		
		
		WriteData wd = new WriteData();
//		wd.writeTriple(docs, triList, triList_firstSen, strList, "tri_list_title_sen");
		TemporalExpressionProcessing tep = new TemporalExpressionProcessing();
//		System.out.println("-456");
		tep.getEventTemporalResults(docs, titleSentences, triList);
//		System.out.println("-789");
//		tep.getEventTemporalResults_firstSentence(docs, FirstSentences, triList_firstSen);
		
		GT.modifiedTopicbyTemporalExpression(triList);
//		System.out.println("-987");
//		GT.modifiedTopicbyTemporalExpression(triList_firstSen);
//		System.out.println("-654");
		GT.getTopic(titleSentences , triList);
//		System.out.println("-321");
//		GT.getTopicSingle(FirstSentences, triList_firstSen);
//		System.out.println("-1111");
		MyJSON myjson = new MyJSON();
//		myjson.putTripletsToJSON(docs, triList, strList, triList_firstSen, "results/tri_list_title_sen.JSON");
//		wd.writeTriple(docs, triList, triList_firstSen, strList, "results/tri_list_title_sen");
		//		ontoBuil.buildOntology();
		wd.writeTitleTripleToXML(docs, triList, "results/tri_list_title_sen.xml");
//		wd.writeFisrtSentenceTripleToXML(docs, triList_firstSen, strList, "results/tri_list_firstSentence_sen.xml");
//		wd.writeTitleAndFisrtSentenceTripleToXML(docs, triList, triList_firstSen, strList, "results/tri_list_title_firstSentence_sen.xml");
	}
}

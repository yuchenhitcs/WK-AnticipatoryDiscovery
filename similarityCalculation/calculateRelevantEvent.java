package similarityCalculation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Antology.Lemmatizer;
import timeLine.DocumentTripleElement;
import timeLine.MyNeo4j;
import timeLine.ReadJSON;

public class calculateRelevantEvent {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		ReadJSON readjson = new ReadJSON();
		ArrayList<DocumentTripleElement> docTriList = new ArrayList<DocumentTripleElement>();
//		readjson.getTriplesFromJSON("results/tri_list_title_sen.JSON", docTriList); 
		ReadXML readxml = new ReadXML();
		readxml.getTriplesFromXML("results/tri_list_title_sen.xml", docTriList);
		
		SimilarityCalculation_Cosine scc = new SimilarityCalculation_Cosine();
		Lemmatizer lemma = new Lemmatizer();
//		scc.getSimilarity(docTriList, lemma);
		
		Map<String, TopicElement> TopicsMap = new HashMap<String, TopicElement>();
		scc.getSimilarity(docTriList, lemma, TopicsMap);
//		MyNeo4j neo4j = new MyNeo4j();
//		neo4j.drawGraph_Title(docTriList);
		WriteData wd = new WriteData();
//		wd.writeEventSimilarity(docTriList,  "results/relevantEvent_temp", "results/relevantEvent_xml_temp");
//		wd.writeEventSimilarityTitleXML(docTriList,  "results/relevantEvent_xml_20");
		wd.writeTopicSimilarityTitleXML(TopicsMap, "results/relevantTopic.xml");
	}

}

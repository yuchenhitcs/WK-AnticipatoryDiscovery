package timeLine;

import java.util.ArrayList;

import Antology.Triplet;

public class buildTimeLineInDoc {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ReadJSON readjson = new ReadJSON();
		ArrayList<DocumentTripleElement> docTriList = new ArrayList<DocumentTripleElement>();
		readjson.getTriplesFromJSON("results/tri_list_title_sen.JSON", docTriList);
		
		TimeLineInDoc timeDoc = new TimeLineInDoc();
		timeDoc.findTimeLineInOneDoc(docTriList);
		String path = "TimeLine/SingleDoc";
		timeLineOutput to = new timeLineOutput();
		to.writeTimeLineToXML(path, docTriList);
//		MyNeo4j neo4j = new MyNeo4j();
//		neo4j.drawGraph(docTriList);
	}

}

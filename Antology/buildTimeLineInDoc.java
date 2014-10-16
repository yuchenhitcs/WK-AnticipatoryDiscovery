package Antology;

import java.util.ArrayList;

public class buildTimeLineInDoc {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ReadJSON readjson = new ReadJSON();
		ArrayList<DocumentTripleElement> docTriList = new ArrayList<DocumentTripleElement>();
		readjson.getTriplesFromJSON("results/tri_list_title_sen.JSON", docTriList);
	}

}

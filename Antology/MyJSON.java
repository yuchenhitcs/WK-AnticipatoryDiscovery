package Antology;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.stanford.nlp.time.SUTime.Range;


public class MyJSON {
	private void putTripletToObject(JSONObject obj, Triplet tri) {
		if(tri.Subject!=null) {
			obj.put("Subject", tri.Subject);
		}
		if(tri.Action!=null) {
			obj.put("Action", tri.Action);
		}
		if(tri.entity!=null) {
			obj.put("entity", tri.entity);
		}
		if(tri.DirectObject!=null) {
			obj.put("DirectObject", tri.DirectObject);
		}
		if(tri.InDirectObject!=null) {
			obj.put("InDirectObject", tri.InDirectObject);
		}
		if(tri.Topic!=null) {
			String[] topics = tri.Topic.split("//");
			JSONArray jasonarray = new JSONArray();
			for(String str : topics) {
				jasonarray.add(str);
			}
			obj.put("Topic", jasonarray);
		}
		if(tri.realTemporal!=null) {
			Range range = tri.realTemporal.getRange();
			obj.put("realTemporal", tri.realTemporal.toString());
			obj.put("BeginTime", range.beginTime().toString());
			obj.put("EndTime", range.endTime().toString());
		}
	}
	
	public void putTripletsToJSON(ArrayList<Documents> docs, 
			ArrayList<ArrayList<Triplet>> titleTris, ArrayList<String> strList, 
			ArrayList<ArrayList<Triplet>> firstSentenceTris, String filename) {
		int size = docs.size();
		JSONArray AllTris = new JSONArray();
		for(int i=0; i<size; i++) {
			Documents doc = docs.get(i);
			ArrayList<Triplet>         titleTri = titleTris.get(i);
			ArrayList<Triplet> firstSentenceTri = firstSentenceTris.get(i);
			String                firstSentence = strList.get(i);
			JSONObject obj = new JSONObject();
			obj.put("ID", doc.ID);
			obj.put("Title", doc.Title);
			obj.put("first sentence", firstSentence);
			obj.put("issue time", doc.calendarString);
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
//			String             time = format.format(doc.calendar);
			JSONArray titleTriArr = new JSONArray();
			for(Triplet tri : titleTri) {
				JSONObject objTemp = new JSONObject();
				putTripletToObject(objTemp, tri);
				titleTriArr.add(objTemp);
			}
			if(titleTriArr.size()>0) {
				obj.put("Triples in Title", titleTriArr);
			}
			
			JSONArray sentenceTriArr = new JSONArray();
			for(Triplet tri : firstSentenceTri) {
				JSONObject objTemp = new JSONObject();
				putTripletToObject(objTemp, tri);
				sentenceTriArr.add(objTemp);
			}
			if(sentenceTriArr.size()>0) {
				obj.put("Triples in First Sentence", sentenceTriArr);
			}
			AllTris.add(obj);
		}
		FileWriter file;
		try {
			file = new FileWriter(filename);
			file.write(AllTris.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

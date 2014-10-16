package Antology;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DocumentTripleElement {
	public String ID;
	public String Title;
	public String firstsentence;
	public String issuetime;
	public ArrayList<Triplet> titleTriples;
	public ArrayList<Triplet> sentenceTriples;
	
	DocumentTripleElement(JSONObject jsonObject) {
		this.ID = ((String) jsonObject.get("ID")).trim();
		this.Title = ((String) jsonObject.get("Title")).trim();
		this.firstsentence = ((String) jsonObject.get("first sentence")).trim();
		this.issuetime = ((String) jsonObject.get("issue time")).trim();
		
		JSONArray titleArray = (JSONArray) jsonObject.get("Triples in Title");
		if(titleArray.size()>0) {
			this.titleTriples = new ArrayList<Triplet>();
			getTriple(titleTriples, titleArray);
		}
		
		JSONArray sentenceArray = (JSONArray) jsonObject.get("Triples in Title");
		if(sentenceArray.size()>0) {
			this.sentenceTriples = new ArrayList<Triplet>();
			getTriple(sentenceTriples, sentenceArray);
		}
	}
	
	private void getTriple(ArrayList<Triplet> triList, JSONArray triArray) {
		for(int i=0; i<triArray.size(); i++) {
			JSONObject triObject = (JSONObject) triArray.get(i);
			Triplet tri = new Triplet();
			tri.Action  = ((String) triObject.get("Action")).trim();
			if(triObject.containsKey("Subject"))
				tri.Subject  = ((String) triObject.get("Subject")).trim();
			if(triObject.containsKey("Topic")) {
				JSONArray topicArray = (JSONArray) triObject.get("Topic");
				for(int j=0; j<topicArray.size(); j++) 
				tri.Topic = append(tri.Topic, ((String)topicArray.get(j)).trim());
			}
			triList.add(tri);
		}
	}
	
	private String append(String s1, String s2) {
		if(s1==null)
			return s2;
		return s1+"//"+s2;
	}
}

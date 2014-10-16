package timeLine;

import java.util.ArrayList;

import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Partial;
import org.joda.time.Period;
import org.joda.time.chrono.AssembledChronology.Fields;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.stanford.nlp.time.SUTime;
import edu.stanford.nlp.time.SUTime.Duration;
import edu.stanford.nlp.time.SUTime.DurationWithFields;
import edu.stanford.nlp.time.SUTime.Range;
import edu.stanford.nlp.time.SUTime.Temporal;
import edu.stanford.nlp.time.SUTime.Time;
import edu.stanford.nlp.time.SUTime.TimeIndex;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.Interval;
import Antology.Triplet;

public class DocumentTripleElement {
	public String ID;
	public String Title;
	public String firstsentence;
	public Time issuetime;
	public ArrayList<Triplet> titleTriples;
	public ArrayList<Triplet> sentenceTriples;
	public ArrayList<Triplet> timeLines;
	public ArrayList<Triplet> sortedTriples;
	public boolean linkFlag = false;
	public DocumentTripleElement() {
		
	}
	
	public DocumentTripleElement(JSONObject jsonObject) {
		this.ID = ((String) jsonObject.get("ID")).trim();
		this.Title = ((String) jsonObject.get("Title")).trim();
		this.firstsentence = ((String) jsonObject.get("first sentence")).trim();
		this.issuetime = SUTime.parseDateTime(((String) jsonObject.get("issue time")).trim(), true);
//		System.out.println(this.Title);
//		System.out.println(this.firstsentence);
		JSONArray titleArray = (JSONArray) jsonObject.get("Triples in Title");
		if(titleArray!=null) {
			this.titleTriples = new ArrayList<Triplet>();
			getTriple(titleTriples, titleArray);
		}
		
		JSONArray sentenceArray = (JSONArray) jsonObject.get("Triples in First Sentence");
		if(sentenceArray!=null) {
			this.sentenceTriples = new ArrayList<Triplet>();
			getTriple(sentenceTriples, sentenceArray);
		}
	}
	
	private String timeFormatModification(String temporal) {
		String[] array = temporal.split("-");
		if(array.length==1) {
			temporal += "-XX-XX";
		}
		if(array.length==2) {
			temporal += "-XX";
		}
		return temporal;
	}
	
	private void getTriple(ArrayList<Triplet> triList, JSONArray triArray) {
		for(int i=0; i<triArray.size(); i++) {
			JSONObject triObject = (JSONObject) triArray.get(i);
			Triplet tri = new Triplet();
			tri.Action  = ((String) triObject.get("Action")).trim();
			if(triObject.containsKey("Subject"))
				tri.Subject  = ((String) triObject.get("Subject")).trim();
			if(triObject.containsKey("DirectObject")) 
				tri.DirectObject  = ((String) triObject.get("DirectObject")).trim();
			if(triObject.containsKey("InDirectObject")) 
				tri.InDirectObject  = ((String) triObject.get("InDirectObject")).trim();
			if(triObject.containsKey("Topic")) {
				JSONArray topicArray = (JSONArray) triObject.get("Topic");
				for(int j=0; j<topicArray.size(); j++) 
				tri.Topic = append(tri.Topic, ((String)topicArray.get(j)).trim());
			}
			if(triObject.containsKey("realTemporal")) {
				String  temporal = (String) triObject.get("realTemporal");
				System.out.print(temporal);
				temporal = timeFormatModification(temporal);
				System.out.println(" ...... "+temporal);
//				if(temporal.contains("-SP"))
//					System.out.println();
				tri.realTemporal  = SUTime.parseDateTime(temporal, true);
				if(tri.realTemporal!=null) {
					Range       range = tri.realTemporal.getRange();
					tri.beginTemporal = range.beginTime();
					tri.endTemporal       = range.endTime();
					if(temporal.toString().contains("P1W")) {
						Duration duration = new DurationWithFields(Period.weeks(1));
						tri.endTemporal   = tri.realTemporal.add(duration);
					}
				}
//				if(temporal.toString().contains("P1W")) {
//					Duration duration = new DurationWithFields(Period.weeks(1));
//					tri.realTemporal.add(duration);
//					System.out.println(tri.realTemporal.getDuration().toString());
//					Range   range = tri.realTemporal.getRange();
//					Time beginTime = range.beginTime();
//					Time   endTime = range.endTime();
//					System.out.println(tri.realTemporal.toString()+"--"+beginTime.toString()+"--"+endTime.toString());
//				}
//				System.out.println(duration.getDuration().toString());
//				Duration duration = new Duration("P1W");
//				Range range = tri.realTemporal.getRange();
//				Time beginTime = range.beginTime();
//				Time   endTime = range.endTime();
////				System.out.println(tri.realTemporal.toString()+"--"+beginTime.toString()+"--"+endTime.toString());
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

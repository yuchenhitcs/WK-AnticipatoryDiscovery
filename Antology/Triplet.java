package Antology;

import java.util.ArrayList;

import se.lth.cs.srl.corpus.Word;
import similarityCalculation.RelevantValue;
import edu.stanford.nlp.time.SUTime.Time;

public class Triplet {
	public String Subject;
	public String Action;
	public String DirectObject;
	public String InDirectObject;
	public String ActionLemma;
	public String Location;
	public String Organization;
	public String Person;
	public String TitleString;
	public String States;
	public String taxType;
	public String City;
	public int    DirectObjectID;
	public String entity;
	public String Topic;
	public int    ActionIndex;
	public int    sentenceIndex;
	public String calendarString = null;
	public int[]  temporalIndex = new int[2];
	public Time   realTemporal = null;
	public Time   beginTemporal = null;
	public Time   endTemporal = null;
	public ArrayList<ArrayList<Integer>>      wordNumList = new ArrayList<ArrayList<Integer>>();
	public ArrayList<ArrayList<RelevantValue>>     relavantList = new ArrayList<ArrayList<RelevantValue>>();
	public ArrayList<ArrayList<double[]>> relavantvalues = new ArrayList<ArrayList<double[]>>();
	public boolean initiationFlag = false;
	public boolean linkFlag = false;
	public Triplet() {
		temporalIndex[0]=-1;
		temporalIndex[1]=-1;
	}
	
	public void initiateSimilarityList() {
		if(this.Topic!=null&&this.Topic.trim().length()>0) {
			String[] strs = this.Topic.split("//");
			for(int i=0; i<strs.length; i++) {
				ArrayList<RelevantValue> temp = new ArrayList<RelevantValue>();
				ArrayList<double[]> tt = new ArrayList<double[]>();
				relavantList.add(temp);
				relavantvalues.add(tt);
				initiationFlag = true;
			}
		}
	}
	
	public void setValue(String e, String str) {
		switch(e) {
			case "Subject":
				this.Subject        = str;
				break;
			case "Action":
				this.Action         = str;
				break;
			case "DirectObject":
				this.DirectObject   = str;
				break;
			case "InDirectObject":
				this.InDirectObject = str;
				break;
			case "ActionLemma":
				this.ActionLemma    = str;
				break;
		}
	}
	
	public void addValue(String e, String str) {
		switch(e) {
			case "Subject":
				this.Subject        += "//"+str;
				break;
			case "Action":
				this.Action         += "//"+str;
				break;
			case "DirectObject":
				this.DirectObject   += "//"+str;
				break;
			case "InDirectObject":
				this.InDirectObject += "//"+str;
				break;
			case "ActionLemma":
				this.ActionLemma    += "//"+str;
				break;
		}
	}
}

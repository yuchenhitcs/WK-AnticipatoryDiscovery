package Antology;

import java.util.ArrayList;

import se.lth.cs.srl.corpus.Word;
import edu.stanford.nlp.time.SUTime.Time;

public class TemporalExpressionElment {
	public String                predicate = null;
	public String           calendarString = null;
	public ArrayList<Time>           dates = new ArrayList<Time>();
	public ArrayList<int[]> temporalOffset = new ArrayList<int[]>();
	public ArrayList<int[]>  temporalIndex = new ArrayList<int[]>();
	public ArrayList<Word>         preList = new ArrayList<Word>();
	public Time                  beginTime = null;
	public Time                    endTime = null;
}

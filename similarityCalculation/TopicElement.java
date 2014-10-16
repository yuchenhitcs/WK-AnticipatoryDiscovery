package similarityCalculation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import timeLine.DocumentTripleElement;

public class TopicElement {
	ArrayList<DocumentTripleElement> documentList = new ArrayList<DocumentTripleElement>();
	ArrayList<int[]> values = new ArrayList<int[]>();
	Map<String, Double> similarTopic = new HashMap<String, Double>();
}

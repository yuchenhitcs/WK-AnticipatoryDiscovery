package similarityCalculation;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import scala.collection.mutable.HashTable;
import timeLine.DocumentTripleElement;
import Antology.Lemmatizer;
import Antology.ReadData;
import Antology.Triplet;
import no.uib.cipr.matrix.sparse.SparseVector;

public class SimilarityCalculation_Cosine {
	double  threshold = 0.708;
	Set<String> stopList;
	SimilarityCalculation_Cosine() {
		ReadData rd = new ReadData();
		stopList = rd.ReadSet("data/list/stopword");
	}
	
	private void InsertInteger(ArrayList<Integer> feature, int value) {
		if(feature.size()==0) {
			feature.add(value);
			return;
		}
		for(int i=0; i<feature.size(); i++) {
			if(value<feature.get(i)) {
				feature.add(i, value);
				return;
			}
		}
		feature.add(value);
	}
	
	private int getFeatures(ArrayList<String> tokens, String str, ArrayList<Integer> features, 
			Lemmatizer lemma) {
		String[] strArray = str.trim().split(" ");
		for(String s : strArray) {
			String    ss = lemma.tokenLemmatize(s);
			boolean flag = false;
			if(isNumeric(ss)) 
				continue;
			if(IsAlphaNumericCom(ss))
				continue;
			if(stopList.contains(ss.toLowerCase()))
				continue;
			for(int i=0; i<tokens.size(); i++) {
				if(tokens.get(i).equals(ss.toLowerCase())) {
//					features.add(i);
					InsertInteger(features, i);
					flag = true;
					break;
				}
			}
			if(!flag) {
//				features.add(tokens.size());
				InsertInteger(features, tokens.size());
				tokens.add(ss.toLowerCase());
			}
		}
		return tokens.size();
	}
	
	public boolean IslargerThanOne(String str) {
		String[] s = str.split(" ");
		if(s.length==1)
			return false;
		int size = s.length;
		for(int i=0; i<s.length; i++) {
			if(stopList.contains(s[i].toLowerCase())) {
				size--;
			}
			if(isNumeric(s[i])) 
				size--;
			if(IsAlphaNumericCom(s[i]))
				size--;
		}
		if(size>1)
			return true;
		return false;
	}
	
	public void getSimilarity(ArrayList<DocumentTripleElement> DTEList, Lemmatizer lemma) {
		int size = DTEList.size();
		boolean flag = true;
		for(int i=0; i<size; i++) {
			System.out.println(i);
			if(i==20)
				return;
//			if(i==11)
//				System.out.println();
			DocumentTripleElement DTE1 = DTEList.get(i);
			if(flag&&DTE1.titleTriples!=null) {
				for(Triplet tri : DTE1.titleTriples) {
					tri.initiationFlag = true;
					tri.initiateSimilarityList();
				}
			}
			for(int j=i+1; j<size; j++) {
				DocumentTripleElement DTE2 = DTEList.get(j);
				if(flag&&DTE2.titleTriples!=null) {
					for(Triplet tri : DTE2.titleTriples) {
						tri.initiationFlag = true;
						tri.initiateSimilarityList();
					}
				}
				getSimilarity(DTE1, DTE2, lemma, i, j);
			}
			flag = false;
		}
	}
	
	public void getSimilarity(ArrayList<DocumentTripleElement> DTEList, Lemmatizer lemma, Map<String, TopicElement> TopicsMap) {
		int size = DTEList.size();
		boolean flag = true;
		for(int i=0; i<size; i++) {
			System.out.println(i);
//			if(i==20)
//				return;
//			if(i==11)
//				System.out.println();
			DocumentTripleElement DTE = DTEList.get(i);
			
			if(DTE.titleTriples==null) 
				continue;
			getSimilarity(DTE, lemma, TopicsMap);
		}
	}
	
	public String topicLemmatization(String topic, Lemmatizer lemma) {
		ArrayList<String> tokens = lemma.lemmatize(topic);
		StringBuffer sb = new StringBuffer();
		for(String token : tokens) {
			sb.append(token+" ");
		}
		return sb.toString().trim();
	}
	
	public void addDocumentTripletElement(TopicElement te, DocumentTripleElement DTE, int[] value) {
		ArrayList<DocumentTripleElement> DTEList = te.documentList;
		if(DTEList.size()==0) {
			DTEList.add(DTE);
			te.values.add(value);
		} else {
			boolean flag = false;
			for(int i=0; i<DTEList.size(); i++) {
				DocumentTripleElement D = DTEList.get(i);
				if(DTE.issuetime.compareTo(D.issuetime)<0) {
					DTEList.add(i, DTE);
					te.values.add(i, value);
					flag = true;
					break;
				}
			}
			if(!flag) {
				DTEList.add(DTE);
				te.values.add(value);
			}
		}
	}
	
	public void getSimilarity(DocumentTripleElement DTE, Lemmatizer lemma, Map<String, TopicElement> TopicsMap) {
		ArrayList<Triplet> triList = DTE.titleTriples;
		Set<String> topicSet = TopicsMap.keySet();
		for(int i=0; i<triList.size(); i++) {
			Triplet tri = triList.get(i);
			if(tri.Topic!=null) {
				String[] topics = tri.Topic.split("//");
				for(int j=0; j<topics.length; j++) {
					String topic = topics[j].trim();
					if(!IslargerThanOne(topic)&&!IsAllInCapital(topic)) {
						continue;
					}
					if(Pattern.matches(".+ to .+", topic))
						continue;
//					topic = topicLemmatization(topic, lemma);
					int[] value = new int[2];
					value[0] = i;
					value[1] = j;
					if(TopicsMap.containsKey(topic)) {
						TopicElement te = TopicsMap.get(topic);
						addDocumentTripletElement(te, DTE, value);
						continue;
					}
					TopicElement te = new TopicElement();
					Map<String, Double> similarTopics = te.similarTopic;
					te.documentList.add(DTE);
					te.values.add(value);
					for(String t : topicSet) {
						ArrayList<String>     tokens = new ArrayList<String>();
						ArrayList<Integer> features1 = new ArrayList<Integer>();
						getFeatures(tokens, topic, features1, lemma);
						ArrayList<Integer> features2 = new ArrayList<Integer>();
						int size = getFeatures(tokens, t, features2, lemma);
						double sim = cosine(features1, features2, size);
						if(sim>threshold) {
							similarTopics.put(t, sim);
						}
					}
					TopicsMap.put(topic, te);
				}
			}
		}
	}
	
	private boolean isNumeric(String str)
	{
		return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	private boolean IsAlphaNumericCom(String s) {
		char[] ches = s.toLowerCase().toCharArray();
		boolean fAL = false;
		boolean fNU = false;
		for(char ch:ches) {
			if(fAL&&fNU)
				return true;
			if((ch>96&&ch<123)||ch==44||ch==45||ch==46||ch==47) {
				fAL = true;
				continue;
			} else if(ch>47&&ch<58) {
				fNU = true;
				continue;
			}
		}
		if(fAL&&fNU)
			return true;
		return false;
	}
	
	private boolean IsNested(ArrayList<Integer> list1, ArrayList<Integer> list2) {
		if(list1.containsAll(list2))
			return true;
		if(list2.containsAll(list1))
			return true;
		return false;
	}
	
	private boolean IsAllInCapital(String s) {
		char[] ches = s.toCharArray();
		for(char ch : ches) {
			if((ch>='A'&&ch<='Z')||ch==' ')
				continue;
			return false;
		}
		return true;
	}
	
	public void getSimilarity(DocumentTripleElement DTE1, 
			DocumentTripleElement DTE2, Lemmatizer lemma, int index1, int index2) {
		ArrayList<Triplet> titleTriList1 = DTE1.titleTriples;
		ArrayList<Triplet> titleTriList2 = DTE2.titleTriples;
		if(DTE1.titleTriples!=null) {
			for(int i=0; i<titleTriList1.size(); i++) {
				Triplet tri1 = titleTriList1.get(i);
				if(tri1.Topic!=null&&tri1.Topic.trim().length()>0) {
					String[] topics1 = tri1.Topic.trim().split("//");
					for(int ii=0; ii<topics1.length; ii++) {
						String topic1 = topics1[ii];
						if(!IslargerThanOne(topic1)&&!IsAllInCapital(topic1)) {
							continue;
						}
						if(Pattern.matches(".+ to .+", topic1))
							continue;
						if(titleTriList2!=null) {
							for(int j=0; j<titleTriList2.size(); j++) {
								Triplet tri2 = titleTriList2.get(j);
								if(tri2.Topic!=null&&tri2.Topic.trim().length()>0) {
									String[] topics2 = tri2.Topic.trim().split("//");
									for(int jj=0; jj<topics2.length; jj++) {
										String topic2 = topics2[jj];
										if(!IslargerThanOne(topic2)&&!IsAllInCapital(topic2)) {
											continue;
										}
										ArrayList<String>     tokens = new ArrayList<String>();
										ArrayList<Integer> features1 = new ArrayList<Integer>();
										getFeatures(tokens, topic1, features1, lemma);
										ArrayList<Integer> features2 = new ArrayList<Integer>();
										int size = getFeatures(tokens, topic2, features2, lemma);
//										if(features1.size()==features2.size()&&features1.get(0)!=features2.get(0))
//											continue;
										double sim = cosine(features1, features2, size);
										
										if(sim>threshold) {
											addRelevantEvent(DTE1, DTE2, tri1, tri2, i, ii, j, jj, 
													index1, index2, sim, size); 
//											ArrayList<String>     temptokens = new ArrayList<String>();
//											ArrayList<Integer> tempfeatures1 = new ArrayList<Integer>();
//											ArrayList<Integer> tempfeatures2 = new ArrayList<Integer>();
//											getFeatures(temptokens, topic1, tempfeatures1, lemma);
//											getFeatures(temptokens, topic2, tempfeatures2, lemma);
//											double tempsim = cosine(tempfeatures1, tempfeatures2, size+1);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	private void addRelevantEvent(DocumentTripleElement DTE1, 
			DocumentTripleElement DTE2, Triplet tri1, Triplet tri2, 
			int i, int ii, int j, int jj, int index1, int index2, double sim, int size) {
		double[] info1 = new double[5];
		info1[0] =sim; info1[1] = index2; info1[2] = j; info1[3] = jj; info1[4] = (double)size;
		double[] info2 = new double[5];
		info2[0] =sim; info2[1] = index1; info2[2] = i; info2[3] = ii; info2[4] = (double)size;
		RelevantValue rv1 = new RelevantValue();
		rv1.ID = DTE2.ID;
		rv1.date = DTE2.issuetime;
		rv1.index = index2;
		
		RelevantValue rv2 = new RelevantValue();
		rv2.ID = DTE1.ID;
		rv2.date = DTE1.issuetime;
		rv2.index = index1;
		if(tri1.relavantList.get(ii).size()==0) {
			tri1.relavantList.get(ii).add(rv1);
			tri1.relavantvalues.get(ii).add(info1);
		} else {
			boolean flag = false;
			for(int k=0; k<tri1.relavantList.get(ii).size(); k++) {
				if(DTE2.issuetime.compareTo(tri1.relavantList.get(ii).get(k).date)<0) {
					tri1.relavantList.get(ii).add(k, rv1);
					tri1.relavantvalues.get(ii).add(k, info1);
					flag = true;
					break;
				}
			}
			if(!flag) {
				tri1.relavantList.get(ii).add(rv1);
				tri1.relavantvalues.get(ii).add(info1);
			}
		}

		if(tri2.relavantList.get(jj).size()==0) {
			tri2.relavantList.get(jj).add(rv2);
			tri2.relavantvalues.get(jj).add(info2);
		} else {
			boolean flag = false;
			for(int k=0; k<tri2.relavantList.get(jj).size(); k++) {
				if(DTE1.issuetime.compareTo(tri2.relavantList.get(jj).get(k).date)<0) {
					tri2.relavantList.get(jj).add(k, rv2);
					tri2.relavantvalues.get(jj).add(k, info2);
					flag = true;
					break;
				}
			}
			if(!flag) {
				tri2.relavantList.get(jj).add(rv2);
				tri2.relavantvalues.get(jj).add(info2);
			}
		}
		DTE1.linkFlag = true;
		DTE2.linkFlag = true;
		tri1.linkFlag = true;
		tri2.linkFlag = true;
	}
	
	private SparseVector vectorTransfor(int size, ArrayList<Integer> vector) {
		int[] index = new int[vector.size()];
		double[]  data = new double[vector.size()]; 
		for(int i=0;i<vector.size();i++) {
			index[i] = vector.get(i);
			data[i]  = 1;
		}
		SparseVector sv = new SparseVector(size, index, data);
		return sv;
	}
	public double cosine(SparseVector sv1, SparseVector sv2) {
		double value = sv1.dot(sv2)/Math.pow((sv1.dot(sv1)*sv2.dot(sv2)), 0.5);
		return 1-value;
	}
	public double cosine(ArrayList<Integer> vector1, ArrayList<Integer> vector2, int size) {
		SparseVector sv1 = vectorTransfor(size, vector1);
		SparseVector sv2 = vectorTransfor(size, vector2);
		double value = cosine(sv1, sv2);
		return 1-value;
	}
}

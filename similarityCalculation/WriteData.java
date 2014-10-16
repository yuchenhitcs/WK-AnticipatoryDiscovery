package similarityCalculation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Antology.Documents;
import Antology.Triplet;
import timeLine.DocumentTripleElement;

public class WriteData {
	public void TripleToStringBuffer(StringBuffer sb, Triplet tri) {
		if(tri.Subject!=null)
			sb.append("\t\t\t<Subject>"+tri.Subject+"</Subject>\n");
		if(tri.DirectObject!=null)
			sb.append("\t\t\t<DirectObject>"+tri.DirectObject+"</DirectObject>\n");
		if(tri.InDirectObject!=null)
			sb.append("\t\t\t<InDirectObject>"+tri.InDirectObject+"</InDirectObject>\n");
		sb.append("\t\t\t<TopicContent>"+tri.Topic+"</TopicContent>\n");
		sb.append("\t\t\t<EntityType>"+tri.Action+"</EntityType>\n");
	}
	
	public void writeEventSimilarity(ArrayList<DocumentTripleElement> docList,  String file, String file_xml) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
			BufferedWriter bw_xml = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_xml),"UTF-8"));
			bw_xml.write("<Similarity>\n");
			for(int i=0; i<docList.size(); i++) {
				System.out.println(i);
//				if(i==10)
//					System.out.println();
				DocumentTripleElement DTE = docList.get(i);
				ArrayList<Triplet> triList = DTE.titleTriples;
				StringBuffer sb = new StringBuffer();
				sb.append(i+":"+DTE.Title+"\n");
				StringBuffer sb_xml = new StringBuffer();
				sb_xml.append("\t<Doucment ID=\""+DTE.ID+"\">\n");
				sb_xml.append("\t\t<IssuedDate>"+DTE.issuetime+"</IssuedDate>\n");
				sb_xml.append("\t\t<Title>"+DTE.Title+"</Title>\n");
				boolean flag = false;
				if(triList!=null&&triList.size()>0) {
					for(Triplet tri : triList) {
						sb.append("-----------------\n");
						int index = 0;
//						ArrayList<ArrayList<double[]>> infomation = tri.relavantvalues;
						sb.append(tri.Subject+"---"+tri.Action+"---"+
							tri.DirectObject+"---"+tri.InDirectObject+"---"+tri.Topic+"\n");
						if(tri.Topic!=null&&tri.Topic.length()>0) {
							boolean flag_relevant = false;
							String[] topics = tri.Topic.split("//");
							ArrayList<ArrayList<double[]>> information = tri.relavantvalues;
							StringBuffer sb_tri = new StringBuffer();
							for(int j=0; j<topics.length; j++) {
								ArrayList<double[]> info = information.get(j);
								sb.append("Topic:"+topics[j]+"\n~~~~~~~~~~~~~\n");
								if(info.size()>0) {
									flag_relevant = true;
									sb_tri.append("\t\t\t<Topic content=\""+topics[j]+"\">\n");
									sb_tri.append("\t\t\t\t<RelevantDoc>\n");
									for(int k=0; k<info.size(); k++) {
										flag = true;
										double[] values = info.get(k);
										DocumentTripleElement DTE2 = docList.get((int)values[1]);
										Triplet               tri2 = DTE2.titleTriples.get((int)values[2]);
										sb.append((int)values[1]+": "+DTE2.Title+"\n");
										sb.append(tri2.Topic+"\n~~~~~~~~~~~~~~~~~~~\n");
										
										sb_tri.append("\t\t\t\t\t<RelevantDocInfo ID=\""+DTE2.ID+"\">\n");
										sb_tri.append("\t\t\t\t\t\t<Date>"+DTE2.issuetime+"</Date>\n");
										sb_tri.append("\t\t\t\t\t\t"+DTE2.Title+"\n\t\t\t\t\t</RelevantDocInfo>\n");
									}
									sb_tri.append("\t\t\t\t</RelevantDoc>\n");
									sb_tri.append("\t\t\t</Topic>\n");
								}
							}
							if(flag_relevant) {
								sb_xml.append("\t\t<Triple ID=\""+index+"\">\n");
								TripleToStringBuffer(sb_xml, tri);
								sb_xml.append(sb_tri.toString());
								index++;
								sb_xml.append("\t\t</Triple>\n");
							}
						}
					}
					if(flag) {
						bw.write(sb.toString()+"***********************\n\n");
						sb_xml.append("\t</Doucment>\n");
						bw_xml.write(sb_xml.toString());
					}
				}
			}
			bw_xml.write("</Similarity>\n");
			bw.close();
			bw_xml.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 
	}
	
	public void writeEventSimilarityTitleXML(ArrayList<DocumentTripleElement> docList, String file_xml) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("SimilarityInfo");
			doc.appendChild(rootElement);
	 
			int size = docList.size();
			for(int i=0; i<size; i++) {
				DocumentTripleElement DTE = docList.get(i);
				if(!DTE.linkFlag)
					continue;
				Element docEle = doc.createElement("Document");
				rootElement.appendChild(docEle);
				DocumentTripleElement mydoc = docList.get(i);
				
				docEle.setAttribute("id",mydoc.ID);
				docEle.setAttribute("issueDate",mydoc.issuetime.toString());
				Element titleEle = doc.createElement("Title");
				docEle.appendChild(titleEle);
				titleEle.setTextContent(mydoc.Title);
				ArrayList<Triplet> triList = DTE.titleTriples;
				if(triList!=null&&triList.size()>0) {
					for(Triplet tri : triList) {
						ArrayList<ArrayList<double[]>> information = tri.relavantvalues;
						Element triEle = doc.createElement("Triple");
						docEle.appendChild(triEle);
						triEle.setAttribute("sentenceIndex", String.valueOf(tri.sentenceIndex));
						
						Element eventEle = doc.createElement("EventType");
						triEle.appendChild(eventEle);
						eventEle.setTextContent(tri.Action);
						
						if(tri.Subject!=null) {
							Element subjectEle = doc.createElement("Subject");
							triEle.appendChild(subjectEle);
							subjectEle.setTextContent(tri.Subject);
						}
						
						if(tri.DirectObject!=null) {
							Element objectEle = doc.createElement("DirectObject");
							triEle.appendChild(objectEle);
							objectEle.setTextContent(tri.DirectObject);
						}
						
						if(tri.InDirectObject!=null) {
							Element inobjectEle = doc.createElement("InDirectObject");
							triEle.appendChild(inobjectEle);
							inobjectEle.setTextContent(tri.InDirectObject);
						}
						if(tri.entity!=null) {
							Element entityEle = doc.createElement("Entity");
							triEle.appendChild(entityEle);
							entityEle.setTextContent(tri.entity);
						}
						if(tri.realTemporal!=null) {
							Element temporalEle = doc.createElement("TemporalExpression");
							triEle.appendChild(temporalEle);
							temporalEle.setTextContent(tri.realTemporal.toString());
						}
							
						if(tri.beginTemporal!=null) {
							Element startEle = doc.createElement("StartTime");
							triEle.appendChild(startEle);
							startEle.setTextContent(tri.beginTemporal.toString());
						}
						
						if(tri.endTemporal!=null) {
							Element endEle = doc.createElement("EndTime");
							triEle.appendChild(endEle);
							endEle.setTextContent(tri.endTemporal.toString());
						}
						
						if(tri.Topic!=null) {
							Element topicEle = doc.createElement("Topic");
							triEle.appendChild(topicEle);
							topicEle.setTextContent(tri.Topic);
							Element tEle = doc.createElement("RelevantDoc");
							triEle.appendChild(tEle);
							String[] topics = tri.Topic.split("//");
							for(int j=0; j<topics.length; j++) {
								String topic = topics[j];
								ArrayList<double[]> info = information.get(j);
								if(info.size()>0) {
									Element ReleTEle = doc.createElement("RelevantTopic");
									tEle.appendChild(ReleTEle);
									ReleTEle.setAttribute("Topic", topic);
									for(int k=0; k<info.size(); k++) {
										double[] values = info.get(k);
										DocumentTripleElement DTE2 = docList.get((int)values[1]);
										Triplet               tri2 = DTE2.titleTriples.get((int)values[2]);
										Element ReTEle = doc.createElement("RelevantDocInfo");
										ReleTEle.appendChild(ReTEle);
										ReTEle.setAttribute("ID", DTE2.ID);
										
										Element ReDateEle = doc.createElement("RelevantIssueDate");
										ReTEle.appendChild(ReDateEle);
										ReDateEle.setTextContent(DTE2.issuetime.toString());
										
										Element ReTitleEle = doc.createElement("RelevantTitle");
										ReTEle.appendChild(ReTitleEle);
										ReTitleEle.setTextContent(DTE2.Title);
										
										Triplet reletri = DTE2.titleTriples.get((int)values[2]);
										String[] releTopics = reletri.Topic.split("//");
										
										Element ReTopicEle = doc.createElement("RelevantTopic");
										ReTEle.appendChild(ReTopicEle);
										ReTopicEle.setTextContent(releTopics[(int)values[3]]);
										
										Element ReScoreEle = doc.createElement("SimilarityScore");
										ReTEle.appendChild(ReScoreEle);
										ReScoreEle.setTextContent(String.valueOf(values[0]));
										
									}
								}
							}
						}
					}
				}
			}
	 
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(file_xml));
	 
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
	 
			transformer.transform(source, result);
	 
			System.out.println("File saved!");
	 
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
	
	public void writeTopicSimilarityTitleXML(Map<String, TopicElement> topicsMap, String file_xml) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("TopicSimilarityInfo");
			doc.appendChild(rootElement);
	 
			Set<String> topics = topicsMap.keySet();
			int i = 0;
			System.out.println("topic size is: "+topics.size());
			for(String topic : topics) {
				System.out.println(topic);
				Element topicEle = doc.createElement("Topic");
				rootElement.appendChild(topicEle);
				topicEle.setAttribute("Topic", topic);
				TopicElement te = topicsMap.get(topic);
				
				ArrayList<DocumentTripleElement> documentList = te.documentList;
				ArrayList<int[]> values = te.values;
				for(int j=0; j<documentList.size(); j++) {
					Element docEle = doc.createElement("Document");
					topicEle.appendChild(docEle);
					DocumentTripleElement DTE = documentList.get(j);
					int[] value = values.get(j);
					docEle.setAttribute("id", DTE.ID);
					docEle.setAttribute("tripleID", String.valueOf(value[0]));
					docEle.setAttribute("topicID", String.valueOf(value[1]));
					docEle.setAttribute("issueDate", DTE.issuetime.toString());
					
					Element titleEle = doc.createElement("Title");
					docEle.appendChild(titleEle);
					titleEle.setTextContent(DTE.Title);
				}
				
				Map<String, Double> similarTopic = te.similarTopic;
				Set<String> keys = similarTopic.keySet();
				for(String key : keys) {
					Element similarEle = doc.createElement("similarTopic");
					topicEle.appendChild(similarEle);
					similarEle.setAttribute("score", String.valueOf(similarTopic.get(key)));
					similarEle.setTextContent(key);
				}
			}
	 
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(file_xml));
	 
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
	 
			transformer.transform(source, result);
	 
			System.out.println("File saved!");
	 
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
}

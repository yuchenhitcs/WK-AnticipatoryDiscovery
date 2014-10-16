package Antology;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.io.File;

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

import edu.stanford.nlp.time.SUTime.Time;


public class WriteData {
	public void writeTriple(ArrayList<Documents> docList, ArrayList<ArrayList<Triplet>> triList, String FileName) {
		int size = docList.size();
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileName),"UTF-8"));
			for(int i=0; i<size; i++) {
				ArrayList<Triplet> triplets = triList.get(i);
				if(triplets.size()>0) {
					Documents doc = docList.get(i);
					bw.write(i+": "+doc.Title+"\n");
					for(Triplet tri : triplets) {
						bw.write("Subject: "+tri.Subject+"\n"+
							"Action: "+tri.Action+"\n"+
							"Direct-Object: "+tri.DirectObject+"\n"+
							"Indirect-Object: "+tri.InDirectObject+"\n"+
							"Entity: "+tri.entity+"\n"+
							"Topic: "+tri.Topic+"\n");
					}
					bw.write("\n------------------\n");
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 
	}
	
	public void writeTriple(ArrayList<Documents> docList, ArrayList<ArrayList<Triplet>> triList, 
			ArrayList<ArrayList<Triplet>> triList_firstSen, ArrayList<String> strList, String FileName) {
		int size = docList.size();
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileName),"UTF-8"));
			for(int i=0; i<size; i++) {
				ArrayList<Triplet> triplets = triList.get(i);
				ArrayList<Triplet> triplets_firstSen = triList_firstSen.get(i);
				if(triplets.size()>0||triplets_firstSen.size()>0) {
					Documents doc = docList.get(i);
					bw.write(i+": "+doc.Title+"\n");
					for(Triplet tri : triplets) {
						bw.write("Subject: "+tri.Subject+"\n"+
							"Action: "+tri.Action+"\n"+
							"Direct-Object: "+tri.DirectObject+"\n"+
							"Indirect-Object: "+tri.InDirectObject+"\n"+
							"Entity: "+tri.entity+"\n"+
							"Topic: "+tri.Topic+"\n");
						if(tri.realTemporal!=null) {
							bw.write("time: "+tri.realTemporal.toString()+"\n");
						}
						bw.write("******************\n");
					}
					bw.write("$$$$$$$$$$$$$$$$$$$$$$\n");
					bw.write(strList.get(i)+"\n");
					for(Triplet tri : triplets_firstSen) {
						bw.write("Subject: "+tri.Subject+"\n"+
							"Action: "+tri.Action+"\n"+
							"Direct-Object: "+tri.DirectObject+"\n"+
							"Indirect-Object: "+tri.InDirectObject+"\n"+
							"Entity: "+tri.entity+"\n"+
							"Topic: "+tri.Topic+"\n");
						if(tri.realTemporal!=null) {
							bw.write("time: "+tri.realTemporal.toString()+"\n");
						}
						bw.write("******************\n");
					}
					bw.write("\n------------------\n");
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 
	}
	
	public void writeTitleTripleToXML(ArrayList<Documents> docList, 
			ArrayList<ArrayList<Triplet>> triList, String FileName) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("TripleInfo");
			doc.appendChild(rootElement);
	 
			int size = docList.size();
			for(int i=0; i<size; i++) {
				Element docEle = doc.createElement("Document");
				rootElement.appendChild(docEle);
				Documents mydoc = docList.get(i);
				
				docEle.setAttribute("id",mydoc.ID);
				docEle.setAttribute("issueDate",mydoc.calendarString);
				
				Element titleEle = doc.createElement("Title");
				docEle.appendChild(titleEle);
				titleEle.setTextContent(mydoc.Title);
				
				for(Triplet tri : triList.get(i)) {
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
					
					if(tri.Topic!=null) {
						Element topicEle = doc.createElement("TopicList");
						triEle.appendChild(topicEle);
						String[] topics = tri.Topic.split("//");
						for(String topic : topics) {
							Element tEle = doc.createElement("Topic");
							topicEle.appendChild(tEle);
							tEle.setTextContent(topic);
						}
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
					
				}
			}
	 
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(FileName));
	 
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
	
	public void writeFisrtSentenceTripleToXML(ArrayList<Documents> docList, 
			ArrayList<ArrayList<Triplet>> triList_firstSen, ArrayList<String> strList, String FileName) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("TripleInfo");
			doc.appendChild(rootElement);
	 
			int size = docList.size();
			for(int i=0; i<size; i++) {
				Element docEle = doc.createElement("Document");
				rootElement.appendChild(docEle);
				Documents mydoc = docList.get(i);
				
				docEle.setAttribute("id",mydoc.ID);
				docEle.setAttribute("issueDate",mydoc.calendarString);
				
				Element titleEle = doc.createElement("FirstSentence");
				docEle.appendChild(titleEle);
				titleEle.setTextContent(strList.get(i));
				
				for(Triplet tri : triList_firstSen.get(i)) {
					Element triEle = doc.createElement("Triple");
					docEle.appendChild(triEle);
//					triEle.setAttribute("sentenceIndex", String.valueOf(tri.sentenceIndex));
					
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
					
					if(tri.Topic!=null) {
						Element topicEle = doc.createElement("TopicList");
						triEle.appendChild(topicEle);
						String[] topics = tri.Topic.split("//");
						for(String topic : topics) {
							Element tEle = doc.createElement("Topic");
							topicEle.appendChild(tEle);
							tEle.setTextContent(topic);
						}
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
					
				}
			}
	 
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(FileName));
	 
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
	
	public void writeTitleAndFisrtSentenceTripleToXML(ArrayList<Documents> docList, ArrayList<ArrayList<Triplet>> triList,
			ArrayList<ArrayList<Triplet>> triList_firstSen, ArrayList<String> strList, String FileName) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("TripleInfo");
			doc.appendChild(rootElement);
	 
			int size = docList.size();
			for(int i=0; i<size; i++) {
				Element docEle = doc.createElement("Document");
				rootElement.appendChild(docEle);
				Documents mydoc = docList.get(i);
				
				docEle.setAttribute("id",mydoc.ID);
				docEle.setAttribute("issueDate",mydoc.calendarString);
				
				Element sentenceEle = doc.createElement("FirstSentence");
				docEle.appendChild(sentenceEle);
				sentenceEle.setTextContent(strList.get(i));
				
				Element titleEle = doc.createElement("Title");
				docEle.appendChild(titleEle);
				titleEle.setTextContent(mydoc.Title);
				
				Element titleTriEle = doc.createElement("Title");
				docEle.appendChild(titleTriEle);
				
				for(Triplet tri : triList.get(i)) {
					Element triEle = doc.createElement("Triple");
					titleTriEle.appendChild(triEle);
					triEle.setAttribute("sentenceIndex", String.valueOf(tri.sentenceIndex));
					
					Element eventEle = doc.createElement("EventType");
					titleTriEle.appendChild(eventEle);
					eventEle.setTextContent(tri.Action);
					
					if(tri.Subject!=null) {
						Element subjectEle = doc.createElement("Subject");
						titleTriEle.appendChild(subjectEle);
						subjectEle.setTextContent(tri.Subject);
					}
					
					if(tri.DirectObject!=null) {
						Element objectEle = doc.createElement("DirectObject");
						titleTriEle.appendChild(objectEle);
						objectEle.setTextContent(tri.DirectObject);
					}
					
					if(tri.InDirectObject!=null) {
						Element inobjectEle = doc.createElement("InDirectObject");
						titleTriEle.appendChild(inobjectEle);
						inobjectEle.setTextContent(tri.InDirectObject);
					}
					
					if(tri.entity!=null) {
						Element entityEle = doc.createElement("Entity");
						titleTriEle.appendChild(entityEle);
						entityEle.setTextContent(tri.entity);
					}
					
					if(tri.Topic!=null) {
						Element topicEle = doc.createElement("TopicList");
						titleTriEle.appendChild(topicEle);
						String[] topics = tri.Topic.split("//");
						for(String topic : topics) {
							Element tEle = doc.createElement("Topic");
							topicEle.appendChild(tEle);
							tEle.setTextContent(topic);
						}
					}
					if(tri.realTemporal!=null) {
						Element temporalEle = doc.createElement("TemporalExpression");
						titleTriEle.appendChild(temporalEle);
						temporalEle.setTextContent(tri.realTemporal.toString());
					}
						
					if(tri.beginTemporal!=null) {
						Element startEle = doc.createElement("StartTime");
						titleTriEle.appendChild(startEle);
						startEle.setTextContent(tri.beginTemporal.toString());
					}
					
					if(tri.endTemporal!=null) {
						Element endEle = doc.createElement("EndTime");
						titleTriEle.appendChild(endEle);
						endEle.setTextContent(tri.endTemporal.toString());
					}
					
				}
				
				Element sentenceTriEle = doc.createElement("Title");
				docEle.appendChild(sentenceTriEle);
				
				for(Triplet tri : triList_firstSen.get(i)) {
					Element triEle = doc.createElement("Triple");
					sentenceTriEle.appendChild(triEle);
//					triEle.setAttribute("sentenceIndex", String.valueOf(tri.sentenceIndex));
					
					Element eventEle = doc.createElement("EventType");
					sentenceTriEle.appendChild(eventEle);
					eventEle.setTextContent(tri.Action);
					
					if(tri.Subject!=null) {
						Element subjectEle = doc.createElement("Subject");
						sentenceTriEle.appendChild(subjectEle);
						subjectEle.setTextContent(tri.Subject);
					}
					
					if(tri.DirectObject!=null) {
						Element objectEle = doc.createElement("DirectObject");
						sentenceTriEle.appendChild(objectEle);
						objectEle.setTextContent(tri.DirectObject);
					}
					
					if(tri.InDirectObject!=null) {
						Element inobjectEle = doc.createElement("InDirectObject");
						sentenceTriEle.appendChild(inobjectEle);
						inobjectEle.setTextContent(tri.InDirectObject);
					}
					
					if(tri.entity!=null) {
						Element entityEle = doc.createElement("Entity");
						sentenceTriEle.appendChild(entityEle);
						entityEle.setTextContent(tri.entity);
					}
					
					if(tri.Topic!=null) {
						Element topicEle = doc.createElement("TopicList");
						sentenceTriEle.appendChild(topicEle);
						String[] topics = tri.Topic.split("//");
						for(String topic : topics) {
							Element tEle = doc.createElement("Topic");
							topicEle.appendChild(tEle);
							tEle.setTextContent(topic);
						}
					}
					if(tri.realTemporal!=null) {
						Element temporalEle = doc.createElement("TemporalExpression");
						sentenceTriEle.appendChild(temporalEle);
						temporalEle.setTextContent(tri.realTemporal.toString());
					}
						
					if(tri.beginTemporal!=null) {
						Element startEle = doc.createElement("StartTime");
						sentenceTriEle.appendChild(startEle);
						startEle.setTextContent(tri.beginTemporal.toString());
					}
					
					if(tri.endTemporal!=null) {
						Element endEle = doc.createElement("EndTime");
						sentenceTriEle.appendChild(endEle);
						endEle.setTextContent(tri.endTemporal.toString());
					}
					
				}
			}
	 
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(FileName));
	 
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

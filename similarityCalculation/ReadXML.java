package similarityCalculation;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.stanford.nlp.time.SUTime;
import Antology.Documents;
import Antology.Triplet;
import timeLine.DocumentTripleElement;

public class ReadXML {
	private boolean hasNode(Node node, String name) {
		Stack<Node> stack = new Stack<Node>(); 
		stack.add(node);
		while(stack.size()>0) {
			Node e= stack.pop();
			if(e.hasChildNodes()) {
				NodeList nodeList = e.getChildNodes();
				for(int i=0; i<nodeList.getLength(); i++) {
					if(nodeList.item(i).getNodeName().equals(name))
						return true;
					stack.add(nodeList.item(i));
				}
			}
		}
		return false;
	}
	
	private String timeFormatModification(String s) {
//		s = s.replaceAll("-01", "-1");
//		s = s.replaceAll("-02", "-2");
//		s = s.replaceAll("-03", "-3");
//		s = s.replaceAll("-04", "-4");
//		s = s.replaceAll("-05", "-5");
//		s = s.replaceAll("-06", "-6");
//		s = s.replaceAll("-07", "-7");
//		s = s.replaceAll("-08", "-8");
//		s = s.replaceAll("-09", "-9");
		
		String[] array = s.split("-");
		if(array.length==1) {
			s += "-XX-XX";
		}
		if(array.length==2) {
			s += "-XX";
		}
		
		return s;
	}
	
	public void getTriplesFromXML(String path, ArrayList<DocumentTripleElement> docTriList) {
		try {
			DocumentBuilderFactory Factory = DocumentBuilderFactory.newInstance();
			Factory.setValidating(false);
			DocumentBuilder dBuilder        = Factory.newDocumentBuilder();
			Document doc                   = dBuilder.parse(path);
				
			NodeList nList = doc.getElementsByTagName("Document");
			for (int i=0; i<nList.getLength(); i++) {
//				if(i==1084)
//					System.out.println(i);
				DocumentTripleElement article = new DocumentTripleElement();
				Element eElement       = (Element) nList.item(i);
				article.ID             = eElement.getAttribute("id");
				article.issuetime      = SUTime.parseDateTime(eElement.getAttribute("issueDate"));
				if(hasNode(eElement, "Title")) {
					article.Title = ((Element)eElement.getElementsByTagName("Title").item(0)).getTextContent();
				}
				if(hasNode(eElement, "Triple")) {
					NodeList tList   = eElement.getElementsByTagName("Triple");
					article.titleTriples = new ArrayList<Triplet>(); 
					for(int j=0; j<tList.getLength(); j++) {
						Element tElement       = (Element) tList.item(j);
						Triplet tri = new Triplet();
						tri.Action = ((Element)eElement.getElementsByTagName("EventType").item(0)).getTextContent();
						tri.sentenceIndex = Integer.parseInt(tElement.getAttribute("sentenceIndex"));
						if(hasNode(tElement, "Subject")) {
							tri.Subject = ((Element)eElement.getElementsByTagName("Subject").item(0)).getTextContent();
						}
						if(hasNode(tElement, "DirectObject")) {
							tri.DirectObject = ((Element)eElement.getElementsByTagName("DirectObject").item(0)).getTextContent();
						}
						if(hasNode(tElement, "InDirectObject")) {
							tri.InDirectObject = ((Element)eElement.getElementsByTagName("InDirectObject").item(0)).getTextContent();
						}
						if(hasNode(tElement, "Entity")) {
							tri.entity = ((Element)eElement.getElementsByTagName("Entity").item(0)).getTextContent();
						}
						if(hasNode(tElement, "TemporalExpression")) {
							tri.realTemporal = SUTime.parseDateTime(timeFormatModification(
									((Element)eElement.getElementsByTagName("TemporalExpression").item(0)).getTextContent()),true);
						}
						if(hasNode(tElement, "StartTime")) {
							tri.beginTemporal = SUTime.parseDateTime(timeFormatModification(
									((Element)eElement.getElementsByTagName("StartTime").item(0)).getTextContent()),true);
						}
						if(hasNode(tElement, "EndTime")) {
							tri.endTemporal = SUTime.parseDateTime(timeFormatModification(
									((Element)eElement.getElementsByTagName("EndTime").item(0)).getTextContent()),true);
						}
						
						if(hasNode(tElement, "TopicList")) {
							NodeList pList   = tElement.getElementsByTagName("TopicList");
							NodeList cList   = ((Element)pList.item(0)).getElementsByTagName("Topic");
							StringBuffer sb = new StringBuffer();
							for(int k=0; k<cList.getLength(); k++) {
								Element cElement       = (Element) cList.item(k);
								if(k==cList.getLength()-1) {
									sb.append(cElement.getTextContent());
									break;
								}
								sb.append(cElement.getTextContent()+"//");
							}
							tri.Topic = sb.toString();
						}
						article.titleTriples.add(tri);
					}
				}			
				docTriList.add(article);
			}
		} catch(Exception ioe) {
			System.out.println(ioe.getMessage());
		}
	}
}

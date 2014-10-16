package Antology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.EntityResolver;

import se.lth.cs.srl.corpus.Sentence;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ReadData {
	public ArrayList<Documents> readDoc() {
		String              path  = "data/raw/ftd01_WK-US_DOCUMENTS.xml";
		ReadData               RD = new ReadData();
		ArrayList<Documents> data = RD.read(path);
		return data;
	}
	
	public ArrayList<Documents> read(String path) {
		ArrayList<Documents> data = new ArrayList<Documents>();
		try {
			ArrayList<String> pathList = readFileName(path);
			for(String directPath:pathList)
				data.addAll(read_dom(directPath));
		} catch (Exception ie){
			System.out.println(ie.getMessage());
		}
		return data;
	}
	
	public ArrayList<String> readFileName(String directoryPath) {
		Stack<String> directoryList = new Stack<String>();
		directoryList.push(directoryPath);
		ArrayList<String> pathList = new ArrayList<String>();
		while(!directoryList.isEmpty()) {
			String path = directoryList.pop();
			File fileRoot = new File(path);
			File[] tempFile = null;
			if (fileRoot.isDirectory()) {
				tempFile = new File[fileRoot.listFiles().length];
				tempFile = fileRoot.listFiles();
				for (int i = 0; i < tempFile.length; i++) {
					directoryList.push(tempFile[i].toString());
				}
				continue;
			} 
			pathList.add(path);
		}
		return pathList;
	}
	
	public String getContentInNode (Element pElement) {
		String content = "";
		NodeList childList   = pElement.getChildNodes();
		for(int k=0; k<childList.getLength(); k++) {
			Node cNode     = childList.item(k);
			switch(cNode.getNodeName()) {
			case "quote":
				content         += "\""+cNode.getTextContent()+"\""; 
				break;
			case "italic":
				content         += cNode.getTextContent();
				break;
			case "#text":
				content         += cNode.getTextContent();
				break;
			case "attachment":
				break;
			case "wkattachment:metadata":
				break;
			case "description":
				break;
			case "wklink:url":
				content         += "URL";
				break;
			case "wklink:cite-ref":
				break;
			case "bold":
				content         += cNode.getTextContent();
				break;
			case "underline":
				content         += cNode.getTextContent();
				break;
			case "application-link":
				content         += "APPLICATIONLINK";
				break;
			case "wklink:email":
				content         += "EMAILADDRESS";
				break;
			case "fraction":
				break;
			case "break":
				content         += "\n";
				break;
			default:
				System.out.println("Other Tag Name exist: "+cNode.getNodeName()+"\n"
						+"57 line in read_data");
				System.exit(-1);
			}
		}
		return content.replaceAll("\n", " ");
	}
	
	public ArrayList<Documents> read_dom(String path) throws Exception {
		ArrayList<Documents> doc_list = new ArrayList<Documents>();
//		File fXmlFile = new File(path);
//		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//		Document doc = dBuilder.parse(fXmlFile);
		DocumentBuilderFactory Factory = DocumentBuilderFactory.newInstance();
		Factory.setValidating(false);
		DocumentBuilder dBuilder        = Factory.newDocumentBuilder();
		
		dBuilder.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
				if (systemId.contains("document.dtd")) {
					return new InputSource(new StringReader(""));
				} else {
					return null;
				}
			}
		});

		Document doc                   = dBuilder.parse(path);
		
		NodeList nList = doc.getElementsByTagName("wkdoc:document");
		for (int i=0; i<nList.getLength(); i++) {
			Documents article = new Documents();
//			if(i>200)
//				break;
			Element eElement       = (Element) nList.item(i);
			if(hasPublishDateNode(eElement, "publication-date")) {
				article.calendarString = ((Element)eElement.getElementsByTagName("publication-date").item(0)).getAttribute("date");
				article.dateFlag = true;
			} else if(hasPublishDateNode(eElement, "publishing-dates")) {
				Element pdElement      = (Element)eElement.getElementsByTagName("publishing-dates").item(0);
				article.calendarString = ((Element)pdElement.getElementsByTagName("sort-date").item(0)).getAttribute("date");
				article.dateFlag       = true;
			} else {
				findDateInTitle(article, eElement);
			}
			
			article.ID             = eElement.getAttribute("id");
//			if(article.ID.equals("ftd0138d135b87be11000afedd8d385ad169409"))
//				System.out.println(article.ID);
			article.Title          = eElement.getElementsByTagName("heading").item(0).getTextContent().replace("\n", " ");
			article.super_class    = ((Element)eElement.getElementsByTagName("super-class").item(0)).getAttribute("super-class");
			article.sub_class      = ((Element)eElement.getElementsByTagName("sub-class").item(0)).getAttribute("super-class");
			NodeList paraList      = eElement.getElementsByTagName("para");
			for(int j=0; j<paraList.getLength(); j++) {
				Element pElement         = (Element) paraList.item(j);
				if(pElement.hasChildNodes()) {
					String content   = getContentInNode (pElement);
					if(content.length()>0)
						article.Context += content + "\n";
				} 
			}
			article.Context = article.Context.trim();
//			if(article.Context.substring(0,1).equals("."))
//				System.out.println();
//			NodeList titleList     = eElement.getElementsByTagName("title");
//			for(int j=0; j<titleList.getLength(); j++) {
//				Element tElement   = (Element) titleList.item(j);
//				String  title      = tElement.getTextContent();
//				int     index      = title.lastIndexOf("(");
//				if(index<0)
//					continue;
//				if(index+1>=title.length()-1)
//					System.out.println("166");
//				String date  = title.substring(index+1, title.length()-1);
//				setDate(article, date);
//				if(article.dateFlag)
//					break;
//			}
			doc_list.add(article);
//			if(doc_list.size()>20)
//				return doc_list;
		}
		return doc_list;	
	}
	
	private boolean hasPublishDateNode(Node node, String name) {
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
	
	private void findDateInTitle(Documents doc, Element eElement) {
		try{
			NodeList titleList     = eElement.getElementsByTagName("title");
			for(int j=0; j<titleList.getLength(); j++) {
				Element tElement   = (Element) titleList.item(j);
				String  title      = tElement.getTextContent();
				int     index      = title.lastIndexOf("(");
				if(index<0)
					continue;
				if(index+1>=title.length()-1)
					System.out.println("166");
				String date  = title.substring(index+1, title.length()-1);
				setDate(doc, date);
				if(doc.dateFlag)
					break;
			} 
		}catch (Exception ie) {
			System.out.println(ie.getMessage());
		}
	}
	
	public void setDate(Documents doc, String dateStr) throws Exception {
//		if(doc.ID.equals("std01c347ad1a7be31000ab0090b11c2ac4f1010"))
//			System.out.println();
		int    indexMon = dateStr.indexOf(".");
		if(indexMon==-1)
			indexMon = dateStr.indexOf(" ");
		if(indexMon==-1)
			return;
		String monthStr = dateStr.substring(0, indexMon).toLowerCase(); 
		int    monthNum = getMonthNumber(monthStr);
		if(monthNum==-1)
			return;
		int    ind_beg  = dateStr.indexOf(" ", indexMon);
		int    ind_end  = dateStr.indexOf(",", ind_beg+1);
//		if(ind_beg+1>=ind_end)
//			System.out.println("186");
//		if(ind_end+2>=dateStr.length())
//			System.out.println("188");
		int    dayNum   = Integer.parseInt(dateStr.substring(ind_beg+1, ind_end));
		int    yearNum  = Integer.parseInt(dateStr.substring(ind_end+2, dateStr.length()));
   
		doc.calendar.set(yearNum, monthNum-1, dayNum);
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-DD");
	    doc.calendarString = format1.format(doc.calendar.getTime());
		
		doc.dateFlag = true;
	    
		
	}
	
	
	private Integer getMonthNumber(String str) {
		
		switch(str) {
			case "jan":
				return 1;
			case "feb":
				return  2;
			case "mar":
				return  3;
			case "apr":
				return  4;
			case "may":
				return  5;
			case "jun":
				return  06;
			case "jul":
				return  7;
			case "aug":
				return  8;
			case "sep":
				return  9;
			case "sept":
				return  9;
			case "oct":
				return  10;
			case "nov":
				return  11;
			case "dec":
				return  12;
			default:
//				System.out.println("Month format is error "+str+"\n");
//				System.exit(-1);
		}
		return -1;
	}
	
	public ArrayList<String[]> checkLetterCases(ArrayList<Documents> docList) {
//	public void checkLetterCases(ArrayList<Document> docList) {
		Lemmatizer lema = new Lemmatizer();
		ArrayList<String[]> titleTokensList = new ArrayList<String[]>();
		for(Documents doc:docList) {
//			System.out.println(doc.Title);
//			String[] titleTokens       = doc.Title.split(" ");
//			String[] content           = doc.Context.split("\n");
			ArrayList<String> titleTokens = lema.Tokenize(doc.Title.trim());
			ArrayList<String> tTokenLe    = lema.lemmatize(doc.Title.trim().toLowerCase());
			if(titleTokens.size()!=tTokenLe.size()) {
				tTokenLe                  = lema.lemmatize(doc.Title);
				for(int i=0; i<tTokenLe.size(); i++) {
					String str = tTokenLe.get(i).toLowerCase();
					tTokenLe.set(i, str);
				}
			}
			int length = titleTokens.size();
			for(int i=0; i<length; i++) {
				if(titleTokens.get(i).equals("-LRB-")) {
					titleTokens.set(i, "(");
				}
				if(titleTokens.get(i).equals("-RRB-")) {
					titleTokens.set(i, ")");
				}	
				if(titleTokens.get(i).equals(titleTokens.get(i).toUpperCase())) 
					continue;
				if(!titleTokens.get(i).toLowerCase().equals(tTokenLe.get(i))) {
					titleTokens.set(i, titleTokens.get(i).toLowerCase());
				}
			}

			ArrayList<String> cTokenLe = lema.lemmatize(doc.Context);

			for(int i=0; i<length; i++) {
				if(titleTokens.get(i).equals(titleTokens.get(i).toUpperCase())) 
					continue;
				String token = titleTokens.get(i);
				String tokLe = tTokenLe.get(i);
				int flag = 0;
				if(token.length()>1&&(token.charAt(0)-'A'>=0)&&(token.charAt(0)-'Z'<=0)) {
					for(String cToken : cTokenLe) {
						if(tokLe.equals(cToken.toLowerCase())&&token.equals(cToken)&&flag==0) {
							//titleTokens.set(i, titleTokens.get(i).toLowerCase());
							flag = 1;
						}
						if(tokLe.equals(cToken.toLowerCase())&&token.toLowerCase().equals(cToken)&&flag==1)
							flag = 2;
					}
					if(flag!=1)
						titleTokens.set(i, titleTokens.get(i).toLowerCase());
				}
			}
			doc.Title        = "";
			int len          = titleTokens.size();
			String[] tiToken = new String[len];
			for(int i=0; i<len; i++) {
				String str = titleTokens.get(i);
				doc.Title +=str+" ";
				tiToken[i] = str;
			}
			titleTokensList.add(tiToken);
//			System.out.println(doc.Title+"\n-----\n");
		} 
		return titleTokensList;
	}
	
	public ArrayList<ArrayList<String[]>> checkLetterCases_semiColon(ArrayList<Documents> docList) {
//		public void checkLetterCases(ArrayList<Document> docList) {
			Lemmatizer lema = new Lemmatizer();
			ArrayList<ArrayList<String[]>> titleTokensList = new ArrayList<ArrayList<String[]>>();
			for(int ind=0; ind<docList.size(); ind++) {
				Documents doc = docList.get(ind);
//				if(ind==18)
//					System.out.println(ind);
				String Title        = "";
//				System.out.println(doc.Title);
//				String[] titleTokens = doc.Title.split(" ");
//				String[]     content = doc.Context.split("\n");
				String tempStr;
				doc.Title = IsCodeCombination(doc.Title);
				if(doc.Title.charAt(doc.Title.length()-1)==')') {
					int index = doc.Title.lastIndexOf("(");
					tempStr   = doc.Title.substring(0, index);
				}else {
					tempStr   = doc.Title;
				}
				String[]   semiColon = tempStr.split(";");
//				doc.OriginalTitle    = doc.Title;
				ArrayList<String[]>   subList = new ArrayList<String[]>();
				ArrayList<String> cTokenLe = lema.lemmatize(doc.Context);
				for(int ii=0; ii<semiColon.length; ii++) {
					String sentence = semiColon[ii];
					ArrayList<String> titleTokens = lema.Tokenize(sentence.trim());
					ArrayList<String>    tTokenLe = lema.lemmatize(sentence.trim().toLowerCase());
					if(titleTokens.size()!=tTokenLe.size()) {
						tTokenLe                  = lema.lemmatize(sentence);
						for(int i=0; i<tTokenLe.size(); i++) {
							String str = tTokenLe.get(i).toLowerCase();
							tTokenLe.set(i, str);
						}
					}
					int length = titleTokens.size();
					for(int i=0; i<length; i++) {
						if(titleTokens.get(i).equals("-LRB-")) {
							titleTokens.set(i, "(");
						}
						if(titleTokens.get(i).equals("-RRB-")) {
							titleTokens.set(i, ")");
						}	
						if(titleTokens.get(i).equals(titleTokens.get(i).toUpperCase())) 
							continue;
						if(!titleTokens.get(i).toLowerCase().equals(tTokenLe.get(i))) {
							titleTokens.set(i, titleTokens.get(i).toLowerCase());
						}
					}
	
	
					for(int i=0; i<length; i++) {
						if(titleTokens.get(i).equals(titleTokens.get(i).toUpperCase())) 
							continue;
						String token = titleTokens.get(i);
						String tokLe = tTokenLe.get(i);
						int flag = 0;
						if(token.length()>1&&(token.charAt(0)-'A'>=0)&&(token.charAt(0)-'Z'<=0)) {
							for(String cToken : cTokenLe) {
								if(tokLe.equals(cToken.toLowerCase())&&token.equals(cToken)&&flag==0) {
									//titleTokens.set(i, titleTokens.get(i).toLowerCase());
									flag = 1;
								}
								if(tokLe.equals(cToken.toLowerCase())&&token.toLowerCase().equals(cToken)&&flag==1)
									flag = 2;
							}
							if(flag!=1)
								titleTokens.set(i, titleTokens.get(i).toLowerCase());
						}
					}
					int len          = titleTokens.size();
					if(ii>0)
						Title += "; ";
					String[] tiToken = new String[len];
					for(int i=0; i<len; i++) {
						String str = titleTokens.get(i);
						Title +=str+" ";
						tiToken[i] = str;
					}
					subList.add(tiToken);
					
				}
				doc.Title = Title;
				titleTokensList.add(subList);
//				System.out.println(doc.Title+"\n-----\n");
			} 
			return titleTokensList;
		}
	
	public ArrayList<ArrayList<String[]>> checkLetterCases_semiColon_String(ArrayList<String> strList, ArrayList<Documents> docList) {
//		public void checkLetterCases(ArrayList<Document> docList) {
			Lemmatizer lema = new Lemmatizer();
			ArrayList<ArrayList<String[]>> titleTokensList = new ArrayList<ArrayList<String[]>>();
			for(int ind=0; ind<strList.size(); ind++) {
				Documents doc = docList.get(ind);
				String sentenceStr = strList.get(ind);
				String Title        = "";
				String[]            semiColon = sentenceStr.split(";"); 
				ArrayList<String[]>   subList = new ArrayList<String[]>();
				ArrayList<String> cTokenLe = lema.lemmatize(doc.Context);
				for(int ii=0; ii<semiColon.length; ii++) {
					String sentence = semiColon[ii];
					ArrayList<String> titleTokens = lema.Tokenize(sentence.trim());
					ArrayList<String>    tTokenLe = lema.lemmatize(sentence.trim().toLowerCase());
					if(titleTokens.size()!=tTokenLe.size()) {
						tTokenLe                  = lema.lemmatize(sentence);
						for(int i=0; i<tTokenLe.size(); i++) {
							String str = tTokenLe.get(i).toLowerCase();
							tTokenLe.set(i, str);
						}
					}
					int length = titleTokens.size();
					for(int i=0; i<length; i++) {
						if(titleTokens.get(i).equals("-LRB-")) {
							titleTokens.set(i, "(");
						}
						if(titleTokens.get(i).equals("-RRB-")) {
							titleTokens.set(i, ")");
						}	
						if(titleTokens.get(i).equals(titleTokens.get(i).toUpperCase())) 
							continue;
						if(!titleTokens.get(i).toLowerCase().equals(tTokenLe.get(i))) {
							titleTokens.set(i, titleTokens.get(i).toLowerCase());
						}
					}
	
	
					for(int i=0; i<length; i++) {
						if(titleTokens.get(i).equals(titleTokens.get(i).toUpperCase())) 
							continue;
						String token = titleTokens.get(i);
						String tokLe = tTokenLe.get(i);
						int flag = 0;
						if(token.length()>1&&(token.charAt(0)-'A'>=0)&&(token.charAt(0)-'Z'<=0)) {
							for(String cToken : cTokenLe) {
								if(tokLe.equals(cToken.toLowerCase())&&token.equals(cToken)&&flag==0) {
									//titleTokens.set(i, titleTokens.get(i).toLowerCase());
									flag = 1;
								}
								if(tokLe.equals(cToken.toLowerCase())&&token.toLowerCase().equals(cToken)&&flag==1)
									flag = 2;
							}
							if(flag!=1)
								titleTokens.set(i, titleTokens.get(i).toLowerCase());
						}
					}
					int len          = titleTokens.size();
					if(ii>0)
						Title += "; ";
					String[] tiToken = new String[len];
					for(int i=0; i<len; i++) {
						String str = titleTokens.get(i);
						Title +=str+" ";
						tiToken[i] = str;
					}
					subList.add(tiToken);
				}
				Title = IsCodeCombination(Title);
				doc.Title = Title;
				titleTokensList.add(subList);
//				System.out.println(doc.Title+"\n-----\n");
			} 
			return titleTokensList;
		}
	
	public ArrayList<String> ReadList(String File) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(File),"UTF-8"));
			String oneLine;
			while((oneLine=br.readLine())!=null) {
				list.add(oneLine.trim());
			}
			br.close();
		} catch (IOException ie) {
			System.out.println(ie.getMessage());
		}
		return list;
	}
	
	public Set<String> ReadSet(String File) {
		Set<String> set = new HashSet<String>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(File),"UTF-8"));
			String oneLine;
			while((oneLine=br.readLine())!=null) {
				if(Pattern.matches(".+ : .+", oneLine)) {
					int index = oneLine.indexOf(":");
					set.add(oneLine.substring(0, index).trim());
				}
				set.add(oneLine.trim());
			}
			br.close();
		} catch (IOException ie) {
			System.out.println(ie.getMessage());
		}
		return set;
	}
	
	public ArrayList<String> ReadKey(String File) {
		ArrayList<String> keys = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(File),"UTF-8"));
			String oneLine;
			while((oneLine=br.readLine())!=null) {
				if(Pattern.matches(".+ : .+", oneLine)) {
					int index = oneLine.indexOf(":");
					keys.add(oneLine.substring(0, index).trim());
					continue;
				}
				keys.add(oneLine.trim());
			}
			br.close();
		} catch (IOException ie) {
			System.out.println(ie.getMessage());
		}
		return keys;
	}
	
	private String IsCodeCombination(String title) {
//		if(title.toLowerCase().contains("code"))
//				System.out.println();
		while(Pattern.matches(".+code sec\\. .+", title.toLowerCase())) {
			int begin    = title.toLowerCase().indexOf("code sec.");
			String part1 = title.substring(0, begin);
			int end      = title.indexOf(" ",begin+10);
			if(end==-1)
				end = title.length();
			String part2 = title.substring(begin, end).replaceAll(" ", "");
			String part3 = title.substring(end, title.length());
			title = part1.trim()+" "+part2.trim()+" "+part3.trim();
		}
		return title;
	}
}

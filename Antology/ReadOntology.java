package Antology;


import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

public class ReadOntology {
	public void ReadRDFFile(String inputFileName, Model model) {
		InputStream in = FileManager.get().open( inputFileName );
		if (in == null) {
		    throw new IllegalArgumentException(
		                                 "File: " + inputFileName + " not found");
		}
		model.read(in, null);
	}
	public ArrayList<String> ListSourceContentFromRDF(String namespace, String field, Model model){
		ArrayList<String> list = new ArrayList<String>();
		Property      property = model.getProperty(namespace, field);
		ResIterator      ResIt = model.listSubjects();
//		StmtIterator    stmtIt = model.listStatements();
		while(ResIt.hasNext()) {
			Resource  resource = ResIt.next();
			Statement     stat = resource.getProperty(property);
			list.add(stat.getObject().toString());
		}
		for(String str : list)
			System.out.println(str);
		return list;
	}
	
	public ArrayList<String> ListSourceContentFromRDF_US(String namespace, String field, Model model){
		ArrayList<String> list = new ArrayList<String>();
		Property      property = model.getProperty(namespace, field);
		Property   property_US = model.getProperty("http://wolterskluwer.com/ceres/ltr-v1.0/", "geographicArea");
		ResIterator      ResIt = model.listSubjects();
		while(ResIt.hasNext()) {
			Resource  resource = ResIt.next();
			Statement     stat = resource.getProperty(property);
			Statement  stat_US = resource.getProperty(property_US);
			if(stat_US!=null&&stat_US.getObject().toString().equals("http://wk-us.com/meta/regions/#US")) {
				list.add(stat.getObject().toString());
			}
		}
		for(String str : list)
			System.out.println(str);
		return list;
	}
	
	public String getSourceFromRDF_USGov(String namespace, String field, Model model, String str){
		Property      property = model.getProperty(namespace, field);
		Property   property_US = model.getProperty("http://wolterskluwer.com/ceres/ltr-v1.0/", "geographicArea");
		ResIterator      ResIt = model.listSubjects();
		while(ResIt.hasNext()) {
			Resource  resource = ResIt.next();
			Statement     stat = resource.getProperty(property);
			Statement  stat_US = resource.getProperty(property_US);
			if(stat_US!=null&&stat_US.getObject().toString().equals("http://wk-us.com/meta/regions/#US")) {
				if(stat!=null) {
					String obj = stat.getObject().toString();
					if(obj.toUpperCase().equals(str.toUpperCase()))
						return resource.getURI();
					if(obj.toUpperCase().equals(str.toUpperCase()+"@en"))
						return resource.getURI();
				}
			}
		}
		return null;
	}
	
	public HashMap<String, String> MapSourceContentFromRDF_US(String namespaceKey, String fieldKey,
			String namespaceValue, String fieldValue, Model model){
		HashMap<String, String> map = new HashMap<String, String>();
		Property        propertyKey = model.getProperty(namespaceKey, fieldKey);
		Property      propertyValue = model.getProperty(namespaceValue, fieldValue);
		Property        property_US = model.getProperty("http://wolterskluwer.com/ceres/ltr-v1.0/", "geographicArea");
		ResIterator      ResIt = model.listSubjects();
		while(ResIt.hasNext()) {
			Resource   resource = ResIt.next();
			Statement      stat = resource.getProperty(propertyKey);
			Statement statValue = resource.getProperty(propertyValue);
			Statement   stat_US = resource.getProperty(property_US);
			if(stat_US!=null&&stat_US.getObject().toString().equals("http://wk-us.com/meta/regions/#US"))
				map.put(stat.getObject().toString(), statValue.getObject().toString());
		}
//		for(String str : list)
//			System.out.println(str);
		return map;
	}
	
	public ArrayList<Resource> ListSourceFromRDF_US(String namespace, String field, Model model){
		ArrayList<Resource> list = new ArrayList<Resource>();
//		Property        property = model.getProperty(namespace, field);
		Property     property_US = model.getProperty(namespace, field);
		ResIterator        ResIt = model.listSubjects();
		while(ResIt.hasNext()) {
			Resource  resource = ResIt.next();
//			Statement     stat = resource.getProperty(property);
			Statement  stat_US = resource.getProperty(property_US);
			if(stat_US!=null&&stat_US.getObject().toString().equals("http://wk-us.com/meta/regions/#US")) {
				list.add(resource);
			}
		}
		return list;
	}
}

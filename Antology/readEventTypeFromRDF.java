package Antology;

import java.io.InputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

public class readEventTypeFromRDF {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Model          model = ModelFactory.createDefaultModel();
//		String inputFileName = "data/RDF/US-EventType.skos.rdf";
		String inputFileName = "data/RDF/US-AmericanStates.skos.rdf";
		InputStream       in = FileManager.get().open( inputFileName );
		if (in == null) {
		    throw new IllegalArgumentException(
		                                 "File: " + inputFileName + " not found");
		}
		model.read(in, null);
//		StmtIterator iter = model.listStatements(); 
//		while (iter.hasNext()) { 
//			Statement stmt = iter.next(); 
//			Resource subject = stmt.getSubject(); 
//			Property predicate = stmt.getPredicate(); 
//			RDFNode object = stmt.getObject(); 
//			System.out.print("(" + predicate.toString() + ","); 
//			System.out.print(" " + subject.toString() + ","); 
//			if (object instanceof Resource) { 
//				System.out.print(" " + object.toString()); } 
//			else { 
//				System.out.print(" \"" + object.toString() + "\""); } System.out.println(")"); } 
		
//		ResIterator resIte = model.listSubjects();
		Property predicate = model.getProperty("http://www.w3.org/2004/02/skos/core#prefLabel");
		ResIterator resIte = model.listSubjectsWithProperty(predicate);
		while(resIte.hasNext()) {
			Resource   sub = resIte.next();
			NodeIterator t = model.listObjectsOfProperty(sub, predicate);
			while(t.hasNext()) {
				String subject = sub.toString();
				String 	object = t.next().toString();
				System.out.print(object.substring(0, object.length()-3)+
						" "+subject.substring(subject.length()-2, subject.length())+"\n");	
			}
//			System.out.println(" ---------- ");
		}
//		Property   predicate = model.getProperty("http://www.w3.org/2004/02/skos/core#prefLabel");
//		NodeIterator    iter = model.listObjectsOfProperty(predicate);
//		while (iter.hasNext()) { 
//			RDFNode object = iter.next(); 
//			System.out.println(object.toString().substring(0, object.toString().length()-3));
//		}
	}

}

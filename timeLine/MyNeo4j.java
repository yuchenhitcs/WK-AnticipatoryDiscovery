package timeLine;

import java.io.File;
import java.util.ArrayList;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import similarityCalculation.RelevantValue;
import Antology.Triplet;

public class MyNeo4j {
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running application).
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    } );
	}
	
	private static enum RelTypes implements RelationshipType
	{
	    SUBJECT, TOPIC, TIME, BEFORE, EVENT
	}
	private void deleteFileOrDirectory( File file ) {
		if ( file.exists() ) {
			if ( file.isDirectory() ) {
				for ( File child : file.listFiles() ) {
					deleteFileOrDirectory( child );
	            }
	        }
	        file.delete();
	    }
	}
	public void drawGraph(ArrayList<DocumentTripleElement> docTriList) {
//		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("C:/Users/yu.chen/Documents/Neo4j/default.graphdb");
		String DB_PATH = "data/neo4j/default.graphdb";
		deleteFileOrDirectory( new File( DB_PATH ) );
		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		registerShutdownHook( graphDb );
		try ( Transaction tx = graphDb.beginTx() )
		{
			Label    event = DynamicLabel.label( "Event" );
			Label  subject = DynamicLabel.label( "Subject" );
			Label    topic = DynamicLabel.label( "Topic" );
			Label temporal = DynamicLabel.label( "Temporal" );
			for(DocumentTripleElement DTE : docTriList) {
//				if(DTE.sortedTriples.size()>2)
//					continue;
				if(DTE.sortedTriples.size()>2) {
					ArrayList<Node> nodes = new ArrayList<Node>();
					Node previous = null;
					for(int i=0; i<DTE.sortedTriples.size(); i++) {
						Triplet tri = DTE.sortedTriples.get(i);
						Node eventNode = graphDb.createNode(event);
						eventNode.setProperty( "event",  tri.Action);
						if(tri.Subject!=null) {
							Node subjectNode = graphDb.createNode(subject);
							subjectNode.setProperty( "Subject", tri.Subject );
							Relationship relationship = eventNode.createRelationshipTo(subjectNode, RelTypes.SUBJECT );
							relationship.setProperty( "TYPE", "SUBJECT" );
						}
						if(tri.Topic!=null) {
							Node topicNode = graphDb.createNode(topic);
							topicNode.setProperty( "Topic", tri.Topic);
							Relationship relationship = eventNode.createRelationshipTo(topicNode, RelTypes.TOPIC );
							relationship.setProperty( "TYPE", "TOPIC" );
						}
						if(tri.realTemporal!=null) {
							Node temporalNode = graphDb.createNode(temporal);
							temporalNode.setProperty( "Temporal", tri.realTemporal.toString());
							Relationship relationship = eventNode.createRelationshipTo(temporalNode, RelTypes.TIME);
							relationship.setProperty( "TYPE", "TEMPORAL" );
						}
						if(i>0) {
							Relationship relationship = previous.createRelationshipTo(eventNode, RelTypes.BEFORE);
							relationship.setProperty( "TIMELINE", "Before" );
						}
						previous = eventNode;
					}
				}
			}
			    // Database operations go here
			tx.success();
		}

		graphDb.shutdown();
	}
	
	public void drawGraph_Title(ArrayList<DocumentTripleElement> docTriList) {
//		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("C:/Users/yu.chen/Documents/Neo4j/default.graphdb");
		String DB_PATH = "data/neo4j/default.graphdb";
		deleteFileOrDirectory( new File( DB_PATH ) );
		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		registerShutdownHook( graphDb );
		try ( Transaction tx = graphDb.beginTx() )
		{
			Label    event = DynamicLabel.label( "Event" );
			Label titleCenter = DynamicLabel.label( "Center" );
			Label  title = DynamicLabel.label( "Title" );
			Label    topic = DynamicLabel.label( "Topic" );
			Label temporal = DynamicLabel.label( "Temporal" );
			for(int i=0; i<docTriList.size(); i++) {
				DocumentTripleElement DTE = docTriList.get(i);
				System.out.println(i);
				if(!DTE.linkFlag)
					continue;
				if(i>20)
					continue;
				if(i!=13)
					continue;
//				if(i==110)
//					System.out.println();
				boolean flag = false;
				if(flag)
					break;
				ArrayList<Triplet> triList = DTE.titleTriples;
				Node titleNode = graphDb.createNode(titleCenter);
				titleNode.setProperty( "Title", DTE.Title );
				
//				Node lastNode = null;
				Node temporalNode = graphDb.createNode(temporal);
				temporalNode.setProperty( "Temporal", DTE.issuetime.toString());
				Relationship TimeRelationship = titleNode.createRelationshipTo(temporalNode, RelTypes.TIME);
				TimeRelationship.setProperty( "TYPE", "TEMPORAL" );
				
				
				if(triList!=null&&triList.size()>0) {
					for(Triplet tri : triList) {
						if(tri.Topic!=null&&tri.Topic.length()>0) {
							if(!tri.linkFlag)
								continue;
							
//							Node eventNode = graphDb.createNode(event);
//							eventNode.setProperty( "event",  tri.Action);
//							Relationship relationship = titleNode.createRelationshipTo(eventNode, RelTypes.EVENT );
//							relationship.setProperty( "TYPE", "EVENTTYPE" );
							
							Node eventNode = graphDb.createNode(event);
							eventNode.setProperty( "EventType", tri.Action);
							Relationship eventRelationship = titleNode.createRelationshipTo(eventNode, RelTypes.EVENT);
							eventRelationship.setProperty( "TYPE", "EVENT" );
							
							
							String[] topics = tri.Topic.split("//");
							ArrayList<ArrayList<double[]>> information = tri.relavantvalues;
							for(int j=0; j<topics.length; j++) {
								ArrayList<double[]> info = information.get(j);
								if(info.size()>0) {
									
									Node lastNode = null;
									Node topicNode = graphDb.createNode(topic);
									topicNode.setProperty( "Topic", topics[j]);
									Relationship topicRelationship = titleNode.createRelationshipTo(topicNode, RelTypes.TOPIC);
									topicRelationship.setProperty( "TYPE", "TOPIC" );
									
									int index = -1;
									index = getNodeindex(DTE, tri.relavantList.get(j)); 
									int simiInd = 0;
									for(int k=0; k<info.size(); k++) {
										if(index==0) {
											lastNode = titleNode;
										}
										if(k==index&&index!=0) {
											double[] infoArray = info.get(simiInd);
											simiInd++;
											Relationship relationship = lastNode.createRelationshipTo(titleNode, RelTypes.BEFORE );
											relationship.setProperty( "Similarity", String.valueOf(infoArray[0]));
											lastNode = titleNode;
										}
										
										DocumentTripleElement DTE2 = docTriList.get((int)info.get(k)[1]);
										Node nextNode = graphDb.createNode(title);
										nextNode.setProperty("Title",  DTE2.Title);
										
										Triplet nextTri = DTE2.titleTriples.get((int)info.get(k)[2]);
										Node nextEventNode = graphDb.createNode(event);
										nextEventNode.setProperty( "EventType", nextTri.Action);
										Relationship nextEventRelationship = nextNode.createRelationshipTo(nextEventNode, RelTypes.EVENT);
										nextEventRelationship.setProperty( "TYPE", "EVENT" );
										
										Node NextTemporalNode = graphDb.createNode(temporal);
										NextTemporalNode.setProperty( "Temporal", DTE2.issuetime.toString());
										Relationship NextTimeRelationship = nextNode.createRelationshipTo(NextTemporalNode, RelTypes.TIME);
										NextTimeRelationship.setProperty( "TYPE", "TEMPORAL" );
										
										String[] nextTopics = nextTri.Topic.split("//");
										Node nextTopicNode = graphDb.createNode(topic);
										nextTopicNode.setProperty( "Topic", nextTopics[(int)info.get(k)[3]]);
										Relationship nextTopicRelationship = nextNode.createRelationshipTo(nextTopicNode, RelTypes.TOPIC);
										nextTopicRelationship.setProperty( "TYPE", "TOPIC" );
										
										if(lastNode!=null) {
											double[] infoArray = info.get(simiInd);
											simiInd++;
											Relationship relationship = lastNode.createRelationshipTo(nextNode, RelTypes.BEFORE );
											relationship.setProperty( "Similarity", String.valueOf(infoArray[0]));
										}
										lastNode = nextNode;
										flag = true;
									}
									if(info.size()==index) {
										double[] infoArray = info.get(simiInd);
										simiInd++;
										Relationship relationship = lastNode.createRelationshipTo(titleNode, RelTypes.BEFORE );
										relationship.setProperty( "Similarity", String.valueOf(infoArray[0]));
										lastNode = titleNode;
									}
								}
							}
						}
					}
				}
			}
			    // Database operations go here
			tx.success();
		}

		graphDb.shutdown();
	}
	
	private int getNodeindex(DocumentTripleElement DTE, ArrayList<RelevantValue> RVList) {
		for(int i=0; i<RVList.size(); i++) {
			if(DTE.issuetime.compareTo(RVList.get(i).date)<0)
			return i;
		}
		return RVList.size();
	}
}

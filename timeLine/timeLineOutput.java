package timeLine;

import java.util.ArrayList;
import java.util.Map;

import org.joda.time.DateTimeFieldType;
import org.joda.time.Partial;
import org.joda.time.Period;

import edu.stanford.nlp.time.SUTime;
import edu.stanford.nlp.time.SUTime.Duration;
import edu.stanford.nlp.time.SUTime.Range;
import edu.stanford.nlp.time.SUTime.Time;
import edu.stanford.nlp.time.SUTime.DurationWithFields;
import Antology.Triplet;

public class timeLineOutput {
	String[] months = new String[12];
	public timeLineOutput() {
		months[0]  = "Jan";
		months[1]  = "Feb";
		months[2]  = "Mar";
		months[3]  = "Apr";
		months[4]  = "May";
		months[5]  = "Jun";
		months[6]  = "Jul";
		months[7]  = "Aug";
		months[8]  = "Sep";
		months[9]  = "Oct";
		months[10] = "Nov";
		months[11] = "Dec";
		
	}
	public String TimeFormatTrandfer(Time t) {
		Partial partial = t.getJodaTimePartial();
		DateTimeFieldType[] f = partial.getFieldTypes();
		String  year = String.valueOf(partial.get(f[0]));
		String month = months[partial.get(f[1])-1];
		String   day = String.valueOf(partial.get(f[2]));
		
		return  month+" "+day+" "+year+" 00:00:00 GMT";
	}
	
	public String getTitle(Triplet tri) {
		StringBuffer sb = new StringBuffer();
		boolean flag = false;
		if(tri.Subject!=null) {
			sb.append(tri.Subject+" ");
			flag = true;
		}
		if(flag) {
			sb.append(tri.Action+" ");
			if(tri.DirectObject!=null) {
				sb.append(tri.DirectObject+" ");
			} else {
				if(tri.InDirectObject!=null) {
					sb.append(tri.InDirectObject+" ");
				}
			}
		} else {
			if(tri.DirectObject!=null) {
				sb.append(tri.DirectObject+" "+tri.Action);
			} else {
				if(tri.InDirectObject!=null) {
					sb.append(tri.InDirectObject+" "+tri.Action);
				}
			}
		}
			return sb.toString().trim();
	}
	
	public void writeTimeLineToXML(String path, ArrayList<DocumentTripleElement> docTriList) {
		for(DocumentTripleElement DTE : docTriList) {
			ArrayList<Triplet> triples =  DTE.sortedTriples;
			if(triples.size()<2)
				continue;
			StringBuffer sb = new StringBuffer();
			sb.append("<data>\n");
			String exactpath = path+"/"+DTE.ID+".xml";
			for(Triplet tri : triples) {
//				Range    range = tri.realTemporal.getRange();
//				Time beginTime = range.beginTime();
//				Time   endTime = range.endTime();
//				if(tri.realTemporal.toString().contains("P1W")) {
//					Duration duration = new DurationWithFields(Period.weeks(1));
//					endTime = tri.realTemporal.add(duration);
//				}
				
				sb.append("\t<event\n"+
						"\t\tstart=\""+TimeFormatTrandfer(tri.beginTemporal)+"\"\n"+
						"\t\tend=\""+TimeFormatTrandfer(tri.endTemporal)+"\"\n");
				if(tri.beginTemporal.compareTo(tri.endTemporal)!=0) {
					sb.append("\t\tisDuration=\"true\"\n");
				}
				sb.append("\t\ttitle=\""+getTitle(tri)+"\"\n\t\t>\n");
				if(tri.Subject!=null) {
					sb.append("\t\tSubject:"+tri.Subject+"\n");
				}
				if(tri.Action!=null) {
					sb.append("\t\tEventType:"+tri.Action+"\n");
				}
				if(tri.Topic!=null) {
					sb.append("\t\tTopic:"+tri.Topic+"\n");
				}
				sb.append("\t\tTitle:"+DTE.Title+"\n");
				sb.append("\t\tFirst Sentence:"+DTE.firstsentence+"\n");
				sb.append("\t</event>\n");
			}
			sb.append("</data>\n\n");
			WriteData wd = new WriteData();
			wd.writeString(exactpath, sb.toString().trim());
		}
	}
}

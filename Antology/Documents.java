package Antology;

import java.util.ArrayList;
import java.util.Calendar;

public class Documents {
	public String   ID;
	public String   Title;
	public String   OriginalTitle;
	public String   Context  = "";
	public Calendar calendar = Calendar.getInstance();
	public boolean  dateFlag = false;
	public String   labeledContext;
	public String   titleERLabel;
	public String   calendarString;
	public String   super_class;
	public String   sub_class;
	public String   TitleLocation = "null";
	public String   TitleTaxType  = "null";
	public ArrayList<String> temporal      = new ArrayList<String>();
	public ArrayList<String> titleTemporal = new ArrayList<String>();
}

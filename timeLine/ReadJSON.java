package timeLine;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ReadJSON {
	public JSONArray readJSON(String path) {
		JSONArray jsonArray = null;
		try {	
			JSONParser parser = new JSONParser();
			Object        obj = parser.parse(new FileReader(path));
			jsonArray        = (JSONArray) obj;
			
		} catch (Exception e) {
	        e.printStackTrace();
	    }
		return jsonArray;
	}
	
	public void getTriplesFromJSON(String path, ArrayList<DocumentTripleElement> docTriList) {
//		public void getTriplesFromJSON(String path) {
			JSONArray jsonArray = readJSON(path.toString());
			for(int i=0; i<jsonArray.size(); i++) {
				JSONObject     jsonObject = (JSONObject) jsonArray.get(i);
				DocumentTripleElement DTE = new DocumentTripleElement(jsonObject);
				docTriList.add(DTE);
			}
//			JSONArray jArr = (JSONArray)jsonObject.keySet().get();
		}
}

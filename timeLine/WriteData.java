package timeLine;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class WriteData {
	public void writeString(String path, String s) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path),"UTF-8"));
			bw.write(s);
			bw.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 
	}
}

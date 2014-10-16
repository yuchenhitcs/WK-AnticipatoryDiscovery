package Antology;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class MySentenceDetector {
	public SentenceModel model;
	public SentenceDetectorME sdetector;
	public MySentenceDetector() {
		InputStream is;
		try {
			is = new FileInputStream("models/en-sent.bin");
			this.model = new SentenceModel(is);
			this.sdetector = new SentenceDetectorME(model);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}

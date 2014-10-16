package Antology;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Lemmatizer {
	protected StanfordCoreNLP pipeline;

    public Lemmatizer() {
        // Create StanfordCoreNLP object properties, with POS tagging
        // (required for lemmatization), and lemmatization
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");

        // StanfordCoreNLP loads a lot of models, so you probably
        // only want to do this once per execution
        this.pipeline = new StanfordCoreNLP(props);
    }

    public ArrayList<String> lemmatize(String documentText) {
        ArrayList<String> lemmas = new ArrayList<String>();

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);

        // run all Annotators on this text
        this.pipeline.annotate(document);

        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the
                // list of lemmas
            	String s = token.get(LemmaAnnotation.class);
            	if(s.contains("/")) {
            		int ind = s.indexOf("/");
            		if(ind!=0&&ind<s.length()-1) {
            			lemmas.add(s.substring(0, ind));
            			lemmas.add("/");
            			lemmas.add(s.substring(ind+1, s.length()));
            			continue;
            		}
            	}
                lemmas.add(token.get(LemmaAnnotation.class));
            }
        }
        return lemmas;
    }
    
    public String tokenLemmatize(String documentText) {
        ArrayList<String> lemmas = new ArrayList<String>();

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);

        // run all Annotators on this text
        this.pipeline.annotate(document);

        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the
                // list of lemmas
            	String s = token.get(LemmaAnnotation.class);
            	if(s.contains("/")) {
            		int ind = s.indexOf("/");
            		if(ind!=0&&ind<s.length()-1) {
            			lemmas.add(s.substring(0, ind));
            			lemmas.add("/");
            			lemmas.add(s.substring(ind+1, s.length()));
            			continue;
            		}
            	}
                lemmas.add(token.get(LemmaAnnotation.class));
            }
        }
        return lemmas.get(0);
    }
    
    public ArrayList<String> lemmatizeTokens(Set<String> tokens) {
        ArrayList<String> lemmas = new ArrayList<String>();
        for(String token : tokens) {
        	 Annotation t = new Annotation(token);
        	 this.pipeline.annotate(t);
        	 List<CoreMap> tt = t.get(SentencesAnnotation.class);
        	 for(CoreMap ttt: tt) {
                 // Iterate over all tokens in a sentence
                 for (CoreLabel tttt: ttt.get(TokensAnnotation.class)) {
                     // Retrieve and add the lemma for each word into the
                     // list of lemmas
                 	String s = tttt.get(LemmaAnnotation.class);
             // Iterate over all of the sentences found
                 	lemmas.add(s);
                 }
        	 }
        }
        return lemmas;
    }
    public ArrayList<String> Tokenize(String documentText) {
        ArrayList<String> tokens = new ArrayList<String>();

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);

        // run all Annotators on this text
        this.pipeline.annotate(document);

        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        boolean flag = false;
        String temp = "";
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the
                // list of lemmas
            	String s = token.toString();
            	if(s.contains("/")) {
            		int ind = s.indexOf("/");
            		if(ind!=0&&ind<s.length()-1) {
            			tokens.add(s.substring(0, ind));
            			tokens.add("/");
            			tokens.add(s.substring(ind+1, s.length()));
            			continue;
            		}
            	}
//            	if(token.toString().toLowerCase().equals("codesec")) {
//            		flag = true;
//            		temp = token.toString();
//            		continue;
//            	}
//            	if(flag) {
//            		flag = false;
//            		temp += token.toString();
//            		tokens.add(temp);
//            		continue;
//            	}
                tokens.add(token.toString());
            }
        }
        return tokens;
    }
    
    public String[] myTokenize(String string) {
    	ArrayList<String> tok = Tokenize(string);
		String[]          str = new String[tok.size()];
		for(int i=0; i<tok.size(); i++) {
			if(tok.get(i).equals("-LRB-")) {
				tok.set(i, "(");
			}
			if(tok.get(i).equals("-RRB-")) {
				tok.set(i, ")");
			}	
			str[i] = tok.get(i);
		}
		return str;
    }
    
    public String myTokenizeToString(String string) {
    	ArrayList<String> tok = Tokenize(string);
//		String[]          str = new String[tok.size()];
		String s = "";
		for(int i=0; i<tok.size(); i++) {
			if(tok.get(i).equals("-LRB-")) {
				tok.set(i, "(");
			}
			if(tok.get(i).equals("-RRB-")) {
				tok.set(i, ")");
			}	
//			str[i] = tok.get(i);
			s += tok.get(i)+" ";
		}
		return s.trim();
    }
}

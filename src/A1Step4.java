import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.StringTokenizer;


public class A1Step4 {

	private static Hashtable<String, Integer> lang2 = new Hashtable<String, Integer>();
	private static Hashtable<String, Integer> lang3 = new Hashtable<String, Integer>();
	private static Hashtable<String, Hashtable<String, Double>> langModel = new Hashtable<String, Hashtable<String, Double>>();
	private static Hashtable<String, Integer> lex = new Hashtable<String, Integer>();
	private static Hashtable<String, Integer> tags = new Hashtable<String, Integer>();
	private static Hashtable<String, Hashtable<String, Double>> taskModel = new Hashtable<String, Hashtable<String, Double>>();
	private static Hashtable<String, Double> taskModel0 = new Hashtable<String, Double>();
	private static ArrayList<String> testTagged = new ArrayList<String>();
	private static int totalN = 0;
	private static int correctN = 0;
	
	
	public static void main(String[] args) {
		boolean smoothing = false;
		
		
		if (args.length != 4) {
			System.out.println("Program requires 4 arguments");
			System.exit(1);
		}
		
		if(args[0].equals("yes")) {
			smoothing = true;
		}else if(args[0].equals("no")) {
			smoothing = false;
		}else {
			System.out.println("first argument must be [yes|no]");
			System.exit(1);
		}
		
		String trainPath = args[1];
		String testPath = args[2];
		String testOutPath = args[3];
		
		System.out.println("Starting A1Step4");
		System.out.println("training models...");
		
		trainTagger(trainPath);

		if (smoothing) {
			System.out.println("calculating smoothed probabilities...");
			propSmooth();
		}else {
			System.out.println("calculating unsmoothed probabilities...");
			propNoSmooth();			
		}
		
		System.out.println("extracting and tagging sentences...");
		tag(testPath);
				
		System.out.println("writing to file...");
		writeToFile(testOutPath);
		
		System.out.print("Accuracy: ");
		System.out.println( String.format( "%.1f", (((double) correctN)/totalN)*100) + "%");
		System.out.println("Done!");
	}

	private static void writeToFile(String testOutPath) {
		try {			 
			File file = new File(testOutPath);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for(String line : testTagged) {
				bw.write(line);
				bw.newLine();
			}
			bw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private static void propNoSmooth() {
		for(Entry<String, Integer> entry : lang3.entrySet()) {
		    String key3 = entry.getKey();
		    double value3 = entry.getValue().doubleValue();
		    
		    String key2 = key3.split(" ")[0] + " " + key3.split(" ")[1] + " ";
		    double value2 = lang2.get(key2);
		    
		    double prop = value3/value2;

		    Hashtable<String, Double> triMap = langModel.get(key2);
		    if( triMap ==null) {
		    	triMap = new Hashtable<String, Double>();
		    	langModel.put(key2, triMap);
		    }
		    triMap.put(key3.split(" ")[2], prop);		    			    
		}
		for(Entry<String, Integer> entry : lex.entrySet()) {
		    String wordTag = entry.getKey();
		    double valueWordTag = entry.getValue().doubleValue();
		    
		    String[] splitWordTag = wordTag.split("/");
		    String tag = splitWordTag[splitWordTag.length-1];
		    String word = splitWordTag[splitWordTag.length-2];
        	if(splitWordTag.length >= 3) {
            	for(int c = splitWordTag.length-3; c >= 0 ; c--) {
            		word = splitWordTag[c] + "/" + word;
            	}
        	}
        	
		    double valueTag = tags.get(tag);
		    
		    double prop = valueWordTag/valueTag;
		    
		    Hashtable<String, Double> wordMap = taskModel.get(tag);
		    if( wordMap ==null) {
		    	wordMap = new Hashtable<String, Double>();
		    	taskModel.put(tag, wordMap);
		    }
		    wordMap.put(word, prop);			    			    
		}
		for(Entry<String, Integer> entry : tags.entrySet()) {
		    String tag = entry.getKey();
		    taskModel0.put(tag, 0.0);			    			    
		}
		taskModel0.put("STOP", 0.0);
		Hashtable<String, Double> stopMap = new Hashtable<String, Double>();
		stopMap.put("STOP", 1.0);
		taskModel.put("STOP", stopMap);
	}

	private static void propSmooth() {
		for(Entry<String, Integer> entry : lang3.entrySet()) {
		    String key3 = entry.getKey();
		    double value3 = entry.getValue().doubleValue();
		    
		    String key2 = key3.split(" ")[0] + " " + key3.split(" ")[1] + " ";
		    double value2 = lang2.get(key2);
		    
		    double prop = value3/value2;
		    
		    Hashtable<String, Double> triMap = langModel.get(key2);
		    if( triMap ==null) {
		    	triMap = new Hashtable<String, Double>();
		    	langModel.put(key2, triMap);
		    }
		    triMap.put(key3.split(" ")[2], prop);
		    		    			    
		}
		for(Entry<String, Integer> entry : lex.entrySet()) {
		    String wordTag = entry.getKey();
		    double valueWordTag = entry.getValue().doubleValue();
		    if(valueWordTag == 1.0) {
		    	valueWordTag = 0.5;
		    }
		    
		    String[] splitWordTag = wordTag.split("/");
		    String tag = splitWordTag[splitWordTag.length-1];
		    String word = splitWordTag[splitWordTag.length-2];
        	if(splitWordTag.length >= 3) {
            	for(int c = splitWordTag.length-3; c >= 0 ; c--) {
            		word = splitWordTag[c] + "/" + word;
            	}
        	}
		    double valueTag = tags.get(tag);
		    
		    double prop = valueWordTag/valueTag;
		    
		    Hashtable<String, Double> wordMap = taskModel.get(tag);
		    if( wordMap ==null) {
		    	wordMap = new Hashtable<String, Double>();
		    	taskModel.put(tag, wordMap);
		    }
		    wordMap.put(word, prop);			    			    
		}
		for(Entry<String, Integer> entry : tags.entrySet()) {
		    String tag = entry.getKey();
		    double nvalue = entry.getValue().doubleValue();
		    int n = 0;
		    
		    for(Entry<String, Integer> lexEntry : lex.entrySet()) {
		    	String[] splitted = lexEntry.getKey().split("/");
			    String tag2 = splitted[splitted.length-1];
			    
		    	if(tag2.equals(tag) && lexEntry.getValue() == 1) {
		    		n++;
		    	}
		    }
		    
		    double prop = 0.5*(n/nvalue);
		    taskModel0.put(tag, prop);			    			    
		}
		taskModel0.put("STOP", 0.0);
		Hashtable<String, Double> stopMap = new Hashtable<String, Double>();
		stopMap.put("STOP", 1.0);
		taskModel.put("STOP", stopMap);
	}

	private static void tag(String testPath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(testPath));
			String str = reader.readLine();
            
			String[] sentence = new String[18];
			Arrays.fill(sentence, "STOP");
            sentence[0] = "START";
            sentence[1] = "START";
            
            String[] correctTags = new String[18];
            Arrays.fill(correctTags, "STOP");
            correctTags[0] = "START";
            correctTags[1] = "START";
            
            int i = 2;
            
            while (str != null) {
            	if (!sentence[2].equals("STOP") && (str.equals("======================================") || str.matches(".*\\./\\. "))) {
            		tagSentence(sentence, correctTags, i-1);

            		i = 2;
            		
            		Arrays.fill(sentence, "STOP");
                    sentence[0] = "START";
                    sentence[1] = "START";
                    
                    Arrays.fill(correctTags, "STOP");
                    sentence[0] = "START";
                    sentence[1] = "START";
                    str = reader.readLine();
            	}else if (str.length() != 0) {
            		StringTokenizer st = new StringTokenizer(str, " []");

            		while(st.hasMoreTokens() && i < 18) {
                        String token = st.nextToken();
                        if (token.matches(".+/[a-zA-Z0-9]+")) {
                        	String[] splitToken = token.split("/");
                        	String word = splitToken[splitToken.length-2];
                        	if(splitToken.length >= 3) {
	                        	for(int c = splitToken.length-3; c >= 0 ; c--) {
	                        		word = splitToken[c] + "/" + word;
	                        	}
                        	}
                        	String tag = splitToken[splitToken.length-1];
                        	
                        	sentence[i] = word;
                        	correctTags[i] = tag;
                        	i++;
                        }
                    }
            		str = reader.readLine();
            		if(st.hasMoreTokens() && i == 18) {                		
                		while(str != null && !str.equals("======================================") && !str.matches(".*\\./\\. ")) {
                			str = reader.readLine();
                		}
                	}
            	}else {
            		str = reader.readLine();
            	}
            	
            }
			reader.close();
		} catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
	}

	private static void tagSentence(String[] sentence, String[] correctTags,
			int n) {
		if(n == 17) {
			testTagged.add(sequenceToString(sentence, 2, 17) + "...");
			testTagged.add("- sentence too long -");
			testTagged.add("=====");
		}else {			
			String[] newSentence = new String[n];
			for (int j = 2; j <= n+1; j++) {
				newSentence[j-2] = sentence[j];
			}
			Object[] ret = viterbi(newSentence);
			String[] tags = ((String) ret[1]).split(",");
			for(int j = 1; j < tags.length; j++) {
				totalN++;
				if(tags[j].equals(correctTags[j+1])) {
					correctN++;
				}
			}
			String predictedTags = "";
			try {
				predictedTags = sequenceToString(((String) ret[1]).split(","), 1, n-1);
			}catch(Exception e){}
			
			testTagged.add("      sentence: " + sequenceToString(newSentence,0,n-2));
			testTagged.add("predicted tags: " + predictedTags);
			testTagged.add("  correct tags: " + sequenceToString(correctTags, 2, n));
			testTagged.add("=====");
		}
		
	}

	// viterbi algorithm from:
	// http://en.wikibooks.org/wiki/Algorithm_Implementation/Viterbi_algorithm
	// addapted to our needs
	private static Object[] viterbi(String[] sentence) {
		
		Hashtable<String, Object[]> T = new Hashtable<String, Object[]>();
		
		for (String state : tags.keySet()) {
			T.put(state, new Object[] {0.0, state, 0.0, "START"});
		}
		T.put("START", new Object[] {1.0, "START", 1.0, "START"});
 
		for (String output : sentence) {
			Hashtable<String, Object[]> U = new Hashtable<String, Object[]>();
			
			for (String next_state : tags.keySet()) {
				double total = 0;
				String argmax = "";
				double valmax = 0;
				String prevmax = "";
 
				double prob = 1;
				String v_path = "";
				double v_prob = 1;	
				String prev = "";
				
				for (String source_state : tags.keySet()) {
					Object[] objs = T.get(source_state);
					prob = ((Double) objs[0]).doubleValue();
					v_path = (String) objs[1];
					v_prob = ((Double) objs[2]).doubleValue();
					prev = (String) objs[3];

					Double p_task = null;
					try {
						p_task = taskModel.get(next_state).get(output);
					}catch(Exception e) {}
					
					if(p_task == null) {
						p_task = taskModel0.get(next_state);
					}
					
					Double p_lang = null;
					try {
						p_lang = langModel.get(prev + " " + source_state + " ").get(next_state);
					}catch(Exception e) {}
					
					if(p_lang == null) {
						p_lang = 0.0;
					}
			
					double p = p_task * p_lang;
					prob *= p;
					v_prob *= p;
					total += prob;
					if (v_prob > valmax)
					{
						argmax = v_path + "," + next_state;
						valmax = v_prob;
						prevmax = prev;
					}
				}
				U.put(next_state, new Object[] {total, argmax, valmax, prevmax});
			}
			T = U;			
		}
 
		double total = 0;
		String argmax = "";
		double valmax = 0;
		String prevmax = "";
		
		double prob;
		String v_path;
		double v_prob;
		String prev;
		
		for (String states : T.keySet())
		{
			Object[] objs = T.get(states);
			prob = ((Double) objs[0]).doubleValue();
			v_path = (String) objs[1];
			v_prob = ((Double) objs[2]).doubleValue();
			prev = (String) objs[3];
			total += prob;
			
			if (v_prob > valmax)
			{
				argmax = v_path;
				valmax = v_prob;
				prevmax = prev;
			}
		}	
		return new Object[]{total, argmax, valmax, prevmax};
		
	}

	private static void trainTagger(String path) {
		try {
			String[] seq3 = new String[3];
			String[] seq2 = new String[2];
			Arrays.fill(seq3, "START");
			Arrays.fill(seq2, "START");
			String key;
			
			lang2.clear();
			lang3.clear();
			lex.clear();
			tags.clear();
			
			lang2.put(sequenceToString(seq2), 1);
			lang3.put(sequenceToString(seq3), 1);
		
			BufferedReader crp = new BufferedReader(new FileReader(path));	
            String str = crp.readLine();
            
            while (str != null) {
            	if (str.equals("======================================") || str.matches(".*\\./\\. ")) {
            		for(int i=1; i <= 2;i++) {
	            		seq2 = put(seq2, "STOP");
	            		key = sequenceToString(seq2);
	            		if(lang2.containsKey(key)) {
	            			lang2.put(key, lang2.get(key) + 1);
	            		}else {
	            			lang2.put(key, 1);
	            		}
	            	}
            		
            		for(int i=1; i <= 3;i++) {
	            		seq3 = put(seq3, "STOP");
	            		key = sequenceToString(seq3);
	            		if(lang3.containsKey(key)) {
	            			lang3.put(key, lang3.get(key) + 1);
	            		}else {
	            			lang3.put(key, 1);
	            		}
	            	}
            		
            		Arrays.fill(seq2, "START");
                    key = sequenceToString(seq2);
           			lang2.put(key, lang2.get(key) + 1);
           			
           			Arrays.fill(seq3, "START");
                    key = sequenceToString(seq3);
           			lang3.put(key, lang3.get(key) + 1);
            	}else if (str.length() != 0) {
            		StringTokenizer st = new StringTokenizer(str, " []");
            		
            		while(st.hasMoreTokens()) {
                        String token = st.nextToken();
                        if (token.matches(".+/[a-zA-Z0-9]+")) {
                        	String[] splitToken = token.split("/");
                        	String word = splitToken[splitToken.length-2];
                        	if(splitToken.length >= 3) {
	                        	for(int c = splitToken.length-3; c >= 0 ; c--) {
	                        		word = splitToken[c] + "/" + word;
	                        	}
                        	}
                        	String tag = splitToken[splitToken.length-1];

                        	seq3 = put(seq3, tag);
	                        key = sequenceToString(seq3);
	                        if(lang3.containsKey(key)) {
	                           lang3.put(key, lang3.get(key) + 1);
	                        }else {
	                           lang3.put(key, 1);
	                        }
	                        
	                        seq2 = put(seq2, tag);
	                        key = sequenceToString(seq2);
	                        if(lang2.containsKey(key)) {
	                           lang2.put(key, lang2.get(key) + 1);
	                        }else {
	                           lang2.put(key, 1);
	                        }
	                        
	                        if(lex.containsKey(token)) {
		                           lex.put(token, lex.get(token) + 1);
		                    }else {
		                       lex.put(token, 1);
		                    }
	                        
	                        if(tags.containsKey(tag)) {
		                           tags.put(tag, tags.get(tag) + 1);
		                    }else {
		                       tags.put(tag, 1);
		                    }
                        }
                    }            		
            	}            	
            	
                str = crp.readLine();	            
            }      
            
            for(int i=1; i <= 2;i++) {
        		seq2 = put(seq2, "STOP");
        		key = sequenceToString(seq2);
        		if(lang2.containsKey(key)) {
        			lang2.put(key, lang2.get(key) + 1);
        		}else {
        			lang2.put(key, 1);
        		}
        	}
    		
    		for(int i=1; i <= 3;i++) {
        		seq3 = put(seq3, "STOP");
        		key = sequenceToString(seq3);
        		if(lang3.containsKey(key)) {
        			lang3.put(key, lang3.get(key) + 1);
        		}else {
        			lang3.put(key, 1);
        		}
        	}
    		
    		tags.put("STOP", 1);
    		tags.put("START", 1);
    		
            crp.close(); 
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

	}
	
	private static String[] put(String[] sequence, String word) {
		if (sequence.length > 1) {
			for (int i = 1; i < sequence.length; i++) {
				sequence[i-1] = sequence[i]; 
			}
		}
		sequence[sequence.length-1] = word;
		return sequence;
	}
	
	private static String sequenceToString(String[] sequence, int x, int y) {
		String seq = "";
		for (int i = x; i <= y; i++) {
			seq += sequence[i] + " ";
		}
		return seq;
	}
	
	private static String sequenceToString(String[] sequence) {
		String seqString = "";
		for (int i = 0; i < sequence.length; i++) {
			if (!sequence[i].equals("")) {
				seqString += sequence[i] + " ";
			}
		}
		return seqString;
	}
}

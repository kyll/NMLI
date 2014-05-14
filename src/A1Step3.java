import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;


public class A1Step3 {

	public static void main(String[] args) {
		String trncrp = args[0];
		String tstcrp = args[1];
		
		int n = Integer.parseInt(args[2]);
		if(n<2) {
			exit(1, "enter an n > 2");
		}
		
		Hashtable<String, Integer> ngram = ngram(n, trncrp);
		Hashtable<String, Integer> mgram = ngram(n-1, trncrp);
		String[][] nArray = hashToArray(ngram);
		String[][] mArray = hashToArray(mgram);		
		nArray = sortArray(nArray);
		mArray = sortArray(mArray);
		
		int v;		
		if (n==2) {
			v = mgram.size();
		}else {
			v = ngram(n-1, trncrp).size();
		}
		
		if (args[3].equals("no")) {
			noSmoothing(mgram, ngram, n, tstcrp);
		}else if (args[3].equals("add1")) {
			add1(mgram, ngram, n, v, tstcrp);
		}else if (args[3].equals("gt")) {
			gt(ngram, n, tstcrp);
		}else {
			exit(1, "invalid smoothing, use [no|add1|gt]");
		}
	}

	private static void gt(Hashtable<String, Integer> ngram, int n, String path) {
		
		int[] bin = new int[7];
		Arrays.fill(bin, 0);
		String[][] nArray = sortArray(hashToArray(ngram));
		int y;
		int x = nArray.length-1;
		for(int j = 1; j <= 6; j++) {
			for (y = x; Integer.parseInt(nArray[y][1]) == j; y--) {
				bin[j]++;
			}
			x=y;
		}
				
		float[] count = new float[6];
		for(int r = 1; r < 6; r++) {
			count[r] = ((r+1)*(((float) bin[r+1])/bin[r]) - r*(((float) 6*bin[6])/bin[1]))/(1-((float) (6)*bin[6])/bin[1]);
		}
		
		float sum = 0;
		for (int i = x; i >= 0; i--) {
			sum += Integer.parseInt(nArray[i][1]);
		}
		for (int i = 1; i < 6; i++) {
			sum += bin[i]*count[i];
		}

		float prop0 = ((float) bin[1])/nArray.length;
		
		try {
			System.out.println(" -good Turing smoothing-");
			BufferedReader crp = new BufferedReader(new FileReader(path));
            String str = crp.readLine();
            int totalSentences = 0;
            int zeroSentences = 0;
            while (str != null) {
                StringTokenizer st = new StringTokenizer(str, " ,.:;\"(){}[]?!*^\r\n\t");
                int m = st.countTokens();
                if (m > 0) {
                	totalSentences++;
                	float p = 1;
	               	String[] sequence = new String[m+n];
	               	sequence[m+n-1] = "STOP";
	               	for (int i = 0; i < n-1; i++) {
	               		sequence[i] = "START";
	               	}
	               	for (int i = n-1; i <= m+n-1; i++) {
	               		if (i < m+n-1) {
	               			sequence[i] = st.nextToken();
	               		}
	               		String key1 = sequenceToString(sequence, i - n +1, i);
	               			
	               		if (ngram.containsKey(key1)) {
	               			int r = ngram.get(key1).intValue();
	               			if (r <= 5) {
	               				p *= count[r]/sum;
	               			}else {
	               				p*= ngram.get(key1).floatValue()/sum;
	               			}
	               		}else {
	               			p *= prop0;	               				
	               		}	
	               	}	               	
	               	if (p==0) {
	               		zeroSentences++;
	               		if (zeroSentences <= 5) {
	               			System.out.println(totalSentences + " - " + str);
	               		}
	               	}
                }
	            str = crp.readLine();
            }
            
            System.out.print(((float)zeroSentences)/totalSentences);
            System.out.println("% zero probability sentences");
            crp.close();
		}catch(Exception e){exit(1, "Read error");}
		
	}

	private static void add1(Hashtable<String, Integer> mgram,
			Hashtable<String, Integer> ngram, int n, int v, String path) {
		
		try {
			System.out.println(" -add 1 smoothing-");
			BufferedReader crp = new BufferedReader(new FileReader(path));
            String str = crp.readLine();
            int totalSentences = 0;
            int zeroSentences = 0;
            while (str != null) {
                StringTokenizer st = new StringTokenizer(str, " ,.:;\"(){}[]?!*^\r\n\t");
                int m = st.countTokens();
                if (m > 0) {
                	totalSentences++;
                	float p = 1;
	               	String[] sequence = new String[m+n];
	               	sequence[m+n-1] = "STOP";
	               	for (int i = 0; i < n-1; i++) {
	               		sequence[i] = "START";
	               	}
	               	for (int i = n-1; i <= m+n-1; i++) {
	               		if (i < m+n-1) {
	               			sequence[i] = st.nextToken();
	               		}
	               		String key1 = sequenceToString(sequence, i - n +1, i);
	               		String key2 = sequenceToString(sequence, i - n +1, i-1);
	               		float count1 = 0;
	               		float count2 = 0;
	               		
	               		if (ngram.containsKey(key1)) {
	               			count1 = ngram.get(key1).floatValue();
	               		}
	               		if (mgram.containsKey(key2)) {
	               			count2 = mgram.get(key2).floatValue();
	               		}
	               		
               			p *= (count1 + 1)/(count2 + v);
	
	               	}
	               	
	               	if (p==0) {
	               		zeroSentences++;
	               		if (zeroSentences <= 5) {
	               			System.out.println(totalSentences + " - " + str);
	               		}
	               	}
                }
	            str = crp.readLine();
            }
            
            System.out.print(((float)zeroSentences)/totalSentences);
            System.out.println("% zero probability sentences");
            crp.close();
		}catch(Exception e){exit(1, "Read error");}
		
	}

	private static void noSmoothing(Hashtable<String, Integer> mgram,
			Hashtable<String, Integer> ngram, int n, String path) {
		
		try {
			System.out.println(" -No smoothing-");
			BufferedReader crp = new BufferedReader(new FileReader(path));
            String str = crp.readLine();
            int totalSentences = 0;
            int zeroSentences = 0;
            while (str != null) {
                StringTokenizer st = new StringTokenizer(str, " ,.:;\"(){}[]?!*^\r\n\t");
                int m = st.countTokens();
                if (m > 0) {
                	totalSentences++;
                	float p = 1;
	               	String[] sequence = new String[m+n];
	               	sequence[m+n-1] = "STOP";
	               	for (int i = 0; i < n-1; i++) {
	               		sequence[i] = "START";
	               	}
	               	for (int i = n-1; i <= m+n-1; i++) {
	               		if (i < m+n-1) {
	               			sequence[i] = st.nextToken();
	               		}
	               		String key1 = sequenceToString(sequence, i - n +1, i);
	               		String key2 = sequenceToString(sequence, i - n +1, i-1);
	               			
	               		if (ngram.containsKey(key1) && mgram.containsKey(key2)) {
	               			p *= ngram.get(key1).floatValue()/mgram.get(key2).floatValue();
	               		}else {
	               			p *= 0;	               				
	               		}	
	               	}	               	
	               	if (p==0) {
	               		zeroSentences++;
	               		if (zeroSentences <= 5) {
	               			System.out.println(totalSentences + " - " + str);
	               		}
	               	}
                }
	            str = crp.readLine();
            }
            
            System.out.print(((float)zeroSentences)/totalSentences);
            System.out.println("% zero probability sentences");
            crp.close();
		}catch(Exception e){exit(1, "Read error");}
		
	}

	private static Hashtable<String, Integer> ngram(int n, String path) {
		try {
			BufferedReader crp = new BufferedReader(new FileReader(path));	

			String[] sequence = new String[n];
			Arrays.fill(sequence, "START");
			Hashtable<String, Integer> table = new Hashtable<String, Integer>();
			
			String key = sequenceToString(sequence);
			if(table.containsKey(key)) {
				table.put(key, table.get(key) + 1);
			}else {
				table.put(key, 1);
			}
		

            String str = crp.readLine();
            while (str != null) {
                StringTokenizer st = new StringTokenizer(str, " ,.:;\"(){}[]?!*^\r\n\t");
                int t = st.countTokens();
                while(st.hasMoreTokens()) {
                    String word = st.nextToken();
                    sequence = put(sequence, word);
                    key = sequenceToString(sequence);
                    if(table.containsKey(key)) {
                       table.put(key, table.get(key) + 1);
                    }else {
                       table.put(key, 1);
                    }
                }
                
	            str = crp.readLine();
	            if((str == null || str.length() == 0) && t > 0) {
	            	for(int i=1; i < n;i++) {
	            		sequence = put(sequence, "STOP");
	            		key = sequenceToString(sequence);
	            		if(table.containsKey(key)) {
	            			table.put(key, table.get(key) + 1);
	            		}else {
	            			table.put(key, 1);
	            		}
	            	}
                    
                    Arrays.fill(sequence, "START");
                    key = sequenceToString(sequence);
            		if(table.containsKey(key)) {
            			table.put(key, table.get(key) + 1);
            		}else {
            			table.put(key, 1);
            		}
	            }
            }
            
            crp.close();
            return table;
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
		return null;
	}
	
	private static String sequenceToString(String[] sequence, int x, int y) {
		String seq = "";
		for (int i = x; i <= y; i++) {
			seq += sequence[i] + " ";
		}
		return seq;
	}
	
	private static String[][] sortArray(String[][] array) {
		Arrays.sort(array, new Comparator<String[]>() {
            @Override
            public int compare(String[] entry1, String[] entry2) {
                Integer nr1 = Integer.parseInt(entry1[1]);
                Integer nr2 = Integer.parseInt(entry2[1]);
                int sComp = nr2.compareTo(nr1);
                
                if (sComp != 0) {
                    return sComp;
                 } else {
                    String s1 = entry1[0];
                    String s2 = entry2[0];
                    return s1.compareTo(s2);
                 }
            }
        });
		return array;
	}

	private static String[][] hashToArray(Hashtable<String, Integer> table) {
		int size = table.size();
		String[][] array = new String[size][2];
		Set<String> keySet = table.keySet();
		int i = 0;
		for(String key : keySet) {
			array[i][0] = key;
			array[i][1] = table.get(key).toString();
			i++;
		}
		return array;
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

	private static String sequenceToString(String[] sequence) {
		String seqString = "";
		for (int i = 0; i < sequence.length; i++) {
			if (!sequence[i].equals("")) {
				seqString += sequence[i] + " ";
			}
		}
		return seqString;
	}
	
	public static int sum(String[][] array) {
		int sum = 0;
		for (int i = 0; i < array.length; i++) {
			sum += Integer.parseInt(array[i][1]);
		}
		return sum;
	}

	private static void exit(int i, String msg) {
		System.out.print(msg);
		System.exit(i);
	}
}

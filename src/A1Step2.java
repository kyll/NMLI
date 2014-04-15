import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;


public class A1Step2 {
	
	private static ArrayList<String[]> perms = new ArrayList<String[]>();

	public static void main(String[] args) {
		BufferedReader crp = null;
		BufferedReader cpf = null;
		int n = 0;
		Hashtable<String, Integer> table1 = new Hashtable<String, Integer>();
		String[][] array1;
		Hashtable<String, Integer> table2 = new Hashtable<String, Integer>();
		String[][] array2 = null;
		
		if (args.length == 2 || args.length == 3) {
			try {
				crp = new BufferedReader(new FileReader(args[0]));	
				n = Integer.parseInt(args[1]);
			}catch(Exception e) {exit(1, "input must be: -corpus [path] -n [value] -conditional/sequence-prob-file [path]");}
			if(args.length == 3) {
				try {
					cpf = new BufferedReader(new FileReader(args[2]));				
				}catch(Exception e) {exit(1, "input must be: -corpus [path] -n [value] -conditional/sequence-prob-file [path]");}
			}
		}else {
			exit(1, "input must be: -corpus [path] -n [value] -conditional-prob-file [path]");
		}
		if(n<1) {
			exit(1, "n must be > 1");
		}
		
		table1 = ngram(n, crp);
		array1 = hashToArray(table1);
		array1 = sortArray(array1);
				
		if (n > 1) {
			try {
				crp = new BufferedReader(new FileReader(args[0]));
			}catch(Exception e) {exit(1, "Reading error");}
			table2 = ngram(n-1, crp);
			array2 = hashToArray(table2);
			array2 = sortArray(array2);
		}
		int sum2 = 0;
		if (array2 != null) {
			sum2 = sum(array2);
		}

		if (cpf != null) {
			ArrayList<Float> conProp = conProp(n, cpf, table1, table2, sum(array1), sum2);
			
			try {
				cpf = new BufferedReader(new FileReader(args[2]));
			}catch(Exception e) {exit(1, "Reading error");}
			ArrayList<Float> seqProp = seqProp(n, cpf, table1, table2, sum(array1), sum2);
			
		}
		
		String[] A = { "know", "I", "opinion", "do", "be", "your", "not", "may", "what"};
		String[] B = {"I", "do", "not", "know"};
		permutations(A,0);
		ArrayList<Float> permProp = seqProp(n, perms, table1, table2, sum(array1), sum2);
		for(Float p : permProp) {
			System.out.println(p.floatValue());
		}
	}

	private static ArrayList<Float> seqProp(int n, ArrayList<String[]> permutations,
			Hashtable<String, Integer> table1,
			Hashtable<String, Integer> table2, int sum1, int sum2) {

		ArrayList<Float> seqProp = new ArrayList<Float>();

        for (String[] str : permutations) {
            int m = str.length;
            if (m > 0) {
            	
             	float p = 1;
              	String[] sequence = new String[m+n];
               	sequence[m+n-1] = "STOP";
               	for (int i = 0; i < n-1; i++) {
               		sequence[i] = "START";
               	}
               	for (int i = n-1; i <= m+n-1; i++) {
               		if (i < m+n-1) {
               			sequence[i] = str[i-n+1];
               		}
               		if (n == 1) {
               			String key = sequence[i];
               			if (table1.containsKey(key)) {
             				p += Math.log(table1.get(key).floatValue()/sum1);
               			}else {
               				p += -100000;
               			}
               		}else {
               			String key1 = sequenceToString(sequence, i - n +1, i);
               			String key2 = sequenceToString(sequence, i - n +1, i-1);
               			
               			if (table1.containsKey(key1) && table2.containsKey(key2)) {
               				p += Math.log(table1.get(key1).floatValue()/table2.get(key2).floatValue());
               			}else {
               				p += -100000;	               				
               			}
               		}	               		
               	}	               	
               	seqProp.add(p);
            }
        }

		return seqProp;
	}

	private static void permutations(String[] a, int k) {
		if(k==a.length)
			 perms.add(a);
		else for (int i = k; i < a.length; i++) {
			String temp=a[k];
			a[k]=a[i];
			a[i]=temp;
			permutations(a,k+1);
			temp=a[k];
			a[k]=a[i];
			a[i]=temp;
		}
	}

	private static ArrayList<Float> seqProp(int n, BufferedReader cpf,
			Hashtable<String, Integer> table1,
			Hashtable<String, Integer> table2, int sum1, int sum2) {

		ArrayList<Float> seqProp = new ArrayList<Float>();
		try {
            String str = cpf.readLine();
            while (str != null) {
                StringTokenizer st = new StringTokenizer(str, " ,.:;\"(){}[]?!*^\r\n\t");
                int m = st.countTokens();
                if (m > 0) {
                	
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
	               		if (n == 1) {
	               			String key = sequence[i];
	               			if (table1.containsKey(key)) {
	               				p += Math.log(table1.get(key).floatValue()/sum1);
	               			}else {
	               				p += -100000;
	               			}
	               		}else {
	               			String key1 = sequenceToString(sequence, i - n +1, i);
	               			String key2 = sequenceToString(sequence, i - n +1, i-1);
	               			
	               			if (table1.containsKey(key1) && table2.containsKey(key2)) {
	               				p += Math.log(table1.get(key1).floatValue()/table2.get(key2).floatValue());
	               			}else {
	               				p += -100000;	               				
	               			}
	               		}	               		
	               	}	               	
	               	seqProp.add(p);
                }
	            str = cpf.readLine();
            }
		}catch(Exception e){exit(1, "Read error");}
		return seqProp;
	}

	private static String sequenceToString(String[] sequence, int x, int y) {
		String seq = "";
		for (int i = x; i <= y; i++) {
			seq += sequence[i] + " ";
		}
		return seq;
	}

	private static ArrayList<Float> conProp(int n, BufferedReader cpf,
			Hashtable<String, Integer> table1, Hashtable<String, Integer> table2, int sum1, int sum2) {
		
		ArrayList<Float> seqProp = new ArrayList<Float>();
		try {
            String str = cpf.readLine();
            while (str != null) {
                StringTokenizer st = new StringTokenizer(str, " ,.:;\"(){}[]?!*^\r\n\t");
                int t = st.countTokens();
                if (t == n) {
                	String[] sequence = new String[n];
	                while(st.hasMoreTokens()) {		                	
	                    String word = st.nextToken();
	                    sequence = put(sequence, word);
	                }
	                String key = sequenceToString(sequence);
	                
	                if (n == 1 && table1.containsKey(key)) {
	                	float p = table1.get(key).floatValue()/sum1;
	                	seqProp.add(p);
	                }else if (n > 1) {
	                	String[] sequence2 = sequence;
		                sequence2[n-1] = "";
		                String key2 = sequenceToString(sequence2);
		                Integer p1 = table1.get(key);
		                Integer p2 = table2.get(key2);
		                
		                if(p1 != null && p2 != null && p2.intValue() > 0) {
		                	float p = p1.floatValue()/p2.intValue();
		                	seqProp.add(p);
		                }else {
		                	seqProp.add(0f);
		                }
	                }else {
	                	seqProp.add(0f);
	                }
                }else {
                	seqProp.add(0f);
                }
	            str = cpf.readLine();
            }
		}catch(Exception e){exit(1, "Read error");}
		return seqProp;
	}

	private static Hashtable<String, Integer> ngram(int n, BufferedReader crp) {
		String[] sequence = new String[n];
		Arrays.fill(sequence, "START");
		Hashtable<String, Integer> table = new Hashtable<String, Integer>();
		
		String key = sequenceToString(sequence);
		if(table.containsKey(key)) {
			table.put(key, table.get(key) + 1);
		}else {
			table.put(key, 1);
		}
		
		try {
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
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
		return table;
	}

	public static int sum(String[][] array) {
		int sum = 0;
		for (int i = 0; i < array.length; i++) {
			sum += Integer.parseInt(array[i][1]);
		}
		return sum;
	}

	public static void printArray(String[][] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.println(array[i][0] + "- " + array[i][1]);
		}
	}
	
	public static void printArray(int from, int to, String[][] array) {
		if (from > to) {
			int tmp = from;
			from = to;
			to = tmp;
		}
		
		if(from < 0) {
			from = 0;
		}else if(from > array.length) {
			from = array.length;
		}
		
		if (to > array.length) {
			to = array.length;
		}else if (to < 0) {
			to = 0;
		}
		
		if (from == to) {
			System.out.println(array[from][0] + "- " + array[from][1]);
			return;
		}

		for (int i = from; i < to; i++) {
			System.out.println(array[i][0] + "- " + array[i][1]);
		}
	}

	public static void writeArray(String[][] array) {
		try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                            "Ngram.txt"), true));
			for (int i = 0; i < array.length; i++) {
				//System.out.println(array[i][0] + " " + array[i][1]);
            
				bw.write(array[i][0] + " " + array[i][1]);
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {}
    
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
		
	private static void exit(int i, String msg) {
		System.out.print(msg);
		System.exit(i);
	}

}

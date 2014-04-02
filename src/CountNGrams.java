import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;


public class CountNGrams {
	
	private static int n;
	private static int m;
	private static String[] sequence;
	private static Hashtable<String, Integer> table = new Hashtable<String, Integer>();
	private static String[][] array;
	
	public static void main(String[] args) {

		System.out.print("Loading");
		try{
			n = Integer.parseInt(args[1]);
			m = Integer.parseInt(args[2]);
			
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		
		sequence = new String[n];
		Arrays.fill(sequence, "");
		
		try {
            BufferedReader src = new BufferedReader(new FileReader(args[0]));
            String str = src.readLine();
            while (str != null) {
                StringTokenizer st = new StringTokenizer(str, " ,.:;\"(){}[]?!*^\n\t");
                while(st.hasMoreTokens()) {
                    String word = st.nextToken();
                       put(word);
                       String key = sequenceToString();
                       if(table.containsKey(key)) {
                       	table.put(key, table.get(key) + 1);
                       }else {
                       	table.put(key, 1);
                    }
                }
                str = src.readLine();
            }
            src.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
		System.out.print(".");
		
		hashToArray();
		System.out.print(".");
		
		sortArray();
		System.out.print(".");
		
		//writeArray();
		//System.out.println(".");
		
		int sum = sum();
		System.out.print(".");
		
		System.out.println("\nDone!\n");		
		System.out.println(sum);
		printArray(0,m);
	}

	private static int sum() {
		int sum = 0;
		for (int i = 0; i < array.length; i++) {
			sum += Integer.parseInt(array[i][1]);
		}
		return sum;
	}

	private static void printArray() {
		for (int i = 0; i < array.length; i++) {
			System.out.println(array[i][0] + "- " + array[i][1]);
		}
	}
	
	private static void printArray(int from, int to) {
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

	private static void writeArray() {
		try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
                            "Ngram.txt"), true));
			for (int i = 0; i < array.length; i++) {
				System.out.println(array[i][0] + " " + array[i][1]);
            
				bw.write(array[i][0] + " " + array[i][1]);
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {}
    
	}

	private static void sortArray() {
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
	}

	private static void hashToArray() {
		int size = table.size();
		array = new String[size][2];
		Set<String> keySet = table.keySet();
		int i = 0;
		for(String key : keySet) {
			array[i][0] = key;
			array[i][1] = table.get(key).toString();
			i++;
		}
	}

	private static void put(String word) {
		if (sequence.length > 1) {
			for (int i = 1; i < sequence.length; i++) {
				sequence[i-1] = sequence[i]; 
			}
		}
		sequence[sequence.length-1] = word;
	}
	
	private static String sequenceToString() {
		String seqString = "";
		for (int i = 0; i < sequence.length; i++) {
			if (!sequence[i].equals("")) {
				seqString += sequence[i] + " ";
			}
		}
		return seqString;
	}

}

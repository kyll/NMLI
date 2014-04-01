import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;


public class CountNGrams {
	
	private static int n;
	private static String[] sequence;
	private static Hashtable<String, Integer> table = new Hashtable<String, Integer>();
	private static String[][] array;
	
	public static void main(String[] args) {

		try{
			n = Integer.parseInt(args[0]);
			
			
		}catch(Exception e){
			System.out.print(e);
			System.exit(0);
		}
		
		sequence = new String[n];
		Arrays.fill(sequence, "");
		try {
            BufferedReader src = new BufferedReader(new FileReader(args[1]));
            String str = src.readLine();
            while (str != null) {
                StringTokenizer st = new StringTokenizer(str, " ,.:;\"-_(){}[]?!*^&'\n\t");
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
        } catch (IOException e) {
            e.printStackTrace();
        }
		hashToArray();
		sortArray();
		printArray();
	}

	private static void printArray() {
		for (String[] s : array) {
            System.out.println(s[0] + " " + s[1]);
        }
	}

	private static void sortArray() {
		Arrays.sort(array, new Comparator<String[]>() {
            @Override
            public int compare(String[] entry1, String[] entry2) {
                String nr1 = entry1[1];
                String nr2 = entry2[1];
                return nr1.compareTo(nr2);
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
		if (sequence.length > 2) {
			for (int i = 1; i < sequence.length; i++) {
				sequence[i-1] = sequence[i]; 
			}
		}
		sequence[sequence.length-1] = word;
	}
	
	private static String sequenceToString() {
		String seqString = "";
		for (int i = 0; i < sequence.length; i++) {
			seqString += sequence[i];
		}
		return seqString;
	}

}

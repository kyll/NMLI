import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;


public class CountNGrams {
	
	private static int n;
	private static String[] sequence;
	
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
                    if (true) {
                        put(word);
                        getSequence();
                    }
                }
                str = src.readLine();
            }
            src.close();
        } catch (IOException e) {
            e.printStackTrace();
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

}

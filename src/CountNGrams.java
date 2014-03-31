import java.io.*;
import java.util.StringTokenizer;


public class CountNGrams {

	public static void main(String[] args) {
		int n;
		try{
			n = Integer.parseInt(args[0]);
			
			
		}catch(Exception e){
			System.out.print(e);
			System.exit(0);
		}
		try {
            BufferedReader src = new BufferedReader(new FileReader(args[1]));
            String str = src.readLine();
            while (str != null) {
                StringTokenizer st = new StringTokenizer(str, " ,.:;\"-_(){}[]?!*^&'\n\t");
                while(st.hasMoreTokens()) {
                    String word = st.nextToken();
                    //hier moet shit
                }
                str = src.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

	}

}

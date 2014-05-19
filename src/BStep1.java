import java.io.*;
import java.util.Stack;


public class BStep1 {

	public static void main(String[] args) {
		File input = new File(args[0]);
		if (!input.canRead()) {
			System.out.println("invalid input file");
			System.exit(1);
		}
		File output = new File(args[1]);
		try {
			if (!output.createNewFile()) {
				//System.out.println("File already exists");
				
			}
		} catch (IOException e) {
			System.out.println("invalid output file");
			System.exit(1);
		}

		try {
			binarize(input, output);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void binarize(File input, File output) throws Exception {		
		BufferedReader reader = new BufferedReader(new FileReader(input));
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		
		Stack<String> tagStack = new Stack<String>();
		
		String line = reader.readLine();
		String outLine = "";
		
		// read lines untill end of file
		while(line != null) {
			
			// parse line char by char
			for (int i = 0; i < line.length(); i++) {
				
				// if open bracket encountered, extract tag and add to tagStack
				// add super tag to tag: i.e. S becomes S^ROOT
				if(line.charAt(i) == '(') {
					outLine += "("; 
					String tag = "";
					i++;
					while(line.charAt(i) != ' ') {
						tag += line.charAt(i);
						i++;
					}
					
					// appends tag with optional super tag
					// TODO: shouldnt do for every tag, for correct see examples
					if(tagStack.isEmpty()) {
						outLine += tag + " ";
					}else {
						outLine += tag + "^" + tagStack.peek() + " ";
					}
					if(line.charAt(i+1) == '(') {
						tagStack.push(tag);
					}
				}
			}
			System.out.println(outLine);
			//uncomment later
			//writer.write(outLine);
			//writer.newLine();
			outLine = "";
			
			line = reader.readLine();
		}
		
		reader.close();
		writer.close();
	}

}

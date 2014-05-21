import java.io.*;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;


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
		String line = reader.readLine();
		// read lines untill end of file
		while(line != null) {
			StringTokenizer parts = new StringTokenizer(line, " ");
			ArrayList<String> tags = new ArrayList<String>();
			String hor = "";
			ArrayList<String> outLine = new ArrayList<String>();
			boolean horFlag = false;
			
			while (parts.hasMoreTokens()) {
				String part = parts.nextToken();
				if (part.startsWith("(")) {
					if (tags.isEmpty()) {
						outLine.add(part);
						tags.add(part.substring(1));
					}else {
						outLine.add(part + "^" + tags.get(tags.size()-1));
						tags.add(part.substring(1));
					}
				}
				if (part.endsWith(")")) {
					if (horFlag) {
						outLine.remove(outLine.size()-1);
						outLine.add("(" + tags.get(tags.size()-1));
						outLine.add(part);
						tags.remove(tags.size()-1))
					}else {
						horFlag = true;
						outLine.remove(outLine.size()-1);
						outLine.add("(" + tags.get(tags.size()-1));
						outLine.add(part);
						tags.remove(tags.size()-1))
					}
				}
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			/*
			ArrayList<String> tags = new ArrayList<String>();		
			String outLine = "";
			int nBelongs = 0;
			boolean belongFlag = false;
			
			// parse line char by char
			for (int i = 0; i < line.length(); i++) {
				for(String t : tags) {
					System.out.print(t + "-");
				}
				System.out.println();
				System.out.println(outLine);
				System.out.println("========");
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
					if(tags.isEmpty()) {
						outLine += tag;
						tags.add(tag);
					}else if(!belongFlag && line.charAt(i+1) == '(') {
						outLine += tag + "^" + tags.get(tags.size()-1);
						tags.add(tag);
					}else if(!belongFlag && line.charAt(i+1) != '(') {
						belongFlag = true;
						nBelongs++;
						String word = "";
						i++;
						while(line.charAt(i) != ')') {
							word += line.charAt(i);
							i++;
						}
						i++;
						outLine += tag + " " + word + ")";
						tags.add(tag);
					}else if(belongFlag && line.charAt(i+1) == '(') {
						outLine += "@" + tags.get(tags.size() - 1 - nBelongs) + "^" + tags.get(tags.size() - 2 - nBelongs) + "->";
						for(int j = 1; j <= nBelongs; j++) {
							outLine += "_" + tags.get(tags.size()-j);
						}
						outLine += " (" + tag + "^" + tags.get(tags.size()-1-nBelongs);
						tags.add(tag);
					}else {
						outLine += "@" + tags.get(tags.size() - 1 - nBelongs) + "^" + tags.get(tags.size() - 2 - nBelongs) + "->";
						for(int j = 1; j <= nBelongs; j++) {
							outLine += "_" + tags.get(tags.size()-j);
						}
						String word = "";
						i++;
						while(line.charAt(i) != ')') {
							word += line.charAt(i);
							i++;
						}
						i++;
						outLine += " (" + tag + " " + word + ")";
						tags.add(tag);
					}
				}
				if(line.charAt(i) == ')') {
					outLine += ")"; 
					if (!tags.isEmpty()) {
						tags.remove(tags.size() - 1);
					}
				}
				if(line.charAt(i) == ' ') {
					outLine += " ";
				}
			}

			//uncomment later
			//writer.write(outLine);
			//writer.newLine();
			outLine = "";
			*/
			line = reader.readLine();
		}
		
		reader.close();
		writer.close();
	}

}

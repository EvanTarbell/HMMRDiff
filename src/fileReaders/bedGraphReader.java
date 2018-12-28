package fileReaders;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import Node.TagNode;

public class bedGraphReader {
	
	private String input;
	
	private ArrayList<TagNode> output;
	
	public bedGraphReader(String i){
		input = i;
		setData();
	}

	
	public ArrayList<TagNode> getData(){return output;}
	
	public HashMap<String,ArrayList<TagNode>> getMappedData(){
		return toMap(output);
	}

	private void setData(){
		output = new ArrayList<TagNode>();
		Scanner inFile = null;
		try{
			inFile = new Scanner((Readable) new FileReader(input));
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		while (inFile.hasNextLine()){
			String line = inFile.nextLine();
			String[] fields = line.split("\\s+");
			//If score column id double, the bedgraph is the HMMR signals
			if (isDouble(fields[3])){
				output.add(new TagNode(fields[0],Integer.parseInt(fields[1]),Integer.parseInt(fields[2]),
						Double.parseDouble(fields[3])) );
			}
			//If score column is not a double, the bedgraph is the annotation track and the score column is EX
			//where X is the state number, as an int
			else{
				output.add(new TagNode(fields[0],Integer.parseInt(fields[1]),Integer.parseInt(fields[2]),
						Integer.parseInt(fields[3].substring(1))) );
			}
		}
	}
	
	boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
	
	public static HashMap<String,ArrayList<TagNode>> toMap(ArrayList<TagNode> i){
		HashMap<String,ArrayList<TagNode>> map = new HashMap<String,ArrayList<TagNode>>();
		for (int x = 0;x < i.size();x++){
			String chr = i.get(x).getChrom();
			if (map.containsKey(chr)){
				ArrayList<TagNode> temp = map.get(chr);
				temp.add(i.get(x));
				map.put(chr, temp);
			}
			else{
				ArrayList<TagNode> temp = new ArrayList<TagNode>();
				temp.add(i.get(x));
				map.put(chr, temp);
			}
		}
		
		return map;
		
	}
}

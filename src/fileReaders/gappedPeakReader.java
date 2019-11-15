package fileReaders;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import Node.TagNode;

public class gappedPeakReader {
	
	private String file;
	private double filter=0;
	
	
	private ArrayList<TagNode> data;
	
	public gappedPeakReader(String f,double fl){
		file = f;
		filter=fl;
		setData();
	}
	
	public ArrayList<TagNode> getData(){return data;}

	private void setData(){
		data = new ArrayList<TagNode>();
		Scanner inFile = null;
		try{
			inFile = new Scanner((Readable) new FileReader(file));
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		while (inFile.hasNextLine()){
			String line = inFile.nextLine();
			String[] fields = line.split("\\s+");
			if (!fields[3].contains("HighCoverage") && Double.parseDouble(fields[12]) >= filter){
				data.add(new TagNode(fields[0],Integer.parseInt(fields[1]),Integer.parseInt(fields[2]) ) );
			}
		}
	}
	

}

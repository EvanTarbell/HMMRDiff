package fileReaders;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class labelCSVReader {
	
	private String CSV;
	
	private ArrayList<String[]> data;
	private int numberLabels;
	
	public labelCSVReader(String c){
		CSV = c;
		setData();
	}
	
	public ArrayList<String[]> getData(){return data;}
	public int getNumberLabels(){return numberLabels;}
	private void setData(){
		
		
		data = new ArrayList<String[]>();
		
		Scanner inFile = null;
		try{
			inFile = new Scanner((Readable) new FileReader(CSV));
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		int counter = 0;
		while (inFile.hasNextLine()){
			counter++;
			String line = inFile.nextLine();
			String[] fields = line.split(",");
			data.add(fields);
		}
		numberLabels=counter;
	}

}

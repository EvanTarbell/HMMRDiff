package HMMR_DIFF;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class SampleHolder {
	
	private String samples;
	
	//File names for each sample
	private ArrayList<String> sampleGappedPeaks;
	private ArrayList<String> sampleBedgraphs;
	private ArrayList<String> sampleLogs;
	private ArrayList<String> sampleModels;
	
	//Values for track creation for each sample
	private ArrayList<String> sampleBAMS;
	private ArrayList<String> sampleBAI;
	private ArrayList<double[]> sampleFragMeans;
	private ArrayList<double[]> sampleFragStds;
	private ArrayList<Integer> sampleMinQ;
	private ArrayList<Integer> sampleTrims;
	private ArrayList<Boolean> sampleRmDups;
	private ArrayList<Double> scalingFactor;
	
	
	public SampleHolder(String s){
		samples=s;
		set();
		
	}
	public ArrayList<String> getBAMS(){return sampleBAMS;}
	public ArrayList<String> getBAIS(){return sampleBAI;}
	public ArrayList<double[]> getFragMeans(){return sampleFragMeans;}
	public ArrayList<double[]> getFragStds(){return sampleFragStds;}
	public ArrayList<Integer> getMinQ(){return sampleMinQ;}
	public ArrayList<Integer> getTrims(){return sampleTrims;}
	public ArrayList<Boolean> getRmDups(){return sampleRmDups;}
	public ArrayList<String> getGappedPeaks(){return sampleGappedPeaks;}
	public ArrayList<String> getBedgraphs(){return sampleBedgraphs;}
	public ArrayList<String> getLogs(){return sampleLogs;}
	public ArrayList<String> getModels(){return sampleModels;}
	public ArrayList<Double> getScalingFactors(){return scalingFactor;}
	
	
	private void set(){
		
		sampleModels = new ArrayList<String>();
		sampleLogs = new ArrayList<String>();
		sampleGappedPeaks = new ArrayList<String>();
		sampleBedgraphs = new ArrayList<String>();
		sampleBAMS = new ArrayList<String>();
		sampleBAI = new ArrayList<String>();
		sampleFragMeans= new  ArrayList<double[]>();
		sampleFragStds = new  ArrayList<double[]>();
		sampleMinQ = new ArrayList<Integer>();
		sampleTrims = new ArrayList<Integer>();
		sampleRmDups = new ArrayList<Boolean>();
		scalingFactor = new ArrayList<Double>();
		
		String[] fields = samples.split(",");
		for (int i = 0 ; i < fields.length;i++){
			String value = fields[i];

			//Populate lists with file names for each sample
			
			sampleGappedPeaks.add(value+"_peaks.gappedPeak");
			
			sampleBedgraphs.add(value+".bedgraph");
			
			sampleLogs.add(value+".log");
			
			sampleModels.add(value+".model");
			
			
			//Read Log file to populate lists of fragment means/sd, BAM/BAI file names
			Scanner inFile = null;
			try{
				inFile = new Scanner((Readable) new FileReader(value+".log"));
			}
			catch(FileNotFoundException e){
				e.printStackTrace();
			}
			int counter=0;
			double[] mu = new double[4];
			double[] sd = new double[4];
			int minq = 30;
			int trim = 0;
			double scale = 1;
			boolean rmdup = true;
			while (inFile.hasNextLine()){
				String line = inFile.nextLine();
				String[] f = line.split("\\s+");
				if (f[0].equals("-b") || f[0].equals("--bam")){
					sampleBAMS.add(f[1]);
				} else if(f[0].equals("-i") || f[0].equals("--index")){
					sampleBAI.add(f[1]);
				}else if(f[0].equals("Mean")){
					mu[counter] = Double.parseDouble(f[1]);
					sd[counter] = Double.parseDouble(f[3]);
					counter++;
				} else if(f[0].equals("-q") || f[0].equals("--minmapq")){
					minq = Integer.parseInt(f[1]);
				} else if (f[0].equals("--trim")){
					trim = Integer.parseInt(f[1]);
				} else if(f[0].equals("--removeDuplicates")){
					if(f[1].contains("f") || f[1].contains("F")){
						rmdup=false;
					}
				} else if(f[0].equals("ScalingFactor")){
					 scale = Double.parseDouble(f[1]);
				}
				
			}
			sampleFragMeans.add(mu);
			sampleFragStds.add(sd);
			sampleMinQ.add(minq);
			sampleTrims.add(trim);
			sampleRmDups.add(rmdup);
			scalingFactor.add(scale);
			
		}
	}
	

}

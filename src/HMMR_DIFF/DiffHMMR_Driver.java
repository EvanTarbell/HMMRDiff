package HMMR_DIFF;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import stats.ANOSIM;
import stats.GenerateSimMatrix;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.io.HmmBinaryReader;
import fileReaders.bedFileReader;
import fileReaders.labelCSVReader;
import ATACFragments.FragPileupGen;
import ATACFragments.TrackHolder;
import LocalModel.buildLocalModel;
import Node.TagNode;
import RobustHMM.RobustHMM;


public class DiffHMMR_Driver {
	

	public static String samples=null;
	public static String labels=null;
	public static String peaks=null;
	public static int ext=0;
	public static double threshold;

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException, IOException {

		
		ArgsParser p = new ArgsParser(args);
		samples=p.getSamples();
		labels=p.getLabels();
		peaks=p.getPeaks();
		ext = p.getExtension();
		threshold=p.getThreshold();
		
		boolean labelsAreFile=false;
		
		//Exit program if  sample list is null or not comma separated 
		// or labels is empty or not a comma separated list or not a csv file
		if ( 
				(samples == null || !samples.contains(",")) || 
				( labels == null || (!labels.contains(",") && !labels.contains(".csv")) )
				
			){		
			p.printUsage();
			System.exit(1);
		}
		
		//Check to make sure that if dynamic labeling then peak file input, then read labels
		ArrayList<String[]> lab = new ArrayList<String[]>();
		int numberLabels=0;
		if(peaks == null && labels.contains(".csv")){
			System.out.println("Warning: Dynamic Labeling requires predefined peak file");
			p.printUsage();
			System.exit(1);
		} else if(peaks != null && labels.contains(".csv")){
			labelsAreFile=true;
			labelCSVReader csvReader = new labelCSVReader(labels);
			lab.addAll(csvReader.getData());
			numberLabels = lab.size();
			csvReader=null;
		} else if(labels.contains(",")){
			
			lab.add(labels.split(","));
			labelsAreFile=false;
			numberLabels=1;
			if(!ANOSIM.isValid(labels.split(","))){
				System.out.println("Warning: Label list must contain 2 conditions and 2 condtions only");
				p.printUsage();
				System.exit(1);
			}
		}
		
		//Populate the sample holder which contains the file names of all samples
		SampleHolder holder = new SampleHolder(samples);
		
		// If peak file is empty, create master list from sample files
		ArrayList<TagNode> masterPeaks;
		if (peaks==null){
			masterPeaks = new GappedPeakMerger(holder,ext).getPeaks();
		} else{
			bedFileReader bedRead = new bedFileReader(peaks);
			masterPeaks = bedRead.getData();
			if(labelsAreFile){
				if (masterPeaks.size() != numberLabels){
					System.out.println("Warning: csv file containing labels is not same length as input bed file of peaks");
					p.printUsage();
					System.exit(1);
				}
			}
		}
		
		//Define mode for FragPileupGen
		double[] mode = new double[4];
		mode[1] = mode[2] = mode[3] = 2;
		mode[0]=0.5;
		
		//Iterate through master peak list and perform analysis
		for (int i = 0;i < masterPeaks.size();i++){
			ArrayList<TagNode> temp = new ArrayList<TagNode>();
			temp.add(masterPeaks.get(i));
			
			ArrayList<Hmm<?>> localModels = new ArrayList<Hmm<?>>();
			ArrayList<String> models = holder.getModels();
			for (int a = 0;a < models.size();a++){
				
				//Extract inputs for FragPileupGen from SampleHolder
				File bam = new File(holder.getBAMS().get(a));
				File index = new File(holder.getBAIS().get(a));
				double[] fragMeans = holder.getFragMeans().get(a);
				double[] fragStddevs = holder.getFragStds().get(a);
				int minMapQ = holder.getMinQ().get(a);
				boolean rmDup = holder.getRmDups().get(a);
				double cpmScale = holder.getScalingFactors().get(a);
				int trim = holder.getTrims().get(a);
				
				//Generate signal matrix
				FragPileupGen gen = new FragPileupGen(bam, index, temp, mode, fragMeans, fragStddevs,minMapQ,rmDup,cpmScale);
				TrackHolder trackHolder = new TrackHolder((gen.transformTracks(gen.scaleTracks(gen.getAverageTracks()))),trim);
				
				//Read in sample model
				Hmm<ObservationVector> hmm = (Hmm<ObservationVector>) HmmBinaryReader.read(new FileInputStream(new File(models.get(a))));
				 
				//Generate annotation track
				RobustHMM HMM = new RobustHMM(trackHolder.getObs(),null,hmm,false,0,"Vector",0);
				int[] states = HMM.getStates();
				ArrayList<ObservationVector> vec = (ArrayList<ObservationVector>) trackHolder.getObs();
				
				//Build the local model
				buildLocalModel builder = new buildLocalModel(states,vec,hmm);
				localModels.add(builder.getLocalModel());
				
				
			}//Loop through samples
			
			//Generate the similarity matrix of kullback lieber distances
			double[][] simMatrix = new GenerateSimMatrix(localModels).getData();
			
			String[] label;
			if(labelsAreFile){
				label=lab.get(i);
			} else{
				label = lab.get(0);
			}
			
			ANOSIM anosim = new ANOSIM(simMatrix, label, threshold);
			
		}//Loop through master peak set
		
		
	}//main

}

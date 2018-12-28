package HMMR_DIFF;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import stats.KullbackLieber;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.io.HmmBinaryReader;
import fileReaders.bedGraphReader;
import fileReaders.gappedPeakReader;
import Node.TagNode;


public class DiffHMMR_Driver {
	

	public static String prefix1;
	public static String prefix2;

	public static void main(String[] args) throws FileNotFoundException, IOException {

		
		ArgsParser p = new ArgsParser(args);
		prefix1 = p.getPrefix1();
		prefix2 = p.getPrefix2();
		
		/**
		 * Check to see if the two models are the same. If not, issue warning
		 */
		Hmm<?> h1 = null;
		Hmm<?> h2 = null;
		h1 = HmmBinaryReader.read(new FileInputStream(prefix1+".model"));
		h2 = HmmBinaryReader.read(new FileInputStream(prefix2+".model"));
		@SuppressWarnings("unchecked")
		KullbackLieber kb = new KullbackLieber((Hmm<ObservationVector>)h1,(Hmm<ObservationVector>) h2);
		if (kb.getDistance() != 0.0){
			System.out.println("Warning!!! The two runs of HMMRATAC used different models./n"+
					"This is not recommended. Will proceed anyway, using first model");
		}
		
		/**
		 * Read the HMMRATAC gappedPeak files and create master list of all merged Peaks
		 */
		ArrayList<TagNode> peaks = new gappedPeakReader(prefix1+"_peaks.gappedPeak").getData();
		peaks.addAll(new gappedPeakReader(prefix2+"_peaks.gappedPeak").getData());
		Collections.sort(peaks, TagNode.basepairComparator);
		ArrayList<TagNode> finalPeaks = new MergeBed(peaks).getResults();//will need to extend these etc
		
		/**
		 * Read in the bedgraphs, both state annotations and raw HMMRATAC signals
		 */
		HashMap<String,ArrayList<TagNode>> oneStates = new bedGraphReader(prefix1+".bedgraph").getMappedData();
		HashMap<String,ArrayList<TagNode>> twoStates = new bedGraphReader(prefix2+".bedgraph").getMappedData();
		
		HashMap<String,ArrayList<TagNode>> oneShort = new bedGraphReader(prefix1+"_shortSignal.bedgraph").getMappedData();
		HashMap<String,ArrayList<TagNode>> oneMono = new bedGraphReader(prefix1+"_monoSignal.bedgraph").getMappedData();
		HashMap<String,ArrayList<TagNode>> oneDi = new bedGraphReader(prefix1+"_diSignal.bedgraph").getMappedData();
		HashMap<String,ArrayList<TagNode>> oneTri = new bedGraphReader(prefix1+"_triSignal.bedgraph").getMappedData();
		
		HashMap<String,ArrayList<TagNode>> twoShort = new bedGraphReader(prefix2+"_shortSignal.bedgraph").getMappedData();
		HashMap<String,ArrayList<TagNode>> twoMono = new bedGraphReader(prefix2+"_monoSignal.bedgraph").getMappedData();
		HashMap<String,ArrayList<TagNode>> twoDi = new bedGraphReader(prefix2+"_diSignal.bedgraph").getMappedData();
		HashMap<String,ArrayList<TagNode>> twoTri = new bedGraphReader(prefix2+"_triSignal.bedgraph").getMappedData();
		
		/**
		 * Go through the peaks and grab any bedgraph entry that overlaps
		 */
		HashMap<String,ArrayList<TagNode>> mappedPeaks = bedGraphReader.toMap(finalPeaks);
		for (String chr : mappedPeaks.keySet()){
			ArrayList<TagNode> chrPeaks = mappedPeaks.get(chr);
			
			ArrayList<TagNode> oneState = oneStates.get(chr);
			ArrayList<TagNode> twoState = twoStates.get(chr);
			
			ArrayList<TagNode> oneSh = oneShort.get(chr);
			ArrayList<TagNode> oneM = oneMono.get(chr);
			ArrayList<TagNode> oneD = oneDi.get(chr);
			ArrayList<TagNode> oneT = oneTri.get(chr);
			
			ArrayList<TagNode> twoSh = twoShort.get(chr);
			ArrayList<TagNode> twoM = twoMono.get(chr);
			ArrayList<TagNode> twoD = twoDi.get(chr);
			ArrayList<TagNode> twoT = twoTri.get(chr);
			
			Collections.sort(chrPeaks,  TagNode.basepairComparator);
			
			Collections.sort(oneState,  TagNode.basepairComparator);
			Collections.sort(twoState,  TagNode.basepairComparator);
			
			Collections.sort(oneSh,  TagNode.basepairComparator);
			Collections.sort(oneM,  TagNode.basepairComparator);
			Collections.sort(oneD,  TagNode.basepairComparator);
			Collections.sort(oneT,  TagNode.basepairComparator);
			
			Collections.sort(twoSh,  TagNode.basepairComparator);
			Collections.sort(twoM,  TagNode.basepairComparator);
			Collections.sort(twoD,  TagNode.basepairComparator);
			Collections.sort(twoT,  TagNode.basepairComparator);
			
			
			int indexS1 = 0;
			int indexSh1 = 0;
			int indexM1 = 0;
			int indexD1 = 0;
			int indexT1 = 0;
			
			int indexS2 = 0;
			int indexSh2 = 0;
			int indexM2 = 0;
			int indexD2 = 0;
			int indexT2 = 0;
			for (int i = 0; i < chrPeaks.size();i++){
				TagNode temp = chrPeaks.get(i);
				
				
				
				ArrayList<TagNode> overlapsS1 = new ArrayList<TagNode>();
				ArrayList<TagNode> overlapsSh1 = new ArrayList<TagNode>();
				ArrayList<TagNode> overlapsM1 = new ArrayList<TagNode>();
				ArrayList<TagNode> overlapsD1 = new ArrayList<TagNode>();
				ArrayList<TagNode> overlapsT1 = new ArrayList<TagNode>();
				
				ArrayList<TagNode> overlapsS2 = new ArrayList<TagNode>();
				ArrayList<TagNode> overlapsSh2 = new ArrayList<TagNode>();
				ArrayList<TagNode> overlapsM2 = new ArrayList<TagNode>();
				ArrayList<TagNode> overlapsD2 = new ArrayList<TagNode>();
				ArrayList<TagNode> overlapsT2 = new ArrayList<TagNode>();
				
				boolean hasHadOverlapS1 = false;
				for (int a = indexS1;a<oneState.size();a++){
					if (SubtractBed.overlap(temp, oneState.get(a)).hasHit()){
						overlapsS1.add(oneState.get(a));hasHadOverlapS1=true;
					}
					else{
						if(hasHadOverlapS1){
							indexS1=a;
							break;
						}
					}
				}
				
				boolean hasHadOverlapSh1 = false;
				for (int b = indexSh1;b<oneSh.size();b++){
					if (SubtractBed.overlap(temp, oneSh.get(b)).hasHit()){
						overlapsSh1.add(oneSh.get(b));hasHadOverlapSh1=true;
					}
					else{
						if(hasHadOverlapSh1){
							indexSh1=b;
							break;
						}
					}
				}
				
				boolean hasHadOverlapM1 = false;
				for (int b = indexM1;b<oneM.size();b++){
					if (SubtractBed.overlap(temp, oneM.get(b)).hasHit()){
						overlapsM1.add(oneM.get(b));hasHadOverlapM1=true;
					}
					else{
						if(hasHadOverlapM1){
							indexM1=b;
							break;
						}
					}
				}
				
				boolean hasHadOverlapD1 = false;
				for (int c = indexD1;c<oneD.size();c++){
					if (SubtractBed.overlap(temp, oneD.get(c)).hasHit()){
						overlapsD1.add(oneD.get(c));hasHadOverlapD1=true;
					}
					else{
						if(hasHadOverlapD1){
							indexD1=c;
							break;
						}
					}
				}
				
				boolean hasHadOverlapT1 = false;
				for (int d = indexT1;d<oneT.size();d++){
					if (SubtractBed.overlap(temp, oneT.get(d)).hasHit()){
						overlapsT1.add(oneT.get(d));hasHadOverlapT1=true;
					}
					else{
						if(hasHadOverlapT1){
							indexT1=d;
							break;
						}
					}
				}
				
				boolean hasHadOverlapS2 = false;
				for (int e = indexS2;e<twoState.size();e++){
					if (SubtractBed.overlap(temp, twoState.get(e)).hasHit()){
						overlapsS2.add(twoState.get(e));hasHadOverlapS2=true;
					}
					else{
						if(hasHadOverlapS2){
							indexS2=e;
							break;
						}
					}
				}
				
				boolean hasHadOverlapSh2 = false;
				for (int f = indexSh2;f<twoSh.size();f++){
					if (SubtractBed.overlap(temp, twoSh.get(f)).hasHit()){
						overlapsSh2.add(twoSh.get(f));hasHadOverlapSh2=true;
					}
					else{
						if(hasHadOverlapSh2){
							indexSh2=f;
							break;
						}
					}
				}
				
				boolean hasHadOverlapM2 = false;
				for (int g = indexM2;g<twoM.size();g++){
					if (SubtractBed.overlap(temp, twoM.get(g)).hasHit()){
						overlapsM2.add(twoM.get(g));hasHadOverlapM2=true;
					}
					else{
						if(hasHadOverlapM2){
							indexM2=g;
							break;
						}
					}
				}
				
				boolean hasHadOverlapD2 = false;
				for (int h = indexD2;h<twoD.size();h++){
					if (SubtractBed.overlap(temp, twoD.get(h)).hasHit()){
						overlapsD2.add(twoD.get(h));hasHadOverlapD2=true;
					}
					else{
						if(hasHadOverlapD2){
							indexD2=h;
							break;
						}
					}
				}
				
				boolean hasHadOverlapT2 = false;
				for (int j = indexT2;j<twoT.size();j++){
					if (SubtractBed.overlap(temp, twoT.get(j)).hasHit()){
						overlapsT2.add(twoT.get(j));hasHadOverlapT2=true;
					}
					else{
						if(hasHadOverlapT2){
							indexT2=j;
							break;
						}
					}
				}
				
			}//for peaks
		}
	
		
		
	}//main

}

package HMMR_DIFF;

import java.util.ArrayList;
import java.util.Collections;

import fileReaders.gappedPeakReader;
import Node.TagNode;

public class GappedPeakMerger {
	
	private SampleHolder hold;
	private int extension;
	
	private ArrayList<TagNode> peaks;
	
	public GappedPeakMerger(SampleHolder h,int x){
		hold=h;
		extension = x;
		makePeaks();
	}
	
	public ArrayList<TagNode> getPeaks(){return peaks;}
	
	private void makePeaks(){
		peaks = new ArrayList<TagNode>();
		ArrayList<String> files = hold.getGappedPeaks();
		for (int i = 0; i < files.size();i++){
			String f = files.get(i);
			peaks.addAll(new ExtendBed
					(new gappedPeakReader(f).getData(),extension).getResults());
			
		}
		
		Collections.sort(peaks, TagNode.basepairComparator);
		peaks = new MergeBed(peaks).getResults();
		
	}

}

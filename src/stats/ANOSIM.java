package stats;

import java.util.HashMap;

public class ANOSIM {
	
	private double[][] matrix;
	private String[] labels;
	private double threshold;
	
	private double score;
	private boolean isSignif=false;
	
	public ANOSIM(double[][] m, String[] l,double t){
		matrix=m;labels=l;threshold=t;
		calculate();
	}
	
	private void calculate(){
		
		if(ANOSIM.isValid(labels)){
			if(isSpecialCase()){
				score = calculateOneToOne();
				if(score >= threshold){
					isSignif = true;
				}
			} else{
				score = calculateStandard();
				if(score >= threshold){
					isSignif = true;
				}
			}
		}
		
	}
	
	/*
	 * TODO: Write method for calculating n X m matices
	 */
	private double calculateStandard(){
		return 0;
	}
	
	private double calculateOneToOne(){
		
		int index1 = -1;
		int index2 = -1;
		for (int i = 0;i< labels.length;i++){
			if (!labels[i].equals("NA")){
				if (index1 >= 0){
					index2=i;
				} else {
					index1=i;
				}
			}
		}
		return matrix[index1][index2];
	}
	
	public boolean isSignificant(){return isSignif;}
	public double getScore(){return score;}
	
	public boolean isSpecialCase(){
		
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		for (int i = 0;i< labels.length;i++){
			if(map.containsKey(labels[i])){
				int value = map.get(labels[i]);
				value+=1;
				map.put(labels[i], value);
			} else if(!map.containsKey(labels[i])){
				int value = 1;
				map.put(labels[i], value);
			}
		}
		int counter = 0;
		for (String key : map.keySet()){
			if(!key.equals("NA")){
				int val = map.get(key);
				if (val==1){
					counter++;
				}
			}
		}
		
		if(counter>1){return true;}
		else {return false;}
	}
	

	public static boolean isValid(String[] labels){
		
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		for (int i = 0;i< labels.length;i++){
			if(map.containsKey(labels[i])){
				int value = map.get(labels[i]);
				value+=1;
				map.put(labels[i], value);
			} else if(!map.containsKey(labels[i])){
				int value = 1;
				map.put(labels[i], value);
			}
		}
		int counter = 0;
		for (String key : map.keySet()){
			if(!key.equals("NA")){
				counter++;
			}
		}
		
		if(counter==2){return true;}
		else {return false;}
	}

}

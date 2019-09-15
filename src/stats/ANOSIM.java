package stats;

import java.util.HashMap;

public class ANOSIM {
	
	private double[][] matrix;
	private String[] labels;
	private double threshold;
	
	public ANOSIM(double[][] m, String[] l,double t){
		matrix=m;labels=l;threshold=t;
		
	}
	
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

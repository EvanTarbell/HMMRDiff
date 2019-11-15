package stats;

import java.util.ArrayList;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;

public class GenerateSimMatrix {
	
	private ArrayList<Hmm<?>> models;
	private double[][] matrix;
	
	public GenerateSimMatrix(ArrayList<Hmm<?>> m){
		models=m;
		setData();
	}
	
	public double[][] getData(){return matrix;}
	
	@SuppressWarnings("unchecked")
	private void setData(){
		
		matrix = new double[models.size()][models.size()];
		for (int i = 0; i < models.size()-1;i++){
			Hmm<ObservationVector> mod1 = (Hmm<ObservationVector>) models.get(i);
			
			for(int a = i+1 ; a < models.size();a++){
				
				Hmm<ObservationVector> mod2 = (Hmm<ObservationVector>) models.get(a);
//				Long startTimeKullback = System.currentTimeMillis();
				double temp = (new KullbackLieber(mod1,mod2).getDistance() + new KullbackLieber(mod2,mod1).getDistance()) / 2;
//				Long stopTimeKullback = System.currentTimeMillis();
//				System.out.println("Time to calculate Kullback in seconds:\t"+( Long.toString((stopTimeKullback - startTimeKullback)/1)));
				
				matrix[i][a] = temp;
				matrix[a][i]=temp;
			}
		}
	}

}

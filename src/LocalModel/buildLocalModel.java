package LocalModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.stat.correlation.StorelessCovariance;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.OpdfMultiGaussian;

public class buildLocalModel {

	private int[] states;
	private ArrayList<ObservationVector> list;
	private Hmm<?> hmm;
	
	
	private Hmm<?> localHMM;
	
	public buildLocalModel(int[] s,ArrayList<ObservationVector> l,Hmm<?> h){
		states = s;
		list = l;
		hmm=h;
		localHMM = build(states,list,hmm);
	}

	public Hmm<?> getLocalModel(){return localHMM;}
	
	private int getK(int[] states){
		HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
		for (int i = 0; i < states.length;i++){
			if (!map.containsKey(states[i])){
				map.put(states[i], 1);
			}
		}
		
		int counter=0;
		for(@SuppressWarnings("unused") int key : map.keySet()){
			counter++;
		}
		
		return counter;
	}

	@SuppressWarnings("unchecked")
	private  Hmm<?> build(int[] states, ArrayList<ObservationVector> list,Hmm<?> hmm){
		
		List<OpdfMultiGaussian> opdf = new ArrayList<OpdfMultiGaussian>();
		OpdfMultiGaussian pdf = (OpdfMultiGaussian) hmm.getOpdf(0);
		int dim = pdf.dimension();
		int K = hmm.nbStates();
//		int K = getK(states);
		
		for (int a = 0; a < K;a++){
			
			int stateCounter=0;
			double[] means = new double[dim];
			StorelessCovariance cov = new StorelessCovariance(dim);
			double[][] var = new double[dim][states.length];
			for (int x = 0;x < states.length;x++){
				if (states[x] == a){
					double[] values = list.get(x).values();
					int counter = 0;
					stateCounter+=1;
					while(counter < dim){
					
						means[counter] += values[counter];
						var[counter][x] = values[counter];
						counter++;
					}
					cov.increment(values);
				}
			}
			for (int y = 0;y < means.length;y++){
				means[y] /= (double) stateCounter;
			}
			//For regular covariance matrix
			/*double[][] covMat = null;
			covMat = cov.getData();
			*/
			//End of regular covariance
			
			//New to ensure diagonal covariance matrix
			double[][] covMat = new double[dim][dim];
			Variance variance = new Variance();
			for (int z = 0;z<dim;z++){
				covMat[z][z] = variance.evaluate(var[z]);
			}
			//End of new code
			
			//printCovariance(covMat);
			if (stateCounter == 0){
				means = new double[dim];
				covMat = new double[dim][dim];
				pdf = new OpdfMultiGaussian(means, covMat);
				opdf.add(pdf);
			}
			else{
				pdf = new OpdfMultiGaussian(means, covMat);
				opdf.add(pdf);
			}
		}
		
		double[][] trans = new double[K][K];
		
			for (int a = 0;a < states.length-1;a++){
				trans[states[a]][states[a+1]]++;
			}
			for (int i = 0; i < trans.length;i++){
			
				int elementCounter=0;
				for (int x = 0;x < trans[i].length;x++){
					elementCounter += trans[i][x];
				}
				for (int y = 0; y < trans[i].length;y++){
					if (elementCounter>0){
						trans[i][y] /= elementCounter;
					} else{
						trans[i][y] = 0;
					}
					
				}
			}
		
		
		double[] initial = new double[K];
		
			for (int i = 0; i < K;i++){
				initial[i] = 1/(double)K;
			}
		
		hmm = new Hmm<ObservationVector>(initial,trans,opdf);
		hmm = checkModel((Hmm<ObservationVector>)hmm);
		return hmm;
	}
	
	/**
	 * Check the model 
	 * @param hmm HMM to check
	 * @return modified HMM after robust correction
	 */
	private Hmm<ObservationVector> checkModel(Hmm<ObservationVector> hmm){
		for (int i = 0;i < hmm.nbStates();i++){
			OpdfMultiGaussian pdf = (OpdfMultiGaussian) hmm.getOpdf(i);
			double[][] cov = pdf.covariance();
			FitRobust fitter = new FitRobust(cov);
			double[][] temp = fitter.getCovariance();
			OpdfMultiGaussian t = new OpdfMultiGaussian(pdf.mean(),temp);
			hmm.setOpdf(i, t);
		}
		return hmm;
	}
}

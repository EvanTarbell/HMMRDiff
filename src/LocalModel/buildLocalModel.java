package LocalModel;

import java.util.ArrayList;
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

	private  Hmm<?> build(int[] states, ArrayList<ObservationVector> list,Hmm<?> hmm){
		
		List<OpdfMultiGaussian> opdf = new ArrayList<OpdfMultiGaussian>();
		OpdfMultiGaussian pdf = (OpdfMultiGaussian) hmm.getOpdf(0);
		int dim = pdf.dimension();
		int K = hmm.nbStates();
		
		for (int a = 0; a < hmm.nbStates();a++){
			
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
				pdf = (OpdfMultiGaussian) hmm.getOpdf(a);
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
					trans[i][y] /= elementCounter;
				}
			}
		
		
		double[] initial = new double[K];
		
			for (int i = 0; i < K;i++){
				initial[i] = 1/(double)K;
			}
		
		hmm = new Hmm<ObservationVector>(initial,trans,opdf);
		return hmm;
	}
}

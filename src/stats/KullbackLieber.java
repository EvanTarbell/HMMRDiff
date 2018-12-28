package stats;

import java.util.List;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.toolbox.MarkovGenerator;

public class KullbackLieber {
	
	private Hmm<ObservationVector> hmm1;
	private Hmm<ObservationVector> hmm2;
	
	private int numSeq = 10;
	private int seqLen=1000;
	
	private double distance;

	public KullbackLieber(Hmm<ObservationVector> h1,Hmm<ObservationVector> h2){
		hmm1=h1;
		hmm2=h2;
		set();
	}
	public KullbackLieber(Hmm<ObservationVector> h1,Hmm<ObservationVector> h2,int numberOfSequence,int SequenceLength){
		hmm1=h1;
		hmm2=h2;
		numSeq=numberOfSequence;
		seqLen=SequenceLength;
		set();
	}
	public double getDistance(){return distance;}
	
	private void set(){
		distance=findDistance(hmm1,hmm2,numSeq,seqLen);
	}
	
	private double findDistance(Hmm<ObservationVector> hmm1,Hmm<ObservationVector> hmm2,int nbSequences,int sequencesLength) {
        double distance = 0.0;
        for (int i = 0; i < nbSequences; i++) {
        	MarkovGenerator<ObservationVector> gen = new MarkovGenerator<ObservationVector>(hmm1);
    		List<ObservationVector> oseq = gen.observationSequence(sequencesLength);
    		int[] states = hmm1.mostLikelyStateSequence(oseq);
            double da = probability(hmm1,oseq,states);
            double db = probability(hmm2,oseq,states);
            distance += da - db;
        }
        return distance / (double)(nbSequences * sequencesLength);
    }
	private double probability(Hmm<ObservationVector> hmm,List<ObservationVector> oseq,int[] states){
		double prob = 0.0;
		double epsilon = 0.0000001;
		for (int i = 0;i < states.length;i++){
			if (i == 0){
				prob += Math.log(hmm.getPi(states[i])+epsilon) + Math.log(hmm.getOpdf(states[i]).probability(oseq.get(i))+epsilon);
			}
			else{
				prob += Math.log(hmm.getAij(states[i-1],states[i])+epsilon) + Math.log(hmm.getOpdf(states[i]).probability(oseq.get(i))+epsilon);
				//System.out.println("value\t"+Math.log(hmm.getAij(states[i-1],states[i])+epsilon)+"\t"+Math.log(hmm.getOpdf(states[i]).probability(oseq.get(i))+epsilon));
			}
		}
		return prob;
	}
}

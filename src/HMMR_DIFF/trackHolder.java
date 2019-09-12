package HMMR_DIFF;

import java.util.ArrayList;
import java.util.HashMap;

import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import Node.TagNode;

public class trackHolder {
	
	private ArrayList<TagNode> states;
	private ArrayList<TagNode> Short;
	private ArrayList<TagNode> Mono;
	private ArrayList<TagNode> Di;
	private ArrayList<TagNode> Tri;
	private TagNode node;
	
	private int[] annotations;
	private ArrayList<ObservationVector> list;
	
	public trackHolder(ArrayList<TagNode> s,ArrayList<TagNode> sh,ArrayList<TagNode> m,
			ArrayList<TagNode> d,ArrayList<TagNode> t,TagNode n){
		states = s;
		Short = sh;
		Mono = m;
		Di = d;
		Tri = t;
		node = n;
		setData();
	}
	
	public void setData(){
		annotations = new int[node.getLength()];
		list = new ArrayList<ObservationVector>();
		
		int bedStart = node.getStart();
		int bedStop = node.getStop();
		
		HashMap<Integer,double[]> pileup = new HashMap<Integer,double[]>();
		HashMap<Integer,Integer> st = new HashMap<Integer,Integer>();
		for (int a = bedStart;a < bedStop;a++){
			if (!pileup.containsKey(a)){
				double[] t = new double[4];
				pileup.put(a, t);
				st.put(a, 0);
			}
			
		}
		//update short track
		for (int i = 0;i < Short.size();i++){
			int start = Short.get(i).getStart();
			int stop = Short.get(i).getStop();
			double value = Short.get(i).getScore2();
			for (int x = start; x < stop;x++){
				if(pileup.containsKey(x)){
					double[] t = pileup.get(x);
					t[0] = value;
					pileup.put(x, t);
					
				}
			}
		}
		//update mono track
		for (int i = 0;i < Mono.size();i++){
			int start = Mono.get(i).getStart();
			int stop = Mono.get(i).getStop();
			double value = Mono.get(i).getScore2();
			for (int x = start; x < stop;x++){
				if(pileup.containsKey(x)){
					double[] t = pileup.get(x);
					t[1] = value;
					pileup.put(x, t);
					
				}
			}
		}
	
		//update di track
		for (int i = 0;i < Di.size();i++){
			int start = Di.get(i).getStart();
			int stop = Di.get(i).getStop();
			double value = Di.get(i).getScore2();
			for (int x = start; x < stop;x++){
				if(pileup.containsKey(x)){
					double[] t = pileup.get(x);
					t[2] = value;
					pileup.put(x, t);
					
				}
			}
		}
		
		//update tri track 
		for (int i = 0;i < Tri.size();i++){
			int start = Tri.get(i).getStart();
			int stop = Tri.get(i).getStop();
			double value = Tri.get(i).getScore2();
			for (int x = start; x < stop;x++){
				if(pileup.containsKey(x)){
					double[] t = pileup.get(x);
					t[3] = value;
					pileup.put(x, t);
					
				}
			}
		}
		//update state annotation track
		for (int i = 0;i < states.size();i++){
			int start = states.get(i).getStart();
			int stop = states.get(i).getStop();
			int value = states.get(i).getScore();
			for (int x = start; x < stop;x++){
				if(pileup.containsKey(x)){
					int t = st.get(x);
					t = value;
					st.put(x, t);
					
				}
			}
		}
		
		for (int x = bedStart;x < bedStop;x++){
			
			ObservationVector vec = new ObservationVector(pileup.get(x));
			list.add(vec);
			annotations[x - bedStart] = st.get(x);
		}
		
	}//method
	
	
	public int[] getAnnotations(){return annotations;}
	public ArrayList<ObservationVector> getObservations(){return list;}

}

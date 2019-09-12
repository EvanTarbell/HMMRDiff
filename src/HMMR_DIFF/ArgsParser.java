package HMMR_DIFF;

public class ArgsParser {
	
	private String[] args;
	
	private String _samples=null;
	private String _labels=null;
	private String _peaks=null;
	private int _ext=0;
	
	public ArgsParser(String[] a){
		args = a;
		
		set();
	}
	
	public String getSamples(){return _samples;}
	public String getLabels(){return _labels;}
	public String getPeaks(){return _peaks;}
	public int getExtension(){return _ext;}
	
	private void set(){
		for (int i = 0; i < args.length; i++) {

			switch (args[i].charAt(0)){
				case'-':
			
			
					switch (args[i].charAt((1))) {
			
			
					case's':
						_samples = (args[i+1]);
						i++;
						break;
					case'l':
						_labels =  (args[i+1]);
						i++;
						break;
		
					case'p':
						_peaks = args[i+1];
						i++;
						break;
					case'e':
						_ext = Integer.parseInt(args[i+1]);
						i++;
						break;
			
					case'h':
						printUsage();
				//System.exit(0);
					case'-':
						switch(args[i].substring(2)){
							case "samples":
								_samples =  (args[i+1]);
								i++;
								break;
							case"labels":
								_labels = (args[i+1]);
								i++;
								break;
							case"peaks":
								_peaks=args[i+1];
								i++;
								break;
							case"extension":
								_ext = Integer.parseInt(args[i+1]);
								i++;
								break;
				
							case"help":
								printUsage();
					//System.exit(0);
						}
					}
			}
		}//for loop
	}
	
	public void printUsage(){
		System.out.println("HMMRATAC_Diff Version:"+"\t1.0");
		System.out.println("Usage: java -jar HMMRATAC_Diff_V#_exe.jar");
		System.out.println("\nRequired Parameters:");
		System.out.println("\t-1 , --first <String> Prefix of first HMMRATAC run. Note: all HMMRATAC output files must be in working directory");
		System.out.println("\t-2 , --second <String> Prefix of first HMMRATAC run. Note: all HMMRATAC output files must be in working directory");
	}
}

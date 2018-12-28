package HMMR_DIFF;

public class ArgsParser {
	
	private String[] args;
	
	private String Prefix1;
	private String Prefix2;
	
	public ArgsParser(String[] a){
		args = a;
		
		set();
	}
	
	public String getPrefix1(){return Prefix1;}
	public String getPrefix2(){return Prefix2;}
	
	private void set(){
		for (int i = 0; i < args.length; i++) {

			switch (args[i].charAt(0)){
				case'-':
			
			
					switch (args[i].charAt((1))) {
			
			
					case'1':
						Prefix1 = (args[i+1]);
						i++;
						break;
					case'2':
						Prefix2 =  (args[i+1]);
						i++;
						break;
			
			
					case'h':
						printUsage();
				//System.exit(0);
					case'-':
						switch(args[i].substring(2)){
							case "first":
								Prefix1 =  (args[i+1]);
								i++;
								break;
							case"second":
								Prefix2 = (args[i+1]);
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

package evolver;

import java.util.StringTokenizer;

import jNeatCommon.FolderConstant;
import jNeatCommon.IOseq;
import jneat.Genome;
import jneat.NNode;
import jneat.Network;
import jneat.Organism;

public class XORTEster {

	public static void main(String[] args) {
		XORTEster tester = new XORTEster();
		String testGenomeFileName ="D:\\Users\\Simon\\Documents\\MarioFun\\jneat\\data\\Winners\\xor_win0";
		tester.run(testGenomeFileName);
	}
	
	private void run(String testGenomeFileName){
		//Open the file with the genome data
		IOseq starterGenomeFile = new IOseq(testGenomeFileName);
		boolean ret = starterGenomeFile.IOseqOpenR();
				
		if (ret){
			//Create starter genome
			Genome testGenome = createGenome(starterGenomeFile);
			testGenome(testGenome);			
		} else{
			System.out.println("Error during opening of " + testGenomeFileName);
		}
	}
		
	private Genome createGenome (IOseq starterGenomeFile){
		String curWord;
		
		System.out.println("Read start genome");
			
		//Read file
		String line = starterGenomeFile.IOseqRead();
		StringTokenizer st = new StringTokenizer(line);
			
		//Skip first word in file
		curWord = st.nextToken();
		
		//Read ID of the genome
		curWord=st.nextToken();
		int id = Integer.parseInt(curWord);
			
		//Create the genome
		System.out.println("Create genome id " + id);
		Genome startGenome = new Genome (id,starterGenomeFile);
			
		//Backup initial genome
		//Probably used for debugging
		startGenome.print_to_filename(FolderConstant.DATA_FOLDER + "\\starterGenome.read");
			
		return startGenome;
			
	}

	private void testGenome(Genome g){
		Organism organism = new Organism(0, g, 1);
		Network _net = organism.net;
		boolean success;
		int net_depth = 0; //The max depth of the network to be activated
	 	  
		 //The four possible input combinations to xor
		 //The first number is for biasing
	  
		 double in[][] = {{1.0, 0.0, 0.0}, {1.0, 0.0, 1.0}, {1.0, 1.0, 0.0}, {1.0, 1.0, 1.0}};
		 int numberOfTrainingSets = in.length;
		 
		 double[] out = new double[numberOfTrainingSets]; //Stores the output from the training sets
		 _net = organism.net;
	  
		 net_depth = _net.max_depth();
	  
		
		 // for each training set, propagate signal .... and compute results
		 for (int trainingSet = 0; trainingSet < numberOfTrainingSets; trainingSet++){
			 //propagate input signal forward
			 success = propagateSignal(_net, net_depth, in[trainingSet] );
			 		 
			//Read the output value
			out[trainingSet] = ((NNode) _net.getOutputs().firstElement()).getActivation();
			_net.flush();
		 }
		 
		 printResults(in, out);
	}
	
	private void printResults(double in[][], double[] out){
		for (int i = 0; i < in.length; i++){
			System.out.print("inputs: ");
			for (int j = 0; j< in[i].length; j++){
				System.out.print(in[i][j] + " ");
			}
			System.out.println();
			System.out.println("Output: " + out[i]);
		}
	}
	
	private boolean propagateSignal(Network net, int net_depth, double[] inputValues){
		boolean success = false;
		 // first activation from sensor to first level of neurons
		 net.load_sensors(inputValues);
		 success = net.activate();
	 
		 // next activation until last level is reached !
		 // use depth to ensure relaxation
	 
		for (int relax = 0; relax <= net_depth; relax++){
			success = net.activate();
		}
		
		return success;
	 }
}

package evolver;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.StringTokenizer;

import jNeatCommon.FolderConstant;
import jNeatCommon.IOseq;
import jNeatCommon.NeatConstant;
import jneat.Evolution;
import jneat.Genome;
import jneat.NNode;
import jneat.Neat;
import jneat.Network;
import jneat.Organism;
import jneat.Population;
import jneat.Species;

public class XorEvolver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		XorEvolver evolver = new XorEvolver();
		evolver.evolveXOR();

	}
	
	private void evolveXOR(){
		
		//Initialise the neat class
		Neat.initbase();
		
		//Import the parameters to be used by NEAT
		String parameterFileName = FolderConstant.DATA_FOLDER + "\\parameters";
		boolean rc = Neat.readParam(parameterFileName);
		
		//Test if parameter read was successful
		if (rc){
			System.out.println("Parameter read okay");
		} else{
			System.out.println("Error in parameter read");
		}
		
		//Save imported parameters to new file
		//Can be used when debugging
		String debugParameterFileName = FolderConstant.DATA_FOLDER + "\\parameters.imported";
		Neat.writeParam(debugParameterFileName);
		
		//Run experiments
		System.out.println("Start XOR experiment");
		String genomeFileName = FolderConstant.DATA_FOLDER + "\\starterGenomeXOR";
		int numberOfGenerations = 100;
		experiment(genomeFileName, numberOfGenerations);
		
	}
	
	/**
	 * Starts the experiment
	 * @param starterGenomeFileName
	 * @param generations
	 */
	private void experiment (String starterGenomeFileName, int generations){
		
		//Open the file with the starter genome data
		IOseq starterGenomeFile = new IOseq(starterGenomeFileName);
		boolean ret = starterGenomeFile.IOseqOpenR();
		
		if (ret){
			//Create starter genome
			Genome starterGenome = createGenome(starterGenomeFile);
			
			//Start experiments
			for (int expCount = 0; expCount < Neat.p_num_runs; expCount++){
				runExperiment(starterGenome, generations);
			}
			
		} else{
			System.out.println("Error during opening of " + starterGenomeFileName);
		}
		
		starterGenomeFile.IOseqCloseR();
	}
	/**
	 * Reads a file and creates a genome based on the data in that file
	 * @param starterGenomeFile
	 * @return
	 */
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
	/**
	 * Runs an experiment where populations are evolved from a basic genome
	 * @param starterGenome
	 * @param generations
	 */
	private void runExperiment(Genome starterGenome, int generations){
		String mask6 = "000000";
		DecimalFormat fmt6 = new DecimalFormat(mask6);
		
		//Create population
		System.out.println("Spawning population from starter genome");
		Population pop = new Population(starterGenome, Neat.p_pop_size);
		
		//Verify population
		System.out.println("Verifying spawned population");
		pop.verify();
		
		//Run experiment
		System.out.println("Starting evolution");
		for (int gen = 1; gen <= generations; gen++){
			System.out.print("\n---------------- E P O C H  < " + gen+" >--------------");
			
			String filenameEpochInfo = "g_" + fmt6.format(gen);
			boolean status = goThroughEpoch(pop, gen, filenameEpochInfo);
			
			//Break out if a good enough organism has been found
			if (status){
				break;
			}
		}
		
		//Prints information about the last generation 
		System.out.print("\n  Population : innov num   = " + pop.getCur_innov_num()); //Prints the current number of innovations
		System.out.print("\n             : cur_node_id = " + pop.getCur_node_id());  //Current number of nodes (??)
		
		//Writes population info to file for the last population 
		String populationInfoFileName = FolderConstant.DATA_FOLDER + "\\population.LastGeneration";
		pop.print_to_filename(populationInfoFileName);
	}
	
	/**
	 * Evolves a new generation for the population
	 * @param pop
	 * @param generation
	 * @param filenameEpochInfo
	 * @return True if a winner has been found in the population. False otherwise
	 */
	private boolean goThroughEpoch(Population pop, int generation, String filenameEpochInfo){
		boolean status = false;
		
		//Evaluate each organism to see if it is a winner
		boolean win = false;
		
		Iterator itr_organism;
		itr_organism = pop.organisms.iterator();
		
		while (itr_organism.hasNext()){
			//point to organism
			Organism curOrganism = ((Organism) itr_organism.next());
			//evaluate 
			status = xor_evaluate(curOrganism);
			// if is a winner , store a flag
			if (status){
				win = true;
			}
		 }
		
		//compute average and max fitness for each species
		Iterator itr_specie;
		itr_specie = pop.species.iterator();
		while (itr_specie.hasNext()) {
			Species curSpecie = ((Species) itr_specie.next());
			curSpecie.compute_average_fitness();
			curSpecie.compute_max_fitness();
		}
		 
		// Only print to file every print_every generations
		if (win || (generation % Neat.p_print_every) == 0){
			pop.print_to_file_by_species(FolderConstant.DATA_FOLDER + "\\Generation Info\\" + filenameEpochInfo);
		}
		  
		// if a winner exist, write to file	   
		if (win) {
			int cnt = 0;
			itr_organism = pop.getOrganisms().iterator();
			while (itr_organism.hasNext()) {
				Organism _organism = ((Organism) itr_organism.next());
				if (_organism.winner){
					System.out.print("\n   -WINNER IS #" + _organism.genome.genome_id);
					_organism.getGenome().print_to_filename(FolderConstant.DATA_FOLDER + "\\Winners\\xor_win" + cnt);
					cnt++;
				}
			}
		}
		
		// wait an epoch and make a reproduction of the best species
		pop.epoch(generation);
		if (win){
			System.out.print("\t\t** I HAVE FOUND A CHAMPION **");
			return true;
		} else { 
			return false;
		}		
	}
	
	/**
	 * Evaluates an organism and returns true if the organism has found and answer
	 * @param organism
	 * @return
	 */
	 private boolean xor_evaluate(Organism organism){
		 Network _net = null;
		 boolean success = false;
		 double errorsum = 0.0;
		 
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
	  
	  
		 // control the result 
		 if (success) {
			errorsum = (double) (Math.abs(out[0])+ Math.abs(1.0 - out[1])+ Math.abs(1.0 - out[2])+ Math.abs(out[3])); 
			organism.setFitness(Math.pow((4.0 - errorsum), 2));
			organism.setError(errorsum);
		 } else {
			errorsum = 999.0;
			organism.setFitness(0.001);
			organism.setError(errorsum);
		 }
		 
		 if ((out[0] < 0.5) && (out[1] >= 0.5) && (out[2] >= 0.5) && (out[3] < 0.5)) {
			organism.setWinner(true);
			return true;
		 } else {
			organism.setWinner(false);
			return false;
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

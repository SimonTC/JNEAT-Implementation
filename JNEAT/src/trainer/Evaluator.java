package trainer;

import jneat.NNode;
import jneat.Network;
import jneat.Organism;

public class Evaluator {
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

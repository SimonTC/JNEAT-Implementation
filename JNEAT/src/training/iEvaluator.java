package training;

import jneat.Organism;

public interface iEvaluator {
	/**
	 * Evaluates an organism and returns true if the organism has found and answer
	 * @param organism
	 * @return
	 */
	 public boolean evaluate(Organism organism);
}

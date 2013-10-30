package evolver;

import jNeatCommon.NeatConstant;
import jneat.Evolution;

public class XorEvolver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private void evolveXOR(){
		Evolution evo = new Evolution();
		
		evo.Experiment6(50, NeatConstant.COLD, 100, "C:\\Users\\Simon\\Documents\\MarioFun\\population.primitive");
	}

}

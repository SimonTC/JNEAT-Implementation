package jneat.evolution;

import java.util.Iterator;
import java.util.Vector;

import jneat.Neat;

public class Speciator extends Neat {
	/**
	 * Takes a vector of organisms and speciates them based on the species in the population
	 * @param organisms
	 * @param pop
	 * @return the highest species number. Should be saved in pop.last_species
	 */
	public Integer speciate (Vector<Organism> organisms, Population pop){
		Iterator<Organism> itr_organism;
		Iterator<Species> itr_specie;
		Vector<Species> species;
		
		species = pop.species;
		if (species == null){
			species = new Vector<>(1);
		}
		 
		Organism compare_org = null; // Organism for comparison
		Species newspecies = null;

		int counter = pop.last_species; // Species counter
		
		boolean speciationOfSpawn = counter > 0; //Species created at the spawn of a new population are not seen as novel. (Used when aging species)
										

		// for each organism.....
		itr_organism = organisms.iterator();
		while (itr_organism.hasNext()) {
			Organism _organism = ((Organism) itr_organism.next());

			if (species.isEmpty()) {
				// if list species is empty , create the first species!
				newspecies = new Species(++counter, speciationOfSpawn);
				species.add(newspecies); // add this species to list of species
				newspecies.add_Organism(_organism);
				// Point organism to its species
				_organism.setSpecies(newspecies); 

			} else {
				// looop in all species.... (each species is a Vector of
				// organism...)
				itr_specie = species.iterator();
				boolean done = false;

				while (!done && itr_specie.hasNext()) {

					// point _species
					Species _specie = ((Species) itr_specie.next());

					// point to first organism of this _specie
					compare_org = (Organism) _specie.getOrganisms()
							.firstElement();
					// compare _organism with first organism
					// in current specie('compare_org')
					double curr_compat = _organism.getGenome().compatibility(
							compare_org.getGenome());

					if (curr_compat < Neat.p_compat_threshold) {
						// Found compatible species, so add this organism to it
						_specie.add_Organism(_organism);
						// update in organism pointer to its species
						_organism.setSpecies(_specie);
						// force exit from this block ...
						done = true;
					}
				}

				// if no found species compatible , create specie
				if (!done) {
					newspecies = new Species(++counter, speciationOfSpawn); // create a new specie
					species.add(newspecies); // add this species to list of
												// species
					newspecies.add_Organism(_organism);
					// Add to new species the current organism
					_organism.setSpecies(newspecies); // Point organism to its
														// species

				}

			}

		}
		pop.setSpecies(species);
		return counter; // Keep track of highest species
	}
}

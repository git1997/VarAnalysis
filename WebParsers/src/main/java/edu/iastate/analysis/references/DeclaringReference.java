package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public abstract class DeclaringReference extends Reference {

	public DeclaringReference(String name, PositionRange location) {
		super(name, location);
	}
	
}

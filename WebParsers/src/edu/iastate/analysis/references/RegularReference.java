package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * A RegularReference is a non-declaring reference.
 * 
 * @author HUNG
 *
 */
public abstract class RegularReference extends Reference {
	
	public RegularReference(String name, PositionRange location) {
		super(name, location);
	}
	
	/**
	 * Returns true if a regularReference refers to a declaringReference (belonging to the same entity).
	 * Subclasses of RegularReference may add more conditions to determine whether this is true.
	 */
	public abstract boolean sameEntityAs(DeclaringReference declaringReference);

}

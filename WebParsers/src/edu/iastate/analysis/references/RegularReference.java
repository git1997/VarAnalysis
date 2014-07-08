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
	 * Returns true if this regularReference refers to a declaringReference.
	 * @param declaringReference
	 */
	public abstract boolean refersTo(DeclaringReference declaringReference);

}

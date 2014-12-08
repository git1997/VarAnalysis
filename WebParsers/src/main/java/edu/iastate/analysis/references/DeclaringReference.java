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
	
	/**
	 * Returns true if two declaringReferences belong to the same entity.
	 * Subclasses of DeclaringReference may add more conditions to determine whether this is true.
	 */
	public boolean sameEntityAs(DeclaringReference declaringReference) {
		return hasSameType(declaringReference) && hasSameName(declaringReference);
	}
	
}

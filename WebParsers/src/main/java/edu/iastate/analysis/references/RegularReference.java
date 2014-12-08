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
	 * Returns true if two regularReferences belong to the same entity.
	 * Subclasses of RegularReference may add more conditions to determine whether this is true.
	 */
	public boolean sameEntityAs(RegularReference regularReference) {
		return hasSameType(regularReference) && hasSameName(regularReference);
	}
	
	/**
	 * Returns true if a regularReference refers to a declaringReference (belonging to the same entity).
	 * Subclasses of RegularReference may add more conditions to determine whether this is true.
	 */
	public boolean sameEntityAs(DeclaringReference declaringReference) {
		return hasMatchedType(declaringReference) && hasSameName(declaringReference);
	}
	
	/**
	 * Returns true if the type of a regularReference is compatible with the type of a declaringReference
	 * (e.g., JsVariableRef is matched with JsVariableDecl).
	 */
	public abstract boolean hasMatchedType(DeclaringReference declaringReference);

}

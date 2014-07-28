package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class PhpFunctionCall extends RegularReference {

	public PhpFunctionCall(String name, PositionRange location) {
		super(name, location);
	}

	@Override
	public boolean hasMatchedType(DeclaringReference declaringReference) {
		return declaringReference instanceof PhpFunctionDecl;
	}
	
}

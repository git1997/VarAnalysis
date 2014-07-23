package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class JsFunctionCall extends RegularReference {

	public JsFunctionCall(String name, PositionRange location) {
		super(name, location);
	}

	@Override
	public boolean hasMatchedType(DeclaringReference declaringReference) {
		return declaringReference instanceof JsFunctionDecl;
	}
	
}

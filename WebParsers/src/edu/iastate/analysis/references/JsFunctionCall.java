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
	public boolean refersTo(DeclaringReference declaringReference) {
		if (declaringReference instanceof JsFunctionDecl) {
			JsFunctionDecl jsFunctionDecl = (JsFunctionDecl) declaringReference;
			return getName().equals(jsFunctionDecl.getName());
		}
		else
			return false;
	}

}

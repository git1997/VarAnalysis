package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class JsVariableRef extends RegularReference {

	public JsVariableRef(String name, PositionRange location) {
		super(name, location);
	}

	@Override
	public boolean sameEntityAs(DeclaringReference declaringReference) {
		return declaringReference instanceof JsVariableDecl
				&& hasSameName(declaringReference);
	}

}

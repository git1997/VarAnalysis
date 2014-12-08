package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class JsVariableDecl extends DeclaringReference {

	public JsVariableDecl(String name, PositionRange location) {
		super(name, location);
	}

}

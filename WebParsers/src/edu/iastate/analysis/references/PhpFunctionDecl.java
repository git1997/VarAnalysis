package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class PhpFunctionDecl extends DeclaringReference {

	public PhpFunctionDecl(String name, PositionRange location) {
		super(name, location);
	}

}

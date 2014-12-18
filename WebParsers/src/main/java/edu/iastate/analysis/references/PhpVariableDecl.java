package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class PhpVariableDecl extends DeclaringReference {

	/**
	 * Constructor
	 */
	public PhpVariableDecl(String name, PositionRange location) {
		super(name, location);
	}

}

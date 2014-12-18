package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class PhpVariableRef extends RegularReference {
	
	/**
	 * Constructor
	 */
	public PhpVariableRef(String name, PositionRange location) {
		super(name, location);
	}
	
}

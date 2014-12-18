package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class PhpRefToSqlTableColumn extends RegularReference {

	/**
	 * Constructor
	 */
	public PhpRefToSqlTableColumn(String name, PositionRange location) {
		super(name, location);
	}
	
}

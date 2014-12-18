package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class SqlTableColumnDecl extends DeclaringReference {
	
	/**
	 * Constructor
	 */
	public SqlTableColumnDecl(String name, PositionRange location) {
		super(name, location);
	}
	
}

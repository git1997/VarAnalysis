package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class SqlTableColumnDecl extends DeclaringReference {

	private String scope;	// The scope of this SqlTableColumnDecl (e.g. 'mysql_query_123456')
							// @see php.nodes.FunctionInvocationNode.php_mysql_query(ArrayList<DataNode>, ElementManager, Object)).
	
	/**
	 * Constructor
	 */
	public SqlTableColumnDecl(String name, PositionRange location, String scope) {
		super(name, location);
		this.scope = scope;
	}
	
	/*
	 * Get properties
	 */
	
	/**
	 * Returns the scope of this SqlTableColumnDecl.
	 */
	public String getScope() {
		return scope;
	}
	
	/*
	 * (non-Javadoc)
	 * @see references.Reference#sameAs(references.Reference)
	 */
	@Override
	public boolean sameAs(Reference reference) {
		return super.sameAs(reference)
				&& (getScope().equals(((SqlTableColumnDecl) reference).getScope()));
	}
	
}

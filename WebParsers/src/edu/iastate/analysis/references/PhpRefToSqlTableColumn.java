package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class PhpRefToSqlTableColumn extends RegularReference {

	private String scope;	// The scope of this PhpRefToSqlTableColumn (e.g. 'mysql_query_123456')
							// @see php.nodes.FunctionInvocationNode.php_mysql_query(ArrayList<DataNode>, ElementManager, Object)).
	
	/**
	 * Constructor
	 */
	public PhpRefToSqlTableColumn(String name, PositionRange location, String scope) {
		super(name, location);
		this.scope = scope;
	}
	
	/*
	 * Get properties
	 */
	
	/**
	 * Returns the scope of this PhpRefToSqlTableColumn.
	 */
	public String getScope() {
		return scope;
	}

	/*
	 * (non-Javadoc)
	 * @see references.RegularReference#refersTo(references.DeclaringReference)
	 */
	@Override
	public boolean refersTo(DeclaringReference declaringReference) {
		if (declaringReference instanceof SqlTableColumnDecl) {
			SqlTableColumnDecl sqlTableColumnDecl = (SqlTableColumnDecl) declaringReference;
			return getName().equals(sqlTableColumnDecl.getName())
					&& getScope().equals(sqlTableColumnDecl.getScope());
		}
		else
			return false;
	}
	
}

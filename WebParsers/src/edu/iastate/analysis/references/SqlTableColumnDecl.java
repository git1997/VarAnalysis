package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class SqlTableColumnDecl extends DeclaringReference {
	
	private String scope;	// The scope of this SqlTableColumnDecl (e.g. 'mysql_query_123456')
							// @see edu.iastate.analysis.references.detection.PhpVisitor.onMysqlQuery(FunctionInvocation, DataNode, Env)
							// @see edu.iastate.analysis.references.detection.PhpVisitor.createPhpRefToSqlTableColumn(ArrayAccess, Env)
	
	/**
	 * Constructor
	 */
	public SqlTableColumnDecl(String name, PositionRange location, String scope) {
		super(name, location);
		this.scope = scope;
	}
	
	public String getScope() {
		return scope;
	}

	@Override
	public boolean sameEntityAs(DeclaringReference declaringReference) {
		return super.sameEntityAs(declaringReference)
				&& (getScope().equals(((SqlTableColumnDecl) declaringReference).getScope()));
	}
	
}

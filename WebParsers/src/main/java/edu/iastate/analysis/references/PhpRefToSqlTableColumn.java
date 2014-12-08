package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class PhpRefToSqlTableColumn extends RegularReference {

	private String scope;	// The scope of this PhpRefToSqlTableColumn (e.g. 'mysql_query_123456')
							// @see edu.iastate.analysis.references.detection.PhpVisitor.onMysqlQuery(FunctionInvocation, DataNode, Env)
							// @see edu.iastate.analysis.references.detection.PhpVisitor.createPhpRefToSqlTableColumn(ArrayAccess, Env)
	
	/**
	 * Constructor
	 */
	public PhpRefToSqlTableColumn(String name, PositionRange location, String scope) {
		super(name, location);
		this.scope = scope;
	}
	
	public String getScope() {
		return scope;
	}
	
	@Override
	public boolean sameEntityAs(RegularReference regularReference) {
		return super.sameEntityAs(regularReference)
				&& getScope().equals(((PhpRefToSqlTableColumn) regularReference).getScope());
	}
	
	@Override
	public boolean sameEntityAs(DeclaringReference declaringReference) {
		return super.sameEntityAs(declaringReference)
				&& getScope().equals(((SqlTableColumnDecl) declaringReference).getScope());
	}

	@Override
	public boolean hasMatchedType(DeclaringReference declaringReference) {
		return declaringReference instanceof SqlTableColumnDecl;
	}
	
}

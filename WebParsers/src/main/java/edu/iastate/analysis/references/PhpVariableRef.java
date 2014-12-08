package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class PhpVariableRef extends RegularReference {
	
	private String scope;	// The scope of this PhpVariableRef (e.g. 'FUNCTION_SCOPE_hello')
							// @see edu.iastate.analysis.references.detection.PhpVisitor.createVariable(Variable, Env, boolean)

	/**
	 * Constructor
	 */
	public PhpVariableRef(String name, PositionRange location, String scope) {
		super(name, location);
		this.scope = scope;
	}
	
	public String getScope() {
		return scope;
	}
	
	@Override
	public boolean sameEntityAs(RegularReference regularReference) {
		return super.sameEntityAs(regularReference)
				&& getScope().equals(((PhpVariableRef) regularReference).getScope());
	}
	
	@Override
	public boolean sameEntityAs(DeclaringReference declaringReference) {
		return super.sameEntityAs(declaringReference)
				&& getScope().equals(((PhpVariableDecl) declaringReference).getScope());
	}
	
	@Override
	public boolean hasMatchedType(DeclaringReference declaringReference) {
		return declaringReference instanceof PhpVariableDecl;
	}
	
}

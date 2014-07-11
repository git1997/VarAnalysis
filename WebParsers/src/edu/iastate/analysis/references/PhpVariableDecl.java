package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class PhpVariableDecl extends DeclaringReference {

	private String scope;	// The scope of this PhpVariableDecl (e.g. 'FUNCTION_SCOPE_hello')
							// @see edu.iastate.analysis.references.detection.PhpVisitor.createVariable(Variable, Env, boolean)

	/**
	 * Constructor
	 */
	public PhpVariableDecl(String name, PositionRange location, String scope) {
		super(name, location);
		this.scope = scope;
	}

	public String getScope() {
		return scope;
	}
	
	@Override
	public boolean sameAs(Reference reference) {
		return super.sameAs(reference)
				&& (getScope().equals(((PhpVariableDecl) reference).getScope()));
	}
	
}

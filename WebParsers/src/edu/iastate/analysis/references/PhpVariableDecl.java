package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class PhpVariableDecl extends DeclaringReference {

	private String scope;	// The scope of this PhpVariableDecl (e.g. 'FUNCTION_SCOPE_hello')
							// @see php.nodes.VariableNode.variableDeclFound(ElementManager)

	/**
	 * Constructor
	 */
	public PhpVariableDecl(String name, PositionRange location, String scope) {
		super(name, location);
		this.scope = scope;
	}
	
	/*
	 * Get properties
	 */
	
	/**
	 * Returns the scope of this PhpVariableDecl.
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
				&& (getScope().equals(((PhpVariableDecl) reference).getScope()));
	}
	
}

package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class PhpVariableRef extends RegularReference {
	
	private String scope;	// The scope of this PhpVariableRef (e.g. 'FUNCTION_SCOPE_hello')
							// @see php.nodes.VariableNode.variableRefFound(ElementManager)

	/**
	 * Constructor
	 */
	public PhpVariableRef(String name, PositionRange location, String scope) {
		super(name, location);
		this.scope = scope;
	}
	
	/*
	 * Get properties
	 */
	
	/**
	 * Returns the scope of this PhpVariableRef.
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
		if (declaringReference instanceof PhpVariableDecl) {
			PhpVariableDecl variableDecl = (PhpVariableDecl) declaringReference;
			return getName().equals(variableDecl.getName())
					&& getScope().equals(variableDecl.getScope());
		}
		else
			return false;
	}

}

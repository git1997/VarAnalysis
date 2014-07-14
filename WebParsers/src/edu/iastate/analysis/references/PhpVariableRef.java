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
	public boolean refersTo(DeclaringReference declaringReference) {
		if (declaringReference instanceof PhpVariableDecl) {
			PhpVariableDecl variableDecl = (PhpVariableDecl) declaringReference;
			return getName().equals(variableDecl.getName())
					&& getScope().equals(variableDecl.getScope());
		}
		else
			return false;
	}
	
	@Override
	public boolean hasDataflowFromReference(Reference reference) {
		// TODO PhpVariableRef needs a different way of resolving dataflows
		return false;
	}

}

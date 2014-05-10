package edu.iastate.symex.core;

import edu.iastate.symex.constraints.Constraint;

/**
 * 
 * @author HUNG
 *
 */
public class BranchEnv extends Env {
	
	private Constraint constraint;
	
	/**
	 * Constructor
	 */
	public BranchEnv(Env outerScopeEnv, Constraint constraint) {
		super(outerScopeEnv);
		this.constraint = constraint;
	}
	
	public Constraint getConstraint() {
		return constraint;
	}

}

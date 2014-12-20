package edu.iastate.symex.core;

import java.util.HashSet;

/**
 * 
 * @author HUNG
 *
 */
public class FunctionEnv extends PhpEnv {
	
	private String functionName;
	
	/*
	 * This set is used to keep track of variables passed by reference
	 * (managed by the current scope but created from an outer scope).
	 */
	private HashSet<PhpVariable> referenceVariables = new HashSet<PhpVariable>();
	
	/**
	 * Constructor
	 */
	public FunctionEnv(Env outerScopeEnv, String functionName) {
		super(outerScopeEnv);
		this.functionName = functionName;
	}
	
	public String getFunctionName() {
		return functionName;
	}
	
	/**
	 * Records a parameter passed by reference.
	 * Called by FunctionInvocationNode.execute(Env, ObjectNode) only.
	 * @param phpVariable
	 */
	public void addReferenceVariable(PhpVariable phpVariable) {
		referenceVariables.add(phpVariable);
	}
	
	/**
	 * Returns the set of variables CREATED from the current scope.
	 * Protected method. Should be called by Env only.
	 * This method is different than PhpEnv.getVariablesFromCurrentScope(),
	 * 		which returns the set of variables MANAGED by the current scope (a superset of this set).
	 * @see edu.iastate.symex.core.PhpEnv.getVariablesFromCurrentScope()
	 */
	protected HashSet<PhpVariable> getVariablesCreatedFromCurrentScope() {
		HashSet<PhpVariable> variables = getVariablesFromCurrentScope();
		variables.removeAll(referenceVariables);
		return variables;
	}
	
}
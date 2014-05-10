package edu.iastate.symex.core;

/**
 * 
 * @author HUNG
 *
 */
public class FunctionEnv extends Env {
	
	private String functionName;
	
	/**
	 * Constructor
	 */
	public FunctionEnv(Env outerScopeenv, String functionName) {
		super(outerScopeenv);
		this.functionName = functionName;
	}
	
	public String getFunctionName() {
		return functionName;
	}
	
}
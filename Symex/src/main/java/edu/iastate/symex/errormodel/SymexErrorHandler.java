package edu.iastate.symex.errormodel;

/**
 * 
 * @author HUNG
 * Copied from Christian's code.
 * 
 */
public interface SymexErrorHandler {

	public void fatalError(SymexException exception);
	
	public void error(SymexException exception);

	public void warning(SymexException exception);
	
}
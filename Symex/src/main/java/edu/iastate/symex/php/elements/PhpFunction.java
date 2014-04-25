package edu.iastate.symex.php.elements;

import edu.iastate.symex.php.nodes.FunctionDeclarationNode;

/**
 * 
 * @author HUNG
 *
 */
public class PhpFunction extends PhpElement {
	
	private FunctionDeclarationNode functionDeclarationNode;
	
	/**
	 * Constructor
	 * @param functionDeclarationNode
	 */
	public PhpFunction(FunctionDeclarationNode functionDeclarationNode) {
		this.functionDeclarationNode = functionDeclarationNode;
	}
	
	/*
	 * Get properties
	 */
	
	public FunctionDeclarationNode getFunctionDeclarationNode() {
		return functionDeclarationNode;
	}
	
}

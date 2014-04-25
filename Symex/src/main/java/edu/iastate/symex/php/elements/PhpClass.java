package edu.iastate.symex.php.elements;

import edu.iastate.symex.php.nodes.ClassDeclarationNode;

/**
 * 
 * @author HUNG
 *
 */
public class PhpClass extends PhpElement {

	private ClassDeclarationNode classDeclarationNode;
	
	/**
	 * Constructor
	 * @param classDeclarationNode
	 */
	public PhpClass(ClassDeclarationNode classDeclarationNode) {
		this.classDeclarationNode = classDeclarationNode;
	}
	
	/*
	 * Get properties
	 */
	
	public ClassDeclarationNode getClassDeclarationNode() {
		return classDeclarationNode;
	}
	
}

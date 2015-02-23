package edu.iastate.symex.php.nodes;

import java.io.File;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;

import edu.iastate.symex.position.Range;
import edu.iastate.symex.util.ASTHelper;

/**
 * 
 * @author HUNG
 *
 */
public abstract class PhpNode {
	
	private ASTNode astNode;			// The original AST node
	
	private Range range;				// Its position in the source code
	
	private String sourceCode;			// The source code of the PhpNode
	
	/**
	 * Constructor
	 * @param astNode
	 */
	public PhpNode(ASTNode astNode) {
		File file = ASTHelper.inst.getPhpFileOfPhpASTNode(astNode);	
		
		this.astNode = astNode;
		this.range = new Range(file, astNode.getStart(), astNode.getLength());
		this.sourceCode = ASTHelper.inst.getSourceCodeOfPhpASTNode(astNode); // sourceCode.length is guaranteed to equal range.length
	}
	
	/**
	 * Returns the original ASTNode
	 */
	public ASTNode getAstNode() {
		return astNode;
	}
	
	/**
	 * Returns the position of the PhpNode in the source code.
	 * Note that the location may not be available (can be undefined).
	 */
	public Range getLocation() {
		return range;
	}
	
	/**
	 * Returns the source code of the PhpNode.
	 * Note that the source code may not be available (can be empty).
	 */
	public String getSourceCode() {
		return sourceCode;
	}
		
}

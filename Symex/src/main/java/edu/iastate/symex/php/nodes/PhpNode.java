package edu.iastate.symex.php.nodes;

import java.io.File;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;

import edu.iastate.symex.position.AtomicPositionRange;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.TraceTable;
import edu.iastate.symex.datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public abstract class PhpNode {
	
	private ASTNode astNode;					// The original AST node
	
	private AtomicPositionRange positionRange;	// Its position in the source code
	
	private String sourceCode;					// The source code of the PhpNode
	
	/**
	 * Constructor
	 * @param astNode
	 */
	public PhpNode(ASTNode astNode) {
		File file = TraceTable.getCurrentSourceFileRelativePathOfPhpASTNode(astNode);	
		
		this.astNode = astNode;
		this.positionRange = new AtomicPositionRange(file, astNode.getStart(), astNode.getEnd() - astNode.getStart());
		this.sourceCode = TraceTable.getSourceCodeOfPhpASTNode(astNode);
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
	public AtomicPositionRange getPositionRange() {
		return positionRange;
	}
	
	/**
	 * Returns the source code of the PhpNode.
	 * Note that the source code may not be available (can be empty).
	 */
	public String getSourceCode() {
		return sourceCode;
	}
		
	/**
	 * Executes the given PHP node and updates env along the way.
	 * @param env contains PHP elements such as variables and functions during the execution.
	 * @return A data node describing the returned value.
	 */
	public abstract DataNode execute(Env env);
	
}

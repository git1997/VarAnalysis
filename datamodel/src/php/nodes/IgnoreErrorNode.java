package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.IgnoreError;

import php.ElementManager;


import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class IgnoreErrorNode extends ExpressionNode {

	private ExpressionNode expressionNode;
	
	/*
	Represents ignore error expression 

	e.g. '@$a->foo()' 
	*/
	public IgnoreErrorNode(IgnoreError ignoreError) {
		super(ignoreError);
		expressionNode = ExpressionNode.createInstance(ignoreError.getExpression());
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		return expressionNode.execute(elementManager);
	}

}

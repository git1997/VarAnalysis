package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ParenthesisExpression;

import php.ElementManager;


import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class ParenthesisExpressionNode extends ExpressionNode {

	private ExpressionNode expressionNode;
	
	/*
	Represents a parenthesis expression 

	e.g. ( $a == 2 );
	 echo ($a);
	*/
	public ParenthesisExpressionNode(ParenthesisExpression parenthesisExpression) {
		super(parenthesisExpression);
		expressionNode = ExpressionNode.createInstance(parenthesisExpression.getExpression());
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

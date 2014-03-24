package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.CastExpression;

import php.ElementManager;


import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class CastExpressionNode extends ExpressionNode {
	
	private ExpressionNode expressionNode;
	
	/*
	Represents a type casting expression 

	e.g. (int) $a,
	 (string) $b->foo()
	*/
	public CastExpressionNode(CastExpression castExpression) {
		super(castExpression);
		expressionNode = ExpressionNode.createInstance(castExpression.getExpression());
	}

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		// The cast operator unset is currently not handled correctly, e.g. $t = (unset) $t, meaning $t = null.
		return expressionNode.execute(elementManager);
	}

}

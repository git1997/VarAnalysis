package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ParenthesisExpression;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class ParenthesisExpressionNode extends ExpressionNode {

	private ExpressionNode expression;
	
	/*
	Represents a parenthesis expression 

	e.g. ( $a == 2 );
	 echo ($a);
	*/
	public ParenthesisExpressionNode(ParenthesisExpression parenthesisExpression) {
		super(parenthesisExpression);
		expression = ExpressionNode.createInstance(parenthesisExpression.getExpression());
	}
	
	@Override
	public DataNode execute(Env env) {
		return expression.execute(env);
	}

}

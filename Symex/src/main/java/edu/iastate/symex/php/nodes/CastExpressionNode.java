package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.CastExpression;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class CastExpressionNode extends ExpressionNode {
	
	private ExpressionNode expression;
	
	/*
	Represents a type casting expression 

	e.g. (int) $a,
	 (string) $b->foo()
	*/
	public CastExpressionNode(CastExpression castExpression) {
		super(castExpression);
		expression = ExpressionNode.createInstance(castExpression.getExpression());
	}

	@Override
	public DataNode execute(Env env) {
		// TODO The cast operator unset is currently not handled correctly, e.g. $t = (unset) $t, meaning $t = null.
		return expression.execute(env);
	}

}

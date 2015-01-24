package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ConditionalExpression;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class ConditionalExpressionNode extends ExpressionNode {

	private ExpressionNode condition;
	private ExpressionNode ifTrue;
	private ExpressionNode ifFalse;
	
	/*
	Represents conditional expression Holds the condition, if true expression and if false expression each on e can be any expression 

	e.g. (bool) $a ? 3 : 4
	 $a > 0 ? $a : -$a
	*/
	public ConditionalExpressionNode(ConditionalExpression conditionalExpression) {
		super(conditionalExpression);
		this.condition = ExpressionNode.createInstance(conditionalExpression.getCondition());
		this.ifTrue = ExpressionNode.createInstance(conditionalExpression.getIfTrue());
		this.ifFalse = ExpressionNode.createInstance(conditionalExpression.getIfFalse());
	}
	
	@Override
	public DataNode execute(Env env) {
		return IfStatementNode.execute(env, condition, ifTrue, ifFalse, false);
	}

}

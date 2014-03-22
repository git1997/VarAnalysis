package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ConditionalExpression;

import php.ElementManager;

import datamodel.nodes.DataNode;
import datamodel.nodes.LiteralNode;

/**
 * 
 * @author HUNG
 *
 */
public class ConditionalExpressionNode extends ExpressionNode {

	private LiteralNode conditionString;
	private ExpressionNode conditionExpressionNode;
	private ExpressionNode ifTrueExpressionNode;
	private ExpressionNode ifFalseExpressionNode;
	
	/*
	Represents conditional expression Holds the condition, if true expression and if false expression each on e can be any expression 

	e.g. (bool) $a ? 3 : 4
	 $a > 0 ? $a : -$a
	*/
	public ConditionalExpressionNode(ConditionalExpression conditionalExpression) {
		super(conditionalExpression);
		this.conditionString = new LiteralNode(conditionalExpression.getCondition());
		this.conditionExpressionNode = ExpressionNode.createInstance(conditionalExpression.getCondition());
		this.ifTrueExpressionNode = ExpressionNode.createInstance(conditionalExpression.getIfTrue());
		this.ifFalseExpressionNode = ExpressionNode.createInstance(conditionalExpression.getIfFalse());
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		return IfStatementNode.execute(elementManager, conditionExpressionNode, conditionString, ifTrueExpressionNode, ifFalseExpressionNode);
	}

}

package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.InfixExpression;

import php.ElementManager;

import datamodel.nodes.DataNode;
import datamodel.nodes.ConcatNode;
import datamodel.nodes.LiteralNode;
import datamodel.nodes.SymbolicNode;

/**
 *
 * @author HUNG
 *
 */
public class InfixExpressionNode extends ExpressionNode {

	private int operator;
	private ExpressionNode leftExpressionNode;
	private ExpressionNode rightExpressionNode;
	
	/*
	Represents an infix expression 

	e.g. $a + 1,
	 3 - 2,
	 foo() * $a->bar(),
	 'string'.$c
	 */
	public InfixExpressionNode(InfixExpression infixExpression) {
		super(infixExpression);
		this.operator = infixExpression.getOperator();
		this.leftExpressionNode = ExpressionNode.createInstance(infixExpression.getLeft());
		this.rightExpressionNode = ExpressionNode.createInstance(infixExpression.getRight());
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		DataNode leftNode = leftExpressionNode.execute(elementManager);
		DataNode rightNode = rightExpressionNode.execute(elementManager);					
		switch (operator) {
			// '.'
			case InfixExpression.OP_CONCAT: 
				return new ConcatNode(leftNode, rightNode);
				
			// '!='
			case InfixExpression.OP_IS_NOT_EQUAL:
				if ((leftNode instanceof LiteralNode || leftNode instanceof ConcatNode) && (rightNode instanceof LiteralNode || rightNode instanceof ConcatNode))
					return leftNode.getApproximateStringValue().equals(rightNode.getApproximateStringValue()) ? new LiteralNode("FALSE") : new LiteralNode("TRUE");
				else
					return new SymbolicNode(this);
			
			// '!=='
			case InfixExpression.OP_IS_NOT_IDENTICAL:
				if ((leftNode instanceof LiteralNode || leftNode instanceof ConcatNode) && (rightNode instanceof LiteralNode || rightNode instanceof ConcatNode))
					return leftNode.getApproximateStringValue().equals(rightNode.getApproximateStringValue()) ? new LiteralNode("FALSE") : new LiteralNode("TRUE");
				else
					return new SymbolicNode(this);
				
			default:
				return new SymbolicNode(this);
		}
	}
	
}

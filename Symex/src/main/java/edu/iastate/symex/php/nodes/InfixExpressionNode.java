package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.InfixExpression;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;

/**
 *
 * @author HUNG
 *
 */
public class InfixExpressionNode extends ExpressionNode {

	private int operator;
	private ExpressionNode left;
	private ExpressionNode right;
	
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
		this.left = ExpressionNode.createInstance(infixExpression.getLeft());
		this.right = ExpressionNode.createInstance(infixExpression.getRight());
	}
	
	@Override
	public DataNode execute(Env env) {
		DataNode leftValue = left.execute(env);
		DataNode rightValue = right.execute(env);					
		switch (operator) {
			// '.'
			case InfixExpression.OP_CONCAT: 
				return DataNodeFactory.createCompactConcatNode(leftValue, rightValue);
				
			// '!='
			case InfixExpression.OP_IS_NOT_EQUAL:
				if ((leftValue instanceof LiteralNode || leftValue instanceof ConcatNode) && (rightValue instanceof LiteralNode || rightValue instanceof ConcatNode))
					return leftValue.getApproximateStringValue().equals(rightValue.getApproximateStringValue()) ? DataNodeFactory.createLiteralNode("FALSE") : DataNodeFactory.createLiteralNode("TRUE");
				else
					return new SymbolicNode(this);
			
			// '!=='
			case InfixExpression.OP_IS_NOT_IDENTICAL:
				if ((leftValue instanceof LiteralNode || leftValue instanceof ConcatNode) && (rightValue instanceof LiteralNode || rightValue instanceof ConcatNode))
					return leftValue.getApproximateStringValue().equals(rightValue.getApproximateStringValue()) ? DataNodeFactory.createLiteralNode("FALSE") : DataNodeFactory.createLiteralNode("TRUE");
				else
					return new SymbolicNode(this);
				
			default:
				return new SymbolicNode(this);
		}
	}
	
}

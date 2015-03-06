package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.InfixExpression;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;

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
	
	public int getOperator() {
		return operator;
	}
	
	public ExpressionNode getLeft() {
		return left;
	}
	
	public ExpressionNode getRight() {
		return right;
	}
	
	@Override
	public DataNode execute(Env env) {
		DataNode leftValue = left.execute(env);
		DataNode rightValue = right.execute(env);					
		switch (operator) {
			// '.'
			case InfixExpression.OP_CONCAT: 
				return DataNodeFactory.createCompactConcatNode(leftValue, rightValue);
				
			// '=='
			case InfixExpression.OP_IS_EQUAL:
				return leftValue.isEqualTo(rightValue);
			
			// '==='
			case InfixExpression.OP_IS_IDENTICAL:
				return leftValue.isIdenticalTo(rightValue);
								
			// '!='
			case InfixExpression.OP_IS_NOT_EQUAL:
				return leftValue.isEqualTo(rightValue).negate();
			
			// '!=='
			case InfixExpression.OP_IS_NOT_IDENTICAL:
				return leftValue.isIdenticalTo(rightValue).negate();
				
			// TODO Implement more cases
			default:
				return DataNodeFactory.createSymbolicNode(this);
		}
	}
	
}

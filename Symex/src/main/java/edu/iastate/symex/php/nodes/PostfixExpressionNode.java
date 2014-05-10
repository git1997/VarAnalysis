package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.PostfixExpression;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;

/**
 * 
 * @author HUNG
 *
 */
public class PostfixExpressionNode extends ExpressionNode {
	
	//private int operator;
	//private VariableBaseNode variableBaseNode;
	
	/*
	Represents a postfix expression 

	e.g. $a++,
	 foo()--
	*/
	public PostfixExpressionNode(PostfixExpression postfixExpression) {
		super(postfixExpression);
		//this.operator = postfixExpression.getOperator();
		//this.variableBaseNode = VariableBaseNode.createInstance(postfixExpression.getVariable());
	}

	@Override
	public DataNode execute(Env env) {
		return DataNodeFactory.createSymbolicNode(this);
	}

}

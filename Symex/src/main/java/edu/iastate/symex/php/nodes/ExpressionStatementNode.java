package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ExpressionStatement;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class ExpressionStatementNode extends StatementNode {

	private ExpressionNode expression;

	/*
	This class holds the expression that should be evaluated. 

	e.g. $a = 5;
	 $a;
	 3+2;
	*/
	public ExpressionStatementNode(ExpressionStatement expressionStatement) {
		super(expressionStatement);
		expression = ExpressionNode.createInstance(expressionStatement.getExpression());
	}
	
	@Override
	public DataNode execute_(Env env) {
		// Return the value of the enclosed expression (e.g., the function call die() returns an EXIT value,
		// this value should be propagated to the the enclosing statement).  
		return expression.execute(env);
	}
	
}

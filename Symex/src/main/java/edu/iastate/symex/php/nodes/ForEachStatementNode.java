package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ForEachStatement;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;

/**
 * 
 * @author HUNG
 *
 */
public class ForEachStatementNode extends StatementNode {

	private LiteralNode conditionString;
	private ExpressionNode expression;
	private StatementNode statement;	
	
	/*
	Represents a for each statement 

	e.g. foreach (array_expression as $value)
	   statement;
	     
	 foreach (array_expression as $key => $value) 
	   statement;
	 
	 foreach (array_expression as $key => $value): 
	   statement;
	   ...
	 endforeach;
	*/
	public ForEachStatementNode(ForEachStatement forEachStatement) {
		super(forEachStatement);
		expression = ExpressionNode.createInstance(forEachStatement.getExpression());
		conditionString = DataNodeFactory.createLiteralNode(expression);
		statement = StatementNode.createInstance(forEachStatement.getStatement());		
	}
	
	@Override
	public DataNode execute(Env env) {
		expression.execute(env);
		return WhileStatementNode.execute(env, conditionString, statement);
	}

}

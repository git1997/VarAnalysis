package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.IfStatement;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;

/**
 * 
 * @author HUNG
 *
 */
public class IfStatementNode extends StatementNode {

	private LiteralNode conditionString;
	private ExpressionNode condition;
	private StatementNode trueStatement;
	private StatementNode falseStatement;
	
	/*
	Represents if statement 

	e.g. 
	 if ($a > $b) {
	   echo "a is bigger than b";
	 } elseif ($a == $b) {
	   echo "a is equal to b";
	 } else {
	   echo "a is smaller than b";
	 },
	 
	 if ($a):
	   echo "a is bigger than b";
	   echo "a is NOT bigger than b";
	 endif;
	 */
	public IfStatementNode(IfStatement ifStatement) {
		super(ifStatement);
		condition = ExpressionNode.createInstance(ifStatement.getCondition());
		conditionString = DataNodeFactory.createLiteralNode(condition);
		trueStatement = StatementNode.createInstance(ifStatement.getTrueStatement());
		falseStatement = (ifStatement.getFalseStatement() != null ? StatementNode.createInstance(ifStatement.getFalseStatement()) : null);
	}
	
	@Override
	public DataNode execute(Env env) {
		IfStatementNode.execute(env, condition, conditionString, trueStatement, falseStatement);
		return null;
	}
	
	/**
	 * Executes different branches and updates the env accordingly.
	 * @see {@link edu.iastate.symex.php.nodes.ConditionalExpressionNode#execute(Env)}
	 */
	public static DataNode execute(Env env, ExpressionNode condition, LiteralNode conditionString, PhpNode trueStatement, PhpNode falseStatement) {
		if (condition != null) {
			DataNode dataNode = condition.execute(env);
			if ( // @see edu.iastate.symex.php.nodes.InfixExpressionNode.execute(env)
				dataNode instanceof ConcatNode && !dataNode.getApproximateStringValue().isEmpty()
				|| dataNode instanceof LiteralNode && 
					!((LiteralNode) dataNode).getStringValue().equals("FALSE") && !((LiteralNode) dataNode).getStringValue().isEmpty()) {
				if (trueStatement != null)
					return trueStatement.execute(env);
				else 
					return DataNodeFactory.createLiteralNode("");
			}
			else if (dataNode instanceof LiteralNode &&
					(dataNode.getApproximateStringValue().equals("FALSE") || dataNode.getApproximateStringValue().isEmpty())) {
				if (falseStatement != null)
					return falseStatement.execute(env);
				else
					return DataNodeFactory.createLiteralNode("");
			}
		}
			
		Env trueBranchEnv = null;
		Env falseBranchEnv = null;
		DataNode trueBranchValue = null;
		DataNode falseBranchValue = null;		
		
		// Execute the branches
		if (trueStatement != null) {
			trueBranchEnv = new Env(env, conditionString, true);
			trueBranchValue = trueStatement.execute(trueBranchEnv);
		}
		if (falseStatement != null) {
			falseBranchEnv = new Env(env, conditionString, false);
			falseBranchValue = falseStatement.execute(falseBranchEnv);
		}
		
		// Update the env
		env.updateWithBranches(conditionString, trueBranchEnv, falseBranchEnv);
		
		// Return a value (in case it is a ConditionalExpression)
		return DataNodeFactory.createCompactSelectNode(conditionString, trueBranchValue, falseBranchValue);		
	}

}

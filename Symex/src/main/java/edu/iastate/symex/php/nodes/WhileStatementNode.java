package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.WhileStatement;

import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.core.BranchEnv;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;

/**
 * 
 * @author HUNG
 *
 */
public class WhileStatementNode extends StatementNode {

	private LiteralNode conditionString;
	private ExpressionNode condition;	
	private StatementNode statement;
	
	/*
	Represents while statement. 

	e.g. while (expr)
	   statement;
	 
	 while (expr):
	   statement
	   ...
	 endwhile; 
	*/
	public WhileStatementNode(WhileStatement whileStatement) {
		super(whileStatement);
		condition = ExpressionNode.createInstance(whileStatement.getCondition());
		conditionString = DataNodeFactory.createLiteralNode(condition);
		statement = StatementNode.createInstance(whileStatement.getBody());
	}

	@Override
	public DataNode execute(Env env) {
		condition.execute(env);
		return execute(env, conditionString, statement);
	}
	
	/**
	 * Executes the loop and updates the env accordingly.
	 */
	public static DataNode execute(Env env, LiteralNode conditionString, StatementNode statement) {
		BranchEnv loopEnv = new BranchEnv(env, ConstraintFactory.createAtomicConstraint(conditionString.getStringValue(), conditionString.getLocation()));

		statement.execute(loopEnv);
		
		env.updateAfterLoopExecution(loopEnv);
		
		return null; // TODO Implement cases of return value
	}

}

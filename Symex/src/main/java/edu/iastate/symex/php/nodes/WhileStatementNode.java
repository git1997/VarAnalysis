package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.WhileStatement;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.core.BranchEnv;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class WhileStatementNode extends StatementNode {

	private ExpressionNode condition;	
	private StatementNode body;
	
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
		body = StatementNode.createInstance(whileStatement.getBody());
	}

	@Override
	public DataNode execute_(Env env) {
		condition.execute(env);
		
		Constraint constraint = ConstraintFactory.createAtomicConstraint(condition.getSourceCode(), condition.getLocation());
		
		return execute(env, constraint, body);
	}
	
	/**
	 * Executes the loop and updates the env accordingly.
	 */
	public static DataNode execute(Env env, Constraint constraint, StatementNode statement) {
		BranchEnv loopEnv = new BranchEnv(env, constraint);

		DataNode retValue = statement.execute(loopEnv);
		
		env.updateAfterLoopExecution(loopEnv);
		
		return retValue; // TODO Revise, see if it's correct
	}

}

package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.IfStatement;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.core.BranchEnv;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode.BooleanNode;

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
		DataNode conditionValue = condition.execute(env);
		return execute(env, conditionValue.convertToBooleanValue(), conditionString, trueStatement, falseStatement);
	}
	
	/**
	 * Executes different branches and updates the env accordingly.
	 * @see {@link edu.iastate.symex.php.nodes.ConditionalExpressionNode#execute(Env)}
	 */
	public static DataNode execute(Env env, BooleanNode conditionValue, LiteralNode conditionString, PhpNode trueStatement, PhpNode falseStatement) {
		if (conditionValue.isTrueValue()) {
			if (trueStatement != null)
				return trueStatement.execute(env);
			else 
				return null;
		}
		else if (conditionValue.isFalseValue()) {
			if (falseStatement != null)
				return falseStatement.execute(env);
			else
				return null;
		}
			
		BranchEnv trueBranchEnv = null;
		BranchEnv falseBranchEnv = null;
		DataNode trueBranchRetValue = null;
		DataNode falseBranchRetValue = null;		
		
		// Execute the branches
		Constraint constraint = ConstraintFactory.createAtomicConstraint(conditionString);
		if (trueStatement != null) {
			trueBranchEnv = new BranchEnv(env, constraint);
			trueBranchRetValue = trueStatement.execute(trueBranchEnv);
		}
		if (falseStatement != null) {
			falseBranchEnv = new BranchEnv(env, ConstraintFactory.createNotConstraint(constraint));
			falseBranchRetValue = falseStatement.execute(falseBranchEnv);
		}
		
		// Update the env
		env.updateWithBranches(constraint, trueBranchEnv, falseBranchEnv);
		
		// Return value
		return DataNodeFactory.createCompactSelectNode(constraint, trueBranchRetValue, falseBranchRetValue);		
	}

}

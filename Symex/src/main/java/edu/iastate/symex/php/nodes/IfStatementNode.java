package edu.iastate.symex.php.nodes;

import java.util.HashMap;

import org.eclipse.php.internal.core.ast.nodes.IfStatement;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.core.BranchEnv;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode.BooleanNode;

/**
 * 
 * @author HUNG
 *
 */
public class IfStatementNode extends StatementNode {

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
		trueStatement = StatementNode.createInstance(ifStatement.getTrueStatement());
		falseStatement = (ifStatement.getFalseStatement() != null ? StatementNode.createInstance(ifStatement.getFalseStatement()) : null);
	}
	
	@Override
	public DataNode execute(Env env) {
		return IfStatementNode.execute(env, condition, trueStatement, falseStatement);
	}
	
	/**
	 * Executes different branches and updates the env accordingly.
	 * Depending on the evaluated result of the condition, we may execute only one branch or both branches.
	 * @see {@link edu.iastate.symex.php.nodes.ConditionalExpressionNode#execute(Env)}
	 */
	public static DataNode execute(Env env, ExpressionNode condition, PhpNode trueStatement, PhpNode falseStatement) {
		DataNode evaluatedCondition = condition.execute(env);
		BooleanNode conditionValue = evaluatedCondition.convertToBooleanValue();
		
		/*
		 * If condition evaluates to either TRUE or FALSE, then execute the corresponding branch only.
		 */
		if (conditionValue.isTrueValue()) {
			if (trueStatement != null)
				return trueStatement.execute(env);
			else 
				return SpecialNode.ControlNode.OK;
		}
		else if (conditionValue.isFalseValue()) {
			if (falseStatement != null)
				return falseStatement.execute(env);
			else
				return SpecialNode.ControlNode.OK;
		}
		
		/*
		 * Else, execute both branches.
		 */
		LiteralNode conditionString = DataNodeFactory.createLiteralNode(condition);
		Constraint constraint = ConstraintFactory.createAtomicConstraint(conditionString.getStringValue(), conditionString.getLocation());
		
		return execute(env, constraint, trueStatement, falseStatement);
	}
	
	/**
	 * Executes different branches and updates the env accordingly.
	 * @see {@link edu.iastate.symex.php.nodes.SwitchStatementNode.FakeSwitchStatementNode#execute(Env)}
	 */
	public static DataNode execute(Env env, Constraint constraint, PhpNode trueStatement, PhpNode falseStatement) {
		/*
		 * Execute the branches
		 */
		HashMap<PhpVariable, DataNode> dirtyValuesInTrueBranch = new HashMap<PhpVariable, DataNode>();
		HashMap<PhpVariable, DataNode> dirtyValuesInFalseBranch = new HashMap<PhpVariable, DataNode>();
		DataNode trueBranchRetValue = SpecialNode.ControlNode.OK;
		DataNode falseBranchRetValue = SpecialNode.ControlNode.OK;
		
		if (trueStatement != null) {
			BranchEnv trueBranchEnv = new BranchEnv(env, constraint);
			trueBranchRetValue = trueStatement.execute(trueBranchEnv);
			dirtyValuesInTrueBranch = env.backtrackAfterBranchExecution(trueBranchEnv);
		}
		if (falseStatement != null) {
			BranchEnv falseBranchEnv = new BranchEnv(env, ConstraintFactory.createNotConstraint(constraint));
			falseBranchRetValue = falseStatement.execute(falseBranchEnv);
			dirtyValuesInFalseBranch = env.backtrackAfterBranchExecution(falseBranchEnv);
		}
		
		// Update the env
		env.updateAfterBranchExecution(constraint, dirtyValuesInTrueBranch, dirtyValuesInFalseBranch, trueBranchRetValue, falseBranchRetValue);
		
		return DataNodeFactory.createCompactSelectNode(constraint, trueBranchRetValue, falseBranchRetValue);		
	}

}

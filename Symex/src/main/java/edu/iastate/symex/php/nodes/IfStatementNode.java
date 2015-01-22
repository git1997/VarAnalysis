package edu.iastate.symex.php.nodes;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.php.internal.core.ast.nodes.IfStatement;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.core.BranchEnv;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.SpecialNode.ControlNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode.BooleanNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode.UnsetNode;
import edu.iastate.symex.instrumentation.WebAnalysis;

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
	public DataNode execute_(Env env) {
		return IfStatementNode.execute(env, condition, trueStatement, falseStatement, false);
	}
	
	/**
	 * Executes different branches and updates the env accordingly.
	 * Depending on the evaluated result of the condition, we may execute only one branch or both branches.
	 * @see {@link edu.iastate.symex.php.nodes.ConditionalExpressionNode#execute(Env)}
	 */
	public static DataNode execute(Env env, ExpressionNode condition, PhpNode trueNode, PhpNode falseNode, boolean isExpression) {
		DataNode evaluatedCondition = condition.execute(env);
		BooleanNode conditionValue = evaluatedCondition.convertToBooleanValue();
		
		/*
		 * If condition evaluates to either TRUE or FALSE, then execute the corresponding branch only.
		 */
		if (conditionValue.isTrueValue()) {
			if (trueNode != null)
				return trueNode.execute(env);
			else 
				return (isExpression ? UnsetNode.UNSET : ControlNode.OK);
		}
		else if (conditionValue.isFalseValue()) {
			if (falseNode != null)
				return falseNode.execute(env);
			else
				return (isExpression ? UnsetNode.UNSET : ControlNode.OK);
		}
		
		/*
		 * Else, execute both branches.
		 */
		Constraint constraint = ConstraintFactory.createAtomicConstraint(condition.getSourceCode(), condition.getLocation());
		
		return execute(env, constraint, trueNode, falseNode, isExpression);
	}
	
	/**
	 * Executes different branches and updates the env accordingly.
	 * @see {@link edu.iastate.symex.php.nodes.SwitchStatementNode.FakeSwitchStatementNode#execute(Env)}
	 */
	public static DataNode execute(Env env, Constraint constraint, PhpNode trueNode, PhpNode falseNode, boolean isExpression) {
		/*
		 * Execute the branches
		 */
		HashMap<PhpVariable, DataNode> dirtyVarsInTrueBranch = new HashMap<PhpVariable, DataNode>();
		HashMap<PhpVariable, DataNode> dirtyVarsInFalseBranch = new HashMap<PhpVariable, DataNode>();
		DataNode trueBranchRetValue = (isExpression ? UnsetNode.UNSET : ControlNode.OK);
		DataNode falseBranchRetValue = (isExpression ? UnsetNode.UNSET : ControlNode.OK);

		/*
		 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB ANALYSIS CODE
		if (WebAnalysis.isEnabled())
			WebAnalysis.onTrueBranchExecutionStarted(env);
		// END OF WEB ANALYSIS CODE
		
		if (trueNode != null) {
			BranchEnv trueBranchEnv = new BranchEnv(env, constraint);
			trueBranchRetValue = trueNode.execute(trueBranchEnv);
			dirtyVarsInTrueBranch = env.backtrackAfterExecution(trueBranchEnv);
		}

		/*
		 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB ANALYSIS CODE
		if (WebAnalysis.isEnabled())
			WebAnalysis.onFalseBranchExecutionStarted(env);
		// END OF WEB ANALYSIS CODE
		
		if (falseNode != null) {
			BranchEnv falseBranchEnv = new BranchEnv(env, ConstraintFactory.createNotConstraint(constraint));
			falseBranchRetValue = falseNode.execute(falseBranchEnv);
			dirtyVarsInFalseBranch = env.backtrackAfterExecution(falseBranchEnv);
		}
		
		/*
		 * Handle return/exit statements in the branches.
		 * For an ifStatement: E; if (C) { A; return; } else { B; } D;
		 * the best transformation is
		 * 		=> E; if (C) A; else { B; D; }
		 * However, currently we can probably only use an approximate transformation as follows
		 * 		=> E; B; D; (disregard A)
		 * 
		 * The reason we want to cut the branch with exit/return is so that if there's echo statements in there, they don't get concatenated with
		 * other strings after the branch.
		 * 
		 * To keep the some important values (output, return values) that are cut, at exit/return statements, we store these values immediately.
		 * However, at return statements, we may still lose some OUTPUT values.
		 */
		if (isExitOrReturn(trueBranchRetValue)) {
			if (isExitOrReturn(falseBranchRetValue)) {
				// Do nothing
			}
			else {
				env.updateWithOneBranchOnly(dirtyVarsInFalseBranch);
			}
		}
		else {
			if (isExitOrReturn(falseBranchRetValue)) {
				env.updateWithOneBranchOnly(dirtyVarsInTrueBranch);
			}
			else {
				// Normal case
				env.updateAfterBranchExecution(constraint, dirtyVarsInTrueBranch, dirtyVarsInFalseBranch);
			}
		}
		
		/*
		 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB ANALYSIS CODE
		if (WebAnalysis.isEnabled())
			WebAnalysis.onBothBranchesExecutionFinished(new HashSet<PhpVariable>(dirtyVarsInTrueBranch.keySet()), new HashSet<PhpVariable>(dirtyVarsInFalseBranch.keySet()), env);
		// END OF WEB ANALYSIS CODE
		
		return DataNodeFactory.createCompactSelectNode(constraint, trueBranchRetValue, falseBranchRetValue);		
	}
	
	/**
	 * Returns true if the CONTROL value is an EXIT or RETURN.
	 */
	private static boolean isExitOrReturn(DataNode control) {
		return control == ControlNode.EXIT || control == ControlNode.RETURN;
	}

}

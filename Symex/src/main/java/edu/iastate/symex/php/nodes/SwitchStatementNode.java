package edu.iastate.symex.php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Statement;
import org.eclipse.php.internal.core.ast.nodes.SwitchStatement;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode.BooleanNode;

/**
 * 
 * @author HUNG
 *
 */
public class SwitchStatementNode extends StatementNode {

	protected ExpressionNode expression;
	protected ArrayList<SwitchCaseNode> switchCases;
	
	/*
	Represents a switch statement. 

	e.g. 
	 switch ($i) {
	   case 0:
	     echo "i equals 0";
	     break;
	   case 1:
	     echo "i equals 1";
	     break;
	   default:
	     echo "i not equals 0 or 1";
	     break;
	 }
	*/
	public SwitchStatementNode(SwitchStatement switchStatement) {
		super(switchStatement);
		
		// Get switchCaseNodes
		ArrayList<SwitchCaseNode> switchCaseNodes = new ArrayList<SwitchCaseNode>();
		for (Statement statement : switchStatement.getBody().statements()) {
			SwitchCaseNode switchCaseNode = (SwitchCaseNode) StatementNode.createInstance(statement);
			switchCaseNodes.add(switchCaseNode);
		}
		
		/*
		 * Modify the statementNodes within switchCaseNodes so that there's no fall through across cases
		 *   (so we can turn a SwitchStatement into equivalent IfStatements),
		 * Also, make sure that there's no BREAK statements in the SwitchStatement anymore
		 *   (that is, BREAK statements should be "eaten" by the SwitchStatement, not by an enclosing loop for example).
		 * Example:
		 * 		case 0: echo '0'; 
		 * 		case 1: echo '1'; break;
		 * 		case 2: echo '2'; 
		 * First we turn them into 
		 * 		case 0: echo '0'; echo '1';
		 * 		case 1: echo '1';
		 * 		case 2: echo '2';
		 * Then we turn them into 
		 * 		if (case 0)
		 * 			{ echo '0'; echo '1'; }
		 * 		else if (case 1)
		 * 			echo '1';
		 * 		else
		 * 			echo '2';
		 */
		for (int i = 0; i < switchCaseNodes.size(); i++) {
			if (!switchCaseNodes.get(i).hasBreakStatement()) {
				for (int j = i + 1; j < switchCaseNodes.size(); j++) {
					switchCaseNodes.get(i).addStatementNodes(switchCaseNodes.get(j));
					if (switchCaseNodes.get(j).hasBreakStatement())
						break;
				}
			}
			switchCaseNodes.get(i).removeBreakStatementNode();
		}
		
		// Reorder the switchCaseNodes so that the default switchCase is put at the end.
		// TODO Is this necessary, and more importantly, is this the correct way to handle a SwitchStatement?
		for (int i = 0; i < switchCaseNodes.size() - 1; i++) { // Use size() - 1, so that if it's already at the end, don't do anything
			if (switchCaseNodes.get(i).isDefault()) {
				SwitchCaseNode defaultSwitchCaseNode = switchCaseNodes.get(i);
				switchCaseNodes.remove(i);
				switchCaseNodes.add(defaultSwitchCaseNode);
				break;
			}
		}
		
		this.expression = ExpressionNode.createInstance(switchStatement.getExpression());
		this.switchCases = switchCaseNodes;
	}

	@Override
	public DataNode execute_(Env env) {
		DataNode expressionResult = expression.execute(env);
		FakeSwitchStatementNode fakeSwitchExpressionNode = new FakeSwitchStatementNode((SwitchStatement) this.getAstNode(), expression, expressionResult, switchCases);
		return fakeSwitchExpressionNode.execute(env);
	}
	
	/**
	 * Use FakeSwitchStatementNode to model a SwitchStatement as a list of IfStatements
	 */
	private class FakeSwitchStatementNode extends StatementNode {
		
		private SwitchStatement originalSwitchStatement;
		
		private ExpressionNode expression;
		private DataNode expressionResult;
		private ArrayList<SwitchCaseNode> switchCases;
		
		private FakeSwitchStatementNode(SwitchStatement originalSwitchStatement, ExpressionNode expression, DataNode expressionResult, ArrayList<SwitchCaseNode> switchCases) {
			super(originalSwitchStatement);
			this.originalSwitchStatement = originalSwitchStatement;
			
			this.expression = expression;
			this.expressionResult = expressionResult;
			this.switchCases = switchCases;
		}
		
		@Override
		public DataNode execute_(Env env) {
			// Turn a SwitchStatement into an IfStatement such that:
			//  + The then branch is the first SwitchCase
			//  + The else branch is a FakeSwitchStatement, consisting of the remaining SwitchCases except the first SwitchCase
			SwitchCaseNode thenBranch = switchCases.get(0);
			ArrayList<SwitchCaseNode> remainingSwitchCases = new ArrayList<SwitchCaseNode>(switchCases);
			remainingSwitchCases.remove(0);
			FakeSwitchStatementNode elseBranch = remainingSwitchCases.isEmpty() ? null : new FakeSwitchStatementNode(originalSwitchStatement, expression, expressionResult, remainingSwitchCases);
			
			// Execute the branches
			if (thenBranch.isDefault()) {
				return thenBranch.execute(env);
			}
			else {
				DataNode caseResult = thenBranch.getValue().execute(env);
				BooleanNode conditionValue = expressionResult.isEqualTo(caseResult);
				
				/*
				 * If condition evaluates to either TRUE or FALSE, then execute the corresponding branch only.
				 */
				if (conditionValue.isTrueValue())
					return thenBranch.execute(env);
				else if (conditionValue.isFalseValue()) {
					if (elseBranch != null)
						return elseBranch.execute(env);
					else
						return SpecialNode.ControlNode.OK;
				}

				/*
				 * Else, execute both branches.
				 */
				Constraint constraint = ConstraintFactory.createEqualConstraint(expression, thenBranch.getValue()); 

				return IfStatementNode.execute(env, constraint, thenBranch, elseBranch, true);
			}
		}
		
	}
	
}

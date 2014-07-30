package edu.iastate.symex.php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Statement;
import org.eclipse.php.internal.core.ast.nodes.SwitchStatement;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode.BooleanNode;
import edu.iastate.symex.position.CompositeRange;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class SwitchStatementNode extends StatementNode {

	protected ExpressionNode expression;
	protected LiteralNode conditionString;
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
		 * Modify the statementNodes within switchCaseNodes.
		 * For example, 
		 * 		case 0: echo '0'; 
		 * 		case 1: echo '1'. 
		 * Then, the statementNodes of case 0 include echo '0' and echo '1'.
		 * If 
		 * 		case 0: echo '0'; break;
		 * 		case 1: echo '1'; break;
		 * Then, the statementNodes of case 0 include echo '0' only. 
		 */
		for (int i = 0; i < switchCaseNodes.size() - 1; i++) {
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
		for (int i = 0; i < switchCaseNodes.size() - 1; i++) { // Use size() - 1, so that if it's already at the end, don't do anything
			if (switchCaseNodes.get(i).isDefault()) {
				SwitchCaseNode defaultSwitchCaseNode = switchCaseNodes.get(i);
				switchCaseNodes.remove(i);
				switchCaseNodes.add(defaultSwitchCaseNode);
				break;
			}
		}
		
		this.expression = ExpressionNode.createInstance(switchStatement.getExpression());
		this.conditionString = DataNodeFactory.createLiteralNode(expression);		
		this.switchCases = switchCaseNodes;
	}

	@Override
	public DataNode execute(Env env) {
		DataNode expressionResult = expression.execute(env);
		FakeSwitchStatementNode fakeSwitchExpressionNode = new FakeSwitchStatementNode((SwitchStatement) this.getAstNode(), expressionResult, conditionString, switchCases);
		return fakeSwitchExpressionNode.execute(env);
	}
	
	/**
	 * Use FakeSwitchStatementNode to model a SwitchStatement as a list of IfStatements
	 */
	private class FakeSwitchStatementNode extends StatementNode {
		
		private SwitchStatement originalSwitchStatement;
		
		private DataNode expressionResult;
		private LiteralNode conditionString;
		private ArrayList<SwitchCaseNode> switchCases;
		
		private FakeSwitchStatementNode(SwitchStatement originalSwitchStatement, DataNode expressionResult, LiteralNode conditionString, ArrayList<SwitchCaseNode> switchCases) {
			super(originalSwitchStatement);
			this.originalSwitchStatement = originalSwitchStatement;
			
			this.expressionResult = expressionResult;
			this.conditionString = conditionString;
			this.switchCases = switchCases;
		}
		
		@Override
		public DataNode execute(Env env) {
			// Turn a SwitchStatement into an IfStatement such that:
			//  + The then branch is the first SwitchCase
			//  + The else branch is a FakeSwitchStatement, consisting of the remaining SwitchCases except the first SwitchCase
			SwitchCaseNode thenBranch = switchCases.get(0);
			ArrayList<SwitchCaseNode> remainingSwitchCases = new ArrayList<SwitchCaseNode>(switchCases);
			remainingSwitchCases.remove(0);
			FakeSwitchStatementNode elseBranch = remainingSwitchCases.isEmpty() ? null : new FakeSwitchStatementNode(originalSwitchStatement, expressionResult, conditionString, remainingSwitchCases);
			
			// Execute the branches
			if (thenBranch.isDefault()) {
				return thenBranch.execute(env);
			}
			else {
				DataNode caseResult = thenBranch.getValue().execute(env);
				BooleanNode conditionValue = expressionResult.isEqualTo(caseResult);

				PositionRange location = new CompositeRange(conditionString.getLocation(), thenBranch.getConditionString().getLocation());
				String stringValue = conditionString.getStringValue() + " == " + thenBranch.getConditionString().getStringValue(); 
				LiteralNode conditionString = DataNodeFactory.createLiteralNode(location, stringValue);

				return IfStatementNode.execute(env, conditionValue, conditionString, thenBranch, elseBranch);
			}
		}
		
	}
	
}

package edu.iastate.symex.php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Statement;
import org.eclipse.php.internal.core.ast.nodes.SwitchStatement;

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
public class SwitchStatementNode extends StatementNode {

	protected ExpressionNode expression;
	protected DataNode switchExpressionNodeResult;
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
			for (int j = i + 1; j < switchCaseNodes.size(); j++)
				switchCaseNodes.get(i).addStatementNodesUntilBreak(switchCaseNodes.get(j));
		}
		
		// Reorder the switchCaseNodes so that the default switchCase is put at the end.
		SwitchCaseNode defaultSwitchCaseNode = null;
		for (int i = 0; i < switchCaseNodes.size(); i++) {
			if (switchCaseNodes.get(i).isDefault()) {
				defaultSwitchCaseNode = switchCaseNodes.get(i);
				switchCaseNodes.remove(i);
				break;
			}
		}
		if (defaultSwitchCaseNode != null) {
			switchCaseNodes.add(defaultSwitchCaseNode);
		}
		
		this.expression = ExpressionNode.createInstance(switchStatement.getExpression());
		this.conditionString = DataNodeFactory.createLiteralNode(expression);		
		this.switchCases = switchCaseNodes;
	}

	@Override
	public DataNode execute(Env env) {
		if (!(this instanceof FakeSwitchStatementNode))
			switchExpressionNodeResult = expression.execute(env);
		
		if (switchCases.isEmpty())
			return null;
		
		// Remove the first switchCaseNode from the current SwitchStatementNode to create a fake SwitchStatementNode
		SwitchCaseNode firstSwitchCaseNode = switchCases.get(0);
		FakeSwitchStatementNode fakeSwitchStatementNode = new FakeSwitchStatementNode((SwitchStatement) this.getAstNode());
		fakeSwitchStatementNode.expression = expression;
		fakeSwitchStatementNode.switchExpressionNodeResult = switchExpressionNodeResult;
		fakeSwitchStatementNode.conditionString = conditionString;			
		fakeSwitchStatementNode.switchCases = new ArrayList<SwitchCaseNode>(switchCases);
		fakeSwitchStatementNode.switchCases.remove(0);
		
		// Execute the firstSwitchCaseNode and the fake SwitchStatementNode
		if (firstSwitchCaseNode.isDefault()) {
			firstSwitchCaseNode.execute(env);
		}
		else {
			DataNode firstSwitchCaseNodeResult = firstSwitchCaseNode.getValue().execute(env);
			if (switchExpressionNodeResult instanceof LiteralNode && firstSwitchCaseNodeResult instanceof LiteralNode) {
				if (switchExpressionNodeResult.isEqualTo(firstSwitchCaseNodeResult).isTrueValue())
					firstSwitchCaseNode.execute(env);
				else
					fakeSwitchStatementNode.execute(env);
			}
			else {
				LiteralNode switchCaseExpressionString = firstSwitchCaseNode.getConditionString();
				LiteralNode conditionString = switchCaseExpressionString; // Or it could be: conditionString = new LiteralNode(switchExpressionString.getStringValue() + " == " + switchCaseExpressionString.getStringValue());
				IfStatementNode.execute(env, BooleanNode.UNKNOWN, conditionString, firstSwitchCaseNode, fakeSwitchStatementNode);
			}
		}
		
		return null;
	}
	
	private class FakeSwitchStatementNode extends SwitchStatementNode {
		private FakeSwitchStatementNode(SwitchStatement switchStatement) {
			super(switchStatement);
		}
	}
	
}

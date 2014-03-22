package php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Statement;
import org.eclipse.php.internal.core.ast.nodes.SwitchStatement;

import php.ElementManager;

import datamodel.nodes.DataNode;
import datamodel.nodes.LiteralNode;


/**
 * 
 * @author HUNG
 *
 */
public class SwitchStatementNode extends StatementNode {

	protected ExpressionNode switchExpressionNode;
	protected DataNode switchExpressionNodeResult;
	protected LiteralNode switchExpressionString;
	protected ArrayList<SwitchCaseNode> switchCaseNodes;
	
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
		
		this.switchExpressionNode = ExpressionNode.createInstance(switchStatement.getExpression());
		this.switchExpressionString = new LiteralNode(switchStatement.getExpression());		
		this.switchCaseNodes = switchCaseNodes;
	}

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		if (!(this instanceof FakeSwitchStatementNode))
			switchExpressionNodeResult = switchExpressionNode.execute(elementManager);
		
		if (switchCaseNodes.isEmpty())
			return null;
		
		// Remove the first switchCaseNode from the current SwitchStatementNode to create a fake SwitchStatementNode
		SwitchCaseNode firstSwitchCaseNode = switchCaseNodes.get(0);
		FakeSwitchStatementNode fakeSwitchStatementNode = new FakeSwitchStatementNode();
		fakeSwitchStatementNode.switchExpressionNode = switchExpressionNode;
		fakeSwitchStatementNode.switchExpressionNodeResult = switchExpressionNodeResult;
		fakeSwitchStatementNode.switchExpressionString = switchExpressionString;			
		fakeSwitchStatementNode.switchCaseNodes = new ArrayList<SwitchCaseNode>(switchCaseNodes);
		fakeSwitchStatementNode.switchCaseNodes.remove(0);
		
		// Execute the firstSwitchCaseNode and the fake SwitchStatementNode
		if (firstSwitchCaseNode.isDefault()) {
			firstSwitchCaseNode.execute(elementManager);
		}
		else {
			DataNode firstSwitchCaseNodeResult = firstSwitchCaseNode.getSwitchCaseExpressionNode().execute(elementManager);
			if (switchExpressionNodeResult instanceof LiteralNode && firstSwitchCaseNodeResult instanceof LiteralNode) {
				if (switchExpressionNodeResult.getApproximateStringValue().equals(firstSwitchCaseNodeResult.getApproximateStringValue()))
					firstSwitchCaseNode.execute(elementManager);
				else
					fakeSwitchStatementNode.execute(elementManager);
			}
			else {
				LiteralNode switchCaseExpressionString = firstSwitchCaseNode.getSwitchCaseExpressionString();
				LiteralNode conditionString = switchCaseExpressionString; // Or it could be: conditionString = new LiteralNode(switchExpressionString.getStringValue() + " == " + switchCaseExpressionString.getStringValue());
				IfStatementNode.execute(elementManager, null, conditionString, firstSwitchCaseNode, fakeSwitchStatementNode);
			}
		}
		
		return null;
	}
	
	private SwitchStatementNode() { // To allow empty constructor of the FakeSwitchStatementNode class
	}
	
	private class FakeSwitchStatementNode extends SwitchStatementNode {
	}
	
}

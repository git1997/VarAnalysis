package php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Statement;
import org.eclipse.php.internal.core.ast.nodes.SwitchCase;

import php.ElementManager;

import datamodel.nodes.DataNode;
import datamodel.nodes.LiteralNode;

/**
 * 
 * @author HUNG
 *
 */
public class SwitchCaseNode extends StatementNode {
	
	private ExpressionNode switchCaseExpressionNode;
	private LiteralNode switchCaseExpressionString;	
	private ArrayList<StatementNode> statementNodes;
	
	private boolean isDefault;
	private boolean hasBreakStatement;
	
	/*
	Represents a case statement. A case statement is part of switch statement 

	e.g. 
	 case expr:
	   statement1;
	   break;,
	 
	 default:
	   statement2;
	*/
	public SwitchCaseNode(SwitchCase switchCase) {
		ArrayList<StatementNode> statementNodes = new ArrayList<StatementNode>();
		boolean hasBreakStatement = false;
		for (Statement statement : switchCase.actions()) {
			StatementNode statementNode = StatementNode.createInstance(statement);
			statementNodes.add(statementNode);
			// [Required]:
			if (statement.getType() == Statement.RETURN_STATEMENT || statement.getType() == Statement.BREAK_STATEMENT) {
				hasBreakStatement = true;
				break;
			}
		}
		this.switchCaseExpressionNode = (switchCase.getValue() != null ? ExpressionNode.createInstance(switchCase.getValue()) : null);
		this.switchCaseExpressionString = new LiteralNode(switchCase);
		this.statementNodes = statementNodes;
		this.isDefault = switchCase.isDefault();
		this.hasBreakStatement = hasBreakStatement;
	}
	
	/*
	 * Get properties
	 */
	
	public ExpressionNode getSwitchCaseExpressionNode() {
		return switchCaseExpressionNode;
	}
	
	public LiteralNode getSwitchCaseExpressionString() {
		return switchCaseExpressionString;
	}
	
	public boolean isDefault() {
		return isDefault;
	}
	
	/*
	 * Set properties
	 */
	
	/**
	 * Adds statement nodes of the switchCaseNodes below until a break statement is encountered.
	 */
	public void addStatementNodesUntilBreak(SwitchCaseNode switchCaseNode) {
		if (!this.hasBreakStatement) {
			statementNodes.addAll(switchCaseNode.statementNodes);
			if (switchCaseNode.hasBreakStatement)
				hasBreakStatement = true;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		for (StatementNode statementNode : statementNodes) {
			statementNode.execute(elementManager);
		}
		return null;
	}
	
}

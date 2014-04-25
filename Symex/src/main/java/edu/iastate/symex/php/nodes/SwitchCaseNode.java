package edu.iastate.symex.php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Statement;
import org.eclipse.php.internal.core.ast.nodes.SwitchCase;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;

/**
 * 
 * @author HUNG
 *
 */
public class SwitchCaseNode extends StatementNode {
	
	private ExpressionNode value;
	private LiteralNode condtionString;	
	private ArrayList<StatementNode> statements;
	
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
		super(switchCase);
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
		this.value = (switchCase.getValue() != null ? ExpressionNode.createInstance(switchCase.getValue()) : null);
		this.condtionString = DataNodeFactory.createLiteralNode(this.value);
		this.statements = statementNodes;
		this.isDefault = switchCase.isDefault();
		this.hasBreakStatement = hasBreakStatement;
	}
	
	public ExpressionNode getValue() {
		return value;
	}
	
	public LiteralNode getConditionString() {
		return condtionString;
	}
	
	public boolean isDefault() {
		return isDefault;
	}
	
	/**
	 * Adds statement nodes of the switchCaseNodes below until a break statement is encountered.
	 */
	public void addStatementNodesUntilBreak(SwitchCaseNode switchCaseNode) {
		if (!this.hasBreakStatement) {
			statements.addAll(switchCaseNode.statements);
			if (switchCaseNode.hasBreakStatement)
				hasBreakStatement = true;
		}
	}
	
	@Override
	public DataNode execute(Env env) {
		return BlockNode.executeStatements(statements, env);
	}
	
}

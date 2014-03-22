package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.WhileStatement;

import php.ElementManager;

import datamodel.nodes.DataNode;
import datamodel.nodes.LiteralNode;

/**
 * 
 * @author HUNG
 *
 */
public class WhileStatementNode extends StatementNode {

	private LiteralNode conditionString;
	private ExpressionNode conditionNode;	
	private StatementNode statementNode;
	
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
		conditionString = new LiteralNode(whileStatement.getCondition());
		conditionNode = ExpressionNode.createInstance(whileStatement.getCondition());
		statementNode = StatementNode.createInstance(whileStatement.getBody());
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		conditionNode.execute(elementManager);
		ForStatementNode.execute(elementManager, conditionString, statementNode);
		return null;
	}

}

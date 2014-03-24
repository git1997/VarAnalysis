package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ForEachStatement;

import php.ElementManager;
import datamodel.nodes.DataNode;
import datamodel.nodes.LiteralNode;
import datamodel.nodes.LiteralNodeFactory;

/**
 * 
 * @author HUNG
 *
 */
public class ForEachStatementNode extends StatementNode {

	private LiteralNode conditionString;
	private ExpressionNode expressionNode;
	private StatementNode statementNode;	
	
	/*
	Represents a for each statement 

	e.g. foreach (array_expression as $value)
	   statement;
	     
	 foreach (array_expression as $key => $value) 
	   statement;
	 
	 foreach (array_expression as $key => $value): 
	   statement;
	   ...
	 endforeach;
	*/
	public ForEachStatementNode(ForEachStatement forEachStatement) {
		conditionString =LiteralNodeFactory.createLiteralNode(forEachStatement.getExpression());
		expressionNode = ExpressionNode.createInstance(forEachStatement.getExpression());
		statementNode = StatementNode.createInstance(forEachStatement.getStatement());		
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		expressionNode.execute(elementManager);
		ForStatementNode.execute(elementManager, conditionString, statementNode);
		return null;
	}

}

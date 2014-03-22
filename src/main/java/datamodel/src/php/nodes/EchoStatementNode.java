package php.nodes;

import java.util.ArrayList;
import org.eclipse.php.internal.core.ast.nodes.EchoStatement;
import org.eclipse.php.internal.core.ast.nodes.Expression;

import php.ElementManager;

import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class EchoStatementNode extends StatementNode {

	private ArrayList<ExpressionNode> expressionNodes = new ArrayList<ExpressionNode>();
	
	/*
	Represent a echo statement. 

	e.g. echo "hello",
	 echo "hello", "world"
	*/
	public EchoStatementNode(EchoStatement echoStatement) {
		for (Expression expression : echoStatement.expressions()) {
			ExpressionNode expressionNode = ExpressionNode.createInstance(expression);
			expressionNodes.add(expressionNode);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		ArrayList<DataNode> resolvedExpressionNodes = new ArrayList<DataNode>();
		for (ExpressionNode expressionNode : expressionNodes) {
			DataNode resolvedExpressionNode = expressionNode.execute(elementManager);
			resolvedExpressionNodes.add(resolvedExpressionNode);
		}
		elementManager.appendOutput(resolvedExpressionNodes);
		return null;
	}
	
}

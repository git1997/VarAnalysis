package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ExpressionStatement;

import php.ElementManager;



import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class ExpressionStatementNode extends StatementNode {

	private ExpressionNode expressionNode;

	/*
	This class holds the expression that should be evaluated. 

	e.g. $a = 5;
	 $a;
	 3+2;
	*/
	public ExpressionStatementNode(ExpressionStatement expressionStatement) {
		expressionNode = ExpressionNode.createInstance(expressionStatement.getExpression());
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		expressionNode.execute(elementManager);
		return null;
	}
	
}

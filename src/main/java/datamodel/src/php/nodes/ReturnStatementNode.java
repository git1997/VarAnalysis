package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ReturnStatement;

import php.ElementManager;

import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class ReturnStatementNode extends StatementNode {
	
	private ExpressionNode expressionNode;
	
	/*
	Represent a return statement 

	e.g. return;
	 return $a;
	*/
	public ReturnStatementNode(ReturnStatement returnStatement) {
		this.expressionNode = (returnStatement.getExpression() != null ? ExpressionNode.createInstance(returnStatement.getExpression()) : null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		elementManager.setHasReturnStatement(true);
		if (expressionNode != null) {
			DataNode returnValue = expressionNode.execute(elementManager);
			elementManager.addReturnValue(returnValue);
		}		
		return null;
	}

}
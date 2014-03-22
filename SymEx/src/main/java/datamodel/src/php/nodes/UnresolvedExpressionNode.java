package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Expression;

import php.ElementManager;

import datamodel.nodes.DataNode;
import datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class UnresolvedExpressionNode extends ExpressionNode {

	/**
	 * Constructor
	 */
	public UnresolvedExpressionNode(Expression expression) {
		super(expression);
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		return new SymbolicNode(this);
	}

}

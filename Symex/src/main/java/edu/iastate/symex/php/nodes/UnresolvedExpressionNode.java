package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Expression;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;

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
	
	@Override
	public DataNode execute(Env env) {
		return DataNodeFactory.createSymbolicNode(this);
	}

}

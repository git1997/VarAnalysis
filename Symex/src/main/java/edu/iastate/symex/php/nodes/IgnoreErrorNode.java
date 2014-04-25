package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.IgnoreError;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class IgnoreErrorNode extends ExpressionNode {

	private ExpressionNode expression;
	
	/*
	Represents ignore error expression 

	e.g. '@$a->foo()' 
	*/
	public IgnoreErrorNode(IgnoreError ignoreError) {
		super(ignoreError);
		expression = ExpressionNode.createInstance(ignoreError.getExpression());
	}
	
	@Override
	public DataNode execute(Env env) {
		return expression.execute(env);
	}

}

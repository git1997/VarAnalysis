package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.PrefixExpression;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;

/**
 * 
 * @author HUNG
 *
 */
public class PrefixExpressionNode extends ExpressionNode {

	//private int operator;
	//private VariableBaseNode variableBaseNode;

	/*
	Represents a prefix expression 

	e.g. --$a,
	 --foo()
	*/
	public PrefixExpressionNode(PrefixExpression prefixExpression) {
		super(prefixExpression);
		//this.operator = postfixExpression.getOperator();
		//this.variableBaseNode = VariableBaseNode.createInstance(postfixExpression.getVariable());
	}

	@Override
	public DataNode execute(Env env) {
		return DataNodeFactory.createSymbolicNode(this);
	}

}

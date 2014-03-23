package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.PrefixExpression;

import php.ElementManager;
import datamodel.nodes.DataNode;
import datamodel.nodes.SymbolicNode;

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

	/*
	 * (non-Javadoc)
	 * @see php.nodes.PhpNode#execute(php.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		return new SymbolicNode(this);
	}

}

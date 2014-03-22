package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.PostfixExpression;

import php.ElementManager;
import datamodel.nodes.DataNode;
import datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class PostfixExpressionNode extends ExpressionNode {
	
	//private int operator;
	//private VariableBaseNode variableBaseNode;
	
	/*
	Represents a postfix expression 

	e.g. $a++,
	 foo()--
	*/
	public PostfixExpressionNode(PostfixExpression postfixExpression) {
		super(postfixExpression);
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

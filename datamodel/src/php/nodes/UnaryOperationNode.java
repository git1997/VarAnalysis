package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.UnaryOperation;

import php.ElementManager;

import datamodel.nodes.DataNode;
import datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class UnaryOperationNode extends ExpressionNode {

	//private int operator;
	
	private ExpressionNode expressionNode; 
	
	/*
	Represents an unary operation expression 

	e.g. +$a,
	 -3,
	 -foo(),
	 +-+-$a
	*/ 
	public UnaryOperationNode(UnaryOperation unaryOperation) {
		super(unaryOperation);
		//this.operator = unaryOperation.getOperator();
		this.expressionNode = ExpressionNode.createInstance(unaryOperation.getExpression());
	}

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		expressionNode.execute(elementManager);
		return new SymbolicNode(this);
	}

}
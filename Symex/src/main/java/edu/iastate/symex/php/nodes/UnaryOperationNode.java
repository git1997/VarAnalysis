package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.UnaryOperation;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;

/**
 * 
 * @author HUNG
 *
 */
public class UnaryOperationNode extends ExpressionNode {

	//private int operator;
	private ExpressionNode expression; 
	
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
		this.expression = ExpressionNode.createInstance(unaryOperation.getExpression());
	}

	@Override
	public DataNode execute(Env env) {
		expression.execute(env);
		return DataNodeFactory.createSymbolicNode(this);
	}

}
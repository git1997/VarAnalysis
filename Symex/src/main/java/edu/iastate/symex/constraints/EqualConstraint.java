package edu.iastate.symex.constraints;

import edu.iastate.symex.php.nodes.ExpressionNode;

/**
 * 
 * @author HUNG
 *
 */
public interface EqualConstraint {

	public ExpressionNode getLeftExpression();
	
	public ExpressionNode getRightExpression();

}

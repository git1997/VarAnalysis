package edu.iastate.symex.constraints;

import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.iastate.symex.php.nodes.ExpressionNode;

/**
 * This class is used specifically to handle constraints with the form "$x == some_value".
 * @see edu.iastate.symex.constraints.ConstraintFactory.createEqualConstraint(ExpressionNode, ExpressionNode)
 * 
 * @author HUNG
 *
 */
public class BddEqualConstraint extends BddConstraint implements EqualConstraint {
	
	private ExpressionNode leftExpression;
	private ExpressionNode rightExpression;

	/**
	 * Protected constructor. Called from BddConstraintFactory only.
	 * @param featureExpr
	 * @param leftExpression
	 * @param rightExpression
	 */
	protected BddEqualConstraint(FeatureExpr featureExpr, ExpressionNode leftExpression, ExpressionNode rightExpression) {
		super(featureExpr);
		this.leftExpression = leftExpression;
		this.rightExpression = rightExpression;
	}
	
	public ExpressionNode getLeftExpression() {
		return leftExpression;
	}
	
	public ExpressionNode getRightExpression() {
		return rightExpression;
	}

}

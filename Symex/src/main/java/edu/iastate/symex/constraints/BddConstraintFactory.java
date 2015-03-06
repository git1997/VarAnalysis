package edu.iastate.symex.constraints;

import de.fosd.typechef.featureexpr.FeatureExprFactory;
import de.fosd.typechef.featureexpr.bdd.BDDFeatureExprFactory$;
import edu.iastate.symex.php.nodes.ExpressionNode;

/**
 * 
 * @author HUNG
 *
 */
public class BddConstraintFactory extends ConstraintFactory {
	
	// Use the JavaBDD library instead of Sat4j 
	static {
		FeatureExprFactory.setDefault(BDDFeatureExprFactory$.MODULE$);
	}

	@Override
	protected Constraint createTrue() {
		return new BddConstraint(FeatureExprFactory.True());
	}

	@Override
	protected Constraint createFalse() {
		return new BddConstraint(FeatureExprFactory.False());
	}

	@Override
	protected Constraint createAtomic(String conditionString) {
		return new BddConstraint(FeatureExprFactory.createDefinedExternal(conditionString));
	}

	@Override
	protected Constraint createAnd(Constraint constraint1, Constraint constraint2) {
		return new BddConstraint(((BddConstraint) constraint1).featureExpr.and(((BddConstraint) constraint2).featureExpr));
	}

	@Override
	protected Constraint createOr(Constraint constraint1, Constraint constraint2) {
		return new BddConstraint(((BddConstraint) constraint1).featureExpr.or(((BddConstraint) constraint2).featureExpr));
	}

	@Override
	protected Constraint createNot(Constraint oppositeConstraint) {
		if (oppositeConstraint instanceof BddNotConstraint)
			return ((BddNotConstraint) oppositeConstraint).getOppositeConstraint();
		else
			return new BddNotConstraint(oppositeConstraint);
	}
	
	@Override
	protected Constraint createEqual(String conditionString, ExpressionNode leftExpression, ExpressionNode rightExpression) {
		return new BddEqualConstraint(FeatureExprFactory.createDefinedExternal(conditionString), leftExpression, rightExpression);
	}

}

package edu.iastate.symex.constraints;

import de.fosd.typechef.featureexpr.FeatureExpr;

/**
 * 
 * @author HUNG
 *
 */
public class BddConstraint extends Constraint {
	
	// The FeatureExpr representing this constraint
	protected FeatureExpr featureExpr;
	
	/**
	 * Protected constructor. Called from BddConstraintFactory only.
	 * @param featureExpr
	 */
	protected BddConstraint(FeatureExpr featureExpr) {
		this.featureExpr = featureExpr;
	}
	
	/*
	 * Methods
	 */
	
	/**
	 * Returns the FeatureExpr representing this constraint.
	 */
	public FeatureExpr getFeatureExpr() {
		return featureExpr;
	}

	@Override
	public String toDebugString() {
		return featureExpr.toString();
	}

	@Override
	public boolean isSatisfiable() {
		return featureExpr.isSatisfiable();
	}

}
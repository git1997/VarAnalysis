package edu.iastate.symex.constraints;

/**
 * 
 * @author HUNG
 *
 */
public class SimpleConstraint extends Constraint {
	
	// The FeatureExpr representing this constraint
	protected String featureExpr;
	
	/**
	 * Protected constructor. Called from SimpleConstraintFactory only.
	 * @param featureExpr
	 */
	protected SimpleConstraint(String featureExpr) {
		this.featureExpr = featureExpr;
	}
	
	/*
	 * Methods
	 */
	
	/**
	 * Returns the FeatureExpr representing this constraint.
	 */
	public String getFeatureExpr() {
		return featureExpr;
	}

	@Override
	public String toDebugString() {
		return featureExpr.toString();
	}
	
	@Override
	public boolean isSatisfiable() {
		return this != FALSE;
	}

}

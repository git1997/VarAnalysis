package edu.iastate.symex.constraints;

/**
 * 
 * @author HUNG
 *
 */
public class Constraint {
	
	public static final Constraint TRUE	 = new Constraint("TRUE");
	
	public static final Constraint FALSE = new Constraint("FALSE");
	
	// The FeatureExpr representing this constraint
	protected String featureExpr;
	
	/**
	 * Protected constructor
	 * @param featureExpr
	 */
	protected Constraint(String featureExpr) {
		this.featureExpr = featureExpr;
	}
	
	/*
	 * Methods
	 */
	
	/**
	 * @return True if the constraint is satisfiable
	 */
	public boolean isSatisfiable() {
		return ! isContradiction();
	}
	
	/**
	 * @return True if the constraint is a tautology
	 */
	public boolean isTautology() {
		return this == TRUE;
	}
	
	/**
	 * @return True if the constraint is a contradiction
	 */
	public boolean isContradiction() {
		return this == FALSE;
	}
	
	/**
	 * @param constraint
	 * @return True if the two constraints are equivalent
	 */
	public boolean equivalentTo(Constraint constraint) {
		return (this.featureExpr.equals(constraint.featureExpr));
	}
	
	/**
	 * @param constraint
	 * @return True if the two constraints are opposite of each other
	 */
	public boolean oppositeOf(Constraint constraint) {
		return equivalentTo(ConstraintFactory.createNotConstraint(constraint));
	}
	
	/**
	 * Returns the FeatureExpr representing this constraint.
	 */
	public String getFeatureExpr() {
		return featureExpr;
	}

	/**
	 * @return A string describing the constraint
	 */
	public String toString() {
		return featureExpr.toString();
	}
	
	/**
	 * Given the current constraint, try adding another constraint onto it.
	 * For example, suppose the current constraint is A, the added constraint is B
	 * (A & B can be dependent, e.g. A = a & c, B = b & c),
	 * then we have 3 possible outcomes:
	 * 		+ A & B stays THE SAME:  A & B = A		(equivalently, A & !B = FALSE)
	 * 		+ A & B is ALWAYS FALSE: A & B = FALSE  (equivalently, A & !B = A)
	 * 		+ A & B cannot be determined
	 * @param constraint
	 */
	public Result tryAddingConstraint(Constraint constraint) {
		if (ConstraintFactory.createAndConstraint(this, ConstraintFactory.createNotConstraint(constraint)).isContradiction())
			return Result.THE_SAME;
		
		else if (ConstraintFactory.createAndConstraint(this, constraint).isContradiction())
			return Result.ALWAYS_FALSE;
		
		else
			return Result.UNDETERMINED;
	}

	/**
	 * @see Constraint.tryAddingConstraint(Constraint) 
	 */
	public enum Result {
		THE_SAME, 
		ALWAYS_FALSE,
		UNDETERMINED
	}
	
}

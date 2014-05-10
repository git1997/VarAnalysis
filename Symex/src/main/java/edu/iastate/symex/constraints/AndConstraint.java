package edu.iastate.symex.constraints;

/**
 * 
 * @author HUNG
 *
 */
public class AndConstraint extends Constraint {
	
	public Constraint constraint1;
	public Constraint constraint2;
	
	/**
	 * Protected constructor, called from ConstraintFactory only.
	 */
	protected AndConstraint(Constraint constraint1, Constraint constraint2) {
		super(constraint1.featureExpr.and(constraint2.featureExpr));
		this.constraint1 = constraint1;
		this.constraint2 = constraint2;
	}
	
	public Constraint getConstraint1() {
		return constraint1;
	}
	
	public Constraint getConstraint2() {
		return constraint2;
	}
	
}

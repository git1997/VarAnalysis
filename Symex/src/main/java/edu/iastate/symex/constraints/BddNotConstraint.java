package edu.iastate.symex.constraints;

/**
 * 
 * @author HUNG
 *
 */
public class BddNotConstraint extends BddConstraint {
	
	private Constraint oppositeConstraint;
	
	/**
	 * Protected constructor. Called from BddConstraintFactory only.
	 * @param oppositeConstraint
	 */
	protected BddNotConstraint(Constraint oppositeConstraint) {
		super(((BddConstraint) oppositeConstraint).featureExpr.not());
		this.oppositeConstraint = oppositeConstraint;
	}
	
	public Constraint getOppositeConstraint() {
		return oppositeConstraint;
	}
	
}

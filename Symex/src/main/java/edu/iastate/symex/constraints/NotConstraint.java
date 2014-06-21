package edu.iastate.symex.constraints;

/**
 * 
 * @author HUNG
 *
 */
public class NotConstraint extends Constraint {
	
	public Constraint oppositeConstraint;
	
	/**
	 * Protected constructor, called from ConstraintFactory only.
	 */
	protected NotConstraint(Constraint oppositeConstraint) {
		super("NOT(" + oppositeConstraint.featureExpr + ")");
		this.oppositeConstraint = oppositeConstraint;
	}
	
	public Constraint getOppositeConstraint() {
		return oppositeConstraint;
	}
	
}

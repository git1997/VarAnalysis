package edu.iastate.symex.constraints;

import edu.iastate.symex.position.PositionRange;

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
	
	@Override
	public PositionRange getLocation() {
		return oppositeConstraint.getLocation();
	}
	
}

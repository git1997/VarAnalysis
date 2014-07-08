package edu.iastate.symex.constraints;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class AtomicConstraint extends Constraint {
	
	private PositionRange location;
	
	/**
	 * Protected constructor, called from ConstraintFactory only.
	 */
	protected AtomicConstraint(String conditionString, PositionRange location) {
		super("ATOMIC(" + conditionString + ")");
		this.location = location;
	}
	
	@Override
	public PositionRange getLocation() {
		return location;
	}
	
}

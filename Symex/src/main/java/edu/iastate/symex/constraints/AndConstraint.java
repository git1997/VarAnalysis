package edu.iastate.symex.constraints;

import edu.iastate.symex.position.CompositeRange;
import edu.iastate.symex.position.PositionRange;

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
		super("AND(" + constraint1.featureExpr + ", " + constraint2.featureExpr + ")");
		this.constraint1 = constraint1;
		this.constraint2 = constraint2;
	}
	
	public Constraint getConstraint1() {
		return constraint1;
	}
	
	public Constraint getConstraint2() {
		return constraint2;
	}

	@Override
	public PositionRange getLocation() {
		return new CompositeRange(constraint1.getLocation(), constraint2.getLocation());
	}
	
}

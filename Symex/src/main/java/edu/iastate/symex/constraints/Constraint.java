package edu.iastate.symex.constraints;

import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;

/**
 * 
 * @author HUNG
 *
 */
public abstract class Constraint {
	
	public static final Constraint TRUE	 = ConstraintFactory.createTrueConstraint();
	
	public static final Constraint FALSE = ConstraintFactory.createFalseConstraint();
	
	/**
	 * Location(s) of the constraint in the source code
	 */
	protected PositionRange location = Range.UNDEFINED;
	
	/*
	 * Methods
	 */

	/**
	 * @return A string describing the constraint
	 */
	public abstract String toDebugString();
	
	public void setLocation(PositionRange location) {
		this.location = location;
	}
	
	public PositionRange getLocation() {
		return location;
	}
	
	/**
	 * @return True if the constraint is satisfiable
	 */
	public abstract boolean isSatisfiable();
	
	/**
	 * @return True if the constraint is a tautology
	 */
	public boolean isTautology() {
		return ! ConstraintFactory.createNotConstraint(this).isSatisfiable();
	}
	
	/**
	 * @return True if the constraint is a contradiction
	 */
	public boolean isContradiction() {
		return ! isSatisfiable();
	}
	
	/**
	 * @return True if the two constraints are equivalent
	 */
	public boolean equivalentTo(Constraint constraint) {
		return this.implies(constraint) && constraint.implies(this);
	}
	
	/**
	 * @return True if the two constraints are opposite of each other
	 */
	public boolean oppositeOf(Constraint constraint) {
		return equivalentTo(ConstraintFactory.createNotConstraint(constraint));
	}
	
	/**
	 * Returns true if this constraint implies another constraint 
	 * (e.g. A & B implies A, but A & B does not imply B & C).
	 */
	public boolean implies(Constraint constraint) {
		return !(ConstraintFactory.createAndConstraint(this, ConstraintFactory.createNotConstraint(constraint)).isSatisfiable());
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
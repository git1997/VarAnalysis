package edu.iastate.symex.constraints;

import edu.iastate.symex.position.CompositeRange;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public abstract class ConstraintFactory {
	
	private static ConstraintFactory factory = new BddConstraintFactory();
	
	public static Constraint createTrueConstraint() {
		return factory.createTrue();
	}
	
	public static Constraint createFalseConstraint() {
		return factory.createFalse();
	}
	
	public static Constraint createAtomicConstraint(String conditionString, PositionRange location) {
		Constraint constraint = factory.createAtomic(conditionString);
		constraint.setLocation(location);
		return constraint;
	}

	public static Constraint createAndConstraint(Constraint constraint1, Constraint constraint2) {
		Constraint constraint = factory.createAnd(constraint1, constraint2);
		constraint.setLocation(new CompositeRange(constraint1.getLocation(), constraint2.getLocation()));
		return constraint;
	}
	
	public static Constraint createOrConstraint(Constraint constraint1, Constraint constraint2) {
		Constraint constraint = factory.createOr(constraint1, constraint2);
		constraint.setLocation(new CompositeRange(constraint1.getLocation(), constraint2.getLocation()));
		return constraint;
	}
	
	public static Constraint createNotConstraint(Constraint oppositeConstraint) {
		Constraint constraint = factory.createNot(oppositeConstraint);
		constraint.setLocation(oppositeConstraint.getLocation());
		return constraint;
	}
	
	protected abstract Constraint createTrue();
	
	protected abstract Constraint createFalse();
	
	protected abstract Constraint createAtomic(String conditionString);
	
	protected abstract Constraint createAnd(Constraint constraint1, Constraint constraint2);
	
	protected abstract Constraint createOr(Constraint constraint1, Constraint constraint2);
	
	protected abstract Constraint createNot(Constraint oppositeConstraint);
	
}

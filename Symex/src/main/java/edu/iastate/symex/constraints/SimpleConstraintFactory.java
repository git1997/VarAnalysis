package edu.iastate.symex.constraints;

/**
 * 
 * @author HUNG
 *
 */
public class SimpleConstraintFactory extends ConstraintFactory {

	@Override
	protected Constraint createTrue() {
		return new SimpleConstraint("TRUE");
	}

	@Override
	protected Constraint createFalse() {
		return new SimpleConstraint("FALSE");
	}

	@Override
	protected Constraint createAtomic(String conditionString) {
		return new SimpleConstraint("ATOMIC(" + conditionString + ")");
	}

	@Override
	protected Constraint createAnd(Constraint constraint1, Constraint constraint2) {
		return new SimpleConstraint("AND(" + ((SimpleConstraint) constraint1).featureExpr + ", " + ((SimpleConstraint) constraint2).featureExpr + ")");
	}

	@Override
	protected Constraint createOr(Constraint constraint1, Constraint constraint2) {
		return new SimpleConstraint("OR(" + ((SimpleConstraint) constraint1).featureExpr + ", " + ((SimpleConstraint) constraint2).featureExpr + ")");
	}

	@Override
	protected Constraint createNot(Constraint oppositeConstraint) {
		return new SimpleConstraint("NOT(" + ((SimpleConstraint) oppositeConstraint).featureExpr + ")");
	}

}

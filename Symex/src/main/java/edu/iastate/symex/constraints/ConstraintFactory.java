package edu.iastate.symex.constraints;

import edu.iastate.symex.datamodel.nodes.LiteralNode;

/**
 * 
 * @author HUNG
 *
 */
public class ConstraintFactory {
	
	public static Constraint createAtomicConstraint(LiteralNode conditionString) {
		return new AtomicConstraint(conditionString);
	}

	public static Constraint createAndConstraint(Constraint constraint1, Constraint constraint2) {
		return new AndConstraint(constraint1, constraint2);
	}
	
	public static Constraint createOrConstraint(Constraint constraint1, Constraint constraint2) {
		return new OrConstraint(constraint1, constraint2);
	}
	
	public static Constraint createNotConstraint(Constraint oppositeConstraint) {
		return new NotConstraint(oppositeConstraint);
	}
	
}

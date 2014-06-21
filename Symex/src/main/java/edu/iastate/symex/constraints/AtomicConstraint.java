package edu.iastate.symex.constraints;

import edu.iastate.symex.datamodel.nodes.LiteralNode;

/**
 * 
 * @author HUNG
 *
 */
public class AtomicConstraint extends Constraint {
	
	public LiteralNode conditionString; // Use a literal node to describe and locate the condition string
	
	/**
	 * Protected constructor, called from ConstraintFactory only.
	 */
	protected AtomicConstraint(LiteralNode conditionString) {
		super("ATOMIC(" + conditionString.getStringValue() + ")");
		this.conditionString = conditionString;
	}
	
	public LiteralNode getConditionString() {
		return conditionString;
	}
	
}

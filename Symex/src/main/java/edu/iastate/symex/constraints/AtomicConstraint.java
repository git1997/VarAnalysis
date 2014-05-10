package edu.iastate.symex.constraints;

import de.fosd.typechef.featureexpr.FeatureExprFactory;
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
		super(FeatureExprFactory.createDefinedExternal(conditionString.getStringValue()));
		this.conditionString = conditionString;
	}
	
	public LiteralNode getConditionString() {
		return conditionString;
	}
	
}

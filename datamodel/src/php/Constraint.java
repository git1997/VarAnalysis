package php;

import datamodel.nodes.LiteralNode;

/**
 * 
 * @author HUNG
 *
 */
public class Constraint {

	private LiteralNode conditionString;
	private boolean isTrueBranch;
	
	/**
	 * Constructor
	 * @param conditionString
	 * @param isTrueBranch
	 */
	public Constraint(LiteralNode conditionString, boolean isTrueBranch) {
		this.conditionString = conditionString;
		this.isTrueBranch = isTrueBranch;
	}
	
	/*
	 * Get properties
	 */
	
	public LiteralNode getConditionString() {
		return conditionString;
	}

	public boolean isTrueBranch() {
		return isTrueBranch;
	}
	
}

package edu.iastate.parsers.conditional;

import edu.iastate.symex.constraints.Constraint;

/**
 * 
 * @author HUNG
 *
 * @param <T>
 */
public class CondListSelect<T> extends CondList<T> {

	private Constraint constraint;
	private CondList<T> trueBranchNode;
	private CondList<T> falseBranchNode;
	
	/**
	 * Protected constructor, called from CondListFactory only.
	 */ 
	protected CondListSelect(Constraint constraint, CondList<T> trueBranchNode, CondList<T> falseBranchNode) {
		this.constraint = constraint;
		this.trueBranchNode = trueBranchNode;
		this.falseBranchNode = falseBranchNode;
	}
	
	public Constraint getConstraint() {
		return constraint;
	}

	public CondList<T> getTrueBranchNode() {
		return trueBranchNode;
	}

	public CondList<T> getFalseBranchNode() {
		return falseBranchNode;
	}

	@Override
	public String toDebugString() {
		String retString = System.lineSeparator() + "#if (" + constraint.toDebugString() + ")" + System.lineSeparator()
				+ trueBranchNode.toDebugString() + System.lineSeparator()
				+ "#else" + System.lineSeparator()
				+ falseBranchNode.toDebugString() + System.lineSeparator()
				+ "#endif" + System.lineSeparator();
		return retString;
	}
	
}

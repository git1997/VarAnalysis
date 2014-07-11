package edu.iastate.parsers.conditional;

import java.util.ArrayList;

import edu.iastate.symex.constraints.Constraint;

/**
 * 
 * @author HUNG
 *
 * @param <T>
 */
public class CondListSelect<T> extends CondList<T> {

	private Constraint constraint;
	private CondList<T> trueBranchNode;		// Can be null
	private CondList<T> falseBranchNode;	// Can be null
	
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
	public ArrayList<T> getLeftMostItems() {
		ArrayList<T> list = new ArrayList<T>();
		if (trueBranchNode != null)
			list.addAll(trueBranchNode.getLeftMostItems());
		if (falseBranchNode != null)
			list.addAll(falseBranchNode.getLeftMostItems());
		return list;
	}
	
	@Override
	public String toDebugString() {
		String retString = System.lineSeparator() + "#if (" + constraint + ")" + System.lineSeparator()
				+ (trueBranchNode != null ? trueBranchNode.toDebugString() : "null") + System.lineSeparator()
				+ "#else" + System.lineSeparator()
				+ (falseBranchNode != null ? falseBranchNode.toDebugString() : "null") + System.lineSeparator()
				+ "#endif" + System.lineSeparator();
		return retString;
	}
	
}

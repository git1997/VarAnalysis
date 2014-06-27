package edu.iastate.parsers.tree;

import edu.iastate.symex.constraints.Constraint;

/**
 * 
 * @author HUNG
 *
 * @param <T>
 */
public class TreeSelectNode<T> extends TreeNode<T> {

	private Constraint constraint;
	private TreeNode<T> trueBranchNode;		// Can be null
	private TreeNode<T> falseBranchNode;	// Can be null
	
	public TreeSelectNode(Constraint constraint, TreeNode<T> trueBranchNode, TreeNode<T> falseBranchNode) {
		this.constraint = constraint;
		this.trueBranchNode = trueBranchNode;
		this.falseBranchNode = falseBranchNode;
	}
	
	public Constraint getConstraint() {
		return constraint;
	}

	public TreeNode<T> getTrueBranchNode() {
		return trueBranchNode;
	}

	public TreeNode<T> getFalseBranchNode() {
		return falseBranchNode;
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

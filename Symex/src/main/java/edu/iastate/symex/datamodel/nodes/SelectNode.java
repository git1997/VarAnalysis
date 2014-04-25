package edu.iastate.symex.datamodel.nodes;

import edu.iastate.symex.datamodel.DataModelVisitor;

/**
 * 
 * @author HUNG
 * 
 */
public class SelectNode extends DataNode {

	private LiteralNode conditionString; 	// Use a literal node to describe and locate the condition string (can be null)

	private DataNode nodeInTrueBranch; 		// Can be null

	private DataNode nodeInFalseBranch; 	// Can be null

	/*
	 * Constructors.
	 */

	/**
	 * Package-level constructor, called from DataNodeFactory only.
	 */
	SelectNode(LiteralNode conditionString, DataNode nodeInTrueBranch,DataNode nodeInFalseBranch) {
		this.conditionString = conditionString;

		if (nodeInTrueBranch != null && checkAndUpdateDepth(nodeInTrueBranch))
			this.nodeInTrueBranch = nodeInTrueBranch;

		if (nodeInFalseBranch != null && checkAndUpdateDepth(nodeInFalseBranch))
			this.nodeInFalseBranch = nodeInFalseBranch;
	}

	public LiteralNode getConditionString() {
		return conditionString;
	}

	public DataNode getNodeInTrueBranch() {
		return nodeInTrueBranch;
	}

	public DataNode getNodeInFalseBranch() {
		return nodeInFalseBranch;
	}

	public String getSymbolicValue() {
		return "__SELECTION_" + this.hashCode() + "__";
	}

	public static String getSymbolicValueRegularExpression() {
		return "__SELECTION_\\d+__";
	}

	@Override
	final public String getApproximateStringValue() {
		if (getDepth() > 5)
			return this.getSymbolicValue();

		String trueBranchValue = (nodeInTrueBranch != null ? nodeInTrueBranch.getApproximateStringValue() : "");
		String falseBranchValue = (nodeInFalseBranch != null ? nodeInFalseBranch.getApproximateStringValue() : "");
		if (nodeInTrueBranch != null && !containsSymbolicValues(trueBranchValue))
			return trueBranchValue;
		else if (nodeInFalseBranch != null && !containsSymbolicValues(falseBranchValue))
			return falseBranchValue;
		else
			return this.getSymbolicValue();
	}

	/**
	 * Returns true if the string contains symbolic values
	 */
	private boolean containsSymbolicValues(String string) {
		return string.matches("(?s).*" + SymbolicNode.getSymbolicValueRegularExpression() + ".*") // (?s) to consider line terminators
				|| string.matches("(?s).*" + SelectNode.getSymbolicValueRegularExpression()	+ ".*")
				|| string.matches("(?s).*" + ObjectNode.getSymbolicValueRegularExpression() + ".*");
	}

	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitSelectNode(this);
		if (conditionString != null)
			conditionString.accept(dataModelVisitor);
		if (nodeInTrueBranch != null)
			nodeInTrueBranch.accept(dataModelVisitor);
		if (nodeInFalseBranch != null)
			nodeInFalseBranch.accept(dataModelVisitor);
	}
	
}

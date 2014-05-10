package edu.iastate.symex.datamodel.nodes;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.datamodel.DataModelVisitor;

/**
 * 
 * @author HUNG
 * 
 */
public class SelectNode extends DataNode {

	private Constraint constraint;

	private DataNode nodeInTrueBranch; 		// Can be null

	private DataNode nodeInFalseBranch; 	// Can be null

	/*
	 * Constructors.
	 */

	/**
	 * Protected constructor, called from DataNodeFactory only.
	 */
	protected SelectNode(Constraint constraint, DataNode nodeInTrueBranch,DataNode nodeInFalseBranch) {
		this.constraint = constraint;

		if (nodeInTrueBranch != null && checkAndUpdateDepth(nodeInTrueBranch))
			this.nodeInTrueBranch = nodeInTrueBranch;

		if (nodeInFalseBranch != null && checkAndUpdateDepth(nodeInFalseBranch))
			this.nodeInFalseBranch = nodeInFalseBranch;
	}

	public Constraint getConstraint() {
		return constraint;
	}

	public DataNode getNodeInTrueBranch() {
		return nodeInTrueBranch;
	}

	public DataNode getNodeInFalseBranch() {
		return nodeInFalseBranch;
	}

	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitSelectNode(this);
		if (nodeInTrueBranch != null)
			nodeInTrueBranch.accept(dataModelVisitor);
		if (nodeInFalseBranch != null)
			nodeInFalseBranch.accept(dataModelVisitor);
	}
	
}

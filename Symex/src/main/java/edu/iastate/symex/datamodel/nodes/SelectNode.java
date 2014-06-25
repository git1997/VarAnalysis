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

	private DataNode nodeInTrueBranch;

	private DataNode nodeInFalseBranch;

	/*
	 * Constructors.
	 */

	/**
	 * Protected constructor, called from DataNodeFactory only.
	 */
	protected SelectNode(Constraint constraint, DataNode nodeInTrueBranch, DataNode nodeInFalseBranch) {
		this.constraint = constraint;

		if (checkAndUpdateDepth(nodeInTrueBranch))
			this.nodeInTrueBranch = nodeInTrueBranch;
		else
			this.nodeInTrueBranch = DataNodeFactory.createSymbolicNode();

		if (checkAndUpdateDepth(nodeInFalseBranch))
			this.nodeInFalseBranch = nodeInFalseBranch;
		else
			this.nodeInFalseBranch = DataNodeFactory.createSymbolicNode();
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
		nodeInTrueBranch.accept(dataModelVisitor);
		nodeInFalseBranch.accept(dataModelVisitor);
	}
	
}

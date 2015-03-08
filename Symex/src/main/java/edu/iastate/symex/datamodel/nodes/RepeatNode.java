package edu.iastate.symex.datamodel.nodes;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.datamodel.DataModelVisitor;

/**
 * 
 * @author HUNG
 *
 */
public class RepeatNode extends DataNode {
	
	private Constraint constraint;
	
	private DataNode childNode;				// The dataNode which is wrapped around by the repeatNode.

	/**
	 * Protected constructor, called from DataNodeFactory only.
	 */
	protected RepeatNode(Constraint constraint, DataNode dataNode) {
		this.constraint = constraint;
		if (checkAndUpdateSize(dataNode))
			this.childNode = dataNode;
		else
			this.childNode = DataNodeFactory.createSymbolicNode();
	}
	
	public Constraint getConstraint() {
		return constraint;
	}
	
	public DataNode getChildNode() {
		return childNode;
	}
	
	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		boolean continueVisit = dataModelVisitor.visitRepeatNode(this);
		if (!continueVisit)
			return;
		
		childNode.accept(dataModelVisitor);
	}

}

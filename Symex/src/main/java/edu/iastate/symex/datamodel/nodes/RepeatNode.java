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
	 * Constructor
	 */
	public RepeatNode(Constraint constraint, DataNode dataNode) {
		this.constraint = constraint;
		if (checkAndUpdateDepth(dataNode))
			this.childNode = dataNode;
	}
	
	public Constraint getConstraint() {
		return constraint;
	}
	
	public DataNode getChildNode() {
		return childNode;
	}
	
	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitRepeatNode(this);
		childNode.accept(dataModelVisitor);
	}

}

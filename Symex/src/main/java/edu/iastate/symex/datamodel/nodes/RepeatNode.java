package edu.iastate.symex.datamodel.nodes;

import edu.iastate.symex.datamodel.DataModelVisitor;

/**
 * 
 * @author HUNG
 *
 */
public class RepeatNode extends DataNode {
	
	private LiteralNode conditionString;	// Use a literal node to describe and locate the condition string
	
	private DataNode childNode;				// The dataNode which is wrapped around by the repeatNode.

	/**
	 * Constructor
	 */
	public RepeatNode(LiteralNode conditionString, DataNode dataNode) {
		this.conditionString = conditionString;
		if (checkAndUpdateDepth(dataNode))
			this.childNode = dataNode;
	}
	
	public LiteralNode getConditionString() {
		return conditionString;
	}
	
	public DataNode getChildNode() {
		return childNode;
	}
	
	@Override
	public String getApproximateStringValue() {
		return childNode.getApproximateStringValue();
	}
	
	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitRepeatNode(this);
		conditionString.accept(dataModelVisitor);
		childNode.accept(dataModelVisitor);
	}

}

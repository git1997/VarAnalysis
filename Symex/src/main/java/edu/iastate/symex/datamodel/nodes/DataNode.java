package edu.iastate.symex.datamodel.nodes;

import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.config.SymexConfig;
import edu.iastate.symex.datamodel.DataModelVisitor;
import edu.iastate.symex.datamodel.nodes.SpecialNode.BooleanNode;

/**
 * 
 * @author HUNG
 *
 */
public abstract class DataNode {

	private int size;	// The size of the tree at this node
	
	/**
	 * Protected constructor.
	 */
	protected DataNode() {
		this.size = 1;
	}
	
	public int getSize() {
		return size;
	}
	
	/**
	 * Returns true if the childNode can be added to this node
	 *   (the size of this node has not exceeded a limit).
	 * In that case, also update the size of this node.
	 */
	protected boolean checkAndUpdateSize(DataNode childNode) {
		if (this.size + childNode.size <= SymexConfig.DATA_MODEL_MAX_SIZE) {
			this.size += childNode.size;
			return true;
		}
		else {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In DataNode.java: Data model has reached its maximum size of " + SymexConfig.DATA_MODEL_MAX_SIZE);
			return false;
		}
	}
	
	/**
	 * Returns either an exact string value or null.
	 * Subclasses should override this method if necessary.
	 */
	public String getExactStringValueOrNull() {
		return null;
	}
	
	/**
	 * Returns a string value composed of only literal nodes in the current DataNode.
	 * (Select, Repeat, Symbolic, etc. are treated as empty strings.)
	 * NOTE: This is an approximation of the actual value and should be used with care.
	 * If we want to get the exact value, use DataNode.getExactStringValueOrNull() instead.
	 */
	public String getStringValueFromLiteralNodes() {
		if (this instanceof LiteralNode) {
			return ((LiteralNode) this).getStringValue();
		}
		else if (this instanceof ConcatNode) {
			StringBuilder str = new StringBuilder();
			for (DataNode child : ((ConcatNode) this).getChildNodes())
				str.append(child.getStringValueFromLiteralNodes());
			return str.toString();
		}
		else
			return "";
	}

	/**
	 * Visitor pattern
	 * @param dataModelVisitor
	 */
	public abstract void accept(DataModelVisitor dataModelVisitor);
	
	/*
	 * Handling Boolean operations.
	 */
	
	/**
	 * Converts to Boolean value
	 */
	public BooleanNode convertToBooleanValue() {
		if (this instanceof BooleanNode)
			return (BooleanNode) this;
		
		String stringValue = this.getExactStringValueOrNull();
		if (stringValue == null)
			return BooleanNode.UNKNOWN;
		
		// TODO Handle strings "1", "0", "TRUE", "FALSE", etc.
		if (!stringValue.isEmpty())
			return BooleanNode.TRUE;
		else
			return BooleanNode.FALSE;
	}
	
	/**
	 * Implements operator '=='
	 * E.g., TRUE == 1 returns TRUE
	 * @see edu.iastate.symex.datamodel.nodes.SpecialNode.BooleanNode.isEqualTo(BooleanNode)
	 */
	public BooleanNode isEqualTo(DataNode dataNode) {
		if (this instanceof BooleanNode || dataNode instanceof BooleanNode) {
			BooleanNode v1 = this.convertToBooleanValue();
			BooleanNode v2 = dataNode.convertToBooleanValue();
			return v1.isEqualTo(v2);				
		}
		
		if (this == dataNode)
			return BooleanNode.TRUE;
		
		String leftValue = this.getExactStringValueOrNull();
		String rightValue = dataNode.getExactStringValueOrNull();
		if (leftValue == null || rightValue == null)
			return BooleanNode.UNKNOWN;
		
		if (leftValue.equals(rightValue))
			return BooleanNode.TRUE;
		else
			return BooleanNode.FALSE;
	}
	
	/**
	 * Implements operator '==='
	 * E.g., TRUE === 1 returns FALSE
	 * @see edu.iastate.symex.datamodel.nodes.SpecialNode.BooleanNode.isIdenticalTo(BooleanNode)
	 */
	public BooleanNode isIdenticalTo(DataNode dataNode) {
		if (this instanceof BooleanNode && dataNode instanceof BooleanNode) {
			BooleanNode v1 = (BooleanNode) this;
			BooleanNode v2 = (BooleanNode) dataNode;
			return v1.isIdenticalTo(v2);				
		}
		
		if (this == dataNode)
			return BooleanNode.TRUE;
		else
			return BooleanNode.UNKNOWN; // TODO Currently returning UNKNOWN since the values can be symbolic, this code could be improved by breaking down different cases
	}
	
}
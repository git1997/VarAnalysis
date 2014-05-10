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

	private int depth;	// The depth of the tree at this node
	
	/**
	 * Protected constructor.
	 */
	protected DataNode() {
		this.depth = 1;
	}
	
	public int getDepth() {
		return depth;
	}
	
	/**
	 * Returns true if the childNode can be added to this node
	 * (the depth of this node has not exceeded its limit).
	 * In that case, also update the depth of this node.
	 * @return
	 */
	protected boolean checkAndUpdateDepth(DataNode childNode) {
		if (childNode.depth + 1 <= SymexConfig.DATA_MODEL_MAX_DEPTH) {
			if (childNode.depth + 1 > this.depth)
				this.depth = childNode.depth + 1;
			return true;
		}
		else {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In DataNode.java: Data model has reached its maximum depth of " + SymexConfig.DATA_MODEL_MAX_DEPTH);
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
	 */
	public BooleanNode isEqualTo(DataNode dataNode) {
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
	 */
	public BooleanNode isIdenticalTo(DataNode dataNode) {
		if (this == dataNode)
			return BooleanNode.TRUE;
		else
			return BooleanNode.FALSE;
	}
	
}
package datamodel.nodes;

import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



import util.logging.MyLevel;
import util.logging.MyLogger;
import config.DataModelConfig;
import datamodel.GraphvizFormat;

/**
 * 
 * @author HUNG
 *
 */
public abstract class DataNode {

	private static int nodeCount = 0;
	
	private int nodeId;		// The unique ID of the node
	
	protected int depth;	// The depth of the tree at this node
	
	/**
	 * Protected constructor.
	 */
	protected DataNode() {
		this.nodeId = ++nodeCount;
		this.depth = 1;
	}
	
	/**
	 * Returns a cloned version of this node.
	 */
	public abstract DataNode clone();
	
	/**
	 * Returns a compacted version of this node.
	 * This method is overridden in some of its subclasses.
	 */
	public DataNode compact() {
		return this;
	}
	
	/*
	 * Get properties
	 */
	
	private int getNodeId() {
		return nodeId;
	}
	
	/**
	 * Returns true if the childNode can be added to this node
	 * (the depth of this node has not exceeded its limit).
	 * In that case, also update the depth of this node.
	 * @return
	 */
	protected boolean checkAndUpdateDepth(DataNode childNode) {
		if (childNode.depth + 1 <= DataModelConfig.DATA_MODEL_MAX_DEPTH) {
			if (childNode.depth + 1 > this.depth)
				this.depth = childNode.depth + 1;
			return true;
		}
		else {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In DataNode.java: Data model has reached its maximum depth of " + DataModelConfig.DATA_MODEL_MAX_DEPTH);
			return false;
		}
	}
	
	/**
	 * Returns the approximate string value of this node
	 */
	public abstract String getApproximateStringValue();
	
	/*
	 * Provide formatting for XML.
	 */
	
	/**
	 * Prints the graph rooted at this node to XML format.
	 * This method will be overridden by the methods of its subclasses.
	 * The parentNodes are used to detect unexpected loops in the tree.
	 */
	public abstract Element printGraphToXmlFormat(Document document, HashSet<DataNode> parentNodes);
	
	/**
	 * Detects loops during the printing of the data model tree.
	 * Also checks if the data model has reached its maximum depth.
	 */
	protected boolean checkForLoops(HashSet<DataNode> parentNodes) {
		if (parentNodes.contains(this)) {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In DataNode.java: A loop was detected in the data model.");
			return true;
		}
		else if (parentNodes.size() == DataModelConfig.DATA_MODEL_MAX_DEPTH) {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In DataNode.java: Data model has reached its maximum depth of " + DataModelConfig.DATA_MODEL_MAX_DEPTH);
			return true;
		}
		else
			return false;
	}
	
	/*
	 * Provide formatting for Graphviz.
	 */
	
	public String getGraphvizNodeId() {
		return "DataNode" + getNodeId();
	}
	
	public String getGraphvizLabel() {
		return this.getGraphvizNodeId();
	}
	
	public String getGraphvizAttributes() {
		return "";
	}
	
	/**
	 * Prints the graph rooted at this node to Graphviz format.
	 * This method is overridden in some of its subclasses.
	 * @param setOfPrintedNodes		To avoid printing one node twice
	 */
	public String printGraphToGraphvizFormat(HashSet<DataNode> setOfPrintedNodes) {
		if (setOfPrintedNodes.contains(this))
			return "";
		
		setOfPrintedNodes.add(this);
		return GraphvizFormat.printNode(this);
	}

	public abstract void visit(DataModelVisitor dataModelVisitor);
	
}
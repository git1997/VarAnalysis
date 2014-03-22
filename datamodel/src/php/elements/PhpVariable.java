package php.elements;

import java.util.HashSet;

import logging.MyLevel;
import logging.MyLogger;

import datamodel.GraphvizFormat;
import datamodel.nodes.DataNode;
import datamodel.nodes.ConcatNode;
import datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class PhpVariable extends PhpElement {
	
	private String name;		// The name of the phpVariable
	
	private DataNode dataNode;	// The string value of the phpVariable, represented by a dataNode.
	
	/**
	 * Constructor
	 * @param name
	 */
	public PhpVariable(String name) {
		this.name = name;
		this.dataNode = null; // Its value must be set shortly after the creation of this object.
	}
	
	/*
	 * Get properties
	 */
	
	public String getName() {
		return name;
	}
	
	public DataNode getDataNode() {
		if (dataNode != null)
			return dataNode;
		else {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In PhpVariable.java: Variable " + name + " has no associated data node.");
			return new SymbolicNode();
		}
	}
	
	/*
	 * Set properties
	 */
	
	public void setDataNode(DataNode dataNode) {
		this.dataNode = dataNode;
	}
	
	public void appendStringValue(DataNode dataNode) {
		if (this.dataNode == null)
			this.dataNode = dataNode;
		else
			this.dataNode = new ConcatNode(this.dataNode, dataNode);
	}

	/*
	 * Provide formatting for Graphviz.
	 */
	
	@Override
	public String getGraphvizLabel() {
		return "$" + this.name;
	}
	
	@Override
	public String printGraphToGraphvizFormat(HashSet<DataNode> setOfPrintedNodes) {
		StringBuilder string = new StringBuilder();
		string.append(GraphvizFormat.printElement(this));
		if (dataNode != null) {
			string.append(this.dataNode.printGraphToGraphvizFormat(setOfPrintedNodes));
			string.append(GraphvizFormat.printEdge(this, dataNode));
		}
		return string.toString();
	}
	 
}

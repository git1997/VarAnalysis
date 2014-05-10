package edu.iastate.symex.core;

import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;

/**
 * 
 * @author HUNG
 *
 */
public class PhpVariable {
	
	private String name;		// The name of the phpVariable
	
	private DataNode dataNode;	// The value of the phpVariable, represented by a dataNode.
	
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
			return DataNodeFactory.createSymbolicNode();
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
			this.dataNode = DataNodeFactory.createCompactConcatNode(this.dataNode, dataNode);
	}

}

package php.elements;

import java.util.HashSet;

import datamodel.GraphvizFormat;
import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public abstract class PhpElement {
	
	private static int elementCount = 0;
	
	private int elementId;		// The unique ID of the element
	
	/**
	 * Protected constructor.
	 */
	protected PhpElement() {
		this.elementId = ++elementCount;
	}
	
	/*
	 * Get properties
	 */
	
	private int getElementId() {
		return elementId;
	}
	
	/*
	 * Provide formatting for Graphviz.
	 */
	
	public String getGraphvizNodeId() {
		return "PhpElement" + getElementId();
	}
	
	public String getGraphvizLabel() {
		return this.getGraphvizNodeId();
	}
	
	public String getGraphvizAttributes() {
		return "";
	}
	
	/**
	 * Prints the element and the graph representing its string value.
	 * This method is typically overriden by the methods of its subclasses.
	 * @param setOfPrintedNodes		To avoid printing one node twice
	 */
	public String printGraphToGraphvizFormat(HashSet<DataNode> setOfPrintedNodes) {
		return GraphvizFormat.printElement(this);
	}
	
}

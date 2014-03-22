package datamodel.nodes;

import java.util.HashMap;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author HUNG
 *
 */
public class ArrayNode extends DataNode {
	
	private HashMap<String, DataNode> elementTable = new HashMap<String, DataNode>();

	@Override
	public DataNode clone() {
		ArrayNode clonedNode = new ArrayNode();
		for (String key : elementTable.keySet())
			clonedNode.elementTable.put(key, elementTable.get(key).clone());
		return clonedNode;
	}

	/*
	 * Get properties
	 */
	
	public DataNode getElement(String key) {
		return elementTable.get(key);
	}
	
	@Override
	public String getApproximateStringValue() {
		StringBuilder string = new StringBuilder();
		string.append("Array(");
		for (String key : elementTable.keySet()) {
			string.append(key);
			string.append("=>");
			string.append(elementTable.get(key).getApproximateStringValue());
		}
		string.append(")");
		return string.toString();
	}
	
	/*
	 * Set properties
	 */
	
	public void setElement(String key, DataNode dataNode) {
		elementTable.put(key, dataNode);
	}

	/*
	 * Provide formatting for XML.
	 */
	
	@Override
	public Element printGraphToXmlFormat(Document document,	HashSet<DataNode> parentNodes) {
		LiteralNode literalNode = new LiteralNode(this.getApproximateStringValue());
		return literalNode.printGraphToXmlFormat(document, parentNodes);
	}
	
	/*
	 * Provide formatting for Graphviz.
	 */
	
	@Override
	public String printGraphToGraphvizFormat(HashSet<DataNode> setOfPrintedNodes) {
		LiteralNode literalNode = new LiteralNode(this.getApproximateStringValue());
		return literalNode.printGraphToGraphvizFormat(setOfPrintedNodes);
	}

}

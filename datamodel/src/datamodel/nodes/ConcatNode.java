package datamodel.nodes;

import java.util.ArrayList;
import java.util.HashSet;

import logging.MyLevel;
import logging.MyLogger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import config.DataModelConfig;
import datamodel.GraphvizFormat;

/**
 * 
 * @author HUNG
 *
 */
public class ConcatNode extends DataNode {
	
	private ArrayList<DataNode> childNodes = new ArrayList<DataNode>();

	/*
	 * Constructors
	 */
	
	public ConcatNode() {
	}
	
	public ConcatNode(DataNode childNode1, DataNode childNode2) {
		this.appendChildNode(childNode1);
		this.appendChildNode(childNode2);
	}
	
	@Override
	public DataNode clone() {
		ConcatNode clonedNode = new ConcatNode();
		clonedNode.depth = this.depth;
		for (DataNode childNode : this.childNodes) 
			clonedNode.childNodes.add(childNode.clone());
		return clonedNode;
	}
	
	/*
	 * Get properties
	 */
	
	public ArrayList<DataNode> getChildNodes() {
		return new ArrayList<DataNode>(this.childNodes);
	}
	
	@Override
	public String getApproximateStringValue() {
		StringBuilder string = new StringBuilder();
		for (DataNode childNode : childNodes) {
			string.append(childNode.getApproximateStringValue());
		}
		return string.toString();
	}
	
	/*
	 * Set properties
	 */

	public void appendChildNode(DataNode childNode) {
		if (childNode instanceof ConcatNode)
			this.appendChildNodes(((ConcatNode) childNode).getChildNodes());
		else {
			if (checkAndUpdateDepth(childNode))
				childNodes.add(childNode);
		}
	}	
	
	public void appendChildNodes(ArrayList<DataNode> childNodes) {
		if (childNodes.isEmpty()) {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In ConcatNode.java: appendChildNodes should not have empty parameters.");
			return;
		}		
		for (DataNode childNode : childNodes)
			this.appendChildNode(childNode);
	}
	
	/*
	 * Provide formatting for XML.
	 */
	
	@Override
	public Element printGraphToXmlFormat(Document document, HashSet<DataNode> parentNodes) {
		Element element = document.createElement(DataModelConfig.XML_CONCAT);
		if (!checkForLoops(parentNodes)) {
			parentNodes.add(this);
			for (DataNode childNode : childNodes)
				element.appendChild(childNode.printGraphToXmlFormat(document, parentNodes));
			parentNodes.remove(this);
		}
		return element;
	}
	
	/*
	 * Provide formatting for Graphviz.
	 */
	
	@Override
	public String getGraphvizLabel() {
		return "Concat";
	}
	
	@Override
	public String printGraphToGraphvizFormat(HashSet<DataNode> setOfPrintedNodes) {
		if (setOfPrintedNodes.contains(this))
			return "";
		
		setOfPrintedNodes.add(this);
		StringBuilder string = new StringBuilder();
		string.append(GraphvizFormat.printNode(this));
		for (DataNode childNode : childNodes) {
			string.append(childNode.printGraphToGraphvizFormat(setOfPrintedNodes));
			string.append(GraphvizFormat.printEdge(this, childNode));
		}
		return string.toString();
	}

}

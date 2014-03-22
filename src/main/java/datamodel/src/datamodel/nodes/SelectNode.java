package datamodel.nodes;

import java.util.ArrayList;
import java.util.HashSet;


import org.w3c.dom.Document;
import org.w3c.dom.Element;


import config.DataModelConfig;
import datamodel.GraphvizFormat;

/**
 * 
 * @author HUNG
 *
 */
public class SelectNode extends DataNode {
	
	private LiteralNode conditionString;	// Use a literal node to describe and locate the condition string (can be null)

	private DataNode nodeInTrueBranch;		// Can be null

	private DataNode nodeInFalseBranch;		// Can be null
	
	/*
	 * Constructors.
	 */
	
	private SelectNode() { // Do not allow empty construtor
	}
	
	public SelectNode(LiteralNode conditionString, DataNode nodeInTrueBranch, DataNode nodeInFalseBranch) {
		this.conditionString = conditionString;
		
		if (nodeInTrueBranch != null && checkAndUpdateDepth(nodeInTrueBranch))
			this.nodeInTrueBranch = nodeInTrueBranch;
		
		if (nodeInFalseBranch != null && checkAndUpdateDepth(nodeInFalseBranch))
			this.nodeInFalseBranch = nodeInFalseBranch;
	}
	
	@Override
	public DataNode clone() {
		SelectNode clonedNode = new SelectNode();
		clonedNode.depth = this.depth;
		clonedNode.conditionString = this.conditionString;
		clonedNode.nodeInTrueBranch = (this.nodeInTrueBranch != null ? this.nodeInTrueBranch.clone() : null);
		clonedNode.nodeInFalseBranch = (this.nodeInFalseBranch != null ? this.nodeInFalseBranch.clone() : null);
		return clonedNode;
	}
	
	@Override
	public DataNode compact() {
		// Only continue if the branches are Concat/LiteralNodes
		if ( !( (nodeInTrueBranch instanceof ConcatNode || nodeInTrueBranch instanceof LiteralNode) && (nodeInFalseBranch instanceof ConcatNode || nodeInFalseBranch instanceof LiteralNode) ) )
			return this;
		
		// Count the common nodes
		ArrayList<DataNode> nodesInTrueBranch = new ArrayList<DataNode>();
		if (nodeInTrueBranch instanceof LiteralNode)
			nodesInTrueBranch.add(nodeInTrueBranch);
		else
			nodesInTrueBranch.addAll(((ConcatNode) nodeInTrueBranch).getChildNodes());
		ArrayList<DataNode> nodesInFalseBranch = new ArrayList<DataNode>();
		if (nodeInFalseBranch instanceof LiteralNode)
			nodesInFalseBranch.add(nodeInFalseBranch);
		else
			nodesInFalseBranch.addAll(((ConcatNode) nodeInFalseBranch).getChildNodes());
		
		int commonNodesBefore = 0;
		for (int i = 0; i < nodesInTrueBranch.size(); i++) {
			if (i < nodesInFalseBranch.size() && nodesInTrueBranch.get(i) == nodesInFalseBranch.get(i))
				commonNodesBefore++;
			else
				break;
		}
		int commonNodesAfter = 0;
		for (int i = 1; i <= nodesInTrueBranch.size() - commonNodesBefore; i++) {
			if (nodesInFalseBranch.size() - i >= 0 && nodesInTrueBranch.get(nodesInTrueBranch.size() - i) == nodesInFalseBranch.get(nodesInFalseBranch.size() - i))
				commonNodesAfter++;
			else
				break;
		}
		
		// Only continue if the branches share some common nodes
		if (commonNodesBefore == 0 && commonNodesAfter == 0)
			return this;
			
		// Extract the common nodes on the left hand side
		ConcatNode concatNode = new ConcatNode();
		for (int i = 0; i < commonNodesBefore; i++)
			concatNode.appendChildNode(nodesInTrueBranch.get(i));
		
		// Create a selection node for the uncommon parts in the middle
		DataNode diffNodesInTrueBranch;
		if (commonNodesBefore + commonNodesAfter == nodesInTrueBranch.size())
			diffNodesInTrueBranch = null;
		else if (commonNodesBefore + commonNodesAfter + 1 == nodesInTrueBranch.size())
			diffNodesInTrueBranch = nodesInTrueBranch.get(commonNodesBefore);
		else {
			diffNodesInTrueBranch = new ConcatNode();
			for (int i = commonNodesBefore; i < nodesInTrueBranch.size() - commonNodesAfter; i++)
				((ConcatNode) diffNodesInTrueBranch).appendChildNode(nodesInTrueBranch.get(i));
		}
		
		DataNode diffNodesInFalseBranch;
		if (commonNodesBefore + commonNodesAfter == nodesInFalseBranch.size())
			diffNodesInFalseBranch = null;
		else if (commonNodesBefore + commonNodesAfter + 1 == nodesInFalseBranch.size())
			diffNodesInFalseBranch = nodesInFalseBranch.get(commonNodesBefore);
		else {
			diffNodesInFalseBranch = new ConcatNode();
			for (int i = commonNodesBefore; i < nodesInFalseBranch.size() - commonNodesAfter; i++)
				((ConcatNode) diffNodesInFalseBranch).appendChildNode(nodesInFalseBranch.get(i));
		}
		
		if (diffNodesInTrueBranch != null || diffNodesInFalseBranch != null) {
			SelectNode middleNode = new SelectNode(conditionString, diffNodesInTrueBranch, diffNodesInFalseBranch);
			concatNode.appendChildNode(middleNode);
		}
		
		// Extract the common nodes on the right hand side
		for (int i = 1; i <= commonNodesAfter; i++)
			concatNode.appendChildNode(nodesInTrueBranch.get(nodesInTrueBranch.size() - i));		
		
		// Return the compact DataNode that represents the same value as the SelectNode
		if (concatNode.getChildNodes().size() == 1)
			return concatNode.getChildNodes().get(0);
		else
			return concatNode;
	}
	
	/*
	 * Get properties
	 */
	
	public LiteralNode getConditionString() {
		return conditionString;
	}
	
	public DataNode getNodeInTrueBranch() {
		return nodeInTrueBranch;
	}
	
	public DataNode getNodeInFalseBranch() {
		return nodeInFalseBranch;
	}
	
	public String getSymbolicValue() {
		return "__SELECTION_" + this.hashCode() + "__";
	}
	
	public static String getSymbolicValueRegularExpression() {
		return "__SELECTION_\\d+__";
	}
	
	@Override
	final public String getApproximateStringValue() {
		if (depth > 5)
			return this.getSymbolicValue();
		
		String trueBranchValue = (nodeInTrueBranch != null ? nodeInTrueBranch.getApproximateStringValue() : "");
		String falseBranchValue = (nodeInFalseBranch != null ? nodeInFalseBranch.getApproximateStringValue() : "");
		if (nodeInTrueBranch != null && !containsSymbolicValues(trueBranchValue))
			return trueBranchValue;
		else if (nodeInFalseBranch != null && !containsSymbolicValues(falseBranchValue))
			return falseBranchValue;
		else
			return this.getSymbolicValue();
	}
	
	/**
	 * Returns true if the string contains symbolic values
	 */
	private boolean containsSymbolicValues(String string) {
		return string.matches("(?s).*" + SymbolicNode.getSymbolicValueRegularExpression() + ".*") // (?s) to consider line terminators
			|| string.matches("(?s).*" + SelectNode.getSymbolicValueRegularExpression() + ".*")
			|| string.matches("(?s).*" + ObjectNode.getSymbolicValueRegularExpression() + ".*");
	}
	
	/*
	 * Provide formatting for XML.
	 */
	
	@Override
	public Element printGraphToXmlFormat(Document document, HashSet<DataNode> parentNodes) {
		Element element = document.createElement(DataModelConfig.XML_SELECT);
		if (conditionString != null) {
			element.setAttribute(DataModelConfig.XML_STRING_VALUE, conditionString.getStringValue());
			element.setAttribute(DataModelConfig.XML_FILE_PATH, conditionString.getLocation().getLocationAtOffset(0).getFilePath());
			element.setAttribute(DataModelConfig.XML_POSITION, Integer.toString(conditionString.getLocation().getLocationAtOffset(0).getPosition()));
		}
		if (!checkForLoops(parentNodes)) {			
			if (nodeInTrueBranch != null) {
				parentNodes.add(this);
				Element elementInTrueBranch = document.createElement(DataModelConfig.XML_SELECT_TRUE);
				element.appendChild(elementInTrueBranch);
				elementInTrueBranch.appendChild(nodeInTrueBranch.printGraphToXmlFormat(document, parentNodes));
				parentNodes.remove(this);
			}
			if (nodeInFalseBranch != null) {
				parentNodes.add(this);
				Element elementInFalseBranch = document.createElement(DataModelConfig.XML_SELECT_FALSE);
				element.appendChild(elementInFalseBranch);
				elementInFalseBranch.appendChild(nodeInFalseBranch.printGraphToXmlFormat(document, parentNodes));
				parentNodes.remove(this);
			}
		}
		return element;
	}
	
	/*
	 * Provide formatting for Graphviz.
	 */
	
	@Override
	public String getGraphvizLabel() {
		return (conditionString != null ? conditionString.getStringValue() + " ?" : "");
	}
	
	@Override
	public String getGraphvizAttributes() {
		return " shape=diamond";
	}
	
	@Override
	public String printGraphToGraphvizFormat(HashSet<DataNode> setOfPrintedNodes) {
		if (setOfPrintedNodes.contains(this))
			return "";
		
		setOfPrintedNodes.add(this);
		StringBuilder string = new StringBuilder();
		string.append(GraphvizFormat.printNode(this));
		if (nodeInTrueBranch != null) {
			string.append(nodeInTrueBranch.printGraphToGraphvizFormat(setOfPrintedNodes));
			string.append(GraphvizFormat.printEdge(this, nodeInTrueBranch, "True"));
		}
		if (nodeInFalseBranch != null) {
			string.append(nodeInFalseBranch.printGraphToGraphvizFormat(setOfPrintedNodes));
			string.append(GraphvizFormat.printEdge(this, nodeInFalseBranch, "False"));
		}
		return string.toString();
	}
	
}

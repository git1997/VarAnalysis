package datamodel.nodes;

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
public class RepeatNode extends DataNode {
	
	private LiteralNode conditionString;	// Use a literal node to describe and locate the condition string
	
	private DataNode dataNode;				// The dataNode which is wrapped around by the repeatNode.

	/*
	 * Constructor
	 */
	
	public RepeatNode(LiteralNode conditionString, DataNode dataNode) {
		this.conditionString = conditionString;
		if (checkAndUpdateDepth(dataNode))
			this.dataNode = dataNode;
	}
	
	@Override
	public DataNode clone() {
		RepeatNode clonedNode = new RepeatNode(conditionString, dataNode.clone());
		clonedNode.depth = this.depth;
		return clonedNode;
	}
	
	/*
	 * Get properties
	 */

	public DataNode getChildNode() {
		return dataNode;
	}
	
	@Override
	public String getApproximateStringValue() {
		return dataNode.getApproximateStringValue();
	}
	
	/*
	 * Provide formatting for XML.
	 */
	
	@Override
	public Element printGraphToXmlFormat(Document document, HashSet<DataNode> parentNodes) {
		Element element = document.createElement(DataModelConfig.XML_REPEAT);
		
		element.setAttribute(DataModelConfig.XML_STRING_VALUE, conditionString.getStringValue());
		element.setAttribute(DataModelConfig.XML_FILE_PATH, conditionString.getLocation().getLocationAtOffset(0).getFilePath().getPath());
		element.setAttribute(DataModelConfig.XML_POSITION, Integer.toString(conditionString.getLocation().getLocationAtOffset(0).getPosition()));
		
		if (!checkForLoops(parentNodes) && dataNode != null) {
			parentNodes.add(this);
			element.appendChild(dataNode.printGraphToXmlFormat(document, parentNodes));
			parentNodes.remove(this);
		}
		return element;
	}

	/*
	 * Provide formatting for Graphviz.
	 */
	
	@Override
	public String getGraphvizLabel() {
		return conditionString.getStringValue() + " ?";
	}
	
	@Override
	public String getGraphvizAttributes() {
		return " shape=trapezium";
	}
	
	@Override
	public String printGraphToGraphvizFormat(HashSet<DataNode> setOfPrintedNodes) {
		if (setOfPrintedNodes.contains(this))
			return "";
		
		setOfPrintedNodes.add(this);
		StringBuilder string = new StringBuilder();
		string.append(GraphvizFormat.printNode(this));
		if (dataNode != null) {
			string.append(dataNode.printGraphToGraphvizFormat(setOfPrintedNodes));
			string.append(GraphvizFormat.printEdge(this, dataNode));
		}
		return string.toString();
	}

	
	@Override
	public void visit(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitRepeatNode(this);
		conditionString.visit(dataModelVisitor);
		dataNode.visit(dataModelVisitor);
	}

}

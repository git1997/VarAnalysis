package datamodel.nodes.ext;

import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import config.DataModelConfig;

/**
 * 
 * @author HUNG
 *
 */
public class ConcatNode extends DataNode {
	
	private ArrayList<DataNode> childNodes = new ArrayList<DataNode>();

	/**
	 * Creates a ConcatNode read from an XML element.
	 */
	public ConcatNode(Element xmlElement) {
		NodeList childrenList = xmlElement.getChildNodes();
		for (int i = 0; i < childrenList.getLength(); i++) {
			Element childNode = (Element) childrenList.item(i);
			childNodes.add(DataNode.createInstance(childNode));
		}
	}
	
	/**
	 * Creates a ConcatNode read from another ConcatNode.
	 */
	public ConcatNode(datamodel.nodes.ConcatNode concatNode) {
		for (datamodel.nodes.DataNode childNode : concatNode.getChildNodes()) {
			childNodes.add(DataNode.createInstance(childNode));
		}
	}
	
	/*
	 * Get properties
	 */
	
	public ArrayList<DataNode> getChildNodes() {
		return new ArrayList<DataNode>(childNodes);
	}
	
	/**
	 * Compacts the ConcatNode
	 */
	public void compact() {
		// Step 1 - Replace concat child nodes with their own child nodes
		ArrayList<DataNode> childNodesCopy = new ArrayList<DataNode>(childNodes);
		childNodes.clear();
		for (DataNode childNode : childNodesCopy) {
			if (childNode instanceof ConcatNode) {
				ConcatNode concatChildNode = (ConcatNode) childNode;
				concatChildNode.compact();
				childNodes.addAll(concatChildNode.getChildNodes());
			}
			else 
				childNodes.add(childNode);
		}
		
		// Step 2 - Replace consecutive literal child nodes with a combined literal node
		childNodesCopy = new ArrayList<DataNode>(childNodes);
		childNodes.clear();
		for (DataNode childNode : childNodesCopy) {
			if (childNode instanceof LiteralNode && !childNodes.isEmpty() && childNodes.get(childNodes.size() - 1) instanceof LiteralNode) {
				LiteralNode node1 = (LiteralNode) childNodes.get(childNodes.size() - 1);
				LiteralNode node2 = (LiteralNode) childNode;			
				LiteralNode combinedLiteralNode = new LiteralNode(node1, node2);
				childNodes.set(childNodes.size() - 1, combinedLiteralNode);
			}
			else
				childNodes.add(childNode);
		}
	}
	
	/*
	 * Visitor design pattern
	 */
	
	@Override
	final protected void acceptVisitor(DataNodeVisitor visitor) {
		if (visitor.visit(this)) {
			for (DataNode childNode : childNodes)
				childNode.accept(visitor);
		}
	}
	
	/*
	 * Provide formatting for XML.
	 */
	
	@Override
	public Element printGraphToXmlFormat(Document document) {
		Element element = document.createElement(DataModelConfig.XML_CONCAT);
		for (DataNode childNode : childNodes) {
			element.appendChild(childNode.printGraphToXmlFormat(document));
		}
		return element;
	}
	
}

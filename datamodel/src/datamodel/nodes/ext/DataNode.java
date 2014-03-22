package datamodel.nodes.ext;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import logging.MyLevel;
import logging.MyLogger;
import config.DataModelConfig;

/**
 * 
 * @author HUNG
 *
 */
public abstract class DataNode {

	/**
	 * Creates a DataNode read from an XML element.
	 */
	public static DataNode createInstance(Element xmlElement) {
		if (xmlElement == null)
			return null;
		else if (xmlElement.getNodeName().equals(DataModelConfig.XML_CONCAT))
			return new ConcatNode(xmlElement);
		else if (xmlElement.getNodeName().equals(DataModelConfig.XML_SELECT))
			return new SelectNode(xmlElement);
		else if (xmlElement.getNodeName().equals(DataModelConfig.XML_LITERAL))
			return new LiteralNode(xmlElement);
		else if (xmlElement.getNodeName().equals(DataModelConfig.XML_SYMBOLIC))
			return new SymbolicNode(xmlElement);
		else if (xmlElement.getNodeName().equals(DataModelConfig.XML_REPEAT))
			return new RepeatNode(xmlElement);
		else {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In datamodel.nodes.DataNode.java: xmlElement not recognized: " + xmlElement);
			return null;
		}
	}
	
	/**
	 * Creates a DataNode read from another DataNode obtained from the Data Model Creation module
	 */
	public static DataNode createInstance(datamodel.nodes.DataNode dataNode) {
		if (dataNode == null)
			return null;
		else if (dataNode instanceof datamodel.nodes.ConcatNode)
			return new ConcatNode((datamodel.nodes.ConcatNode) dataNode);
		else if (dataNode instanceof datamodel.nodes.SelectNode)
			return new SelectNode((datamodel.nodes.SelectNode) dataNode);
		else if (dataNode instanceof datamodel.nodes.LiteralNode)
			return new LiteralNode((datamodel.nodes.LiteralNode) dataNode);
		else if (dataNode instanceof datamodel.nodes.SymbolicNode)
			return new SymbolicNode((datamodel.nodes.SymbolicNode) dataNode);
		else if (dataNode instanceof datamodel.nodes.RepeatNode)
			return new RepeatNode((datamodel.nodes.RepeatNode) dataNode);
		else {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In ext.datamodel.nodes.DataNode.java: dataNode not recognized: " + dataNode);
			return null;
		}
	}
	
	/*
	 * Visitor design pattern
	 */
	
	/**
	 * Accepts a visitor.
	 */
	public void accept(DataNodeVisitor visitor) {
		visitor.preVisit(this);
		this.acceptVisitor(visitor);
		visitor.postVisit(this);
	}
	
	/**
	 * Dynamic dispatch to internal method for type-specific visit/endVisit.
	 * @see datamodel.nodes.ext.DataNode.accept(DataNodeVisitor)
	 */
	protected abstract void acceptVisitor(DataNodeVisitor visitor);
	
	/*
	 * Provide formatting for XML.
	 */
	
	/**
	 * Prints the graph rooted at this node to XML format.
	 * This method will be overridden by the methods of its subclasses.
	 */
	public abstract Element printGraphToXmlFormat(Document document);
	
}

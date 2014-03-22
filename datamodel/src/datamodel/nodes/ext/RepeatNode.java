package datamodel.nodes.ext;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import config.DataModelConfig;

/**
 * 
 * @author HUNG
 *
 */
public class RepeatNode extends DataNode {

	private DataNode dataNode;
	
	/**
	 * Creates a RepeatNode read from an XML element.
	 */
	public RepeatNode(Element xmlElement) {
		if (xmlElement.hasChildNodes())
			dataNode = DataNode.createInstance((Element) xmlElement.getChildNodes().item(0));
	}
	
	/**
	 * Creates a RepeatNode read from another RepeatNode.
	 */
	public RepeatNode(datamodel.nodes.RepeatNode repeatNode) {
		dataNode = DataNode.createInstance(repeatNode.getChildNode());
	}
	
	/*
	 * Get properties
	 */
	
	public DataNode getDataNode() {
		return dataNode;
	}
	
	/*
	 * Visitor design pattern
	 */
	
	@Override
	protected void acceptVisitor(DataNodeVisitor visitor) {
		if (visitor.visit(this) && dataNode != null)
			dataNode.accept(visitor);
	}
	
	/*
	 * Provide formatting for XML.
	 */
	
	@Override
	public Element printGraphToXmlFormat(Document document) {
		Element element = document.createElement(DataModelConfig.XML_REPEAT);
		if (dataNode != null) {
			element.appendChild(dataNode.printGraphToXmlFormat(document));
		}
		return element;
	}

}

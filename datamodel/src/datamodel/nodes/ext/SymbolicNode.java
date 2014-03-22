package datamodel.nodes.ext;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import config.DataModelConfig;

import php.nodes.PhpNode;

/**
 * 
 * @author HUNG
 *
 */
public class SymbolicNode extends DataNode {
	
	private PhpNode phpNode = null;
	
	private SymbolicNode parentNode = null;
	
	/**
	 * Creates a SymbolicNode read from an XML element.
	 */
	public SymbolicNode(Element xmlElement) {
	}
	
	/**
	 * Creates a SymbolicNode read from another SymbolicNode.
	 */
	public SymbolicNode(datamodel.nodes.SymbolicNode symbolicNode) {
		phpNode = symbolicNode.getPhpNode();
		if (symbolicNode.getParentNode() != null)
			parentNode = new SymbolicNode(symbolicNode.getParentNode()); 
	}
	
	/*
	 * Get properties
	 */
	
	public PhpNode getPhpNode() {
		return phpNode;
	}
	
	public SymbolicNode getParentNode() {
		return parentNode;
	}
	
	/*
	 * Visitor design pattern
	 */
	
	@Override
	final protected void acceptVisitor(DataNodeVisitor visitor) {
		visitor.visit(this);
	}
	
	/*
	 * Provide formatting for XML.
	 */
	
	@Override
	public Element printGraphToXmlFormat(Document document) {
		Element element = document.createElement(DataModelConfig.XML_SYMBOLIC);
		if (phpNode != null) {
			element.setAttribute(DataModelConfig.XML_STRING_VALUE, this.phpNode.getStringValue());
			element.setAttribute(DataModelConfig.XML_FILE_PATH, this.phpNode.getLocation().getLocationAtOffset(0).getFilePath());
			element.setAttribute(DataModelConfig.XML_POSITION, Integer.toString(this.phpNode.getLocation().getLocationAtOffset(0).getPosition()));
		}
		if (parentNode != null) {
			element.appendChild(parentNode.printGraphToXmlFormat(document));
		}
		return element;
	}
	
}

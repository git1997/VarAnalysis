package datamodel.nodes.ext;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import util.sourcetracing.SourceCodeLocation;
import config.DataModelConfig;

/**
 * 
 * @author HUNG
 *
 */
public class SelectNode extends DataNode {

	private LiteralNode conditionString;
	private DataNode nodeInTrueBranch = null;
	private DataNode nodeInFalseBranch = null;
	
	/**
	 * Creates a SelectNode read from an XML element.
	 */
	public SelectNode(Element xmlElement) {
		if (xmlElement.hasAttribute(DataModelConfig.XML_STRING_VALUE)) {
			String stringValue = xmlElement.getAttribute(DataModelConfig.XML_STRING_VALUE);
			String filePath = xmlElement.getAttribute(DataModelConfig.XML_FILE_PATH);
			int position = Integer.valueOf(xmlElement.getAttribute(DataModelConfig.XML_POSITION));
			conditionString = new LiteralNode(new datamodel.nodes.LiteralNode(stringValue, new SourceCodeLocation(new File(filePath), position)));
		}
		
		NodeList childrenList = xmlElement.getChildNodes();
		for (int i = 0; i < childrenList.getLength(); i++) {
			Element childNode = (Element) childrenList.item(i);
			boolean isTrueBranch = (childNode.getNodeName().equals(DataModelConfig.XML_SELECT_TRUE));
			childNode = (Element) childNode.getFirstChild();
			if (isTrueBranch)
				nodeInTrueBranch = DataNode.createInstance(childNode);
			else
				nodeInFalseBranch = DataNode.createInstance(childNode);
		}
	}
	
	/**
	 * Creates a SelectNode read from another SelectNode.
	 */
	public SelectNode(datamodel.nodes.SelectNode selectNode) {
		conditionString = (LiteralNode) DataNode.createInstance(selectNode.getConditionString());
		nodeInTrueBranch = DataNode.createInstance(selectNode.getNodeInTrueBranch());
		nodeInFalseBranch = DataNode.createInstance(selectNode.getNodeInFalseBranch());
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
	
	/*
	 * Visitor design pattern
	 */
	
	@Override
	final protected void acceptVisitor(DataNodeVisitor visitor) {
		if (visitor.visit(this)) {
			if (nodeInTrueBranch != null)
				nodeInTrueBranch.accept(visitor);
			if (nodeInFalseBranch != null)
				nodeInFalseBranch.accept(visitor);
		}
	}
	
	/*
	 * Provide formatting for XML.
	 */
	
	@Override
	public Element printGraphToXmlFormat(Document document) {
		Element element = document.createElement(DataModelConfig.XML_SELECT);
		if (conditionString != null) {
			element.setAttribute(DataModelConfig.XML_STRING_VALUE, conditionString.getStringValue());
			element.setAttribute(DataModelConfig.XML_FILE_PATH, conditionString.getLocation().getLocationAtOffset(0).getFilePath().getPath());
			element.setAttribute(DataModelConfig.XML_POSITION, Integer.toString(conditionString.getLocation().getLocationAtOffset(0).getPosition()));
		}
		if (nodeInTrueBranch != null) {
			Element elementInTrueBranch = document.createElement(DataModelConfig.XML_SELECT_TRUE);
			element.appendChild(elementInTrueBranch);
			elementInTrueBranch.appendChild(nodeInTrueBranch.printGraphToXmlFormat(document));
		}
		if (nodeInFalseBranch != null) {
			Element elementInFalseBranch = document.createElement(DataModelConfig.XML_SELECT_FALSE);
			element.appendChild(elementInFalseBranch);
			elementInFalseBranch.appendChild(nodeInFalseBranch.printGraphToXmlFormat(document));
		}
		return element;
	}
	
}

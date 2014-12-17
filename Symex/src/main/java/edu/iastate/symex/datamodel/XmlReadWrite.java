package edu.iastate.symex.datamodel;

import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import edu.iastate.symex.config.SymexConfig;
import edu.iastate.symex.datamodel.nodes.ArrayNode;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.ObjectNode;
import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.symex.datamodel.nodes.SelectNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.util.XmlDocument;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 * 
 * Revise the code in this file. Its consitencies with other parts of the code base is no longer maintained.
 */
public class XmlReadWrite {
	
	/**
	 * Prints the DataModel to XML file.
	 */
	public void printDataModelToXmlFile(DataModel dataModel, File xmlFile) {
		Document document = XmlDocument.newDocument();
		Element outputElement = document.createElement(SymexConfig.XML_DATAMODEL);
		document.appendChild(outputElement);
		if (dataModel.getRoot() != null) {
			outputElement.appendChild(printDataNodeToXml(dataModel.getRoot(), document));
		}
		XmlDocument.writeXmlDocumentToFile(document, xmlFile);
	}
	
	/**
	 * Reads the DataModel from XML file.
	 */
	public DataModel readDataModelFromXmlFile(File xmlFile) {
		Document document = XmlDocument.readXmlDocumentFromFile(xmlFile);
		Element outputElement = (Element) document.getElementsByTagName(SymexConfig.XML_DATAMODEL).item(0);
		Element outputDataNodeElement = (Element) outputElement.getFirstChild();
		DataNode root = readDataNodeFromXml(outputDataNodeElement);
		return new DataModel(root);
	}
	
	/**
	 * Prints the DataNode to XML.
	 */
	public Element printDataNodeToXml(DataNode dataNode, Document document) {
		if (dataNode instanceof ArrayNode)
			return printArrayNodeToXml((ArrayNode) dataNode, document);
		else if (dataNode instanceof ConcatNode)
			return printConcatNodeToXml((ConcatNode) dataNode, document);
		else if (dataNode instanceof LiteralNode)
			return printLiteralNodeToXml((LiteralNode) dataNode, document);
		else if (dataNode instanceof ObjectNode)
			return printObjectNodeToXml((ObjectNode) dataNode, document);
		else if (dataNode instanceof RepeatNode)
			return printRepeatNodeToXml((RepeatNode) dataNode, document);
		else if (dataNode instanceof SelectNode)
			return printSelectNodeToXml((SelectNode) dataNode, document);
		else if (dataNode instanceof SpecialNode)
			return printSpecialNodeToXml((SpecialNode) dataNode, document);
		else if (dataNode instanceof SymbolicNode)
			return printSymbolicNodeToXml((SymbolicNode) dataNode, document);
		else {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In XmlReadWrite.java: dataNode not recognized: " + dataNode.getClass());
			return null;
		}
	}
	
	/**
	 * Reads the DataNode from XML.
	 */
	public DataNode readDataNodeFromXml(Element xmlElement) {
		if (xmlElement.getNodeName().equals(SymexConfig.XML_ARRAY))
			return readArrayNodeFromXml(xmlElement);
		else if (xmlElement.getNodeName().equals(SymexConfig.XML_CONCAT))
			return readConcatNodeFromXml(xmlElement);
		else if (xmlElement.getNodeName().equals(SymexConfig.XML_LITERAL))
			return readLiteralNodeFromXml(xmlElement);
		else if (xmlElement.getNodeName().equals(SymexConfig.XML_OBJECT))
			return readObjectNodeFromXml(xmlElement);
		else if (xmlElement.getNodeName().equals(SymexConfig.XML_REPEAT))
			return readRepeatNodeFromXml(xmlElement);
		else if (xmlElement.getNodeName().equals(SymexConfig.XML_SELECT))
			return readSelectNodeFromXml(xmlElement);
		else if (xmlElement.getNodeName().equals(SymexConfig.XML_SYMBOLIC))
			return readSymbolicNodeFromXml(xmlElement);
		else {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In XmlReadWrite.java: xmlElement not recognized: " + xmlElement);
			return null;
		}
	}
	
	/**
	 * Prints the ArrayNode to XML.
	 */
	public Element printArrayNodeToXml(ArrayNode arrayNode, Document document) {
//		LiteralNode literalNode = LiteralNodeFactory.createLiteralNode(this.getApproximateStringValue());
//		return literalNode.printGraphToXmlFormat(document, parentNodes);
		return null;
	}
	
	/**
	 * Reads the ArrayNode from XML.
	 */
	public ArrayNode readArrayNodeFromXml(Element xmlElement) {
		return null;
	}
	
	/**
	 * Prints the ConcatNode to XML.
	 */
	public Element printConcatNodeToXml(ConcatNode concatNode, Document document) {
//		Element element = document.createElement(SymexConfig.XML_CONCAT);
//		if (!checkForLoops(parentNodes)) {
//			parentNodes.add(this);
//			for (DataNode childNode : childNodes)
//				element.appendChild(childNode.printGraphToXmlFormat(document, parentNodes));
//			parentNodes.remove(this);
//		}
//		return element;
		return null;
	}
	
	/**
	 * Reads the ConcatNode from XML.
	 */
	public ConcatNode readConcatNodeFromXml(Element xmlElement) {
//		NodeList childrenList = xmlElement.getChildNodes();
//		for (int i = 0; i < childrenList.getLength(); i++) {
//			Element childNode = (Element) childrenList.item(i);
//			childNodes.add(DataNode.createInstance(childNode));
//		}
		return null;
	}
	
	/**
	 * Prints the LiteralNode to XML.
	 */
	public Element printLiteralNodeToXml(LiteralNode literalNode, Document document) {
//		Element element = document.createElement(DataModelConfig.XML_LITERAL);
//		
//		element.setAttribute(DataModelConfig.XML_STRING_VALUE, this.getStringValue());
//		element.setAttribute(DataModelConfig.XML_FILE_PATH, location.getLocationAtOffset(0).getFilePath());
//		element.setAttribute(DataModelConfig.XML_POSITION, Integer.toString(location.getLocationAtOffset(0).getPosition()));
//		
//		if (DataModelConfig.PRINT_TRACING_INFO) {
//			element.appendChild(this.getLocation().printToXmlFormat(document, 0));
//		}
//		return element;
		return null;
	}
	
	/**
	 * Reads the LiteralNode from XML.
	 */
	public LiteralNode readLiteralNodeFromXml(Element xmlElement) {
//		String stringValue = xmlElement.getAttribute(DataModelConfig.XML_STRING_VALUE);
//		
//		String filePath =  xmlElement.getAttribute(DataModelConfig.XML_FILE_PATH);
//		int position =  Integer.valueOf(xmlElement.getAttribute(DataModelConfig.XML_POSITION));
//		Location location = new SourceCodeLocation(filePath, position);
//		
//		this.location = location;
//		this.stringValue = stringValue;	
		return null;
	}
	
	/**
	 * Prints the ObjectNode to XML.
	 */
	public Element printObjectNodeToXml(ObjectNode objectNode, Document document) {
		return null;
	}
	
	/**
	 * Reads the ObjectNode from XML.
	 */
	public ObjectNode readObjectNodeFromXml(Element xmlElement) {
		return null;
	}
	
	/**
	 * Prints the RepeatNode to XML.
	 */
	public Element printRepeatNodeToXml(RepeatNode repeatNode, Document document) {
//		Element element = document.createElement(SymexConfig.XML_REPEAT);
//		
//		element.setAttribute(SymexConfig.XML_TEXT, conditionString.getStringValue());
//		element.setAttribute(SymexConfig.XML_FILE, conditionString.getPositionRange().getLocationAtOffset(0).getFilePath().getPath());
//		element.setAttribute(SymexConfig.XML_OFFSET, Integer.toString(conditionString.getPositionRange().getLocationAtOffset(0).getPosition()));
//		
//		if (!checkForLoops(parentNodes) && dataNode != null) {
//			parentNodes.add(this);
//			element.appendChild(dataNode.printGraphToXmlFormat(document, parentNodes));
//			parentNodes.remove(this);
//		}
//		return element;
		return null;
	}
	
	/**
	 * Reads the RepeatNode from XML.
	 */
	public RepeatNode readRepeatNodeFromXml(Element xmlElement) {
//		if (xmlElement.hasChildNodes())
//		dataNode = DataNode.createInstance((Element) xmlElement.getChildNodes().item(0));
		return null;
	}
	
	/**
	 * Prints the SelectNode to XML.
	 */
	public Element printSelectNodeToXml(SelectNode selectNode, Document document) {
//		Element element = document.createElement(SymexConfig.XML_SELECT);
//		if (conditionString != null) {
//			element.setAttribute(SymexConfig.XML_TEXT,
//					conditionString.getStringValue());
//			element.setAttribute(SymexConfig.XML_FILE, conditionString
//					.getPositionRange().getLocationAtOffset(0).getFilePath()
//					.getPath());
//			element.setAttribute(
//					SymexConfig.XML_OFFSET,
//					Integer.toString(conditionString.getPositionRange()
//							.getLocationAtOffset(0).getPosition()));
//		}
//		if (!checkForLoops(parentNodes)) {
//			if (nodeInTrueBranch != null) {
//				parentNodes.add(this);
//				Element elementInTrueBranch = document
//						.createElement(SymexConfig.XML_SELECT_TRUE);
//				element.appendChild(elementInTrueBranch);
//				elementInTrueBranch.appendChild(nodeInTrueBranch
//						.printGraphToXmlFormat(document, parentNodes));
//				parentNodes.remove(this);
//			}
//			if (nodeInFalseBranch != null) {
//				parentNodes.add(this);
//				Element elementInFalseBranch = document
//						.createElement(SymexConfig.XML_SELECT_FALSE);
//				element.appendChild(elementInFalseBranch);
//				elementInFalseBranch.appendChild(nodeInFalseBranch
//						.printGraphToXmlFormat(document, parentNodes));
//				parentNodes.remove(this);
//			}
//		}
//		return element;
		return null;
	}
	
	/**
	 * Reads the SelectNode from XML.
	 */
	public SelectNode readSelectNodeFromXml(Element xmlElement) {
//		if (xmlElement.hasAttribute(DataModelConfig.XML_STRING_VALUE)) {
//			String stringValue = xmlElement.getAttribute(DataModelConfig.XML_STRING_VALUE);
//			String filePath = xmlElement.getAttribute(DataModelConfig.XML_FILE_PATH);
//			int position = Integer.valueOf(xmlElement.getAttribute(DataModelConfig.XML_POSITION));
//			conditionString = new LiteralNode(new datamodel.nodes.LiteralNode(stringValue, new SourceCodeLocation(filePath, position)));
//		}
//		
//		NodeList childrenList = xmlElement.getChildNodes();
//		for (int i = 0; i < childrenList.getLength(); i++) {
//			Element childNode = (Element) childrenList.item(i);
//			boolean isTrueBranch = (childNode.getNodeName().equals(DataModelConfig.XML_SELECT_TRUE));
//			childNode = (Element) childNode.getFirstChild();
//			if (isTrueBranch)
//				nodeInTrueBranch = DataNode.createInstance(childNode);
//			else
//				nodeInFalseBranch = DataNode.createInstance(childNode);
//		}
		return null;
	}
	
	/**
	 * Prints the SpecialNode to XML.
	 */
	public Element printSpecialNodeToXml(SpecialNode specialNode, Document document) {
		return null;
	}
	
	/**
	 * Reads the SpecialNode from XML.
	 */
	public SpecialNode readSpecialNodeFromXml(Element xmlElement) {
		return null;
	}
	
	/**
	 * Prints the SymbolicNode to XML.
	 */
	public Element printSymbolicNodeToXml(SymbolicNode selectNode, Document document) {
//		Element element = document.createElement(SymexConfig.XML_SYMBOLIC);
//		if (phpNode != null) {
//			element.setAttribute(SymexConfig.XML_TEXT, this.phpNode.getSourceCode());
//			element.setAttribute(SymexConfig.XML_FILE, this.phpNode.getPositionRange().getLocationAtOffset(0).getFilePath().getPath());
//			element.setAttribute(SymexConfig.XML_OFFSET, Integer.toString(this.phpNode.getPositionRange().getLocationAtOffset(0).getPosition()));
//		}
//		if (parentNode != null && !checkForLoops(parentNodes)) {
//			parentNodes.add(this);
//			element.appendChild(parentNode.printGraphToXmlFormat(document, parentNodes));
//			parentNodes.remove(this);
//		}
//		return element;
		return null;
	}
	
	/**
	 * Reads the SymblicNode from XML.
	 */
	public SymbolicNode readSymbolicNodeFromXml(Element xmlElement) {
		return null;
	}

	/**
	 * Prints the graph rooted at this node to XML format.
	 * This method will be overridden by the methods of its subclasses.
	 * The parentNodes are used to detect unexpected loops in the tree.
	 */
//	public abstract Element printGraphToXmlFormat(Document document, HashSet<DataNode> parentNodes);
	
	/**
	 * Detects loops during the printing of the data model tree.
	 * Also checks if the data model has reached its maximum depth.
	 */
//	protected boolean checkForLoops(HashSet<DataNode> parentNodes) {
//		if (parentNodes.contains(this)) {
//			MyLogger.log(MyLevel.USER_EXCEPTION, "In DataNode.java: A loop was detected in the data model.");
//			return true;
//		}
//		else if (parentNodes.size() == SymexConfig.DATA_MODEL_MAX_DEPTH) {
//			MyLogger.log(MyLevel.USER_EXCEPTION, "In DataNode.java: Data model has reached its maximum depth of " + SymexConfig.DATA_MODEL_MAX_DEPTH);
//			return true;
//		}
//		else
//			return false;
//	}
	
	/**
	 * Prints a position to XML.
	 */
//	public Element printPositionToXml(Document document, int offsetPosition) {
//		Element element = document.createElement("SourceCodeLocation");
//		element.setAttribute("File", file.getPath());
//		element.setAttribute("Position", Integer.toString(offset + offsetPosition));
//		return element;
//	}
	
	/**
	 * Reads a position from XML.
	 */
//	public Element readPositionFromXml(Document document, int offsetPosition) {
//		Element element = document.createElement("SourceCodeLocation");
//		element.setAttribute("File", file.getPath());
//		element.setAttribute("Position", Integer.toString(offset + offsetPosition));
//		return element;
//	}
	
}

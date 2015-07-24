package edu.iastate.symex.datamodel;

import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.iastate.symex.config.SymexConfig;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.datamodel.nodes.ArrayNode;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.ObjectNode;
import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.symex.datamodel.nodes.SelectNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;
import edu.iastate.symex.util.XmlDocument;

/**
 * 
 * @author HUNG
 * 
 */
public class ReadWriteDataModelToFromXml {
	
	/**
	 * Writes DataModel to XML file
	 */
	public void writeDataModelToXmlFile(DataModel dataModel, String xmlFile) {
		Document document = XmlDocument.newDocument();
		
		Element dataModelElement = document.createElement(SymexConfig.XML_DATAMODEL);
		document.appendChild(dataModelElement);
		
		Element rootElement = writeDataNodeToXmlElement(dataModel.getRoot(), document);
		dataModelElement.appendChild(rootElement);
		
		XmlDocument.writeXmlDocumentToFile(document, xmlFile);
	}
	
	/**
	 * Reads DataModel from XML file
	 */
	public DataModel readDataModelFromXmlFile(String xmlFile) {
		Document document = XmlDocument.readXmlDocumentFromFile(xmlFile);
		Element dataModelElement = document.getDocumentElement();
		Element rootElement = (Element) dataModelElement.getFirstChild();
		
		DataNode root = readDataNodeFromXmlElement(rootElement);
		return new DataModel(root);
	}
	
	/**
	 * Writes DataNode to XML element
	 */
	private Element writeDataNodeToXmlElement(DataNode dataNode, Document document) {
		Element element;
		
		if (dataNode instanceof ArrayNode)
			element = writeArrayNodeToXmlElement((ArrayNode) dataNode, document);
		else if (dataNode instanceof ConcatNode)
			element = writeConcatNodeToXmlElement((ConcatNode) dataNode, document);
		else if (dataNode instanceof LiteralNode)
			element = writeLiteralNodeToXmlElement((LiteralNode) dataNode, document);
		else if (dataNode instanceof ObjectNode)
			element = writeObjectNodeToXmlElement((ObjectNode) dataNode, document);
		else if (dataNode instanceof RepeatNode)
			element = writeRepeatNodeToXmlElement((RepeatNode) dataNode, document);
		else if (dataNode instanceof SelectNode)
			element = writeSelectNodeToXmlElement((SelectNode) dataNode, document);
		else if (dataNode instanceof SpecialNode)
			element = writeSpecialNodeToXmlElement((SpecialNode) dataNode, document);
		else // if (dataNode instanceof SymbolicNode)
			element = writeSymbolicNodeToXmlElement((SymbolicNode) dataNode, document);
		
		return element;
	}
	
	/**
	 * Reads DataNode from XML element
	 */
	private DataNode readDataNodeFromXmlElement(Element element) {
		DataNode dataNode;
		
		if (element.getNodeName().equals(SymexConfig.XML_ARRAY))
			dataNode = readArrayNodeFromXmlElement(element);
		else if (element.getNodeName().equals(SymexConfig.XML_CONCAT))
			dataNode = readConcatNodeFromXmlElement(element);
		else if (element.getNodeName().equals(SymexConfig.XML_LITERAL))
			dataNode = readLiteralNodeFromXmlElement(element);
		else if (element.getNodeName().equals(SymexConfig.XML_OBJECT))
			dataNode = readObjectNodeFromXmlElement(element);
		else if (element.getNodeName().equals(SymexConfig.XML_REPEAT))
			dataNode = readRepeatNodeFromXmlElement(element);
		else if (element.getNodeName().equals(SymexConfig.XML_SELECT))
			dataNode = readSelectNodeFromXmlElement(element);
		else if (element.getNodeName().equals(SymexConfig.XML_SPECIAL))
			dataNode = readSpecialNodeFromXmlElement(element);
		else // if (xmlElement.getNodeName().equals(SymexConfig.XML_SYMBOLIC))
			dataNode = readSymbolicNodeFromXmlElement(element);
		
		return dataNode;
	}
	
	/**
	 * Writes ArrayNode to XML element
	 */
	private Element writeArrayNodeToXmlElement(ArrayNode arrayNode, Document document) {
		return document.createElement(SymexConfig.XML_ARRAY);
	}
	
	/**
	 * Reads ArrayNode from XML element
	 */
	private DataNode readArrayNodeFromXmlElement(Element element) {
		return DataNodeFactory.createArrayNode(); // TODO Should add more info to ArrayNode
	}
	
	/**
	 * Writes ConcatNode to XML element
	 */
	private Element writeConcatNodeToXmlElement(ConcatNode concatNode, Document document) {
		Element concatElement = document.createElement(SymexConfig.XML_CONCAT);
		for (DataNode childNode : concatNode.getChildNodes()) {
			Element childElement = writeDataNodeToXmlElement(childNode, document);
			concatElement.appendChild(childElement);
		}
		return concatElement;
	}
	
	/**
	 * Reads ConcatNode from XML element
	 */
	private DataNode readConcatNodeFromXmlElement(Element element) {
		ArrayList<DataNode> childNodes = new ArrayList<DataNode>();
		
		NodeList childElements = element.getChildNodes();
		for (int i = 0; i < childElements.getLength(); i++) {
			Element childElement = (Element) childElements.item(i);
			DataNode childNode = readDataNodeFromXmlElement(childElement);
			childNodes.add(childNode);
		}
		
		return DataNodeFactory.createCompactConcatNode(childNodes);
	}
	
	/**
	 * Writes LiteralNode to XML element
	 */
	private Element writeLiteralNodeToXmlElement(LiteralNode literalNode, Document document) {
		Element element = document.createElement(SymexConfig.XML_LITERAL);
		element.setAttribute(SymexConfig.XML_TEXT, literalNode.getStringValue());
		writeLocationToXmlElement(literalNode.getLocation(), element);
		return element;
	}
	
	/**
	 * Reads LiteralNode from XML element
	 */
	private LiteralNode readLiteralNodeFromXmlElement(Element element) {
		String stringValue = element.getAttribute(SymexConfig.XML_TEXT);
		PositionRange location = readLocationFromXmlElement(element);
		return DataNodeFactory.createLiteralNode(stringValue, location);
	}
	
	/**
	 * Writes ObjectNode to XML element
	 */
	private Element writeObjectNodeToXmlElement(ObjectNode objectNode, Document document) {
		return document.createElement(SymexConfig.XML_OBJECT);
	}
	
	/**
	 * Reads ObjectNode from XML element
	 */
	private DataNode readObjectNodeFromXmlElement(Element element) {
		return DataNodeFactory.createObjectNode(null); // TODO The argument should not be null and should add more info to ObjectNode
	}
	
	/**
	 * Write RepeatNode to XML element
	 */
	private Element writeRepeatNodeToXmlElement(RepeatNode repeatNode, Document document) {
		Element element = document.createElement(SymexConfig.XML_REPEAT);
		
		Constraint constraint = repeatNode.getConstraint();
		Element constraintElement = writeConstraintToXmlElement(constraint, document);
		element.appendChild(constraintElement);

		DataNode childNode = repeatNode.getChildNode();
		Element childElement = writeDataNodeToXmlElement(childNode, document);
		element.appendChild(childElement);
		
		return element;
	}
	
	/**
	 * Reads RepeatNode from XML element
	 */
	private RepeatNode readRepeatNodeFromXmlElement(Element element) {
		Element constraintElement = (Element) element.getFirstChild();
		Constraint constraint = readConstraintFromXmlElement(constraintElement);
		
		Element childElement = (Element) element.getChildNodes().item(1);
		DataNode childNode = readDataNodeFromXmlElement(childElement);
		
		return DataNodeFactory.createRepeatNode(constraint, childNode);
	}
	
	/**
	 * Writes SelectNode to XML element
	 */
	private Element writeSelectNodeToXmlElement(SelectNode selectNode, Document document) {
		Element element = document.createElement(SymexConfig.XML_SELECT);
		
		Constraint constraint = selectNode.getConstraint();
		Element constraintElement = writeConstraintToXmlElement(constraint, document);
		element.appendChild(constraintElement);

		DataNode trueChild = selectNode.getNodeInTrueBranch();
		element.appendChild(writeDataNodeToXmlElement(trueChild, document));
		
		DataNode falseChild = selectNode.getNodeInFalseBranch();
		element.appendChild(writeDataNodeToXmlElement(falseChild, document));
		
		return element;
	}
	
	/**
	 * Reads SelectNode from XML element
	 */
	private DataNode readSelectNodeFromXmlElement(Element element) {
		Element constraintElement = (Element) element.getFirstChild();
		Constraint constraint = readConstraintFromXmlElement(constraintElement);
		
		Element trueElement = (Element) element.getChildNodes().item(1);
		DataNode trueChild = readDataNodeFromXmlElement(trueElement);
		
		Element falseElement = (Element) element.getChildNodes().item(2);
		DataNode falseChild = readDataNodeFromXmlElement(falseElement);
		
		return DataNodeFactory.createCompactSelectNode(constraint, trueChild, falseChild);
	}
	
	/**
	 * Writes SpecialNode to XML element
	 */
	private Element writeSpecialNodeToXmlElement(SpecialNode specialNode, Document document) {
		return document.createElement(SymexConfig.XML_SPECIAL);
	}
	
	/**
	 * Reads SpecialNode from XML element
	 */
	private DataNode readSpecialNodeFromXmlElement(Element element) {
		return DataNodeFactory.createSymbolicNode(); // TODO Should create SpecialNode instead of SymbolicNode
	}
	
	/**
	 * Writes SymbolicNode to XML element
	 */
	private Element writeSymbolicNodeToXmlElement(SymbolicNode symbolicNode, Document document) {
		Element element = document.createElement(SymexConfig.XML_SYMBOLIC);
		//element.setAttribute(SymexConfig.XML_TEXT, symbolicNode.getPhpNode().getSourceCode());
		//writeLocationToXmlElement(symbolicNode.getLocation(), element);
		return element;
	}
	
	/**
	 * Reads SymbolicNode from XML element
	 */
	private DataNode readSymbolicNodeFromXmlElement(Element element) {
		return DataNodeFactory.createSymbolicNode(); // TODO Should add more info to SymbolicNode
	}

	/**
	 * Writes Constraint to XML element
	 */
	private Element writeConstraintToXmlElement(Constraint constraint, Document document) {
		Element element = document.createElement(SymexConfig.XML_CONSTRAINT);
		element.setAttribute(SymexConfig.XML_TEXT, constraint.toDebugString());
		writeLocationToXmlElement(constraint.getLocation(), element);
		return element;
	}
	
	/**
	 * Reads Constraint from XML element
	 */
	private Constraint readConstraintFromXmlElement(Element element) {
		String text = element.getAttribute(SymexConfig.XML_TEXT);
		PositionRange location = readLocationFromXmlElement(element);
		return ConstraintFactory.createAtomicConstraint(text, location);
	}
	
	/**
	 * Writes Location to XML element
	 */
	private void writeLocationToXmlElement(PositionRange location, Element element) {
		element.setAttribute(SymexConfig.XML_FILE, location.getFilePath() != null ? location.getFilePath() : "");
		element.setAttribute(SymexConfig.XML_OFFSET, String.valueOf(location.getOffset()));
		element.setAttribute(SymexConfig.XML_LENGTH, String.valueOf(location.getLength())); // TODO Revise for the case location is scattered
	}
	
	/**
	 * Reads Location from XML element
	 */
	private PositionRange readLocationFromXmlElement(Element element) {
		String filePath = element.getAttribute(SymexConfig.XML_FILE);
		File file = (!filePath.isEmpty() ? new File(filePath) : null);
		int offset = Integer.valueOf(element.getAttribute(SymexConfig.XML_OFFSET));
		int length = Integer.valueOf(element.getAttribute(SymexConfig.XML_LENGTH));

		if (offset == -1) {
			if (length == -1)
				return Range.UNDEFINED;
			else
				return new Range(length);
		}
		else
			return new Range(file, offset, length);
	}
	
}

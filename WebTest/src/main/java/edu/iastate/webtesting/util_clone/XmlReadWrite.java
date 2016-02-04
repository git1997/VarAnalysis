package edu.iastate.webtesting.util_clone;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.iastate.symex.util.XmlDocument;
import edu.iastate.webtesting.values_clone.Concat;
import edu.iastate.webtesting.values_clone.CondValue;
import edu.iastate.webtesting.values_clone.CondValueFactory;
import edu.iastate.webtesting.values_clone.Literal;
import edu.iastate.webtesting.values_clone.NullValue;

/**
 * 
 * @author HUNG
 *
 */
public class XmlReadWrite {
	
	/*
	 * XML identifiers
	 */
	public static final String XML_CONCAT 							= "Concat";
	public static final String XML_CONCAT_CONCATENATOR				= "Concatenator";
	public static final String XML_LITERAL							= "Literal";
	public static final String XML_LITERAL_STRINGVALUE				= "StringValue";
	public static final String XML_LITERAL_LOCATION					= "Location";
	public static final String XML_NULLVALUE						= "NullValue";

	/**
	 * Writes CondValue to XML file
	 */
	public void writeCondValueToXmlFile(CondValue condValue, String xmlFile) {
		Document document = XmlDocument.newDocument();
		Element rootElement = writeCondValueToXml(condValue, document);
		document.appendChild(rootElement);
		XmlDocument.writeXmlDocumentToFile(document, xmlFile);
	}
	
	/**
	 * Reads CondValue from XML file
	 */
	public CondValue readCondValueFromXmlFile(File xmlFile) {
		Document document = XmlDocument.readXmlDocumentFromFile(xmlFile);
		Element rootElement = (Element) document.getFirstChild();
		CondValue root = readCondValueFromXml(rootElement);
		return root;
	}
	
	/**
	 * Writes CondValue to XML
	 */
	public Element writeCondValueToXml(CondValue condValue, Document document) {
		if (condValue instanceof Concat)
			return writeConcatToXml((Concat) condValue, document);
		else if (condValue instanceof Literal)
			return writeLiteralToXml((Literal) condValue, document);
		else // if (dataNode instanceof NullValue)
			return writeNullValueToXml((NullValue) condValue, document);
	}
	
	/**
	 * Reads CondValue from XML
	 */
	public CondValue readCondValueFromXml(Element xmlElement) {
		if (xmlElement.getNodeName().equals(XML_CONCAT))
			return readConcatFromXml(xmlElement);
		else if (xmlElement.getNodeName().equals(XML_LITERAL))
			return readLiteralFromXml(xmlElement);
		else // if (xmlElement.getNodeName().equals(XML_NULLVALUE))
			return readNullValueFromXml(xmlElement);
	}
	
	/**
	 * Writes Concat to XML
	 */
	public Element writeConcatToXml(Concat concat, Document document) {
		Element element = document.createElement(XML_CONCAT);
		for (CondValue childValue : concat.getChildValues()) {
			element.appendChild(writeCondValueToXml(childValue, document));
		}
		return element;
	}
	
	/**
	 * Reads Concat from XML
	 */
	public Concat readConcatFromXml(Element xmlElement) {
		List<CondValue> childValues = new ArrayList<CondValue>();
		NodeList childNodes = xmlElement.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			childValues.add(readCondValueFromXml((Element) childNodes.item(i)));
		}
		return CondValueFactory.createConcat(childValues);
	}
	
	/**
	 * Writes Literal to XML
	 */
	public Element writeLiteralToXml(Literal literal, Document document) {
		Element element = document.createElement(XML_LITERAL);
		element.setAttribute(XML_LITERAL_STRINGVALUE, literal.getStringValue());
		element.setAttribute(XML_LITERAL_LOCATION, codeLocationToString(literal.getLocation()));
		return element;
	}
	
	/**
	 * Reads Literal from XML
	 */
	public Literal readLiteralFromXml(Element xmlElement) {
		String stringValue = xmlElement.getAttribute(XML_LITERAL_STRINGVALUE);
		CodeLocation location = stringToCodeLocation(xmlElement.getAttribute(XML_LITERAL_LOCATION));
		return CondValueFactory.createLiteral(stringValue, location);
	}
	
	/**
	 * Writes NullValue to XML
	 */
	public Element writeNullValueToXml(NullValue nullValue, Document document) {
		Element element = document.createElement(XML_NULLVALUE);
		return element;
	}
	
	/**
	 * Reads NullValue from XML
	 */
	public NullValue readNullValueFromXml(Element xmlElement) {
		return NullValue.NOT_IMPLEMENTED;
	}
	
	/*
	 * Utility methods
	 */
	
	public static String codeLocationToString(CodeLocation codeLocation) {
		return CodeLocation.getShortPath(codeLocation.getFile()) + "@" + codeLocation.getLine() + "@" + codeLocation.getOffset();
	}
	
	public static CodeLocation stringToCodeLocation(String string) {
		String[] conditionParts = string.split("@");
		String file = conditionParts[0];
		int line = Integer.valueOf(conditionParts[1]);
		int offset = Integer.valueOf(conditionParts[2]);
		return (offset == -1 ? CodeLocation.UNDEFINED : new CodeLocation(file, line, offset));
	}
}

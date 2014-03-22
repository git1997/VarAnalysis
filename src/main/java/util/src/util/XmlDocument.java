package util;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * 
 * @author HUNG
 *
 */
public class XmlDocument {

	public static Document newDocument() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
			return null;
		}
	}
	
	/*
	 * Read/write an XML document
	 */

	public static void writeXmlDocumentToFile(Document xmlDocument, String xmlFile) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "NO");
			transformer.setOutputProperty(OutputKeys.INDENT, "YES");			
			
			StringWriter xmlWriter = new StringWriter();
			transformer.transform(new DOMSource(xmlDocument), new StreamResult(xmlWriter));
			FileIO.writeStringToFile(xmlWriter.toString(), xmlFile);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static Document readXmlDocumentFromFile(String xmlFile) {
		try {
			// TODO Adhoc code to fix error with the &# characters
			String source = FileIO.readStringFromFile(xmlFile);
			source = source.replace("&#", "&amp;#");
			
			ByteArrayInputStream fileContent = new ByteArrayInputStream(source.getBytes("UTF-8"));
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fileContent);
			document.getDocumentElement().normalize();
			removeEmptyXmlTextNodes(document);
			return document;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
			return null;
		}
	}

	private static void removeEmptyXmlTextNodes(Node xmlNode) {
		NodeList nodeList = xmlNode.getChildNodes();
		ArrayList<Node> childrenList = new ArrayList<Node>(); 
		for (int i = 0; i < nodeList.getLength(); i++)
			childrenList.add(nodeList.item(i));
		for (Node childNode : childrenList) {
			if (childNode.getNodeType() == Node.TEXT_NODE && ((Text) childNode).getNodeValue().trim().isEmpty())
				xmlNode.removeChild(childNode);
			else
				XmlDocument.removeEmptyXmlTextNodes(childNode);
		}
	}
	
}
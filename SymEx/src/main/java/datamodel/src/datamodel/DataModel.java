package datamodel;

import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import php.ElementManager;
import php.PhpExecuter;
import php.elements.PhpFunction;
import php.elements.PhpVariable;

import config.DataModelConfig;

import util.XmlDocument;
import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class DataModel {

	private ElementManager elementManager;
	
	/**
	 * Constructor.
	 * @param serverGraph
	 */
	public DataModel(String projectFolder, String phpFileRelativePath) {
		elementManager = new PhpExecuter().execute(projectFolder, phpFileRelativePath);
	}	
	
	/*
	 * Get some properties after the execution
	 */
	
	/**
	 * Returns the DataNode representing the output
	 */
	public DataNode getOutputDataNode() {
		return elementManager.getFinalOutput() != null ? elementManager.getFinalOutput().getDataNode() : null;
	}
	
	/**
	 * Returns the set of executed server files.
	 */
	public HashSet<String> getExecutedFiles() {
		return elementManager.getInvokedFiles();
	}
	
	/*
	 * Print the data model
	 */
	
	/**
	 * Prints the Output to XML file.
	 */
	public void printOutputToXmlFile(String xmlFile) {
		Document document = XmlDocument.newDocument();
		Element outputElement = document.createElement(DataModelConfig.XML_OUTPUT);
		document.appendChild(outputElement);
		if (getOutputDataNode() != null) {
			outputElement.appendChild(getOutputDataNode().printGraphToXmlFormat(document, new HashSet<DataNode>()));
		}
		XmlDocument.writeXmlDocumentToFile(document, xmlFile);
	}
	
	/**
	 * Reads the Output from XML format.
	 */
	public static datamodel.nodes.ext.DataNode readOutputFromXmlFile(String xmlFile) {
		Document document = XmlDocument.readXmlDocumentFromFile(xmlFile);
		Element outputElement = (Element) document.getElementsByTagName(DataModelConfig.XML_OUTPUT).item(0);
		Element outputDataNodeElement = (Element) outputElement.getFirstChild();
		return datamodel.nodes.ext.DataNode.createInstance(outputDataNodeElement);
	}
	
	/**
	 * Prints the Output and variable values to Graphviz format.
	 */
	public String printToGraphvizFormat() {
		StringBuilder string = new StringBuilder();
		string.append("digraph DataModel {\r\n");
		HashSet<DataNode> setOfPrintedNodes = new HashSet<DataNode>();
		if (DataModelConfig.PRINT_CLIENT_OUTPUT_ONLY) {
			if (elementManager.getFinalOutput() != null) {
				string.append(elementManager.getFinalOutput().printGraphToGraphvizFormat(setOfPrintedNodes));
			}
		}
		else {
			for (PhpVariable phpVariable : elementManager.getAllVariables()) {
				string.append(phpVariable.printGraphToGraphvizFormat(setOfPrintedNodes));
			}
			for (PhpFunction phpFunction : elementManager.getAllFunctions()) {
				string.append(phpFunction.printGraphToGraphvizFormat(setOfPrintedNodes));
			}
		}
		string.append("}");		
		return string.toString();
	}
	
}
package datamodel.nodes;

import java.util.HashSet;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import php.nodes.PhpNode;

import config.DataModelConfig;


/**
 * 
 * @author HUNG
 *
 */
public class SymbolicNode extends DataNode {

	private PhpNode phpNode = null;			// The PhpNode which has unresolved value.
	
	private SymbolicNode parentNode = null;	// To support the tracing of unresolved values.
	
	/*
	 * Constructors
	 */
	
	public SymbolicNode() {		
	}
	
	public SymbolicNode(PhpNode phpNode) {
		this.phpNode = phpNode;
	}

	@Override
	public DataNode clone() {
		SymbolicNode clonedNode = new SymbolicNode();
		clonedNode.phpNode = this.phpNode;
		clonedNode.parentNode = (this.parentNode != null ? (SymbolicNode) this.parentNode.clone() : null);
		return clonedNode;
	}
	
	/*
	 * Set properties
	 */
	
	public void setParentNode(SymbolicNode parentNode) {
		this.parentNode = parentNode;
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
	
	public String getSymbolicValue() {
		return "__SYMBOLIC_" + this.hashCode() + "__";
	}
	
	public static String getSymbolicValueRegularExpression() {
		return "__SYMBOLIC_\\d+__";
	}
	
	@Override
	public String getApproximateStringValue() {
		return this.getSymbolicValue();
	}
	
	/*
	 * Provide formatting for XML.
	 */
	
	@Override
	public Element printGraphToXmlFormat(Document document, HashSet<DataNode> parentNodes) {
		Element element = document.createElement(DataModelConfig.XML_SYMBOLIC);
		if (phpNode != null) {
			element.setAttribute(DataModelConfig.XML_STRING_VALUE, this.phpNode.getStringValue());
			element.setAttribute(DataModelConfig.XML_FILE_PATH, this.phpNode.getLocation().getLocationAtOffset(0).getFilePath());
			element.setAttribute(DataModelConfig.XML_POSITION, Integer.toString(this.phpNode.getLocation().getLocationAtOffset(0).getPosition()));
		}
		if (parentNode != null && !checkForLoops(parentNodes)) {
			parentNodes.add(this);
			element.appendChild(parentNode.printGraphToXmlFormat(document, parentNodes));
			parentNodes.remove(this);
		}
		return element;
	}
	
	/*
	 * Provide formatting for Graphviz.
	 */
	
	@Override
	public String getGraphvizLabel() {
		if (phpNode != null && phpNode.getStringValue() != null)
			return phpNode.getStringValue();
		else
			return this.getSymbolicValue();
	}
	
	@Override
	public String getGraphvizAttributes() {
		return " style=dashed";
	}
	
}

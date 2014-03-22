package datamodel.nodes.ext;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sourcetracing.Location;
import sourcetracing.ScatteredLocation;
import sourcetracing.SourceCodeLocation;
import config.DataModelConfig;

/**
 * 
 * @author HUNG
 *
 */
public class LiteralNode extends DataNode {
		
	private Location location;		// The location in the source code
	
	private String stringValue;		// The string value of this literal node	
	
	/**
	 * Creates a LiteralNode read from an XML element.
	 */
	public LiteralNode(Element xmlElement) {
		String stringValue = xmlElement.getAttribute(DataModelConfig.XML_STRING_VALUE);
		
		String filePath =  xmlElement.getAttribute(DataModelConfig.XML_FILE_PATH);
		int position =  Integer.valueOf(xmlElement.getAttribute(DataModelConfig.XML_POSITION));
		Location location = new SourceCodeLocation(filePath, position);
		
		this.location = location;
		this.stringValue = stringValue;		
	}
	
	/**
	 * Creates a LiteralNode read from another LiteralNode.
	 */
	public LiteralNode(datamodel.nodes.LiteralNode literalNode) {
		this.location = literalNode.getLocation();
		this.stringValue = literalNode.getUnescapedStringValue();	
	}
	
	/**
	 * Creates a LiteralNode from two sub-LiteralNodes.
	 */
	public LiteralNode(LiteralNode node1, LiteralNode node2) {
		location = new ScatteredLocation(node1.getLocation(), node2.getLocation(), node1.stringValue.length());
		stringValue = node1.stringValue + node2.stringValue;		
	}
	
	/*
	 * Get properties
	 */
	
	public Location getLocation() {
		return location;
	}
	
	public String getStringValue() {
		return stringValue;
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
		Element element = document.createElement(DataModelConfig.XML_LITERAL);
		
		element.setAttribute(DataModelConfig.XML_STRING_VALUE, this.getStringValue());
		element.setAttribute(DataModelConfig.XML_FILE_PATH, location.getLocationAtOffset(0).getFilePath());
		element.setAttribute(DataModelConfig.XML_POSITION, Integer.toString(location.getLocationAtOffset(0).getPosition()));
		
		if (DataModelConfig.PRINT_TRACING_INFO) {
			element.appendChild(this.getLocation().printToXmlFormat(document, 0));
		}
		return element;
	}
	
}

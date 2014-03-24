package datamodel.nodes;

import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.sourcetracing.Location;
import config.DataModelConfig;

/**
 * 
 * @author HUNG
 *
 */
public class LiteralNode extends DataNode {

	private Location location;				// The location of this literal node in the source code
	
	private String type;					// The type of this literal node (whether it is embedded in quotes/apostrophes/or inline)
	
	String stringValue;				// The (escaped) string value of this literal node
	
	String unescapedStringValue;	// The unescaped string value of this literal node
	
	/*
	 * Types of literal nodes
	 */
	public static String LITERAL_QUOTES			= "Q";	// e.g. abc in "abc", or in "abc$x"
	public static String LITERAL_APOSTROPHES	= "A";	// e.g. abc in 'abc'
	public static String LITERAL_CONSTANT		= "C";	// e.g. 123, 123.4, ABC, __ABC__
	public static String LITERAL_INLINE			= "I";	// e.g. InLineHtml
	public static String LITERAL_UNDEFINED		= "?";	// undefined
	
	/*
	 * Constructors
	 */
	
	public LiteralNode(Location l, String t, String s, String u) { 
		this.location = l; 
		this.type = t;
		this.stringValue = s;
		this.unescapedStringValue = u;
	}
	

	
	@Override
	public DataNode clone() {
		LiteralNode clonedNode = new LiteralNode(this.location, this.type, this.stringValue, this.unescapedStringValue);
		return clonedNode;
	}
	
	/*
	 * Get properties
	 */
	
	public Location getLocation() {
		return location;
	}
	
	public String getType() {
		return type;
	}
	
	public String getStringValue() {
		return stringValue;
	}
	
	public String getUnescapedStringValue() {
		return unescapedStringValue;
	}
	
	@Override
	public String getApproximateStringValue() {
		return this.getStringValue();
	}
	
	
	/*
	 * Provide formatting for XML.
	 */
	
	@Override
	public Element printGraphToXmlFormat(Document document, HashSet<DataNode> parentNodes) {
		Element element = document.createElement(DataModelConfig.XML_LITERAL);

		element.setAttribute(DataModelConfig.XML_STRING_VALUE, this.getUnescapedStringValue()); // Write the unescaped string
		element.setAttribute(DataModelConfig.XML_FILE_PATH, location.getLocationAtOffset(0).getFilePath().getPath());
		element.setAttribute(DataModelConfig.XML_POSITION, Integer.toString(location.getLocationAtOffset(0).getPosition()));
		
		if (DataModelConfig.PRINT_TRACING_INFO) {
			element.appendChild(this.getLocation().printToXmlFormat(document, 0));
		}
		return element;
	}
	
	/*
	 * Provide formatting for Graphviz.
	 */
	
	@Override
	public String getGraphvizLabel() {
		return this.getUnescapedStringValue();
	}
	
	@Override
	public String getGraphvizAttributes () {
		return " shape=box";
	}

	@Override
	public void visit(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitLiteralNode(this);
	}

}
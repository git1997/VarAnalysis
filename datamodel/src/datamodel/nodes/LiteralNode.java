package datamodel.nodes;

import java.util.HashMap;
import java.util.HashSet;

import logging.MyLevel;
import logging.MyLogger;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.SwitchCase;
import org.eclipse.php.internal.core.ast.nodes.SwitchStatement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import php.TraceTable;
import php.nodes.IdentifierNode;
import php.nodes.InLineHtmlNode;
import php.nodes.ScalarNode;

import config.DataModelConfig;

import sourcetracing.Location;
import sourcetracing.ScatteredLocation;
import sourcetracing.SourceCodeLocation;
import sourcetracing.UndefinedLocation;
import util.StringUtils;

/**
 * 
 * @author HUNG
 *
 */
public class LiteralNode extends DataNode {

	private Location location;				// The location of this literal node in the source code
	
	private String type;					// The type of this literal node (whether it is embedded in quotes/apostrophes/or inline)
	
	private String stringValue;				// The (escaped) string value of this literal node
	
	private String unescapedStringValue;	// The unescaped string value of this literal node
	
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
	
	private LiteralNode() { // Do not allow empty construtor	
	}
	
	public LiteralNode(ScalarNode scalarNode) {
		this.location = scalarNode.getLocation().getLocationAtOffset(scalarNode.getAdjustedPosition());
		this.type = scalarNode.getLiteralType();
		this.stringValue = scalarNode.getStringValue();
		this.unescapedStringValue = (DataModelConfig.UNESCAPE_LITERAL_STRING_VALUE_PRESERVING_LENGTH ? getUnescapedStringValuePreservingLength(this.stringValue, this.type) : getUnescapedStringValue(this.stringValue, this.type));
	}
	
	public LiteralNode(InLineHtmlNode inLineHtmlNode) {
		this.location = inLineHtmlNode.getLocation();
		this.type = LITERAL_INLINE;
		this.stringValue = inLineHtmlNode.getStringValue();
		this.unescapedStringValue = this.stringValue;
	}
	
	public LiteralNode(IdentifierNode identifierNode) {
		this.location = identifierNode.getLocation();
		this.type = LITERAL_UNDEFINED;
		this.stringValue = identifierNode.getName();
		this.unescapedStringValue = this.stringValue;
	}
	
	public LiteralNode(SymbolicNode symbolicNode) {
		this.location = UndefinedLocation.inst;
		this.type = LITERAL_UNDEFINED;
		this.stringValue = symbolicNode.getSymbolicValue();
		this.unescapedStringValue = this.stringValue;
	}
	
	public LiteralNode(SelectNode selectionNode) {
		this.location = UndefinedLocation.inst;
		this.type = LITERAL_UNDEFINED;
		this.stringValue = selectionNode.getSymbolicValue();
		this.unescapedStringValue = this.stringValue;
	}
	
	public LiteralNode(LiteralNode node1, LiteralNode node2) {
		this.location = new ScatteredLocation(node1.getLocation(), node2.getLocation(), node1.getUnescapedStringValue().length());
		this.type = LITERAL_UNDEFINED;
		this.stringValue = node1.stringValue + node2.stringValue;
		this.unescapedStringValue = node1.unescapedStringValue + node2.unescapedStringValue;
	}
	
	public LiteralNode(ASTNode astNode) {
		String filePath = TraceTable.getCurrentSourceFileRelativePathOfPhpASTNode(astNode);	
		int position = astNode.getStart();
		
		this.location = new SourceCodeLocation(filePath, position);
		this.type = LITERAL_UNDEFINED;
		this.stringValue = TraceTable.getSourceCodeOfPhpASTNode(astNode);
		this.unescapedStringValue = this.stringValue;
	}
	
	public LiteralNode(SwitchCase switchCase) {
		this((ASTNode) switchCase); 
		this.stringValue = "case " 
			+ (switchCase.getParent().getParent() instanceof SwitchStatement ? TraceTable.getSourceCodeOfPhpASTNode(((SwitchStatement) switchCase.getParent().getParent()).getExpression()) : "?")
			+ " == "
			+ (switchCase.getValue() != null ? TraceTable.getSourceCodeOfPhpASTNode(switchCase.getValue()) : TraceTable.getSourceCodeOfPhpASTNode(switchCase));
		this.unescapedStringValue = this.stringValue;
	}
	
	public LiteralNode(String stringValue) {
		this.location = UndefinedLocation.inst; // The value is dynamically generated and cannot be traced back to the source code.
		this.type = LITERAL_UNDEFINED;
		this.stringValue = stringValue;
		this.unescapedStringValue = stringValue;
	}
	
	public LiteralNode(String stringValue, Location location) { // TODO: Consider adding a 'length' property to location
		this.location = location; // The value is dynamically generated from an AST node (e.g. function invocation)
		this.type = LITERAL_UNDEFINED;
		this.stringValue = stringValue;
		this.unescapedStringValue = stringValue;
	}
	
	@Override
	public DataNode clone() {
		LiteralNode clonedNode = new LiteralNode();
		clonedNode.location = this.location;
		clonedNode.type = this.type;
		clonedNode.stringValue = this.stringValue;
		clonedNode.unescapedStringValue = this.unescapedStringValue;
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
	 * Escape/unescape string values
	 */
	
	public static String getUnescapedStringValue(String stringValue, String stringType) {
		stringValue = stringValue.replace("©", "c"); // Fix the copyright character (can't be printed in XML format)
		if (stringType.equals(LITERAL_QUOTES)) {
			HashMap<Character, String> mapTable = new HashMap<Character, String>();
			mapTable.put('t', "\t");
			mapTable.put('r', "\r");
			mapTable.put('n', "\n");
			mapTable.put('\\', "\\");
			mapTable.put('"', "\"");
			mapTable.put('\'', "\\\'");		// Keep \' as \' in quotes
			//mapTable.put('u', "\\u"); 		// Fix a bug with the escape character \\u
			return StringUtils.unescape(stringValue, mapTable);
		}
		else if (stringType.equals(LITERAL_APOSTROPHES)) {
			HashMap<Character, String> mapTable = new HashMap<Character, String>();
			mapTable.put('t', "\t");
			mapTable.put('r', "\r");
			mapTable.put('n', "\n");
			mapTable.put('\\', "\\");
			mapTable.put('\'', "\'");
			mapTable.put('\"', "\\\"");		// Keep \" as \" in apostrophes
			return StringUtils.unescape(stringValue, mapTable);
		}
		else if (stringType.equals(LITERAL_CONSTANT)) {
			// Do nothing for CONSTANT
			return stringValue;
		}
		else if (stringType.equals(LITERAL_INLINE)) {
			// Do nothing for INLINE
			return stringValue;
		}
		else {
			// Don't know how to handle UNDEFINED
			MyLogger.log(MyLevel.TODO, "In LiteralNode.getUnescapedStringValue: Don't know how to handle UNDEFINED string type of LiteralNode: " + stringValue);
			return stringValue;
		}		
	}
	
	public static String getUnescapedStringValuePreservingLength(String stringValue, String stringType) {
		stringValue = stringValue.replace("©", "c"); // Fix the copyright character (can't be printed in XML format)
		if (stringType.equals(LITERAL_QUOTES)) {
			HashMap<Character, String> mapTable = new HashMap<Character, String>();
			mapTable.put('t', " \t");
			mapTable.put('r', " \r");
			mapTable.put('n', "\n ");		// Put the space after so that \r\n -> _[\r][\n]_
			mapTable.put('\\', " \\");
			mapTable.put('"', "\" ");		// Put the space after so that \\\" -> _\"_
			mapTable.put('\'', "\\\'");		// Keep \' as \' in quotes
			//mapTable.put('u', "\\u"); 		// Fix a bug with the escape character \\u
			return StringUtils.unescape(stringValue, mapTable);
		}
		else if (stringType.equals(LITERAL_APOSTROPHES)) {
			HashMap<Character, String> mapTable = new HashMap<Character, String>();
			mapTable.put('t', " \t");
			mapTable.put('r', " \r");
			mapTable.put('n', "\n ");		// Put the space after so that \r\n -> _[\r][\n]_
			mapTable.put('\\', " \\");
			mapTable.put('\'', "\' ");		// Put the space after so that \\\' -> _\'_
			mapTable.put('\"', "\\\"");		// Keep \" as \" in apostrophes
			return StringUtils.unescape(stringValue, mapTable);
		}
		else if (stringType.equals(LITERAL_CONSTANT)) {
			// Do nothing for CONSTANT
			return stringValue;
		}
		else if (stringType.equals(LITERAL_INLINE)) {
			// Do nothing for INLINE
			return stringValue;
		}
		else {
			// Don't know how to handle UNDEFINED
			MyLogger.log(MyLevel.TODO, "In LiteralNode.getUnescapedStringValuePreservingLength: Don't know how to handle UNDEFINED string type of LiteralNode: " + stringValue);
			return stringValue;
		}		
	}
	
	public static String getEscapedStringValue(String stringValue, String stringType) {
		if (stringType.equals(LITERAL_QUOTES)) {
			HashMap<Character, String> mapTable = new HashMap<Character, String>();
			mapTable.put('\\', "\\\\");
			mapTable.put('"', "\\\"");
			return StringUtils.escape(stringValue, mapTable);
		}
		else if (stringType.equals(LITERAL_APOSTROPHES)) {
			HashMap<Character, String> mapTable = new HashMap<Character, String>();
			mapTable.put('\\', "\\\\");
			mapTable.put('\'', "\\\'");
			return StringUtils.escape(stringValue, mapTable);
		}
		else if (stringType.equals(LITERAL_CONSTANT)) {
			// CONSTANT needs a special treatment. For example, if we are to replace a numeric value 10 with string a"bc, then 10 => "a\"bc"
			if (StringUtils.isNumeric(stringValue))
				return stringValue;
			else
				return "\"" + getEscapedStringValue(stringValue, LITERAL_QUOTES) + "\"";
		}
		else if (stringType.equals(LITERAL_INLINE)) {
			// Do nothing for INLINE
			return stringValue;
		}
		else {
			// Don't know how to handle UNDEFINED
			return stringValue;
		}
	}
	
	/*
	 * Provide formatting for XML.
	 */
	
	@Override
	public Element printGraphToXmlFormat(Document document, HashSet<DataNode> parentNodes) {
		Element element = document.createElement(DataModelConfig.XML_LITERAL);

		element.setAttribute(DataModelConfig.XML_STRING_VALUE, this.getUnescapedStringValue()); // Write the unescaped string
		element.setAttribute(DataModelConfig.XML_FILE_PATH, location.getLocationAtOffset(0).getFilePath());
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
	
}
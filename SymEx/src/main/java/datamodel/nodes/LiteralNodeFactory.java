package datamodel.nodes;

import java.io.File;
import java.util.HashMap;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.SwitchCase;
import org.eclipse.php.internal.core.ast.nodes.SwitchStatement;

import php.TraceTable;
import php.nodes.IdentifierNode;
import php.nodes.InLineHtmlNode;
import php.nodes.ScalarNode;
import util.StringUtils;
import util.logging.MyLevel;
import util.logging.MyLogger;
import util.sourcetracing.Location;
import util.sourcetracing.ScatteredLocation;
import util.sourcetracing.SourceCodeLocation;
import util.sourcetracing.UndefinedLocation;
import config.DataModelConfig;

public class LiteralNodeFactory {

	public static LiteralNode createLiteralNode(ScalarNode scalarNode) {
		Location location;
		String type;
		String stringValue;
		String unescapedStringValue;

		location = scalarNode.getLocation().getLocationAtOffset(
				scalarNode.getAdjustedPosition());
		type = scalarNode.getLiteralType();
		stringValue = scalarNode.getStringValue();
		unescapedStringValue = (DataModelConfig.UNESCAPE_LITERAL_STRING_VALUE_PRESERVING_LENGTH ? getUnescapedStringValuePreservingLength(
				stringValue, type) : getUnescapedStringValue(stringValue, type));

		return new LiteralNode(location, type, stringValue,
				unescapedStringValue);
	}

	public static LiteralNode createLiteralNode(InLineHtmlNode inLineHtmlNode) {
		Location location;
		String type;
		String stringValue;
		String unescapedStringValue;

		location = inLineHtmlNode.getLocation();
		type = LiteralNode.LITERAL_INLINE;
		stringValue = inLineHtmlNode.getStringValue();
		unescapedStringValue = stringValue;
		return new LiteralNode(location, type, stringValue,
				unescapedStringValue);
	}

	public static LiteralNode createLiteralNode(IdentifierNode identifierNode) {
		Location location;
		String type;
		String stringValue;
		String unescapedStringValue;

		location = identifierNode.getLocation();
		type = LiteralNode.LITERAL_UNDEFINED;
		stringValue = identifierNode.getName();
		unescapedStringValue = stringValue;

		return new LiteralNode(location, type, stringValue,
				unescapedStringValue);
	}

	public static LiteralNode createLiteralNode(SymbolicNode symbolicNode) {
		Location location;
		String type;
		String stringValue;
		String unescapedStringValue;

		location = UndefinedLocation.inst;
		type = LiteralNode.LITERAL_UNDEFINED;
		stringValue = symbolicNode.getSymbolicValue();
		unescapedStringValue = stringValue;

		return new LiteralNode(location, type, stringValue,
				unescapedStringValue);
	}

	public static LiteralNode createLiteralNode(SelectNode selectionNode) {
		Location location;
		String type;
		String stringValue;
		String unescapedStringValue;

		location = UndefinedLocation.inst;
		type = LiteralNode.LITERAL_UNDEFINED;
		stringValue = selectionNode.getSymbolicValue();
		unescapedStringValue = stringValue;

		return new LiteralNode(location, type, stringValue,
				unescapedStringValue);
	}

	public static LiteralNode createLiteralNode(LiteralNode node1,
			LiteralNode node2) {
		Location location;
		String type;
		String stringValue;
		String unescapedStringValue;

		location = new ScatteredLocation(node1.getLocation(),
				node2.getLocation(), node1.getUnescapedStringValue().length());
		type = LiteralNode.LITERAL_UNDEFINED;
		stringValue = node1.stringValue + node2.stringValue;
		unescapedStringValue = node1.unescapedStringValue
				+ node2.unescapedStringValue;

		return new LiteralNode(location, type, stringValue,
				unescapedStringValue);
	}

	public static LiteralNode createLiteralNode(ASTNode astNode) {
		Location location;
		String type;
		String stringValue;
		String unescapedStringValue;

		File filePath = TraceTable
				.getCurrentSourceFileRelativePathOfPhpASTNode(astNode);
		int position = astNode.getStart();

		location = new SourceCodeLocation(filePath, position);
		type = LiteralNode.LITERAL_UNDEFINED;
		stringValue = TraceTable.getSourceCodeOfPhpASTNode(astNode);
		unescapedStringValue = stringValue;

		return new LiteralNode(location, type, stringValue,
				unescapedStringValue);
	}

	public static LiteralNode createLiteralNode(SwitchCase switchCase) {
		LiteralNode lit = createLiteralNode((ASTNode) switchCase);
		lit.stringValue = "case "
				+ (switchCase.getParent().getParent() instanceof SwitchStatement ? TraceTable
						.getSourceCodeOfPhpASTNode(((SwitchStatement) switchCase
								.getParent().getParent()).getExpression())
						: "?")
				+ " == "
				+ (switchCase.getValue() != null ? TraceTable
						.getSourceCodeOfPhpASTNode(switchCase.getValue())
						: TraceTable.getSourceCodeOfPhpASTNode(switchCase));
		lit.unescapedStringValue = lit.stringValue;
		return lit;
	}

	public static LiteralNode createLiteralNode(String stringValue) {
		Location location;
		String type;
		String unescapedStringValue;

		location = UndefinedLocation.inst; // The value is dynamically generated
											// and cannot be traced back to the
											// source code.
		type = LiteralNode.LITERAL_UNDEFINED;
		unescapedStringValue = stringValue;

		return new LiteralNode(location, type, stringValue,
				unescapedStringValue);
	}

	public static LiteralNode createLiteralNode(String stringValue,
			Location location) { // TODO: Consider adding a 'length' property to
									// location
		String type;
		String unescapedStringValue;

		// The value is dynamically generated from an AST
		// node (e.g. function invocation)
		type = LiteralNode.LITERAL_UNDEFINED;
		unescapedStringValue = stringValue;

		return new LiteralNode(location, type, stringValue,
				unescapedStringValue);
	}

	/*
	 * Escape/unescape string values
	 */

	public static String getUnescapedStringValue(String stringValue,
			String stringType) {
		stringValue = stringValue.replace("©", "c"); // Fix the copyright
														// character (can't be
														// printed in XML
														// format)
		if (stringType.equals(LiteralNode.LITERAL_QUOTES)) {
			HashMap<Character, String> mapTable = new HashMap<Character, String>();
			mapTable.put('t', "\t");
			mapTable.put('r', "\r");
			mapTable.put('n', "\n");
			mapTable.put('\\', "\\");
			mapTable.put('"', "\"");
			mapTable.put('\'', "\\\'"); // Keep \' as \' in quotes
			// mapTable.put('u', "\\u"); // Fix a bug with the escape character
			// \\u
			return StringUtils.unescape(stringValue, mapTable);
		} else if (stringType.equals(LiteralNode.LITERAL_APOSTROPHES)) {
			HashMap<Character, String> mapTable = new HashMap<Character, String>();
			mapTable.put('t', "\t");
			mapTable.put('r', "\r");
			mapTable.put('n', "\n");
			mapTable.put('\\', "\\");
			mapTable.put('\'', "\'");
			mapTable.put('\"', "\\\""); // Keep \" as \" in apostrophes
			return StringUtils.unescape(stringValue, mapTable);
		} else if (stringType.equals(LiteralNode.LITERAL_CONSTANT)) {
			// Do nothing for CONSTANT
			return stringValue;
		} else if (stringType.equals(LiteralNode.LITERAL_INLINE)) {
			// Do nothing for INLINE
			return stringValue;
		} else {
			// Don't know how to handle UNDEFINED
			MyLogger.log(
					MyLevel.TODO,
					"In LiteralNode.getUnescapedStringValue: Don't know how to handle UNDEFINED string type of LiteralNode: "
							+ stringValue);
			return stringValue;
		}
	}

	public static String getUnescapedStringValuePreservingLength(
			String stringValue, String stringType) {
		stringValue = stringValue.replace("©", "c"); // Fix the copyright
														// character (can't be
														// printed in XML
														// format)
		if (stringType.equals(LiteralNode.LITERAL_QUOTES)) {
			HashMap<Character, String> mapTable = new HashMap<Character, String>();
			mapTable.put('t', " \t");
			mapTable.put('r', " \r");
			mapTable.put('n', "\n "); // Put the space after so that \r\n ->
										// _[\r][\n]_
			mapTable.put('\\', " \\");
			mapTable.put('"', "\" "); // Put the space after so that \\\" ->
										// _\"_
			mapTable.put('\'', "\\\'"); // Keep \' as \' in quotes
			// mapTable.put('u', "\\u"); // Fix a bug with the escape character
			// \\u
			return StringUtils.unescape(stringValue, mapTable);
		} else if (stringType.equals(LiteralNode.LITERAL_APOSTROPHES)) {
			HashMap<Character, String> mapTable = new HashMap<Character, String>();
			mapTable.put('t', " \t");
			mapTable.put('r', " \r");
			mapTable.put('n', "\n "); // Put the space after so that \r\n ->
										// _[\r][\n]_
			mapTable.put('\\', " \\");
			mapTable.put('\'', "\' "); // Put the space after so that \\\' ->
										// _\'_
			mapTable.put('\"', "\\\""); // Keep \" as \" in apostrophes
			return StringUtils.unescape(stringValue, mapTable);
		} else if (stringType.equals(LiteralNode.LITERAL_CONSTANT)) {
			// Do nothing for CONSTANT
			return stringValue;
		} else if (stringType.equals(LiteralNode.LITERAL_INLINE)) {
			// Do nothing for INLINE
			return stringValue;
		} else {
			// Don't know how to handle UNDEFINED
			MyLogger.log(
					MyLevel.TODO,
					"In LiteralNode.getUnescapedStringValuePreservingLength: Don't know how to handle UNDEFINED string type of LiteralNode: "
							+ stringValue);
			return stringValue;
		}
	}

	public static String getEscapedStringValue(String stringValue,
			String stringType) {
		if (stringType.equals(LiteralNode.LITERAL_QUOTES)) {
			HashMap<Character, String> mapTable = new HashMap<Character, String>();
			mapTable.put('\\', "\\\\");
			mapTable.put('"', "\\\"");
			return StringUtils.escape(stringValue, mapTable);
		} else if (stringType.equals(LiteralNode.LITERAL_APOSTROPHES)) {
			HashMap<Character, String> mapTable = new HashMap<Character, String>();
			mapTable.put('\\', "\\\\");
			mapTable.put('\'', "\\\'");
			return StringUtils.escape(stringValue, mapTable);
		} else if (stringType.equals(LiteralNode.LITERAL_CONSTANT)) {
			// CONSTANT needs a special treatment. For example, if we are to
			// replace a numeric value 10 with string a"bc, then 10 => "a\"bc"
			if (StringUtils.isNumeric(stringValue))
				return stringValue;
			else
				return "\""
						+ getEscapedStringValue(stringValue,
								LiteralNode.LITERAL_QUOTES) + "\"";
		} else if (stringType.equals(LiteralNode.LITERAL_INLINE)) {
			// Do nothing for INLINE
			return stringValue;
		} else {
			// Don't know how to handle UNDEFINED
			return stringValue;
		}
	}

}

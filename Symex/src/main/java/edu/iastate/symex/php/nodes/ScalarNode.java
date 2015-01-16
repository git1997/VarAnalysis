package edu.iastate.symex.php.nodes;

import java.util.ArrayList;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.Scalar;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.position.Range;
import edu.iastate.symex.position.Position;

/**
 * 
 * @author HUNG
 *
 */
public class ScalarNode extends ExpressionNode {

	private String stringValue;

	private int scalarType;	
	
	/*
	Represents a scalar 

	e.g. 'string',
	 1,
	 1.3,
	 __CLASS__
	 */
	public ScalarNode(Scalar scalar) {
		super(scalar);
		stringValue = scalar.getStringValue();
		scalarType = scalar.getScalarType();
	}
	
	@Override
	public DataNode execute(Env env) {
		int scalarKind;
		
		switch (scalarType) {
			case Scalar.TYPE_BIN:												// [1] e.g. ... // TODO Handle this case
				scalarKind = 1;
				break;
			case Scalar.TYPE_INT:												// [2] e.g. 123
				scalarKind = 2;		
				break;
			case Scalar.TYPE_REAL:												// [3] e.g. 123.4
				scalarKind = 3;
				break;
			case Scalar.TYPE_STRING:
				if (getAstNode().getParent().getType() == ASTNode.QUOTE)		// [4] e.g. abc in "abc$x"
					scalarKind = 4;
				else if (stringValue.startsWith("\""))							// [5] e.g. "abc"
					scalarKind = 5;
				else if (stringValue.startsWith("'"))							// [6] e.g. 'abc'
					scalarKind = 6;
				else															// [7] e.g. ABC
					scalarKind = 7;			
				break;
			case Scalar.TYPE_SYSTEM:											// [8] e.g. __ABC__
				scalarKind = 8;
				break;
			default: // Scalar.TYPE_UNKNOWN										// [9] e.g. ... // TODO Handle this case
				scalarKind = 9;
				break;
		}
		
		switch (scalarKind) {
			// If it is surrounded by quotes/apostrophes
			case 4:
				String string1 = stringValue;
				Position position1 = this.getLocation().getPositionAtRelativeOffset(0);
				return generateDataNode(string1, position1, '\"');
			case 5:
				String string2 = stringValue.substring(1, stringValue.length() - 1);
				Position position2 = this.getLocation().getPositionAtRelativeOffset(1);
				return generateDataNode(string2, position2, '\"');
			case 6:
				String string3 = stringValue.substring(1, stringValue.length() - 1);
				Position position3 = this.getLocation().getPositionAtRelativeOffset(1);
				return generateDataNode(string3, position3, '\'');
			
			// If it is a predefined constants (e.g. WEBSITE_PATH, __FILE__)
			case 7:
			case 8:
				DataNode constantValue = env.getPredefinedConstantValue(stringValue);
				if (constantValue == SpecialNode.UnsetNode.UNSET)
					return DataNodeFactory.createLiteralNode(this);
				else if (constantValue instanceof SymbolicNode)
					return DataNodeFactory.createSymbolicNode(this, (SymbolicNode) constantValue);
				else
					return constantValue;
			
			// Other cases
			default:
				return DataNodeFactory.createLiteralNode(this);
		}
	}
	
	/**
	 * Generates a DataNode from the string of a PHP scalar.
	 * For example, given the PHP scalar "print(\"Hello\")", the inputs are
	 * 		+ string: 		print(\"Hello\")
	 * 		+ position:		1
	 * 		+ stringType: 	" // quotes
	 * The returned value should be a ConcatNode with the following properties:
	 * 		+ fragments:	print(		  "        Hello         "          )
	 * 		+ positions:	print( [1-6], " [7-8], Hello [9-13], " [14-15], ) [16]
	 * @param string		The string of a PHP scalar
	 * @param position		The position of the string
	 * @param stringType	The type of the string (originally enclosed by single quotes ' or double quotes ")
	 * @return
	 */
	private DataNode generateDataNode(String string, Position position, char stringType) {
		// Chop the string into fragments separated by slash \ characters.
		ArrayList<DataNode> fragments = new ArrayList<DataNode>();
		int beginIndex = 0; // beginIndex of a fragment, inclusive
		int endIndex = 0; // endIndex of a fragment, exclusive
		
		while (true) {
			int idx = string.indexOf('\\', beginIndex);
			if (idx == -1) {
				// Get the remaining fragment
				endIndex = string.length();
				String stringValue = string.substring(beginIndex, endIndex);
				Range range = new Range(position.getFile(), position.getOffset() + beginIndex, endIndex - beginIndex);
				LiteralNode literalNode = DataNodeFactory.createLiteralNode(stringValue, range);
				fragments.add(literalNode);
				break;
			}
			else {
				// Get the fragment up to the slash character
				endIndex = idx;
				if (beginIndex < endIndex) {
					String stringValue = string.substring(beginIndex, endIndex);
					Range range = new Range(position.getFile(), position.getOffset() + beginIndex, endIndex - beginIndex);
					LiteralNode literalNode = DataNodeFactory.createLiteralNode(stringValue, range);
					fragments.add(literalNode);
				}
				
				// Get the fragment at the slash character (e.g., \" => ")
				/*
				 * There are 2 strategies for position mapping:
				 * 	1. A generated " character is mapped to \" in the source code: Correct but range.Length != stringValue.Length
				 *  2. A generated " character is mapped to " in the source code: Acceptable, and more importantly, range.Length == stringValue.Length,
				 *  		which facilitates position tracking.
				 * If we want to use strategy 1, we need to modify the implementation of Range: add one more field stringLength to represent the length
				 *   of the string that is mapped to that range and call sites of Range should call Range.getStringLength() rather than Range.getLength().
				 * However, for simplicity, let's use strategy 2 for now.
				 */
				beginIndex = endIndex;
				endIndex = endIndex + 2;
				String stringValue = unescapeString(string.substring(beginIndex, endIndex), stringType);
				Range range = new Range(position.getFile(), position.getOffset() + endIndex - stringValue.length(), stringValue.length());
				LiteralNode literalNode = DataNodeFactory.createLiteralNode(stringValue, range);
				fragments.add(literalNode);
				
				beginIndex = endIndex;
				if (endIndex == string.length())
					break;
			}
		}
		
		return DataNodeFactory.createCompactConcatNode(fragments);
	}
	
	/**
	 * Unescapes a character following a \.
	 * Example 1: 
	 * 		Original string: "abc\"def"
	 * 		Input string: \"
	 * 		String type: "
	 * 		Unescaped string: "
	 * Example 2:
	 * 		Original string: 'abc\"def'
	 * 		Input string: \"
	 * 		String type: '
	 * 		Unescaped string: \"
	 * @param inputString		The input string consisting of a slash \ and a character.
	 * @param stringType		The type of the string (originally enclosed by single quotes ' or double quotes ")
	 * @return					The unescaped character (or the original input string if it fails)
	 */
	private String unescapeString(String inputString, char stringType) {
		switch (inputString) {
			case "\\t": 	return "\t";
			case "\\r":		return "\r";
			case "\\n": 	return "\n";
			case "\\\\": 	return "\\";
			case "\\'":		return (stringType == '\'' ? "'" : inputString);	
			case "\\\"":	return (stringType == '"' ? "\"" : inputString);
			default: 		return inputString;
		}
	}
	
}
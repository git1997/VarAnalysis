package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.Scalar;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.position.Position;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.AtomicPositionRange;
import edu.iastate.symex.util.StringUtils;

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
			default: // Scalar.TYPE_UNKNOWN										// [9] e.g. ...
				scalarKind = 9;
				break;
		}
		
		switch (scalarKind) {
			// If it is a predefined constants (e.g. WEBSITE_PATH, __FILE__)
			case 7:
			case 8:
				DataNode constantValue = env.getPredefinedConstantValue(getSourceCode());
				return constantValue != null ? constantValue : DataNodeFactory.createSymbolicNode(this);
			
			// If it is surrounded by quotes/apostrophes
			case 4:
				String string3 = stringValue;
				Position position3 = this.getPositionRange().getPositionAtRelativeOffset(0);
				return generateLiteralNode(string3, position3, '\"');
			case 5:
				String string1 = stringValue.substring(1, stringValue.length() - 1);
				Position position1 = this.getPositionRange().getPositionAtRelativeOffset(1);
				return generateLiteralNode(string1, position1, '\"');
			case 6:
				String string2 = stringValue.substring(1, stringValue.length() - 1);
				Position position2 = this.getPositionRange().getPositionAtRelativeOffset(1);
				return generateLiteralNode(string2, position2, '\'');
			
			// Other cases
			default:
				return DataNodeFactory.createLiteralNode(this);
		}
	}
	
	/**
	 * Generates a LiteralNode from the string of a PHP scalar.
	 * For example, given the PHP scalar "print(\"Hello\")", the inputs are
	 * 		+ string: 		print(\"Hello\")
	 * 		+ position:		1
	 * 		+ stringType: 	" // quotes
	 * The returned value should be a LiteralNode with the following properties:
	 * 		+ stringValue:	print("Hello")
	 * 		+ position:		print( [1-6], " [7-8], Hello [9-13], ") [15-16]
	 * @param string		The string of a PHP scalar
	 * @param position		The position of the string
	 * @param stringType	The type of the string ("" or '')
	 * @return
	 */
	private LiteralNode generateLiteralNode(String string, Position position, char stringType) {
		// TODO Fix this
		
		PositionRange positionRange = new AtomicPositionRange(position.getFile(), position.getOffset(), stringValue.length());
		String stringValue = StringUtils.getUnescapedStringValuePreservingLength(string, stringType);
		return DataNodeFactory.createLiteralNode(positionRange, stringValue);
	}
	
}
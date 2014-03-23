package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.Scalar;

import php.ElementManager;
import datamodel.nodes.DataNode;
import datamodel.nodes.LiteralNode;

public class ScalarNode extends ExpressionNode {

	private String stringValue;				// The string value of the ScalarNode (without the enclosing quotes and apostrophes) (e.g. "abc" -> abc)
	
	private int adjustedPosition;			// Adjust the position of the ScalarNode if the enclosing quotes and apostrophes are removed
	
	private boolean isPredefinedConstant;	// Whether it is a predefined constants (e.g. WEBSITE_PATH, __FILE__)
	
	private String literalType;				// Convert the scalar type to the literal type. @see datamodel.nodes.LiteralNode	
	
	/*
	Represents a scalar 

	e.g. 'string',
	 1,
	 1.3,
	 __CLASS__
	 */
	public ScalarNode(Scalar scalar) {
		super(scalar);
		
		int scalarCase;
		switch (scalar.getScalarType()) {
			case Scalar.TYPE_INT:										// [1] e.g. 123
				scalarCase = 1;
				break;
			case Scalar.TYPE_REAL:										// [2] e.g. 123.4
				scalarCase = 2;
				break;
			case Scalar.TYPE_STRING:
				if (scalar.getParent().getType() == ASTNode.QUOTE)		// [3] e.g. abc in "abc$x"
					scalarCase = 3;
				else if (scalar.getStringValue().startsWith("\""))		// [4] e.g. abc in "abc"
					scalarCase = 4;
				else if (scalar.getStringValue().startsWith("'"))		// [5] e.g. abc in 'abc'
					scalarCase = 5;
				else													// [6] e.g. ABC
					scalarCase = 6;			
				break;
			case Scalar.TYPE_SYSTEM:									// [7] e.g. __ABC__
				scalarCase = 7;
				break;
			default:													// [8] Scalar.TYPE_UNKNOWN:
				scalarCase = 8;
				break;
		}
		
		if (scalarCase == 4 || scalarCase == 5) {
			this.stringValue = scalar.getStringValue().substring(1, scalar.getStringValue().length() - 1);
			this.adjustedPosition = 1;
		}
		else {
			this.stringValue = scalar.getStringValue();
			this.adjustedPosition = 0;
		}
		
		this.isPredefinedConstant = (scalarCase == 6 || scalarCase == 7);
		
		switch (scalarCase) {
			case 1:
			case 2:
			case 6:
			case 7:
				this.literalType = LiteralNode.LITERAL_CONSTANT;
				break;
			case 3:
			case 4:
				this.literalType = LiteralNode.LITERAL_QUOTES;
				break;
			case 5:
				this.literalType = LiteralNode.LITERAL_APOSTROPHES;
				break;
			case 8:
				this.literalType = LiteralNode.LITERAL_UNDEFINED;
				break;
		}
		
		/*
		 * Code for VarAnalysis Evaluation
		 * Should be deleted afterwards.
		 */
		// BEGIN
//		this.literalType = Evaluation.convertEchoToInlineType(scalar, this.literalType);
		// END
	}
	
	/*
	 * Get properties
	 */
		
	public String getStringValue() {
		return stringValue;
	}
	
	public int getAdjustedPosition() {
		return adjustedPosition;
	}
	
	public boolean isPredefinedConstant() {
		return isPredefinedConstant;
	}
	
	public String getLiteralType() {
		return literalType;
	}

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		if (this.isPredefinedConstant())
			return elementManager.getPredefinedConstantValue(this);
		else
			return new LiteralNode(this);		
	}
	
}
package php.nodes;

import logging.MyLevel;
import logging.MyLogger;

import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.FormalParameter;
import org.eclipse.php.internal.core.ast.nodes.Reference;
import org.eclipse.php.internal.core.ast.nodes.Scalar;
import org.eclipse.php.internal.core.ast.nodes.Variable;

import php.ElementManager;
import php.TraceTable;

import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class FormalParameterNode extends PhpNode {

	private ExpressionNode parameterNameExpressionNode;	// The name of the parameter
	private String parameterName = null;
	
	private boolean isReference;						// Parameter passing by reference or by value
	
	private ExpressionNode defaultValue = null;			// Can be null
	
	/*
	Represents a function formal parameter 

	e.g. $a,
	 MyClass $a,
	 $a = 3,
	 int $a = 3
	*/
	public FormalParameterNode(FormalParameter formalParameter) {
		super(formalParameter);
		Expression parameterNameExpression = formalParameter.getParameterName();
		switch (parameterNameExpression.getType()) {
			case Expression.VARIABLE: 
				this.parameterNameExpressionNode = ExpressionNode.createInstance(((Variable) parameterNameExpression).getName()); 
				this.isReference = false;
				break;
			case Expression.REFERENCE: 
				this.parameterNameExpressionNode = ExpressionNode.createInstance(((Variable)(((Reference) parameterNameExpression).getExpression())).getName());
				this.isReference = true;
				break;
			default:
				MyLogger.log(MyLevel.TODO, "FormalParameterNode.java: Parameter type " + TraceTable.getSourceCodeOfPhpASTNode(formalParameter) + " not yet implemented.");
				this.parameterNameExpressionNode = ExpressionNode.createInstance(parameterNameExpression);
				this.isReference = false;
		}
		Expression defaultValue = formalParameter.getDefaultValue();
		if (defaultValue != null && !(defaultValue instanceof Scalar) || 
				defaultValue instanceof Scalar && !((Scalar) defaultValue).getStringValue().toLowerCase().equals("null")) {
			this.defaultValue = ExpressionNode.createInstance(defaultValue);
		}
	}
	
	/*
	 * Get properties
	 */
	
	public ExpressionNode getParameterNameExpressionNode() {
		return parameterNameExpressionNode;
	}
	
	/**
	 * Resolves the name of the parameter.
	 */
	public String resolveParameterName(ElementManager elementManager) {
		if (parameterName == null)
			parameterName = parameterNameExpressionNode.resolveName(elementManager);
		return parameterName;
	}
	
	public boolean isReference() {
		return isReference;
	}
	
	public ExpressionNode getDefaultValue() {
		return defaultValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		return null;	// This function should not be called
	}

}

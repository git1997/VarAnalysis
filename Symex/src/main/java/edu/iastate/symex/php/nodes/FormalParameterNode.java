package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.FormalParameter;
import org.eclipse.php.internal.core.ast.nodes.Reference;
import org.eclipse.php.internal.core.ast.nodes.Scalar;
import org.eclipse.php.internal.core.ast.nodes.Variable;

import edu.iastate.symex.util.ASTHelper;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class FormalParameterNode extends PhpNode {

	private ExpressionNode parameterName;			// The name of the parameter
	
	private boolean isReference;					// Parameter passing by reference or by value
	
	private ExpressionNode defaultValue = null;		// Can be null
	
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
				this.parameterName = ExpressionNode.createInstance(((Variable) parameterNameExpression).getName()); 
				this.isReference = false;
				break;
			case Expression.REFERENCE: 
				this.parameterName = ExpressionNode.createInstance(((Variable)(((Reference) parameterNameExpression).getExpression())).getName());
				this.isReference = true;
				break;
			default:
				MyLogger.log(MyLevel.TODO, "FormalParameterNode.java: Parameter type " + ASTHelper.inst.getSourceCodeOfPhpASTNode(formalParameter) + " not yet implemented.");
				this.parameterName = ExpressionNode.createInstance(parameterNameExpression);
				this.isReference = false;
		}
		Expression defaultValue = formalParameter.getDefaultValue();
		if (defaultValue != null && !(defaultValue instanceof Scalar) || 
				defaultValue instanceof Scalar && !((Scalar) defaultValue).getStringValue().toLowerCase().equals("null")) {
			this.defaultValue = ExpressionNode.createInstance(defaultValue);
		}
	}
	
	public ExpressionNode getParameterName() {
		return parameterName;
	}
	
	/**
	 * Resolves the name of the parameter.
	 */
	public String getResolvedParameterNameOrNull(Env env) {
		return parameterName.getResolvedNameOrNull(env);
	}
	
	public boolean isReference() {
		return isReference;
	}
	
	public ExpressionNode getDefaultValue() {
		return defaultValue;
	}
	
	@Override
	public DataNode execute(Env env) {
		MyLogger.log(MyLevel.USER_EXCEPTION, "In FormalParameterNode.java: FormalParameterNode + " + this.getSourceCode() + " should not get executed.");
		return null;
	}

}

package php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Variable;

import php.Constraint;
import php.ElementManager;
import php.elements.PhpVariable;
import datamodel.nodes.DataNode;
import datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class VariableNode extends VariableBaseNode {
	
	private ExpressionNode variableNameExpressionNode;	// The name of the variable
	private String variableName = null;
	
	/*
	Holds a variable. note that the variable name can be expression, 
	
	e.g. $a
	*/
	public VariableNode(Variable variable) {
		super(variable);
		variableNameExpressionNode = ExpressionNode.createInstance(variable.getName());
	}
	
	/**
	 * Resolves the name of the variable.
	 */
	public String resolveVariableName(ElementManager elementManager) {
		if (variableName == null)
			variableName = variableNameExpressionNode.resolveName(elementManager);
		return variableName;
	}
	
	/*
	 * The following code is used from BabelRef to identify PHP variable entities.
	 */
	// BEGIN OF BABELREF CODE
	public interface IVariableDeclListener {
		public void variableDeclFound(IdentifierNode variableName, ArrayList<Constraint> constraints, String scope);
	}
	
	public interface IVariableRefListener {
		public void variableRefFound(IdentifierNode variableName, ArrayList<Constraint> constraints, String scope);
	}
	
	public static IVariableDeclListener variableDeclListener = null;
	
	public static IVariableRefListener variableRefListener = null;
	// END OF BABELREF CODE

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		/*
		 * The following code is used from BabelRef to identify PHP variable entities.
		 */
		// BEGIN OF BABELREF CODE
		variableRefFound(elementManager);
		// END OF BABELREF CODE
		
		String variableName = resolveVariableName(elementManager);
		PhpVariable phpVariable = elementManager.getVariableFromFunctionScope(variableName);
		if (phpVariable == null)
			return new SymbolicNode(this);
		else if (phpVariable.getDataNode() instanceof SymbolicNode) {
			SymbolicNode symbolicNode = new SymbolicNode(this);
			symbolicNode.setParentNode((SymbolicNode) phpVariable.getDataNode());
			return symbolicNode;
		}
		else
			return phpVariable.getDataNode();
	}

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.VariableBaseNode#createVariablePossiblyWithNull(servergraph.ElementManager)
	 */
	@Override
	public PhpVariable createVariablePossiblyWithNull(ElementManager elementManager) {
		/*
		 * The following code is used from BabelRef to identify PHP variable entities.
		 */
		// BEGIN OF BABELREF CODE
		variableDeclFound(elementManager);
		// END OF BABELREF CODE
		
		String variableName = resolveVariableName(elementManager);
		PhpVariable phpVariable = new PhpVariable(variableName);
		return phpVariable;
	}
	
	/*
	 * The following code is used from BabelRef to identify PHP variable entities.
	 */
	// BEGIN OF BABELREF CODE
	public void variableDeclFound(ElementManager elementManager) {
		if (variableDeclListener != null) {
			if (variableNameExpressionNode instanceof IdentifierNode)
				variableDeclListener.variableDeclFound((IdentifierNode) variableNameExpressionNode, elementManager.getConstraints(), getFunctionScope(elementManager));
		}
	}
	
	public void variableRefFound(ElementManager elementManager) {
		if (variableRefListener != null) {
			if (variableNameExpressionNode instanceof IdentifierNode)
				variableRefListener.variableRefFound((IdentifierNode) variableNameExpressionNode, elementManager.getConstraints(), getFunctionScope(elementManager));
		}
	}
	
	private String getFunctionScope(ElementManager elementManager) {
		ArrayList<String> functionStack = elementManager.getFunctionStack();
		if (functionStack.size() == 0 || elementManager.getGlobalVariableNames().contains(resolveVariableName(elementManager)))
			return "GLOBAL_SCOPE";
		else
			return "FUNCTION_SCOPE_" + functionStack.get(functionStack.size() - 1);
	}
	// END OF BABELREF CODE	
	
}
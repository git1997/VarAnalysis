package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Variable;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.php.elements.PhpVariable;

/**
 * 
 * @author HUNG
 *
 */
public class VariableNode extends VariableBaseNode {
	
	private ExpressionNode name;	// The name of the variable
	
	/*
	Holds a variable. note that the variable name can be expression, 
	
	e.g. $a
	*/
	public VariableNode(Variable variable) {
		super(variable);
		name = ExpressionNode.createInstance(variable.getName());
	}
	
	/**
	 * Resolves the name of the variable.
	 */
	public String getResolvedVariableNameOrNull(Env env) {
		return name.getResolvedNameOrNull(env);
	}
	
	/*
	 * The following code is used from BabelRef to identify PHP variable entities.
	 */
	// BEGIN OF BABELREF CODE
//	public interface IVariableDeclListener {
//		public void variableDeclFound(IdentifierNode variableName, ArrayList<Constraint> constraints, String scope);
//	}
//	
//	public interface IVariableRefListener {
//		public void variableRefFound(IdentifierNode variableName, ArrayList<Constraint> constraints, String scope);
//	}
//	
//	public static IVariableDeclListener variableDeclListener = null;
//	
//	public static IVariableRefListener variableRefListener = null;
	// END OF BABELREF CODE

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.env)
	 */
	@Override
	public DataNode execute(Env env) {
		/*
		 * The following code is used from BabelRef to identify PHP variable entities.
		 */
		// BEGIN OF BABELREF CODE
//		variableRefFound(env);
		// END OF BABELREF CODE
		
		String variableName = getResolvedVariableNameOrNull(env);
		PhpVariable phpVariable = env.getVariableFromFunctionScope(variableName);
		if (phpVariable == null)
			return new SymbolicNode(this);
		else if (phpVariable.getDataNode() instanceof SymbolicNode) {
			SymbolicNode symbolicNode = new SymbolicNode(this, (SymbolicNode) phpVariable.getDataNode());
			return symbolicNode;
		}
		else
			return phpVariable.getDataNode();
	}

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.VariableBaseNode#createVariablePossiblyWithNull(servergraph.env)
	 */
	@Override
	public PhpVariable createVariablePossiblyWithNull(Env env) {
		/*
		 * The following code is used from BabelRef to identify PHP variable entities.
		 */
		// BEGIN OF BABELREF CODE
//		variableDeclFound(env);
		// END OF BABELREF CODE
		
		String variableName = getResolvedVariableNameOrNull(env);
		PhpVariable phpVariable = new PhpVariable(variableName);
		return phpVariable;
	}
	
	/*
	 * The following code is used from BabelRef to identify PHP variable entities.
	 */
	// BEGIN OF BABELREF CODE
//	public void variableDeclFound(Env env) {
//		if (variableDeclListener != null) {
//			if (name instanceof IdentifierNode)
//				variableDeclListener.variableDeclFound((IdentifierNode) name, env.getConstraints(), getFunctionScope(env));
//		}
//	}
//	
//	public void variableRefFound(Env env) {
//		if (variableRefListener != null) {
//			if (name instanceof IdentifierNode)
//				variableRefListener.variableRefFound((IdentifierNode) name, env.getConstraints(), getFunctionScope(env));
//		}
//	}
//	
//	private String getFunctionScope(Env env) {
//		ArrayList<String> functionStack = env.getFunctionStack();
//		if (functionStack.size() == 0 || env.getGlobalVariableNames().contains(getResolveVariableNameOrNull(env)))
//			return "GLOBAL_SCOPE";
//		else
//			return "FUNCTION_SCOPE_" + functionStack.get(functionStack.size() - 1);
//	}
	// END OF BABELREF CODE	
	
}
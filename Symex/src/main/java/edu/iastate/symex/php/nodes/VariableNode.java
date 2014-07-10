package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Variable;

import edu.iastate.symex.analysis.WebAnalysis;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;

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
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.env)
	 */
	@Override
	public DataNode execute(Env env) {
		/*
		 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB ANALYSIS CODE
		WebAnalysis.onVariableExecute((Variable) this.getAstNode(), env);
		// END OF WEB ANALYSIS CODE
		
		String variableName = getResolvedVariableNameOrNull(env);
		PhpVariable phpVariable = env.readVariable(variableName);
		if (phpVariable == null)
			return DataNodeFactory.createSymbolicNode(this);
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
		String variableName = getResolvedVariableNameOrNull(env);
		PhpVariable phpVariable = new PhpVariable(variableName);
		return phpVariable;
	}
	
}
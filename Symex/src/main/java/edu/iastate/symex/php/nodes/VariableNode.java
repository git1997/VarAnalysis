package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Variable;

import edu.iastate.symex.analysis.WebAnalysis;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.SpecialNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class VariableNode extends VariableBaseNode {
	
	private ExpressionNode name;	// The name of the variable
	private boolean isDollared;		// isDollared or not (e.g., 'bar' in $foo->bar)
	
	/*
	Holds a variable. note that the variable name can be expression, 
	
	e.g. $a
	*/
	public VariableNode(Variable variable) {
		super(variable);
		name = ExpressionNode.createInstance(variable.getName());
		isDollared = variable.isDollared();
	}
	
	/**
	 * Resolves the name of the variable.
	 * Note that this is different from the method Expression.getResolvedNameOrNull:
	 * 		$x = 'foo';
	 * 		echo $a[$x];
	 * 		Variable($x).getResolvedVariableNameOrNull returns 'x'
	 * whereas Expression($x).getResolvedNameOrNull returns 'foo'
	 * @see edu.iastate.symex.php.nodes.ExpressionNode.getResolvedNameOrNull(Env)
	 */
	public String getResolvedVariableNameOrNull(Env env) {
		return name.getResolvedNameOrNull(env);
	}
	
	@Override
	public DataNode execute(Env env) {
		/*
		 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB ANALYSIS CODE
		WebAnalysis.onVariableExecute((Variable) this.getAstNode(), env);
		// END OF WEB ANALYSIS CODE
		
		if (!isDollared) {
			if (name instanceof IdentifierNode)
				return DataNodeFactory.createLiteralNode(name);
			else {
				MyLogger.log(MyLevel.USER_EXCEPTION, "In VariableNode.java: name should be an Identifier when isDollared = false. ");
				return DataNodeFactory.createSymbolicNode(this);
			}
		}
		
		String variableName = getResolvedVariableNameOrNull(env);
		DataNode variableValue = env.readVariable(variableName);
		if (variableValue == SpecialNode.UnsetNode.UNSET)
			return DataNodeFactory.createSymbolicNode(this);
		else if (variableValue instanceof SymbolicNode)
			return DataNodeFactory.createSymbolicNode(this, (SymbolicNode) variableValue);
		else
			return variableValue;
	}

	@Override
	public PhpVariable createVariablePossiblyWithNull(Env env) {
		String variableName = getResolvedVariableNameOrNull(env);
		if (variableName != null) {
			return env.getOrPutVariable(variableName);
		}
		else
			return null;
	}
	
}
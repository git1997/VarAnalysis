package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ReflectionVariable;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class ReflectionVariableNode extends VariableNode {
	
	/*
	Represents an indirect reference to a variable.

	e.g.
	 $$a
	 $$foo()
	*/
	public ReflectionVariableNode(ReflectionVariable reflectionVariable) {
		super(reflectionVariable);
	}
	
	@Override
	public String getResolvedVariableNameOrNull(Env env) {
		MyLogger.log(MyLevel.USER_EXCEPTION, "In ReflectionVariableNode.java: Method getResolvedVariableNameOrNull not yet implemented.");
		return super.getResolvedVariableNameOrNull(env);
	}
	
	@Override
	public DataNode execute(Env env) {
		/*
		 * In the following example,
		 * 		$a = 'b';
		 *		$b = 'y';
		 * 		echo "{$a}"; // output 'b', the brackets are used to limit the variable
		 *		echo $$a;	// output 'y'
		 * The AST node for {$a} and $$a have the same properties: name = $a, isDollared == false, type = ReflectionVariable.
		 * However, they produce two different outputs. (This is possibly due to an error in the PHP parser.)
		 * 
		 * Therefore, we need to find a heuristic way to differentiate {$a} and $$a.
		 */
		if (this.getSourceCode().startsWith("{"))
			return singleEvaluation(env);
		else
			return doubleEvaluation(env);
	}
	
	@Override
	public PhpVariable createVariablePossiblyWithNull(Env env) {
		MyLogger.log(MyLevel.USER_EXCEPTION, "In ReflectionVariableNode.java: Method createVariablePossiblyWithNull not yet implemented.");
		return super.createVariablePossiblyWithNull(env);
	}
	
}
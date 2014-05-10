package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ArrayAccess;
import org.eclipse.php.internal.core.ast.nodes.Dispatch;
import org.eclipse.php.internal.core.ast.nodes.FunctionInvocation;
import org.eclipse.php.internal.core.ast.nodes.Variable;
import org.eclipse.php.internal.core.ast.nodes.VariableBase;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;

import edu.iastate.symex.util.ASTHelper;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public abstract class VariableBaseNode extends ExpressionNode {

	/**
	 * Constructor
	 * @param variableBase
	 */
	public VariableBaseNode(VariableBase variableBase) {
		super(variableBase);
	}
	
	/*
	This interface is base for all the PHP variables including simple variable, function invocation, list, dispatch, etc.
	 */
	public static VariableBaseNode createInstance(VariableBase variableBase) {
		if (variableBase instanceof Dispatch)
			return DispatchNode.createInstance((Dispatch) variableBase);
		
		switch (variableBase.getType()) {
			case VariableBase.ARRAY_ACCESS:			return new ArrayAccessNode((ArrayAccess) variableBase);
			case VariableBase.FUNCTION_INVOCATION:	return new FunctionInvocationNode((FunctionInvocation) variableBase);
			case VariableBase.VARIABLE:				return new VariableNode((Variable) variableBase);
			default:								MyLogger.log(MyLevel.TODO, "VariableBase unimplemented: " + ASTHelper.inst.getSourceCodeOfPhpASTNode(variableBase)); return new UnresolvedVariableBaseNode(variableBase);
		}
	}
	
	/**
	 * Creates a PHP variable from the VariableBase.
	 */
	public abstract PhpVariable createVariablePossiblyWithNull(Env env);
	
}
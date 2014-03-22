package php.nodes;

import logging.MyLevel;
import logging.MyLogger;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.ArrayAccess;
import org.eclipse.php.internal.core.ast.nodes.Dispatch;
import org.eclipse.php.internal.core.ast.nodes.FunctionInvocation;
import org.eclipse.php.internal.core.ast.nodes.Variable;
import org.eclipse.php.internal.core.ast.nodes.VariableBase;

import php.ElementManager;
import php.TraceTable;
import php.elements.PhpVariable;


/**
 * 
 * @author HUNG
 *
 */
public abstract class VariableBaseNode extends ExpressionNode {

	/**
	 * Constructor
	 * @param astNode
	 */
	public VariableBaseNode(ASTNode astNode) {
		super(astNode);
	}
	
	/*
	This interface is base for all the PHP variables including simple variable, function invocation, list, dispatch, etc.
	 */
	public static VariableBaseNode createInstance(VariableBase variableBase) {
		if (variableBase instanceof Dispatch) {
			return DispatchNode.createInstance((Dispatch) variableBase);
		}
		switch (variableBase.getType()) {
			case VariableBase.ARRAY_ACCESS:			return new ArrayAccessNode((ArrayAccess) variableBase);
			case VariableBase.FUNCTION_INVOCATION:	return new FunctionInvocationNode((FunctionInvocation) variableBase);
			case VariableBase.VARIABLE:				return new VariableNode((Variable) variableBase);
			default:								MyLogger.log(MyLevel.TODO, "VariableBase Expression unimplemented: " + TraceTable.getSourceCodeOfPhpASTNode(variableBase)); return new UnresolvedVariableBaseNode(variableBase);
		}
	}
	
	/**
	 * Creates a PHP variable from the VariableBase.
	 */
	public abstract PhpVariable createVariablePossiblyWithNull(ElementManager elementManager);
	
}
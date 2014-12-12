package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Dispatch;
import org.eclipse.php.internal.core.ast.nodes.FieldAccess;
import org.eclipse.php.internal.core.ast.nodes.MethodInvocation;


import edu.iastate.symex.util.ASTHelper;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

public abstract class DispatchNode extends VariableBaseNode {
	
	/**
	 * Constructor
	 * @param dispatch
	 */
	public DispatchNode(Dispatch dispatch) {
		super(dispatch);
	}
	
	/*
	Represents a base class for method invocation and field access 

	e.g. $a->$b,
	 foo()->bar(),
	 $myClass->foo()->bar(),
	 A::$a->foo()
	*/ 
	public static DispatchNode createInstance(Dispatch dispatch) {
		switch (dispatch.getType()) {
			case Dispatch.FIELD_ACCESS: 		return new FieldAccessNode((FieldAccess) dispatch);
			case Dispatch.METHOD_INVOCATION:	return new MethodInvocationNode((MethodInvocation) dispatch);
			default:							MyLogger.log(MyLevel.TODO, "Dispatch AST node type (" + dispatch.getClass().getSimpleName() + ") unimplemented: " + ASTHelper.inst.getSourceCodeOfPhpASTNode(dispatch)); return new UnresolvedDispatchNode(dispatch);
		}
	}

}

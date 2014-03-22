package php.nodes;

import logging.MyLevel;
import logging.MyLogger;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.Dispatch;
import org.eclipse.php.internal.core.ast.nodes.FieldAccess;
import org.eclipse.php.internal.core.ast.nodes.MethodInvocation;

import php.TraceTable;


public abstract class DispatchNode extends VariableBaseNode {
	
	/**
	 * Constructor
	 * @param astNode
	 */
	public DispatchNode(ASTNode astNode) {
		super(astNode);
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
			default:							MyLogger.log(MyLevel.TODO, "Dispatch VariableBase Expression unimplemented: " + TraceTable.getSourceCodeOfPhpASTNode(dispatch)); return new UnresolvedDispatchNode(dispatch);
		}
	}

}

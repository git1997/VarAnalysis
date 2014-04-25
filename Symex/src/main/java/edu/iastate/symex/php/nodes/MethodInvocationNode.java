package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.MethodInvocation;

import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.ObjectNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.php.elements.PhpVariable;

/**
 * 
 * @author HUNG
 *
 */
public class MethodInvocationNode extends DispatchNode {

	private VariableBaseNode dispatcher;
	private FunctionInvocationNode method;
	
	/*
	Represents a dispaching expression 

	e.g. foo()->bar(),
	 $myClass->foo()->bar(),
	 A::$a->foo()
	*/ 
	public MethodInvocationNode(MethodInvocation methodInvocation) {
		super(methodInvocation);
		dispatcher = VariableBaseNode.createInstance(methodInvocation.getDispatcher());
		method = new FunctionInvocationNode(methodInvocation.getMethod());
	}
	
	@Override
	public DataNode execute(Env env) {
		// Get the object
		if (!(dispatcher instanceof VariableNode)) {
			MyLogger.log(MyLevel.TODO, "In MethodInvocationNode.java: VariableBase Expression unimplemented for the MethodInvocation.");
			return new SymbolicNode(this);
		}		
		String variableName = ((VariableNode) dispatcher).getResolvedVariableNameOrNull(env);
		PhpVariable phpVariable = env.getVariableFromFunctionScope(variableName);
		if (phpVariable == null || !(phpVariable.getDataNode() instanceof ObjectNode)) {
			return new SymbolicNode(this);
		}		
		ObjectNode objectNode = (ObjectNode) phpVariable.getDataNode();
		
		// Execute the function
		return method.execute(env, objectNode);
	}

	@Override
	public PhpVariable createVariablePossiblyWithNull(Env env) {
		MyLogger.log(MyLevel.TODO, "In MethodInvocationNode.java: Don't know how to create a variable from a method invocation.");
		return null;
	}

}

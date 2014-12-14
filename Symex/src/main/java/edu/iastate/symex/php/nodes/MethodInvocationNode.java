package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.MethodInvocation;

import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.ObjectNode;

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
		DataNode dataNode = dispatcher.execute(env);
		if (dataNode instanceof ObjectNode) {
			ObjectNode object = (ObjectNode) dataNode;
			return method.execute(env, object);
		}
		else
			return DataNodeFactory.createSymbolicNode(this);
	}

	@Override
	public PhpVariable createVariablePossiblyWithNull(Env env) {
		MyLogger.log(MyLevel.TODO, "In MethodInvocationNode.java: Don't know how to create a variable from a method invocation.");
		return null;
	}

}

package php.nodes;

import logging.MyLevel;
import logging.MyLogger;

import org.eclipse.php.internal.core.ast.nodes.MethodInvocation;

import php.ElementManager;
import php.elements.PhpVariable;

import datamodel.nodes.DataNode;
import datamodel.nodes.ObjectNode;
import datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class MethodInvocationNode extends DispatchNode {

	private VariableBaseNode variableBaseNode;
	private FunctionInvocationNode functionInvocationNode;
	
	/*
	Represents a dispaching expression 

	e.g. foo()->bar(),
	 $myClass->foo()->bar(),
	 A::$a->foo()
	*/ 
	public MethodInvocationNode(MethodInvocation methodInvocation) {
		super(methodInvocation);
		variableBaseNode = VariableBaseNode.createInstance(methodInvocation.getDispatcher());
		functionInvocationNode = new FunctionInvocationNode(methodInvocation.getMethod());
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		// Get the object
		if (!(variableBaseNode instanceof VariableNode)) {
			MyLogger.log(MyLevel.TODO, "In MethodInvocationNode.java: VariableBase Expression unimplemented for the MethodInvocation.");
			return new SymbolicNode(this);
		}		
		String variableName = ((VariableNode) variableBaseNode).resolveVariableName(elementManager);
		PhpVariable phpVariable = elementManager.getVariableFromFunctionScope(variableName);
		if (phpVariable == null || !(phpVariable.getDataNode() instanceof ObjectNode)) {
			return new SymbolicNode(this);
		}		
		ObjectNode objectNode = (ObjectNode) phpVariable.getDataNode();
		
		// Execute the function
		return functionInvocationNode.execute(elementManager, objectNode);
	}

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.VariableBaseNode#createVariablePossiblyWithNull(servergraph.ElementManager)
	 */
	@Override
	public PhpVariable createVariablePossiblyWithNull(ElementManager elementManager) {
		MyLogger.log(MyLevel.TODO, "In MethodInvocationNode.java: Don't know how to create a variable from a method invocation.");
		return null;
	}

}

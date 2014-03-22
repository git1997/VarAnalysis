package php.nodes;

import logging.MyLevel;
import logging.MyLogger;

import org.eclipse.php.internal.core.ast.nodes.FieldAccess;

import php.ElementManager;
import php.elements.PhpVariable;

import datamodel.nodes.DataNode;
import datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class FieldAccessNode extends DispatchNode {
	
	//private VariableBaseNode variableBaseNode;
	//private VariableNode fieldNode;
	
	/*
	Represents a field access 

	e.g. $a->$b
	*/
	public FieldAccessNode(FieldAccess fieldAccess) {
		super(fieldAccess);
		//variableBaseNode = VariableBaseNode.createInstance(fieldAccess.getDispatcher());
		//fieldNode = new VariableNode(fieldAccess.getField());
	}

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.VariableBaseNode#createVariablePossiblyWithNull(servergraph.ElementManager)
	 */
	@Override
	public PhpVariable createVariablePossiblyWithNull(ElementManager elementManager) {
		MyLogger.log(MyLevel.TODO, "In FieldAccessNode.java: Don't know how to create a variable from a fieldAccess.");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		MyLogger.log(MyLevel.TODO, "In FieldAccessNode.java: FieldAccess unimplemented.");
		return new SymbolicNode(this);
	}

}

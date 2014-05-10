package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.FieldAccess;

import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;

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

	@Override
	public PhpVariable createVariablePossiblyWithNull(Env env) {
		MyLogger.log(MyLevel.TODO, "In FieldAccessNode.java: Don't know how to create a variable from a fieldAccess.");
		return null;
	}

	@Override
	public DataNode execute(Env env) {
		MyLogger.log(MyLevel.TODO, "In FieldAccessNode.java: FieldAccess unimplemented.");
		return DataNodeFactory.createSymbolicNode(this);
	}

}

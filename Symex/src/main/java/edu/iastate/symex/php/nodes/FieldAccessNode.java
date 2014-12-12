package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.FieldAccess;

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
public class FieldAccessNode extends DispatchNode {
	
	private VariableBaseNode dispatcher;
	private VariableNode field;
	
	/*
	Represents a field access 

	e.g. $a->$b
	*/
	public FieldAccessNode(FieldAccess fieldAccess) {
		super(fieldAccess);
		dispatcher = VariableBaseNode.createInstance(fieldAccess.getDispatcher());
		field = new VariableNode(fieldAccess.getField());
	}

	@Override
	public PhpVariable createVariablePossiblyWithNull(Env env) {
		MyLogger.log(MyLevel.TODO, "In FieldAccessNode.java: Don't know how to create a variable from a fieldAccess.");
		return null;
	}

	@Override
	public DataNode execute(Env env) {
		DataNode dataNode = dispatcher.execute(env);
		if (dataNode instanceof ObjectNode) {
			ObjectNode object = (ObjectNode) dataNode;
			String fieldName = field.execute(env).getExactStringValueOrNull();
			if (fieldName != null)
				return object.getFieldValue(fieldName);
			else
				return DataNodeFactory.createSymbolicNode(this);
		}
		else {
			MyLogger.log(MyLevel.TODO, "In FieldAccessNode.java: FieldAccess unimplemented for non-object values.");
			return DataNodeFactory.createSymbolicNode(this);
		}
	}

}

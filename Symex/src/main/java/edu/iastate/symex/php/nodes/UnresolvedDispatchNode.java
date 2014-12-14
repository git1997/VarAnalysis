package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Dispatch;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class UnresolvedDispatchNode extends DispatchNode {

	/**
	 * Constructor
	 */
	public UnresolvedDispatchNode(Dispatch dispatch) {
		super(dispatch);
	}

	@Override
	public DataNode execute(Env env) {
		return DataNodeFactory.createSymbolicNode(this);
	}
	
	@Override
	public PhpVariable createVariablePossiblyWithNull(Env env) {
		MyLogger.log(MyLevel.TODO, "In UnresolvedDispatchNode.java: Don't know how to create a variable from a UnresolvedDispatchNode.");
		return null;
	}

}

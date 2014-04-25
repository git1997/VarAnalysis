package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Dispatch;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.php.elements.PhpVariable;

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
		return new SymbolicNode(this);
	}
	
	@Override
	public PhpVariable createVariablePossiblyWithNull(Env env) {
		return null;
	}

}

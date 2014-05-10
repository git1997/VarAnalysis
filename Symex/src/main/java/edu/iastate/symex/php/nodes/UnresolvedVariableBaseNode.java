package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.VariableBase;


import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;

/**
 * 
 * @author HUNG
 *
 */
public class UnresolvedVariableBaseNode extends VariableBaseNode {

	/**
	 * Constructor
	 */
	public UnresolvedVariableBaseNode(VariableBase variableBase) {
		super(variableBase);
	}

	@Override
	public DataNode execute(Env env) {
		return DataNodeFactory.createSymbolicNode(this);
	}
	
	@Override
	public PhpVariable createVariablePossiblyWithNull(Env env) {
		return null;
	}

}

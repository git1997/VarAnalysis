package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.VariableBase;


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
		MyLogger.log(MyLevel.TODO, "In UnresolvedVariableBaseNode.java: Don't know how to create a variable from a UnresolvedVariableBaseNode " + this.getSourceCode());
		return null;
	}

}

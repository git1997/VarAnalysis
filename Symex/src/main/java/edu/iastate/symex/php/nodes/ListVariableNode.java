package edu.iastate.symex.php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.ListVariable;
import org.eclipse.php.internal.core.ast.nodes.VariableBase;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpListVariable;
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
public class ListVariableNode extends VariableBaseNode {
	
	private ArrayList<VariableBaseNode> variables = new ArrayList<VariableBaseNode>();
	
	/*
	Represents a list expression. The list contains variables and/or other lists.
	
	e.g.
	 list($a,$b) = array (1,2),
	 list($a, list($b, $c))
	*/
	public ListVariableNode(ListVariable listVariable) {
		super(listVariable);
		for (VariableBase variable : listVariable.variables())
			variables.add(VariableBaseNode.createInstance(variable));
	}
	
	@Override
	public DataNode execute(Env env) {
		MyLogger.log(MyLevel.USER_EXCEPTION, "In ListVariableNode.java: ListVariable should not get evaluated " + this.getSourceCode());
		return DataNodeFactory.createSymbolicNode(this);
	}

	@Override
	public PhpVariable createVariablePossiblyWithNull(Env env) {
		ArrayList<PhpVariable> phpVariables = new ArrayList<PhpVariable>();
		for (VariableBaseNode variable : variables)
			phpVariables.add(variable.createVariablePossiblyWithNull(env));
		return new PhpListVariable(phpVariables);
	}
	
}
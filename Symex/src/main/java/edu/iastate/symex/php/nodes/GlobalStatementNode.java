package edu.iastate.symex.php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.GlobalStatement;
import org.eclipse.php.internal.core.ast.nodes.Variable;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;

/**
 * 
 * @author HUNG
 *
 */
public class GlobalStatementNode extends StatementNode {

	private ArrayList<VariableNode> variables = new ArrayList<VariableNode>();
	
	/*
	Represents a global statement 

	e.g. global $a
	 global $a, $b
	 global ${foo()->bar()},
	 global $$a 
	*/
	public GlobalStatementNode(GlobalStatement globalStatement) {
		super(globalStatement);
		for (Variable variable : globalStatement.variables()) {
			variables.add(new VariableNode(variable));
		}
	}
	
	@Override
	public DataNode execute_(Env env) {
		for (VariableNode variable : variables) {
			String variableName = variable.getResolvedVariableNameOrNull(env);
			if (variableName != null)
				env.setGlobalVariable(variableName);
		}
		return SpecialNode.ControlNode.OK;
	}

}

package php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.GlobalStatement;
import org.eclipse.php.internal.core.ast.nodes.Variable;

import php.ElementManager;

import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class GlobalStatementNode extends StatementNode {

	private ArrayList<VariableNode> variableNodes = new ArrayList<VariableNode>();
	
	/*
	Represents a global statement 

	e.g. global $a
	 global $a, $b
	 global ${foo()->bar()},
	 global $$a 
	*/
	public GlobalStatementNode(GlobalStatement globalStatement) {
		for (Variable variable : globalStatement.variables()) {
			VariableNode variableNode = new VariableNode(variable);
			variableNodes.add(variableNode);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		for (VariableNode variableNode : variableNodes) {
			elementManager.addGlobalVariable(variableNode);
		}
		return null;
	}

}

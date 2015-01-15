package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Statement;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;

/**
 * 
 * @author HUNG
 *
 */
public class UnresolvedStatementNode extends StatementNode {
	
	/**
	 * Constructor
	 */
	public UnresolvedStatementNode(Statement statement) {
		super(statement);
	}
	
	@Override
	public DataNode execute_(Env env) {
		return SpecialNode.ControlNode.OK;
	}

}

package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Statement;

import php.ElementManager;

import datamodel.nodes.DataNode;

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
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		return null;
	}

}

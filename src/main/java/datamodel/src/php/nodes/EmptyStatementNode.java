package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.EmptyStatement;

import php.ElementManager;


import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class EmptyStatementNode extends StatementNode {

	/*
	This class represents an empty statement. 

	e.g. ;
	 while(true); - the while statement contains empty statement
	*/
	public EmptyStatementNode(EmptyStatement emptyStatement) {
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

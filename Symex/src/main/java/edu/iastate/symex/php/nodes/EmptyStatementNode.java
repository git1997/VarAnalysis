package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.EmptyStatement;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;

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
		super(emptyStatement);
	}

	@Override
	public DataNode execute_(Env env) {
		return SpecialNode.ControlNode.OK;
	}

}

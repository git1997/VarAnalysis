package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ContinueStatement;

import edu.iastate.symex.util.ASTHelper;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;

/**
 * 
 * @author HUNG
 *
 */
public class ContinueStatementNode extends StatementNode {

	/*
	Represent a continue statement
	
	e.g.
	 continue;
	 continue $a;
	*/
	public ContinueStatementNode(ContinueStatement continueStatement) {
		super(continueStatement);
		if (continueStatement.getExpression() != null) {
			MyLogger.log(MyLevel.TODO, "In ContinueStatementNode.java: ContinueStatement not fully implemented (" + ASTHelper.inst.getSourceCodeOfPhpASTNode(continueStatement) + ")");
		}
	}
	
	@Override
	public DataNode execute_(Env env) {
		return SpecialNode.ControlNode.CONTINUE;
	}

}
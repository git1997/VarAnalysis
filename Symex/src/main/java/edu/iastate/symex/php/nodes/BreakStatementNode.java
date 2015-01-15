package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.BreakStatement;

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
public class BreakStatementNode extends StatementNode {

	/*
	Represent a break statement 

	e.g. break;
	 break $a;
	*/
	public BreakStatementNode(BreakStatement breakStatement) {
		super(breakStatement);
		if (breakStatement.getExpression() != null) {
			MyLogger.log(MyLevel.TODO, "In BreakStatementNode.java: BreakStatement not fully implemented (" + ASTHelper.inst.getSourceCodeOfPhpASTNode(breakStatement) + ")");
		}
	}
	
	@Override
	public DataNode execute_(Env env) {
		return SpecialNode.ControlNode.BREAK;
	}

}
package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.BreakStatement;

import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.TraceTable;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;

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
			MyLogger.log(MyLevel.TODO, "In BreakStatementNode.java: BreakStatement not fully implemented (" + TraceTable.getSourceCodeOfPhpASTNode(breakStatement) + ")");
		}
	}
	
	@Override
	public DataNode execute(Env env) {
		return DataNodeFactory.BREAK;
	}

}
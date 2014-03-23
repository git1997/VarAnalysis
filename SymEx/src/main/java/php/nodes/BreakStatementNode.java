package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.BreakStatement;

import php.ElementManager;
import php.TraceTable;
import util.logging.MyLevel;
import util.logging.MyLogger;
import datamodel.nodes.DataNode;

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
		if (breakStatement.getExpression() != null) {
			MyLogger.log(MyLevel.TODO, "In BreakStatementNode.java: BreakStatement not fully implemented (" + TraceTable.getSourceCodeOfPhpASTNode(breakStatement) + ")");
		}
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
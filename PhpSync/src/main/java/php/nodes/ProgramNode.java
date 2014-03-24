package php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.php.internal.core.ast.nodes.Statement;

import php.ElementManager;


import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class ProgramNode extends PhpNode {
	
	private ArrayList<StatementNode> statementNodes = new ArrayList<StatementNode>();

	/*
	The AST root node for PHP program (meaning a PHP file). The program holds array of statements such as Class, Function and evaluation statement. The program also holds the PHP file comments.
	*/
	public ProgramNode(Program program) {
		for (Statement statement : program.statements()) {
			StatementNode statementNode = StatementNode.createInstance(statement);
			this.statementNodes.add(statementNode);
			// [Optional]:
			if (statement.getType() == Statement.RETURN_STATEMENT || statement.getType() == Statement.BREAK_STATEMENT)
				break;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		// Execute function/class declarations first
		for (StatementNode statementNode : statementNodes) {
			if (statementNode instanceof FunctionDeclarationNode || statementNode instanceof ClassDeclarationNode)
				statementNode.execute(elementManager);
		}		
		// Then, execute the regular statements
		for (StatementNode statementNode : statementNodes) {
			if ( !(statementNode instanceof FunctionDeclarationNode) && !(statementNode instanceof ClassDeclarationNode) )
				statementNode.execute(elementManager);
		}
		return null;
	}
	
}

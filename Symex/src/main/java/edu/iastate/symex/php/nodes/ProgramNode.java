package edu.iastate.symex.php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.php.internal.core.ast.nodes.Statement;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class ProgramNode extends PhpNode {
	
	private ArrayList<StatementNode> statements = new ArrayList<StatementNode>();

	/*
	The AST root node for PHP program (meaning a PHP file). The program holds array of statements such as Class, Function and evaluation statement. The program also holds the PHP file comments.
	*/
	public ProgramNode(Program program) {
		super(program);
		for (Statement statement : program.statements()) {
			statements.add(StatementNode.createInstance(statement));
		}
	}
	
	/**
	 * Executes a PHP program. See edu.iastate.symex.php.nodes.StatementNode.execute(Env)
	 * @param env
	 */
	public DataNode execute(Env env) {
		return BlockNode.executeStatements(statements, env);
	}
	
}
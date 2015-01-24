package edu.iastate.symex.php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Block;
import org.eclipse.php.internal.core.ast.nodes.Statement;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode.ControlNode;

/**
 * 
 * @author HUNG
 *
 */
public class BlockNode extends StatementNode {

	private ArrayList<StatementNode> statements = new ArrayList<StatementNode>();
	
	/*
	Represents a block of statements 

	e.g. {
	   statement1;
	   statement2;
	 },
	 :
	   statement1;
	   statement2;
	 ,
	 */
	public BlockNode(Block block) {
		super(block);
		for (Statement statement : block.statements()) {
			statements.add(StatementNode.createInstance(statement));
		}
	}
	
	@Override
	public DataNode execute_(Env env) {
		return executeStatements(statements, env);
	}
	
	public static DataNode executeStatements(ArrayList<StatementNode> statements, Env env) {
		// Execute function/class declarations first
		for (StatementNode statementNode : statements) {
			if (statementNode instanceof FunctionDeclarationNode || statementNode instanceof ClassDeclarationNode)
				statementNode.execute(env); // Always return OK
		}
			
		// Then, execute the regular statements
		for (StatementNode statementNode : statements) {
			if (statementNode instanceof FunctionDeclarationNode || statementNode instanceof ClassDeclarationNode)
				continue;
			
			DataNode control = statementNode.execute(env);

			if (control == ControlNode.OK) // OK
				continue;
			else if (control instanceof ControlNode) // EXIT, RETURN, BREAK, CONTINUE
				return control;
			else {
				// TODO Handle multiple returned CONTROL values here
				continue;
			}
		}
		
		return ControlNode.OK;
	}
	
}

package edu.iastate.symex.php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Block;
import org.eclipse.php.internal.core.ast.nodes.Statement;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;

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
	public DataNode execute(Env env) {
		return executeStatements(statements, env);
	}
	
	public static DataNode executeStatements(ArrayList<StatementNode> statements, Env env) {
		DataNode retValue = null;
		
		// Execute function/class declarations first
		for (StatementNode statementNode : statements) {
			if (statementNode instanceof FunctionDeclarationNode || statementNode instanceof ClassDeclarationNode)
				retValue = statementNode.execute(env);
		}
		
		// Then, execute the regular statements
		for (StatementNode statementNode : statements) {
			if ( !(statementNode instanceof FunctionDeclarationNode) && !(statementNode instanceof ClassDeclarationNode) )
				retValue = statementNode.execute(env);
			
			if (retValue == SpecialNode.ControlNode.RETURN || retValue == SpecialNode.ControlNode.BREAK)
				break;
		}
		
		return retValue;
	}
	
}

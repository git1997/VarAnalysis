package php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Block;
import org.eclipse.php.internal.core.ast.nodes.Statement;

import php.ElementManager;


import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class BlockNode extends StatementNode {

	private ArrayList<StatementNode> statementNodes = new ArrayList<StatementNode>();
	
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
		for (Statement statement : block.statements()) {
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
		for (StatementNode statementNode : statementNodes) {
			statementNode.execute(elementManager);
		}
		return null;
	}
	
}

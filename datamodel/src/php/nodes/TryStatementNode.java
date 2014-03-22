package php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.CatchClause;
import org.eclipse.php.internal.core.ast.nodes.TryStatement;

import php.ElementManager;


import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class TryStatementNode extends StatementNode {

	private BlockNode bodyBlockNode;
	private ArrayList<BlockNode> catchBlockNodes;
	
	/*
	Represents the try statement 

	e.g. 
	 try { 
	   statements...
	 } catch (Exception $e) { 
	   statements...
	 } catch (AnotherException $ae) { 
	   statements...
	 }
	*/
	public TryStatementNode(TryStatement tryStatement) {
		bodyBlockNode = new BlockNode(tryStatement.getBody());
		catchBlockNodes = new ArrayList<BlockNode>();
		for (CatchClause catchClause : tryStatement.catchClauses()) {
			catchBlockNodes.add(new BlockNode(catchClause.getBody()));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		bodyBlockNode.execute(elementManager);
		//for (BlockNode catchBlockNode : catchBlockNodes) {
		//	catchBlockNode.execute(elementManager);
		//}
		return null;
	}

}

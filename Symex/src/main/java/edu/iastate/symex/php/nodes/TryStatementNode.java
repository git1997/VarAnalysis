package edu.iastate.symex.php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.CatchClause;
import org.eclipse.php.internal.core.ast.nodes.TryStatement;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;

/**
 * 
 * @author HUNG
 *
 */
public class TryStatementNode extends StatementNode {

	private BlockNode body;
	private ArrayList<BlockNode> catchClauses;
	
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
		super(tryStatement);
		body = new BlockNode(tryStatement.getBody());
		catchClauses = new ArrayList<BlockNode>();
		for (CatchClause catchClause : tryStatement.catchClauses()) {
			catchClauses.add(new BlockNode(catchClause.getBody()));
		}
	}
	
	@Override
	public DataNode execute_(Env env) {
		body.execute(env); // TODO Consider the returned CONTROL value?
		//for (BlockNode catchBlockNode : catchBlockNodes) {
		//	catchBlockNode.execute(env);
		//}
		return SpecialNode.ControlNode.OK;
	}

}

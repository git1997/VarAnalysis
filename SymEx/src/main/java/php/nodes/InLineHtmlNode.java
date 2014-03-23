package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.InLineHtml;

import php.ElementManager;

import datamodel.nodes.DataNode;
import datamodel.nodes.LiteralNode;

/**
 * 
 * @author HUNG
 *
 */
public class InLineHtmlNode extends StatementNode {
	
	/*
	Represents an HTML blocks in the resource 
	*/
	public InLineHtmlNode(InLineHtml inlineHtml) {
		super(inlineHtml); // @see servergraph.nodes.PhpNode.PhpNode(ASTNode)
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		elementManager.appendOutput(new LiteralNode(this));
		return null;
	}

}

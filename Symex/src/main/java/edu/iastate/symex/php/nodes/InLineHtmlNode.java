package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.InLineHtml;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;

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
		super(inlineHtml);
	}
	
	@Override
	public DataNode execute(Env env) {
		env.appendOutput(DataNodeFactory.createLiteralNode(this));
		return null;
	}
	
}

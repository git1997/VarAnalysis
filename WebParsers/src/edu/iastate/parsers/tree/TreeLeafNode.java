package edu.iastate.parsers.tree;

import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;

/**
 * 
 * @author HUNG
 *
 */
public class TreeLeafNode<T> extends TreeNode<T> {
	
	private T node;
	
	public TreeLeafNode(T node) {
		this.node = node;
	}
	
	public T getNode() {
		return node;
	}

	@Override
	public String toDebugString() {
		if (node instanceof HtmlToken) {
			return ((HtmlToken) node).toDebugString();
		}
		else if (node instanceof HtmlSaxNode) {
			return ((HtmlSaxNode) node).toDebugString();
		}
		else
			return "?";
	}

}

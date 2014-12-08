package edu.iastate.parsers.conditional;

import java.util.ArrayList;

import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;

/**
 * 
 * @author HUNG
 *
 */
public class CondListItem<T> extends CondList<T> {
	
	private T node;
	
	public CondListItem(T node) {
		this.node = node;
	}
	
	public T getNode() {
		return node;
	}

	@Override
	public ArrayList<T> getLeftMostItems() {
		ArrayList<T> list = new ArrayList<T>(1);
		list.add(node);
		return list;
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

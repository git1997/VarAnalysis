package edu.iastate.parsers.conditional;

import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;

/**
 * 
 * @author HUNG
 *
 */
public class CondListItem<T> extends CondList<T> {
	
	private T item;
	
	/**
	 * Protected constructor, called from CondListFactory only.
	 */
	protected CondListItem(T item) {
		this.item = item;
	}
	
	public T getItem() {
		return item;
	}

	@Override
	public String toDebugString() {
		if (item instanceof HtmlToken) {
			return ((HtmlToken) item).toDebugString();
		}
		else if (item instanceof HtmlSaxNode) {
			return ((HtmlSaxNode) item).toDebugString();
		}
		else
			return "?";
	}

}

package edu.iastate.parsers.html.dom.nodes;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlEmpty extends HtmlNode {
	
	public static HtmlEmpty EMPTY = new HtmlEmpty();
	
	/**
	 * Private constructor
	 */
	private HtmlEmpty() {
	}

	@Override
	public String toDebugString() {
		return "";
	}

}

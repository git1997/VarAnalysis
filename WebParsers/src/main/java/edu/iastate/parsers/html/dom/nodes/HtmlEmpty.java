package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.symex.position.PositionRange;

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
		super(PositionRange.UNDEFINED);
	}

	@Override
	public String toDebugString() {
		return "";
	}

}

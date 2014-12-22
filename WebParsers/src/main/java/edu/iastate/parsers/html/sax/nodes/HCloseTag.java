package edu.iastate.parsers.html.sax.nodes;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HCloseTag extends HtmlSaxNode {

	private String type;
	
	/**
	 * Constructor
	 */
	public HCloseTag(String type, PositionRange location) {
		super(location);
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	@Override
	public String toDebugString() {
		return "CloseTag: " + getType();
	}

}

package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public abstract class HtmlNode {
	
	protected PositionRange location;
	
	public HtmlNode(PositionRange location) {
		this.location = location;
	}

	public PositionRange getLocation() {
		return location;
	}

	/**
	 * Returns a string describing this object for debugging purposes.
	 */
	public abstract String toDebugString();
	
}

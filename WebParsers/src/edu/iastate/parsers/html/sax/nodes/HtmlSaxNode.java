package edu.iastate.parsers.html.sax.nodes;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public abstract class HtmlSaxNode {
	
	protected PositionRange location;
	
	public HtmlSaxNode(PositionRange location) {
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

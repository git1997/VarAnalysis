package edu.iastate.parsers.html.sax.nodes;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public abstract class HtmlSaxNode {
	
	protected PositionRange location;
	
	/**
	 * Constructor
	 * @param location
	 */
	public HtmlSaxNode(PositionRange location) {
		this.location = location;
	}

	public PositionRange getLocation() {
		return location;
	}
	
	/**
	 * Used for debugging
	 */
	public abstract String toDebugString();
	
}

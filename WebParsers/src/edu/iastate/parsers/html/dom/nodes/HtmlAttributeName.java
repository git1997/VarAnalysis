package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlAttributeName extends HtmlNode {

	private String name;
	
	/**
	 * Constructor
	 * @param name
	 * @param location
	 */
	public HtmlAttributeName(String name, PositionRange location) {
		super(location); 
		this.name = name.toLowerCase();
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toDebugString() {
		return name;
	}
	
}

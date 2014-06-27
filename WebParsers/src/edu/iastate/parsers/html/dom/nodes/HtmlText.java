package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlText extends HtmlNode {

	public HtmlText(PositionRange location) {
		super(location);
	}

	@Override
	public String toDebugString() {
		return "HtmlText";
	}

}

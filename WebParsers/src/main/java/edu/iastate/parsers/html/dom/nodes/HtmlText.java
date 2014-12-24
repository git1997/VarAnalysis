package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.parsers.html.sax.nodes.HText;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlText extends HtmlNode {
	
	private HText hText;

	public HtmlText(HText hText) {
		this.hText = hText;
	}
	
	public String getStringValue() {
		return hText.getStringValue();
	}
	
	public PositionRange getLocation() {
		return hText.getLocation();
	}

	@Override
	public String toDebugString() {
		return hText.toDebugString();
	}

}

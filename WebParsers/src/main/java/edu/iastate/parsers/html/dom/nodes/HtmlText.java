package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.parsers.html.sax.nodes.HText;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlText extends HtmlNode {
	
	private HText hText;

	public HtmlText(HText hText) {
		super(hText.getLocation());
		this.hText = hText;
	}
	
	public String getStringValue() {
		return hText.getStringValue();
	}

	@Override
	public String toDebugString() {
		return hText.toDebugString();
	}

}

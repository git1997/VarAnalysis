package edu.iastate.parsers.html.sax.nodes;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HText extends HtmlSaxNode {
	
	private String stringValue;
	
	/**
	 * Constructor
	 */
	public HText(String stringValue, PositionRange location) {
		super(location);
		this.stringValue = stringValue;		
	}
	
	public void addText(HText text) {
		// FIXME this.location = new RangeList_deprecated(this.location, text.location, this.stringValue.length());
		this.stringValue = this.stringValue + text.stringValue;		
	}
	
	public String getStringValue() {
		return stringValue;
	}

	@Override
	public String toDebugString() {
		return "Text: " + stringValue;
	}

}

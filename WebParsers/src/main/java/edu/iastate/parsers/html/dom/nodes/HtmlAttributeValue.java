package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.symex.position.CompositeRange;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.util.StringUtils;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlAttributeValue extends HtmlNode {

	private String stringValue = "";
	
	/**
	 * Protected constructor. Called from HtmlAttribute only.
	 */
	protected HtmlAttributeValue() {
		super(PositionRange.UNDEFINED);
	}
	
	public void addValueFragment(String valueFragment, PositionRange valueFragmentlocation) {
		if (stringValue.isEmpty()) {
			this.location = valueFragmentlocation;
			this.stringValue = valueFragment;
		}
		else {
			this.location = new CompositeRange(this.location, valueFragmentlocation);
			this.stringValue = this.stringValue + valueFragment;
		}
	}
	
	public String getStringValue() {
		return stringValue;
	}
	
	/*
	 * Other methods
	 */
	
	@Override
	public HtmlAttributeValue clone() {
		HtmlAttributeValue clone = new HtmlAttributeValue();
		clone.stringValue = stringValue;
		clone.location = location;
		return clone;
	}
	
	/**
	 * Unescapes the string value of the attribute value, preserving its length
	 */
	public void unescapePreservingLength(char stringType) {
		stringValue = StringUtils.getUnescapedStringValuePreservingLength(stringValue, stringType);
	}
	
	@Override
	public String toDebugString() {
		return stringValue;
	}
	
}

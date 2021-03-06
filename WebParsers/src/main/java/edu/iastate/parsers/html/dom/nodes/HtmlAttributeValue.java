package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.symex.position.CompositeRange;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;
import edu.iastate.symex.util.StringUtils;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlAttributeValue {

	private String stringValue;
	private PositionRange location;
	
	/**
	 * Protected constructor. Called from HtmlAttribute only.
	 */
	protected HtmlAttributeValue() {
		this.stringValue = "";
		this.location = new Range(0);
	}
	
	/*
	 * Set properties
	 */
	
	/**
	 * Protected method. Called from HtmlAttribute only.
	 */
	protected void addValueFragment(String valueFragment, PositionRange valueFragmentlocation) {
		if (stringValue.isEmpty()) {
			this.stringValue = valueFragment;
			this.location = valueFragmentlocation;
		}
		else {
			this.stringValue = this.stringValue + valueFragment;
			this.location = new CompositeRange(this.location, valueFragmentlocation);
		}
	}
	
	/**
	 * Protected method. Called from HtmlAttribute only.
	 * Unescapes the string value of the attribute value, preserving its length.
	 */
	protected void unescapePreservingLength(char stringType) {
		stringValue = StringUtils.getUnescapedStringValuePreservingLength(stringValue, stringType);
	}
	
	/*
	 * Get properties
	 */
	
	public String getStringValue() {
		return stringValue;
	}
	
	public PositionRange getLocation() {
		return location;
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
	
	public String toDebugString() {
		if (stringValue.matches("11+"))
			return "SYM";
		else
			return stringValue;
	}
	
}

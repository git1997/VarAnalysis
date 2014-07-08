package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlAttributeValue extends HtmlNode {

	private String stringValue;
	
	/**
	 * Constructor
	 * @param valueFragment
	 * @param location
	 */
	public HtmlAttributeValue(String valueFragment, PositionRange location) {
		super(location);
		this.stringValue = valueFragment;
	}
	
	public void addValueFragment(String valueFragment, PositionRange valueFragmentlocation) {
		if (stringValue.isEmpty()) {
			this.location = valueFragmentlocation;
			this.stringValue = valueFragment;
			
			// TODO: [AdhocCode] Adjust the string value of attribute,
			// e.g. <div id=\'email\'> might have been converted to <div id=' email'> when D-model was built
			if (stringValue.startsWith(" ")) {
				//FIXME location = location.getPositionRangeAtRelativeOffset(1, 0);
				stringValue = stringValue.substring(1);
			}
		}
		else {
			//FIXME this.location = new RangeList(this.location, valueFragmentlocation, this.stringValue.length());
			this.stringValue = this.stringValue + valueFragment;
		}
	}
	
	public String getStringValue() {
		return stringValue;
	}
	
	public HtmlAttributeValue clone() {
		HtmlAttributeValue clonedAttributeValue = new HtmlAttributeValue(stringValue, location);
		return clonedAttributeValue;
	}
	
	/**
	 * Unescapes the string value of the attribute preserving length
	 */
	public void unescapePreservingLength(char stringType) {
//		stringValue = StringUtils.getUnescapedStringValuePreservingLength(stringValue, stringType);
	}
	
	@Override
	public String toDebugString() {
		return stringValue;
	}
	
}

package deprecated.html.elements;

import sourcetracing.Location;
import sourcetracing.ScatteredLocation;
import sourcetracing.SingleLocation;
import util.StringUtils;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlAttributeValue extends HtmlElement {

	private String stringValue;
	
	/**
	 * Constructor
	 * @param valueFragment
	 * @param location
	 */
	public HtmlAttributeValue(String valueFragment, Location location) {
		super(location);
		this.stringValue = valueFragment;
	}
	
	public void addValueFragment(String valueFragment, Location valueFragmentlocation) {
		if (stringValue.isEmpty()) {
			this.location = valueFragmentlocation;
			this.stringValue = valueFragment;
			
			// TODO: [AdhocCode] Adjust the string value of attribute,
			// e.g. <div id=\'email\'> might have been converted to <div id=' email'> when D-model was built
			if (stringValue.startsWith(" ")) {
				location = new SingleLocation(location, 1);
				stringValue = stringValue.substring(1);
			}
		}
		else {
			this.location = new ScatteredLocation(this.location, valueFragmentlocation, this.stringValue.length());
			this.stringValue = this.stringValue + valueFragment;
		}
	}
	
	public String getStringValue() {
		return stringValue;
	}
	
	/**
	 * Unescapes the string value of the attribute preserving length
	 */
	public void unescapePreservingLength(char stringType) {
		stringValue = StringUtils.getUnescapedStringValuePreservingLength(stringValue, stringType);
	}
	
	/*
	 * Used for debugging
	 */
	@Override
	public String print(int depth) {
		return "";
	}

}

package deprecated.html.elements;

import sourcetracing.Location;
import sourcetracing.ScatteredLocation;
import util.StringUtils;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlText extends HtmlElement {
	
	private String stringValue;
	
	/**
	 * Constructor
	 */
	public HtmlText(String stringValue, Location location) {
		super(location);
		this.stringValue = stringValue;		
	}
	
	public void addText(HtmlText text) {
		this.location = new ScatteredLocation(this.location, text.location, this.stringValue.length());
		this.stringValue = this.stringValue + text.stringValue;		
	}
	
	public String getStringValue() {
		return stringValue;
	}

	/*
	 * Used for debugging
	 */
	@Override
	public String print(int depth) {
		return StringUtils.getIndentedTabs(depth) + "Text: " + getStringValue() + "\n";
	}

}

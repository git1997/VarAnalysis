package deprecated.html.elements;

import sourcetracing.Location;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlFormTag extends HtmlTag {

	public HtmlFormTag(String type, Location location) {
		super(type, location);
	}
	
	public String getFormName() {
		HtmlAttributeValue formName = getAttribute("name");
		return (formName != null ? formName.getStringValue() : null);
	}
	
	public String getFormSubmitToPage() {
		HtmlAttributeValue submitToPage = getAttribute("action");
		return (submitToPage != null ? submitToPage.getStringValue() : null);
	}

}

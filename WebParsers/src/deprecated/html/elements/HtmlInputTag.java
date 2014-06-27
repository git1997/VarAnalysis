package deprecated.html.elements;

import sourcetracing.Location;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlInputTag extends HtmlTag {

	public HtmlInputTag(String type, Location location) {
		super(type, location);
	}
	
	public String getInputName() {
		HtmlAttributeValue inputName = getAttribute("name");
		return (inputName != null ? inputName.getStringValue() : null);
	}
	
	public HtmlFormTag getParentFormTag() {
		HtmlElement parent = this.getParentElement();
		while (parent != null) {
			if (parent instanceof HtmlFormTag)
			 	return (HtmlFormTag) parent;
			parent = parent.getParentElement();
		}
		return null;
	}

}

package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.parsers.html.sax.nodes.HOpenTag;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlInput extends HtmlElement {

	public HtmlInput(HOpenTag htmlOpenTag) {
		super(htmlOpenTag);
	}
	
	public String getInputName() {
		HtmlAttributeValue inputName = getAttributeValue("name");
		return (inputName != null ? inputName.getStringValue() : null);
	}
	
	public HtmlForm getParentForm() {
		HtmlElement parent = this.getParentElement();
		while (parent != null) {
			if (parent instanceof HtmlForm)
			 	return (HtmlForm) parent;
			parent = parent.getParentElement();
		}
		return null;
	}

}

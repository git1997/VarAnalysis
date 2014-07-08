package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.parsers.html.sax.nodes.HOpenTag;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlForm extends HtmlElement {

	public HtmlForm(HOpenTag htmlOpenTag) {
		super(htmlOpenTag);
		// TODO Auto-generated constructor stub
	}
	
	public String getFormName() {
		HtmlAttributeValue formName = getAttributeValue("name");
		return (formName != null ? formName.getStringValue() : null);
	}
	
	public String getFormSubmitToPage() {
		HtmlAttributeValue submitToPage = getAttributeValue("action");
		return (submitToPage != null ? submitToPage.getStringValue() : null);
	}

}

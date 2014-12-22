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
	}
	
	public String getFormName() {
		return getAttributeStringValue("name");
	}
	
	public String getFormSubmitToPage() {
		return getAttributeStringValue("action");
	}

}

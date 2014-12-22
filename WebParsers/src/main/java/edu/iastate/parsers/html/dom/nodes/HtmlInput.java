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
		return getAttributeStringValue("name");
	}
	
	public HtmlForm getParentFormOrNull() {
		HtmlElement parent = this.getParentElement();
		while (parent != null) {
			if (parent instanceof HtmlForm)
			 	return (HtmlForm) parent;
			parent = parent.getParentElement();
		}
		return null;
	}

}

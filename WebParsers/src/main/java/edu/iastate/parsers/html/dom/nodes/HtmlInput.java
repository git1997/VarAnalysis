package edu.iastate.parsers.html.dom.nodes;

import java.util.HashSet;

import edu.iastate.parsers.html.sax.nodes.HOpenTag;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlInput extends HtmlElement {
	
	private HashSet<HtmlForm> parentForms = null; // Use this to cache results when searching for parent forms

	public HtmlInput(HOpenTag htmlOpenTag) {
		super(htmlOpenTag);
	}
	
	public String getInputName() {
		return getAttributeStringValue("name");
	}
	
	public HashSet<HtmlForm> getParentForms() {
		if (parentForms == null) {
			parentForms = new HashSet<HtmlForm>();
			for (HtmlNode ancestorNode : this.getAncestorNodes()) {
				if (ancestorNode instanceof HtmlForm)
					parentForms.add((HtmlForm) ancestorNode);
			}
		}
		return parentForms;
	}
	
	public HtmlForm getParentFormOrNull() {
		HashSet<HtmlForm> parentForms = getParentForms();
		if (parentForms.isEmpty())
			return null;
		else {
			// TODO Need to notify the call site that the return value is just one of possible return values.
			return parentForms.iterator().next();
		}
	}

}

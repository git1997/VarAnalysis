package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlInputDecl extends DeclaringReference {

	private HtmlFormDecl htmlFormDecl; // Can be null
	
	/**
	 * Constructor
	 */
	public HtmlInputDecl(String name, PositionRange location, HtmlFormDecl htmlFormDecl) {
		super(name, location);
		this.htmlFormDecl = htmlFormDecl;
	}
	
	/**
	 * Returns the HtmlFormDecl of this HtmlInputDecl, can be null.
	 */
	public HtmlFormDecl getHtmlFormDecl() {
		return htmlFormDecl;
	}
	
	/**
	 * Returns the name of the form containing this input, can be null.
	 */
	public String getFormName() {
		return htmlFormDecl != null ? htmlFormDecl.getName() : null;
	}
	
	/**
	 * Returns the submit-to page of the form containing this input, can be null.
	 */
	public String getSubmitToPage() {
		return htmlFormDecl != null ? htmlFormDecl.getSubmitToPage() : null;
	}
	
}

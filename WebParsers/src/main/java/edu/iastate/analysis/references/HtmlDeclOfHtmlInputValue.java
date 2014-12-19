package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlDeclOfHtmlInputValue extends DeclaringReference {
	
	private HtmlInputDecl htmlInputDecl;

	/**
	 * Constructor
	 */
	public HtmlDeclOfHtmlInputValue(String name, PositionRange location, HtmlInputDecl htmlInputDecl) {
		super(name, location);
		this.htmlInputDecl = htmlInputDecl;
	}
	
	/**
	 * Returns the HtmlInputDecl of this HtmlDeclOfHtmlInputValue
	 */
	public HtmlInputDecl getHtmlInputDecl() {
		return htmlInputDecl;
	}
	
}

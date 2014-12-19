package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlFormDecl extends DeclaringReference {
	
	private String submitToPage; // Can be null

	/**
	 * Constructor
	 */
	public HtmlFormDecl(String name, PositionRange location, String submitToPage) {
		super(name, location);
		this.submitToPage = submitToPage;
	}
	
	/**
	 * Returns the submit-to page, can be null
	 */
	public String getSubmitToPage() {
		return submitToPage;
	}

}

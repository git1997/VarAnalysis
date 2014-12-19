package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlQueryDecl extends DeclaringReference {
	
	private String submitToPage;
	
	/**
	 * Constructor
	 */
	public HtmlQueryDecl(String name, PositionRange location, String submitToPage) {
		super(name, location);
		this.submitToPage = submitToPage;
	}
	
	/**
	 * Returns the submit-to page
	 */
	public String getSubmitToPage() {
		return submitToPage;
	}

}

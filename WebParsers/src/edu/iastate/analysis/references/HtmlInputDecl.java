package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlInputDecl extends DeclaringReference {

	private String formName;		// Can be null
	private String submitToPage;	// Can be null
	
	/**
	 * Constructor
	 */
	public HtmlInputDecl(String name, PositionRange location, String formName, String submitToPage) {
		super(name, location);
		this.formName = formName;
		this.submitToPage = submitToPage;
	}
	
	/*
	 * Get properties
	 */
	
	/**
	 * Returns the name of this input's form, can be null.
	 */
	public String getFormName() {
		return formName;
	}
	
	/**
	 * Returns the submitted page of this input's form, can be null.
	 */
	public String getSubmitToPage() {
		return submitToPage;
	}
	
	@Override
	public boolean sameEntityAs(DeclaringReference declaringReference) {
		return super.sameEntityAs(declaringReference)
				&& (getFormName() == null || ((HtmlInputDecl) declaringReference).getFormName() == null || getFormName().equals(((HtmlInputDecl) declaringReference).getFormName()));
	}
	
}
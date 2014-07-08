package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class JsRefToHtmlInput extends RegularReference {

	private String formName;
	
	/**
	 * Constructor
	 */
	public JsRefToHtmlInput(String name, PositionRange location, String formName) {
		super(name, location);
		this.formName = formName;
	}
	
	/*
	 * Get properties
	 */
	
	public String getFormName() {
		return formName;
	}

	/*
	 * (non-Javadoc)
	 * @see references.RegularReference#refersTo(references.DeclaringReference)
	 */
	@Override
	public boolean refersTo(DeclaringReference declaringReference) {
		if (declaringReference instanceof HtmlInputDecl) {
			HtmlInputDecl htmlInputDecl = (HtmlInputDecl) declaringReference;
			return getName().equals(htmlInputDecl.getName())
					&& (htmlInputDecl.getFormName() == null || getFormName().equals(htmlInputDecl.getFormName()));
		}
		else
			return false;
	}
	
}

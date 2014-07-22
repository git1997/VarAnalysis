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
	
	public String getFormName() {
		return formName;
	}

	@Override
	public boolean sameEntityAs(DeclaringReference declaringReference) {
		return declaringReference instanceof HtmlInputDecl 
				&& hasSameName(declaringReference)
				&& (getFormName() == null || ((HtmlInputDecl) declaringReference).getFormName() == null || getFormName().equals(((HtmlInputDecl) declaringReference).getFormName()));
	}
	
}

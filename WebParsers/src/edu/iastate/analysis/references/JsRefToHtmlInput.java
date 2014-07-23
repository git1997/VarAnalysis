package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class JsRefToHtmlInput extends JsObjectFieldRef {

	/**
	 * Constructor
	 */
	public JsRefToHtmlInput(String name, PositionRange location, JsRefToHtmlForm jsRefToHtmlForm) {
		super(name, location, jsRefToHtmlForm);
	}
	
	public String getFormName() {
		return ((JsRefToHtmlForm) object).getName();
	}
	
	@Override
	public boolean sameEntityAs(DeclaringReference declaringReference) {
		// Don't use this: return super.sameEntityAs(declaringReference);
		return hasMatchedType(declaringReference) 
				&& hasSameName(declaringReference) 
				&& getFormName().equals(((HtmlInputDecl) declaringReference).getFormName());
	}

	@Override
	public boolean hasMatchedType(DeclaringReference declaringReference) {
		return declaringReference instanceof HtmlInputDecl; 
	}
	
}

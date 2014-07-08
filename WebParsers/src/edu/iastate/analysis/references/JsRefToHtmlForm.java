package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class JsRefToHtmlForm extends RegularReference {

	public JsRefToHtmlForm(String name, PositionRange location) {
		super(name, location);
	}

	/*
	 * (non-Javadoc)
	 * @see references.RegularReference#refersTo(references.DeclaringReference)
	 */
	@Override
	public boolean refersTo(DeclaringReference declaringReference) {
		if (declaringReference instanceof HtmlFormDecl) {
			HtmlFormDecl htmlFormDecl = (HtmlFormDecl) declaringReference;
			return getName().equals(htmlFormDecl.getName());
		}
		else
			return false;
	}

}

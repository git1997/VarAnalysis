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

	@Override
	public boolean sameEntityAs(DeclaringReference declaringReference) {
		return declaringReference instanceof HtmlFormDecl
				&& hasSameName(declaringReference);
	}

}

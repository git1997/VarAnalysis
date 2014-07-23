package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class JsRefToHtmlId extends RegularReference {

	public JsRefToHtmlId(String name, PositionRange location) {
		super(name, location);
	}

	@Override
	public boolean hasMatchedType(DeclaringReference declaringReference) {
		return declaringReference instanceof HtmlIdDecl;
	}
	
}

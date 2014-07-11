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
	public boolean refersTo(DeclaringReference declaringReference) {
		if (declaringReference instanceof HtmlIdDecl) {
			HtmlIdDecl htmlIdDecl = (HtmlIdDecl) declaringReference;
			return getName().equals(htmlIdDecl.getName());
		}
		else
			return false;
	}
	
}

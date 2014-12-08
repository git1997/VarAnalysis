package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class JsRefToHtmlForm extends JsObjectFieldRef {

	/**
	 * Constructor
	 */
	public JsRefToHtmlForm(String name, PositionRange location, RegularReference object) {
		super(name, location, object);
	}
	
	@Override
	public boolean sameEntityAs(DeclaringReference declaringReference) {
		// Don't use this: return super.sameEntityAs(declaringReference);
		return hasMatchedType(declaringReference) && hasSameName(declaringReference);
	}

	@Override
	public boolean hasMatchedType(DeclaringReference declaringReference) {
		return declaringReference instanceof HtmlFormDecl;
	}

}

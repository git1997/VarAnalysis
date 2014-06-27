package references;

import sourcetracing.Location;

/**
 * 
 * @author HUNG
 *
 */
public class JsRefToHtmlId extends RegularReference {

	public JsRefToHtmlId(String name, Location location) {
		super(name, location);
	}

	/*
	 * (non-Javadoc)
	 * @see references.RegularReference#refersTo(references.DeclaringReference)
	 */
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

package references;

import sourcetracing.Location;

/**
 * 
 * @author HUNG
 *
 */
public class JsFunctionCall extends RegularReference {

	public JsFunctionCall(String name, Location location) {
		super(name, location);
	}

	/*
	 * (non-Javadoc)
	 * @see references.RegularReference#refersTo(references.DeclaringReference)
	 */
	@Override
	public boolean refersTo(DeclaringReference declaringReference) {
		if (declaringReference instanceof JsFunctionDecl) {
			JsFunctionDecl jsFunctionDecl = (JsFunctionDecl) declaringReference;
			return getName().equals(jsFunctionDecl.getName());
		}
		else
			return false;
	}

}

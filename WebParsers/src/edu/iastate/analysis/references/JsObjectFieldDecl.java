package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class JsObjectFieldDecl extends JsVariableDecl {
	
	protected RegularReference object;

	/**
	 * Constructor
	 */
	public JsObjectFieldDecl(String name, PositionRange location, RegularReference object) {
		super(name, location);
		this.object = object;
	}
	
	public RegularReference getObject() {
		return object;
	}
	
	public String getFullyQualifiedName() {
		if (object instanceof JsObjectFieldRef)
			return ((JsObjectFieldRef) object).getFullyQualifiedName() + "." + name;
		else
			return name;
	}

	@Override
	public boolean sameEntityAs(DeclaringReference declaringReference) {
		return super.sameEntityAs(declaringReference)
				&& (getObject().sameEntityAs(((JsObjectFieldDecl) declaringReference).getObject()));
	}
	
	@Override
	public String toDebugString() {
		if (constraint.isTautology())
			return object.getName() + "." + name;
		else
			return object.getName() + "." + name + " [" + constraint.toDebugString() + "]";
	}
	
}

package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class JsObjectFieldRef extends JsVariableRef {

	protected RegularReference object;
	
	/**
	 * Constructor
	 */
	public JsObjectFieldRef(String name, PositionRange location, RegularReference object) {
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
	public boolean sameEntityAs(RegularReference regularReference) {
		return super.sameEntityAs(regularReference)
				&& (getObject().sameEntityAs(((JsObjectFieldRef) regularReference).getObject()));
	}
	
	@Override
	public boolean sameEntityAs(DeclaringReference declaringReference) {
		return super.sameEntityAs(declaringReference)
				&& (getObject().sameEntityAs(((JsObjectFieldDecl) declaringReference).getObject()));
	}

	@Override
	public boolean hasMatchedType(DeclaringReference declaringReference) {
		return declaringReference instanceof JsObjectFieldDecl;
	}
	
	@Override
	public String toDebugString() {
		if (constraint.isTautology())
			return object.getName() + "." + name;
		else
			return object.getName() + "." + name + " [" + constraint.toDebugString() + "]";
	}

}

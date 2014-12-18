package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public abstract class JsObjectFieldRef extends JsVariableRef {

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
		String objectName = (object instanceof JsObjectFieldRef ? ((JsObjectFieldRef) object).getFullyQualifiedName() : object.getName());
		return objectName + "." + name;
	}
	
	@Override
	public String toDebugString() {
		String fullyQualifiedName = getFullyQualifiedName();
		if (constraint.isTautology())
			return fullyQualifiedName;
		else
			return fullyQualifiedName + " [" + constraint.toDebugString() + "]";
	}

}

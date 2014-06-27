package entities;

import constraints.Constraint;
import constraints.TrueConstraint;

/**
 * UndeclaredEntity is a collection of dangling references.
 * 
 * @author HUNG
 *
 */
public class UndeclaredEntity extends Entity {

	/**
	 * Protected constructor.
	 */
	protected UndeclaredEntity() {
		super(null);
	}
	
	@Override
	public String getName() {
		return " [Dangling References] ";
	}
	
	@Override
	public String getType() {
		return " [Dangling References] ";
	}
	
	@Override
	public String getFilePath() {
		return "";
	}
	
	@Override
	public int getPosition() {
		return -1;
	}
	
	public Constraint getConstraint() {
		return TrueConstraint.inst;
	}

}

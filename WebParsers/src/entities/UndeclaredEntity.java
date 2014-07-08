package entities;

import java.io.File;

import edu.iastate.symex.constraints.Constraint;

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
	public File getFile() {
		return null;
	}
	
	@Override
	public int getPosition() {
		return -1;
	}
	
	public Constraint getConstraint() {
		return Constraint.TRUE;
	}

}

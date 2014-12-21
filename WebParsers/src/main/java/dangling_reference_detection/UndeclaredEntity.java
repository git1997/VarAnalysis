package dangling_reference_detection;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.Position;

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
	public Position getStartPosition() {
		return Position.UNDEFINED;
	}
	
	public Constraint getConstraint() {
		return Constraint.TRUE;
	}

}

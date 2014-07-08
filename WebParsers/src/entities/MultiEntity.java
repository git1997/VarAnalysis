package entities;

import java.util.ArrayList;

import edu.iastate.analysis.references.DeclaringReference;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;

/**
 * A MultiEntity is an Entity with multiple declarations (each with a different path constraint).
 * 
 * @author HUNG
 *
 */
public class MultiEntity extends Entity {

	private ArrayList<DeclaringReference> declaringReferences; // declaringReferences should be included in Entity.references
	
	/**
	 * Constructor. Creates a MultiEntity from two other Entities.
	 * The entity must be linked to its references after this constructor is called, and
	 * the links between the references and the two old entities must be removed.
	 * @param entity1
	 * @param entity2
	 */
	public MultiEntity(Entity entity1, Entity entity2) {
		super(entity1.getDeclaringReference());
		
		declaringReferences = new ArrayList<DeclaringReference>();
		
		if (entity1 instanceof MultiEntity)
			declaringReferences.addAll(((MultiEntity) entity1).getDeclaringReferences());		
		else
			declaringReferences.add(entity1.getDeclaringReference());
		
		if (entity2 instanceof MultiEntity)
			declaringReferences.addAll(((MultiEntity) entity2).getDeclaringReferences());		
		else
			declaringReferences.add(entity2.getDeclaringReference());
	}
	
	/*
	 * Get properties
	 */
	
	public ArrayList<DeclaringReference> getDeclaringReferences() {
		return new ArrayList<DeclaringReference>(declaringReferences);
	}
	
	/**
	 * Returns the constraint of the declaring references of this entity.
	 */
	@Override
	public Constraint getConstraint() {
		return getConstraint(getDeclaringReferences());
	}
	
	private Constraint getConstraint(ArrayList<DeclaringReference> declaringReferences) {
		if (declaringReferences.size() == 1)
			return declaringReferences.get(0).getConstraint();
		else {
			DeclaringReference lastReference = declaringReferences.get(declaringReferences.size() - 1);
			declaringReferences.remove(declaringReferences.size() - 1);
			return ConstraintFactory.createOrConstraint(getConstraint(declaringReferences), lastReference.getConstraint());
		}
	}

}

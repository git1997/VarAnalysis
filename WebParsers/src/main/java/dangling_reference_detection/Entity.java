 package dangling_reference_detection;

import java.util.ArrayList;
import java.util.Comparator;

import edu.iastate.analysis.references.DeclaringReference;
import edu.iastate.analysis.references.Reference;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.Position;

/**
 * 
 * @author HUNG
 *
 */
public class Entity {
	
	protected DeclaringReference declaringReference;
	protected ArrayList<Reference> references = new ArrayList<Reference>(); // references should include declaringReference
	
	/**
	 * Constructor. The entity must be linked to the reference after this constructor is called.
	 * @param declaringReference
	 */
	public Entity(DeclaringReference declaringReference) {
		this.declaringReference = declaringReference;
	}
	
	/*
	 * Set properties
	 */
	
	/**
	 * Adds a reference.
	 * @param reference
	 */
	public void addReference(Reference reference) {
		references.add(reference);
	}
	
	/**
	 * Removes a reference
	 * @param reference
	 */
	public void removeReference(Reference reference) {
		references.remove(reference);
	}
	
	/*
	 * Get properties
	 */
	
	public DeclaringReference getDeclaringReference() {
		return declaringReference;
	}
	
	public ArrayList<Reference> getReferences() {
		return new ArrayList<Reference>(references);
	}
	
	public String getName() {
		return declaringReference.getName();
	}
	
	public String getType() {
		return declaringReference.getType().replace("Decl", "");
	}
	
	/**
	 * Returns the position of the declaring reference
	 */
	public Position getStartPosition() {
		return declaringReference.getStartPosition();
	}
	
	/**
	 * Returns the constraint of the declaring reference of this entity.
	 */
	public Constraint getConstraint() {
		return declaringReference.getConstraint();
	}
	
	/*
	 * Supports sorting of entities
	 */
	
	public static class EntityComparator implements Comparator<Entity> {
		
		private Comparator<Entity> firstComparator, secondComparator, thirdComparator;
		
		public EntityComparator(Comparator<Entity> firstComparator, Comparator<Entity> secondComparator, Comparator<Entity> thirdComparator) {
			this.firstComparator = firstComparator;
			this.secondComparator = secondComparator;
			this.thirdComparator = thirdComparator;
		}
		
		public EntityComparator() {
			this(new EntityComparatorByName(), new EntityComparatorByType(), new EntityComparatorByFile());
		}

		@Override
		public int compare(Entity ent1, Entity ent2) {
			int result = firstComparator.compare(ent1, ent2);
			if (result != 0)
				return result;
			
			result = secondComparator.compare(ent1, ent2);
			if (result != 0)
				return result;
			
			result = thirdComparator.compare(ent1, ent2);
			return result;
		}
	}
	
	public static class EntityComparatorByName implements Comparator<Entity> {

		@Override
		public int compare(Entity ent1, Entity ent2) {
			return ent1.getName().compareTo(ent2.getName());
		}
	}
	
	public static class EntityComparatorByType implements Comparator<Entity> {

		@Override
		public int compare(Entity ent1, Entity ent2) {
			return ent1.getType().compareTo(ent2.getType());
		}
	}
	
	public static class EntityComparatorByFile implements Comparator<Entity> {

		@Override
		public int compare(Entity ent1, Entity ent2) {
			return ent1.getStartPosition().getFilePath().compareTo(ent2.getStartPosition().getFilePath());
		}
	}
	
}

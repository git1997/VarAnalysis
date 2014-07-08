package edu.iastate.analysis.references;

import java.io.File;
import java.util.Comparator;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.PositionRange;
import entities.Entity;

/**
 * 
 * @author HUNG
 *
 */
public abstract class Reference {

	private String name;						// The name of this reference
	private PositionRange location;				// The location of this reference
	
	private Entity entity = null;				// The entity that this reference declares or refers to

	private Constraint constraint = null;		// The path constraints of this reference
	
	/**
	 * Constructor.
	 * @param name
	 * @param location
	 */
	public Reference(String name, PositionRange location) {
		this.name = name;
		this.location = location;
	}
	
	/**
	 * Sets the entity of the reference. 
	 */
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	/**
	 * Sets the constraint for the reference
	 */
	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}
	
	/*
	 * Get properties
	 */
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return this.getClass().getSimpleName();
	}
	
	public PositionRange getLocation() {
		return location;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public Constraint getConstraint() {
		return (constraint != null ? constraint : Constraint.TRUE);
	}
	
	public File getFile() {
		return location.getStartPosition().getFile();
	}
	
	public int getPosition() {
		return location.getStartPosition().getOffset();
	}
	
	public String getLocationString() {
		return getFile().getAbsolutePath() + "@" + getPosition();
	}
	
	/**
	 * Returns true if this reference has the same name and type as the other reference.
	 * Subclasses of Reference may add more conditions to determine whether two references are "the same".
	 */
	public boolean sameAs(Reference reference) {
		return getName().equals(reference.getName()) && getType().equals(reference.getType());
	}
	
	/*
	 * Supports sorting of references
	 */
	
	public static class ReferenceComparator implements Comparator<Reference> {
		
		private Comparator<Reference> firstComparator, secondComparator, thirdComparator;
		
		public ReferenceComparator(Comparator<Reference> firstComparator, Comparator<Reference> secondComparator, Comparator<Reference> thirdComparator) {
			this.firstComparator = firstComparator;
			this.secondComparator = secondComparator;
			this.thirdComparator = thirdComparator;
		}
		
		public ReferenceComparator() {
			this(new ReferenceComparatorByFile(), new ReferenceComparatorByPosition(), new ReferenceComparatorByName());
		}

		@Override
		public int compare(Reference ref1, Reference ref2) {
			int result = firstComparator.compare(ref1, ref2);
			if (result != 0)
				return result;
			
			result = secondComparator.compare(ref1, ref2);
			if (result != 0)
				return result;
			
			result = thirdComparator.compare(ref1, ref2);
			return result;
		}
	}
	
	public static class ReferenceComparatorByName implements Comparator<Reference> {

		@Override
		public int compare(Reference ref1, Reference ref2) {
			return ref1.getName().compareTo(ref2.getName());
		}
	}
	
	public static class ReferenceComparatorByFile implements Comparator<Reference> {

		@Override
		public int compare(Reference ref1, Reference ref2) {
			return ref1.getFile().getAbsolutePath().compareTo(ref2.getFile().getAbsolutePath());
		}
	}
	
	public static class ReferenceComparatorByPosition implements Comparator<Reference> {

		@Override
		public int compare(Reference ref1, Reference ref2) {
			return ref1.getPosition() - ref2.getPosition();
		}
	}
	
	/**
	 * Used for debugging
	 */
	public String toDebugString() {
		return name;
	}
	
}

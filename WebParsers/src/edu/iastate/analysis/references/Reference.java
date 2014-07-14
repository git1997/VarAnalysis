package edu.iastate.analysis.references;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

import deprecated.entities.Entity;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.Position;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public abstract class Reference {
	
	protected String name;								// The name of this reference
	protected PositionRange location;					// The location of this reference
	
	protected Entity entity = null;						// The entity that this reference declares or refers to
	protected Constraint constraint = Constraint.TRUE;	// The path constraints of this reference
	protected ArrayList<Reference> dataflowFromReferences = new ArrayList<Reference>();		// e.g., $x = $y, $y = $z  =>  $z has dataflow from $y, $y from $z
	protected File entryFile = null;					// Then entry file that was run and this reference appeared
	
	/**
	 * Constructor
	 * @param name
	 * @param location
	 */
	public Reference(String name, PositionRange location) {
		this.name = name;
		this.location = location;
	}
	
	/*
	 * Set properties
	 */
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}
	
	public void addDataflowFromReference(Reference reference) {
		this.dataflowFromReferences.add(reference);
	}
	
	public void setEntryFile(File entryFile) {
		this.entryFile = entryFile;
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
		return constraint;
	}
	
	public ArrayList<Reference> getDataflowFromReferences() {
		return new ArrayList<Reference>(dataflowFromReferences);
	}
	
	public File getEntryFile() {
		return entryFile;
	}
	
	/*
	 * Methods
	 */
	
	public Position getStartPosition() {
		return location.getStartPosition();
	}
	
	/**
	 * Returns true if this reference has the same name and type as the other reference.
	 * Subclasses of Reference may add more conditions to determine whether two references are "the same".
	 */
	public boolean sameAs(Reference reference) {
		return getName().equals(reference.getName()) && getType().equals(reference.getType());
	}
	
	/**
	 * Returns true if a reference has a dataflow link from another reference.
	 * Subclasses of Reference may add more conditions to determine whether this is true.
	 */
	public boolean hasDataflowFromReference(Reference reference) {
		return false;
	}
	
	/*
	 * Supports sorting of references
	 */
	
	public static class ReferenceComparator implements Comparator<Reference> {
		
		private Comparator<Reference> firstComparator, secondComparator;
		
		public ReferenceComparator(Comparator<Reference> firstComparator, Comparator<Reference> secondComparator) {
			this.firstComparator = firstComparator;
			this.secondComparator = secondComparator;
		}
		
		@Override
		public int compare(Reference ref1, Reference ref2) {
			int result = firstComparator.compare(ref1, ref2);
			if (result != 0)
				return result;
			else
				return secondComparator.compare(ref1, ref2);
		}
	}
	
	public static class ReferenceComparatorByName implements Comparator<Reference> {

		@Override
		public int compare(Reference ref1, Reference ref2) {
			return ref1.getName().compareTo(ref2.getName());
		}
	}
	
	public static class ReferenceComparatorByPosition implements Comparator<Reference> {

		@Override
		public int compare(Reference ref1, Reference ref2) {
			int result =  ref1.getStartPosition().getFilePath().compareTo(ref2.getStartPosition().getFilePath());
			if (result != 0)
				return result;
			else
				return ref1.getStartPosition().getOffset() - ref2.getStartPosition().getOffset();
		}
	}
	
	/**
	 * Used for debugging
	 */
	public String toDebugString() {
		if (constraint.isTautology())
			return name;
		else
			return name + " [" + constraint.toDebugString() + "]";
	}

}

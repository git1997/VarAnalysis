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
	
	protected Constraint constraint = Constraint.TRUE;	// The path constraint of this reference
	protected File entryFile = null;					// Then entry file that was run and this reference appeared
	
	protected Entity entity = null;						// The entity that this reference belongs to
	protected ArrayList<Reference> dataflowFromReferences = new ArrayList<Reference>();		// e.g., $x = $y, $y = $z  =>  $z has dataflow from $y, $y from $z
	
	
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
	
	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}
	
	public void setEntryFile(File entryFile) {
		this.entryFile = entryFile;
	}
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	public void addDataflowFromReference(Reference reference) {
		this.dataflowFromReferences.add(reference);
	}
	
	/*
	 * Get properties
	 */
	
	public String getType() {
		return this.getClass().getSimpleName();
	}
	
	public String getName() {
		return name;
	}
	
	public PositionRange getLocation() {
		return location;
	}
	
	public Constraint getConstraint() {
		return constraint;
	}
	
	public File getEntryFile() {
		return entryFile;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public ArrayList<Reference> getDataflowFromReferences() {
		return new ArrayList<Reference>(dataflowFromReferences);
	}
	
	/*
	 * Methods
	 */
	
	public boolean hasSameType(Reference reference) {
		return getType().equals(reference.getType());
	}
	
	public boolean hasSameName(Reference reference) {
		return getName().equals(reference.getName());
	}
	
	public Position getStartPosition() {
		return location.getStartPosition();
	}
	
	public boolean constraintImplies(Reference reference) {
		return getConstraint().implies(reference.getConstraint());
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

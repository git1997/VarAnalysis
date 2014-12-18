package edu.iastate.analysis.references;

import java.io.File;
import java.util.Comparator;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.Position;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public abstract class Reference {
	
	protected String name;								// The name of this reference (e.g., 'input1')
	protected String lexeme;							// The lexeme of this reference (e.g., $_GET['input1'])
	
	protected PositionRange location;					// The location of the name of this reference
	protected PositionRange lexemeLocation;				// The location of the lexeme of this reference
	
	protected Constraint constraint = Constraint.TRUE;	// The path constraint of this reference
	protected File entryFile = null;					// Then entry file that was run and this reference appeared
	
	/**
	 * Constructor
	 * @param name
	 * @param lexeme
	 * @param location
	 * @param lexemeLocation
	 */
	public Reference(String name, String lexeme, PositionRange location, PositionRange lexemeLocation) {
		this.name = name;
		this.lexeme = lexeme;
		this.location = location;
		this.lexemeLocation = lexemeLocation;
	}
	
	/**
	 * Constructor
	 * @param name
	 * @param location
	 */
	public Reference(String name, PositionRange location) {
		this(name, name, location, location);
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
	
	/*
	 * Get properties
	 */
	
	public String getType() {
		return this.getClass().getSimpleName();
	}
	
	public String getName() {
		return name;
	}
	
	public String getLexeme() {
		return lexeme;
	}
	
	public PositionRange getLocation() {
		return location;
	}
	
	public PositionRange getLexemeLocation() {
		return lexemeLocation;
	}
	
	public Constraint getConstraint() {
		return constraint;
	}
	
	public File getEntryFile() {
		return entryFile;
	}
	
	/*
	 * Utility methods
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
		
		private Comparator<Reference> firstComparator, secondComparator, thirdComparator;
		
		public ReferenceComparator(Comparator<Reference> firstComparator, Comparator<Reference> secondComparator, Comparator<Reference> thirdComparator) {
			this.firstComparator = firstComparator;
			this.secondComparator = secondComparator;
			this.thirdComparator = thirdComparator;
		}
		
		@Override
		public int compare(Reference ref1, Reference ref2) {
			int result = firstComparator != null ? firstComparator.compare(ref1, ref2) : 0;
			if (result != 0)
				return result;
			
			result = secondComparator != null ? secondComparator.compare(ref1, ref2) : 0;
			if (result != 0)
				return result;
				
			return thirdComparator != null ? thirdComparator.compare(ref1, ref2) : 0;
		}
	}
	
	public static class ReferenceComparatorByType implements Comparator<Reference> {

		@Override
		public int compare(Reference ref1, Reference ref2) {
			return ref1.getType().compareTo(ref2.getType());
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

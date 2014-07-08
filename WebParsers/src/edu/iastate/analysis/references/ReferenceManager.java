package edu.iastate.analysis.references;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import edu.iastate.symex.constraints.ConstraintFactory;

/**
 * 
 * @author HUNG
 *
 */
public class ReferenceManager {
	
	/**
	 * Maps a location to a reference
	 */
	private HashMap<String, Reference> mapLocationToReference = new HashMap<String, Reference>();
	
	/*
	 * Update ReferenceManager
	 */
	
	/**
	 * Adds a reference to ReferenceManager.
	 * @param reference
	 */
	public void addReference(Reference reference) {
		String location = reference.getLocationString();
		Reference existingReference = mapLocationToReference.get(location);
		
		if (existingReference != null) {
			if (existingReference.getConstraint() != reference.getConstraint())
				existingReference.setConstraint(ConstraintFactory.createOrConstraint(existingReference.getConstraint(), reference.getConstraint()));
		}
		else {
			mapLocationToReference.put(location, reference);
		}
	}
	
	/*
	 * Get properties
	 */
	
	public ArrayList<Reference> getReferenceList() {
		return new ArrayList<Reference>(mapLocationToReference.values());
	}
	
	public ArrayList<Reference> getSortedReferenceList() {
		ArrayList<Reference> references = getReferenceList();
		Collections.sort(references, new Reference.ReferenceComparator());
		return references;
	}

}

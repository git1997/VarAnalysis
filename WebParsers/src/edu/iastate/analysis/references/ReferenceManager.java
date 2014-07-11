package edu.iastate.analysis.references;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import edu.iastate.analysis.references.Reference.ReferenceComparatorByName;
import edu.iastate.analysis.references.Reference.ReferenceComparatorByPosition;
import edu.iastate.symex.constraints.ConstraintFactory;

/**
 * 
 * @author HUNG
 *
 */
public class ReferenceManager {
	
	/**
	 * Maps a reference name to the references with that name, to speed up searching
	 */
	private HashMap<String, ArrayList<Reference>> mapNameToReferences = new HashMap<String, ArrayList<Reference>>();
	
	/**
	 * Maps a location to a reference
	 */
	private HashMap<String, Reference> mapLocationToReference = new HashMap<String, Reference>();
	
	/**
	 * Adds a reference to ReferenceManager.
	 * @param reference
	 */
	public void addReference(Reference reference) {
		/*
		 * Record data flows
		 */
		if (reference instanceof RegularReference && mapNameToReferences.containsKey(reference.getName())) {
			for (Reference declaringRef : mapNameToReferences.get(reference.getName())) {
				if (declaringRef instanceof DeclaringReference && ((RegularReference) reference).refersTo((DeclaringReference) declaringRef))
					reference.addLinkedToReference(declaringRef);
			}
		}
		
		/*
		 * Update references
		 */
		String location = reference.getLocationString();
		Reference existingReference = mapLocationToReference.get(location);
		
		if (existingReference != null) {
			if (existingReference.getConstraint() != reference.getConstraint())
				existingReference.setConstraint(ConstraintFactory.createOrConstraint(existingReference.getConstraint(), reference.getConstraint()));
		}
		else {
			mapLocationToReference.put(location, reference);
			
			if (!mapNameToReferences.containsKey(reference.getName()))
				mapNameToReferences.put(reference.getName(), new ArrayList<Reference>());
			mapNameToReferences.get(reference.getName()).add(reference);
		}
	}
	
	/*
	 * Get properties
	 */
	
	public ArrayList<Reference> getReferenceList() {
		return new ArrayList<Reference>(mapLocationToReference.values());
	}
	
	public ArrayList<Reference> getSortedReferenceListByNameThenPosition() {
		ArrayList<Reference> references = getReferenceList();
		Collections.sort(references, new Reference.ReferenceComparator(new ReferenceComparatorByName(), new ReferenceComparatorByPosition()));
		return references;
	}
	
	public ArrayList<Reference> getSortedReferenceListByPositionThenName() {
		ArrayList<Reference> references = getReferenceList();
		Collections.sort(references, new Reference.ReferenceComparator(new ReferenceComparatorByPosition(), new ReferenceComparatorByName()));
		return references;
	}

}

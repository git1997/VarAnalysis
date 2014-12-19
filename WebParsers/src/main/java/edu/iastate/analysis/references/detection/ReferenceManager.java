package edu.iastate.analysis.references.detection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.Reference.ReferenceComparatorByName;
import edu.iastate.analysis.references.Reference.ReferenceComparatorByPosition;
import edu.iastate.analysis.references.Reference.ReferenceComparatorByType;

/**
 * 
 * @author HUNG
 *
 */
public class ReferenceManager {
	
	/**
	 * Maps a location to a set of references
	 */
	private HashMap<String, HashSet<Reference>> map;
	
	/**
	 * [Optional] Manages data flows
	 */
	private DataFlowManager dataFlowManager;
	
	/**
	 * Constructor
	 */
	public ReferenceManager() {
		map = new HashMap<String, HashSet<Reference>>();
		dataFlowManager = new DataFlowManager(this);
	}
	
	public DataFlowManager getDataFlowManager() {
		return dataFlowManager;
	}
	
	/*
	 * MANAGE REFERENCES
	 */
	
	/**
	 * Adds a reference
	 */
	public void addReference(Reference reference) {
		String location = reference.getStartPosition().toString();
		
		if (!map.containsKey(reference))
			map.put(location, new HashSet<Reference>());
		
		map.get(location).add(reference); 
	}
	
	/**
	 * Gets references
	 */
	public ArrayList<Reference> getReferenceList() {
		ArrayList<Reference> references = new ArrayList<Reference>();
		for (HashSet<Reference> refs : map.values())
			references.addAll(refs);
		return references;
	}
	
	/**
	 * Returns references as a map of reference names to speed up searching 
	 */
	public HashMap<String, ArrayList<Reference>> getReferenceListByName() {
		HashMap<String, ArrayList<Reference>> map = new HashMap<String, ArrayList<Reference>>(); 
		for (Reference reference : getReferenceList()) {
			if (!map.containsKey(reference.getName()))
				map.put(reference.getName(), new ArrayList<Reference>());
			map.get(reference.getName()).add(reference);
		}
		return map;
	}
	
	public ArrayList<Reference> getSortedReferenceListByNameThenPosition() {
		ArrayList<Reference> references = getReferenceList();
		Collections.sort(references, new Reference.ReferenceComparator(new ReferenceComparatorByName(), new ReferenceComparatorByPosition(), null));
		return references;
	}

	public ArrayList<Reference> getSortedReferenceListByTypeThenNameThenPosition() {
		ArrayList<Reference> references = getReferenceList();
		Collections.sort(references, new Reference.ReferenceComparator(new ReferenceComparatorByType(), new ReferenceComparatorByName(), new ReferenceComparatorByPosition()));
		return references;
	}
	
}

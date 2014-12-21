package edu.iastate.analysis.references.detection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.Reference.ReferenceComparatorByName;
import edu.iastate.analysis.references.Reference.ReferenceComparatorByPosition;
import edu.iastate.analysis.references.Reference.ReferenceComparatorByType;
import edu.iastate.symex.position.Position;

/**
 * 
 * @author HUNG
 *
 */
public class ReferenceManager {
	
	private LinkedList<Reference> references;	// List of references (should not contain duplicates)
	
	/**
	 * [Optional] Manages data flows
	 */
	private DataFlowManager dataFlowManager;
	
	/**
	 * Constructor
	 */
	public ReferenceManager() {
		references = new LinkedList<Reference>();
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
		references.add(reference); 
	}
	
	/**
	 * Gets references
	 */
	public ArrayList<Reference> getReferenceList() {
		return new ArrayList<Reference>(references);
	}
	
	/**
	 * Returns references as a map of reference names to speed up searching 
	 */
	public HashMap<String, LinkedList<Reference>> getReferenceListByName() {
		HashMap<String, LinkedList<Reference>> map = new HashMap<String, LinkedList<Reference>>(); 
		for (Reference reference : getReferenceList()) {
			if (!map.containsKey(reference.getName()))
				map.put(reference.getName(), new LinkedList<Reference>());
			map.get(reference.getName()).add(reference);
		}
		return map;
	}
	
	public ArrayList<Reference> getSortedReferenceListByNameThenPosition() {
		ArrayList<Reference> references = getReferenceList();
		Collections.sort(references, new Reference.ReferenceComparator(new ReferenceComparatorByName(), new ReferenceComparatorByPosition(), null));
		return references;
	}
	
	public ArrayList<Reference> getSortedReferenceListByPositionThenName() {
		ArrayList<Reference> references = getReferenceList();
		Collections.sort(references, new Reference.ReferenceComparator(new ReferenceComparatorByPosition(), new ReferenceComparatorByName(), null));
		return references;
	}

	public ArrayList<Reference> getSortedReferenceListByTypeThenNameThenPosition() {
		ArrayList<Reference> references = getReferenceList();
		Collections.sort(references, new Reference.ReferenceComparator(new ReferenceComparatorByType(), new ReferenceComparatorByName(), new ReferenceComparatorByPosition()));
		return references;
	}
	
	/**
	 * Returns the text describing the reference list
	 */
	public String writeReferenceListToText() {
		StringBuilder str = new StringBuilder();
		for (Reference ref1 : getSortedReferenceListByPositionThenName()) {
			str.append(writeReferenceToText(ref1) + System.lineSeparator());
			
			ArrayList<Reference> refs2 = dataFlowManager.getDataFlowTo(ref1);
			Collections.sort(refs2, new Reference.ReferenceComparator(new ReferenceComparatorByPosition(), new ReferenceComparatorByName(), null));
			
			for (Reference ref2 : refs2) {
				str.append("\t<- " + writeReferenceToText(ref2) + System.lineSeparator());
			}
		}
		return str.toString();
	}
	
	private String writeReferenceToText(Reference ref) {
		Position startPosition = ref.getLocation().getStartPosition();
		return ref.getName() + " (" + ref.getType() + ") @ " + startPosition.getFile().getName() + ":Line" + startPosition.getLine() + ":Offset" + startPosition.getOffset(); 
	}
}

package edu.iastate.analysis.references.detection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

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
	
	private HashSet<Reference> references;
	
	/**
	 * [Optional] Manages data flows
	 */
	private DataFlowManager dataFlowManager;
	
	/**
	 * Constructor
	 */
	public ReferenceManager() {
		references = new HashSet<Reference>();
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
	 * Removes a reference. Should be called from DataFlowManager only.
	 */
	protected void removeReference(Reference reference) {
		references.remove(reference);
	}
	
	/**
	 * Gets references
	 */
	public ArrayList<Reference> getReferenceList() {
		return new ArrayList<Reference>(references);
	}
	
	/**
	 * Returns a sorted list of references
	 */
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
		
		ArrayList<Reference> refs1 = getReferenceList();
		Collections.sort(refs1, new Reference.ReferenceComparator(new ReferenceComparatorByPosition(), new ReferenceComparatorByName(), new ReferenceComparatorByLinks(dataFlowManager)));
		for (Reference ref1 : refs1) {
			str.append(writeReferenceToText(ref1) + System.lineSeparator());
			
			ArrayList<Reference> refs2 = dataFlowManager.getDataFlowTo(ref1);
			Collections.sort(refs2, new Reference.ReferenceComparator(new ReferenceComparatorByPosition(), new ReferenceComparatorByName(), null));
			for (Reference ref2 : refs2) {
				str.append("\t<- " + writeReferenceToText(ref2) + System.lineSeparator());
			}
		}
		
		return str.toString();
	}
	
	private static String writeReferenceToText(Reference ref) {
		Position startPosition = ref.getLocation().getStartPosition();
		return ref.getName() + " (" + ref.getType() + ") @ " + startPosition.toDebugString(); 
	}
	
	/**
	 * Supports sorting of references
	 */
	public static class ReferenceComparatorByLinks implements Comparator<Reference> {

		private DataFlowManager dataFlowManager;
		
		public ReferenceComparatorByLinks(DataFlowManager dataFlowManager) {
			this.dataFlowManager = dataFlowManager;
		}
		
		@Override
		public int compare(Reference ref1, Reference ref2) {
			ArrayList<Reference> refs1 = dataFlowManager.getDataFlowTo(ref1);
			ArrayList<Reference> refs2 = dataFlowManager.getDataFlowTo(ref2);
			
			int result = new Integer(refs1.size()).compareTo(new Integer(refs2.size()));
			if (result != 0)
				return result;
			
			Collections.sort(refs1, new Reference.ReferenceComparator(new ReferenceComparatorByPosition(), new ReferenceComparatorByName(), null));
			Collections.sort(refs2, new Reference.ReferenceComparator(new ReferenceComparatorByPosition(), new ReferenceComparatorByName(), null));
			
			for (int i = 0; i < refs1.size(); i++) {
				String r1 = writeReferenceToText(refs1.get(i));
				String r2 = writeReferenceToText(refs2.get(i));
				result = r1.compareTo(r2);
				if (result != 0)
					return result;
			}
			
			return 0;
		}
	}
	
}

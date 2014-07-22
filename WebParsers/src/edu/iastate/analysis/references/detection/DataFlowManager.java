package edu.iastate.analysis.references.detection;

import java.util.HashMap;
import java.util.HashSet;

import edu.iastate.analysis.references.DeclaringReference;
import edu.iastate.analysis.references.RegularReference;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class DataFlowManager {
	
	private HashMap<DeclaringReference, PositionRange> mapDeclToRefLocations = new HashMap<DeclaringReference, PositionRange>();
	
	private HashMap<RegularReference, HashSet<DeclaringReference>> mapRefToDecls = new HashMap<RegularReference, HashSet<DeclaringReference>>();

	public void putMapDeclToRefLocations(DeclaringReference declaringReference, PositionRange location) {
		mapDeclToRefLocations.put(declaringReference, location);
	}
	
	public PositionRange getRefLocationsOfDecl(DeclaringReference declaringReference) {
		return mapDeclToRefLocations.get(declaringReference);
	}
	
	public void putMapRefToDecls(RegularReference regularReference, HashSet<DeclaringReference> declaringReferences) {
		mapRefToDecls.put(regularReference, declaringReferences);
	}
	
	public HashSet<DeclaringReference> getDeclsOfRef(RegularReference regularReference) {
		return new HashSet<DeclaringReference>(mapRefToDecls.get(regularReference));
	}
	
}

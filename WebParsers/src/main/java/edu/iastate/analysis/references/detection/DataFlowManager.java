package edu.iastate.analysis.references.detection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.iastate.analysis.references.DeclaringReference;
import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.RegularReference;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class DataFlowManager {
	
	// e.g., $y = $z, $x = $y,  =>  The data flow is $z -> $y -> $x
	/**
	 * List of references that have data flow from a given reference.
	 */
	private HashMap<Reference, ArrayList<Reference>> dataFlowFrom = new HashMap<Reference, ArrayList<Reference>>();
	
	/**
	 * List of references that have data flow to a given reference.
	 */
	private HashMap<Reference, ArrayList<Reference>> dataFlowTo = new HashMap<Reference, ArrayList<Reference>>();
	
	/**
	 * Adds data flow from a DeclaringReference to a RegulerReference
	 */
	public void addDataFlow(DeclaringReference ref1, RegularReference ref2) {
		addDataFlow_(ref1, ref2);
	}
	
	/**
	 * Adds data flow from a set of DeclaringReferences to a RegularReference
	 */
	public void addDataFlow(HashSet<DeclaringReference> refs1, RegularReference ref2) {
		for (DeclaringReference ref1 : refs1)
			addDataFlow_(ref1, ref2);
	}
	
	/**
	 * Adds data flow from a RegularReference to a DeclaringReference
	 */
	public void addDataFlow(RegularReference ref1, DeclaringReference ref2) {
		addDataFlow_(ref1, ref2);
	}
	
	/**
	 * Adds data flow from a set of RegularReferences to a DeclaringReference
	 */
	public void addDataFlow(HashSet<RegularReference> refs1, DeclaringReference ref2) {
		for (RegularReference ref1 : refs1)
			addDataFlow_(ref1, ref2);
	}
	
	private void addDataFlow_(Reference ref1, Reference ref2) {
		if (!dataFlowFrom.containsKey(ref1))
			dataFlowFrom.put(ref1, new ArrayList<Reference>());
		dataFlowFrom.get(ref1).add(ref2);
		
		if (!dataFlowTo.containsKey(ref2))
			dataFlowTo.put(ref2, new ArrayList<Reference>());
		dataFlowTo.get(ref2).add(ref1);
	}
	
	/**
	 * Returns the list of references that have data flow from a given reference.
	 */
	public ArrayList<Reference> getDataFlowFrom(Reference ref) {
		return dataFlowFrom.containsKey(ref) ? new ArrayList<Reference>(dataFlowFrom.get(ref)) : new ArrayList<Reference>();
	}
	
	/**
	 * Returns the list of references that have data flow to a given reference.
	 */
	public ArrayList<Reference> getDataFlowTo(Reference ref) {
		return dataFlowTo.containsKey(ref) ? new ArrayList<Reference>(dataFlowTo.get(ref)) : new ArrayList<Reference>();
	}
	
	/**
	 * Clears data flows that involve a given reference.
	 */
	public void clearDataflow(Reference ref) {
		dataFlowFrom.remove(ref);
		dataFlowTo.remove(ref);
	}
	
	/*
	 * RESOLVING DATA FLOWS
	 */
	
	/**
	 * Resolves data flows for HtmlDocument
	 */
	public void resolveDataFlowforHtmlDocument(HtmlDocument htmlDocument) {
		
	}
	
	/**
	 * Resolves data flows for entire page (including PHP, SQL, and HtmlDocument)
	 */
	public void resolveDataFlowforEntirePage() {
		
	}
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
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
		return mapRefToDecls.containsKey(regularReference) ? new HashSet<DeclaringReference>(mapRefToDecls.get(regularReference)) : new HashSet<DeclaringReference>();
	}
	
}

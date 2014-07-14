package edu.iastate.analysis.references;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import edu.iastate.analysis.references.Reference.ReferenceComparatorByName;
import edu.iastate.analysis.references.Reference.ReferenceComparatorByPosition;
import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.dom.nodes.HtmlAttributeValue;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.dom.nodes.HtmlNodeVisitor;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.position.Position;
import edu.iastate.symex.position.PositionRange;

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
	
	/**
	 * Adds a reference to ReferenceManager.
	 * @param reference
	 */
	public void addReference(Reference reference) {
		String location = reference.getStartPosition().toString();
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
	
	/**
	 * Resolves dataflows
	 */
	public void resolveDataflows(HtmlDocument htmlDocument) {
		// TODO Improve this
		Collection<Reference> references = mapLocationToReference.values();
		
		HashMap<String, ArrayList<Reference>> mapNameToReferences = new HashMap<String, ArrayList<Reference>>();
		for (Reference ref : references) {
			if (!mapNameToReferences.containsKey(ref.getName()))
				mapNameToReferences.put(ref.getName(), new ArrayList<Reference>());
			mapNameToReferences.get(ref.getName()).add(ref);
		}
		
		for (Reference ref1 : references) {
			for (Reference ref2 : mapNameToReferences.get(ref1.getName()))
				if (ref1.hasDataflowFromReference(ref2))
					ref1.addDataflowFromReference(ref2);
		}
		

		(new HtmlNodeVisitor() {
			public void visitElement(HtmlElement htmlElement) {
				super.visitElement(htmlElement);
				
				HtmlAttributeValue attributeValue = htmlElement.getAttributeValue("name");
				if (attributeValue != null) {
					Reference reference = findReferenceAtLocation(attributeValue.getLocation());
					if (reference != null) {
						for (HtmlAttribute attribute : htmlElement.getAttributes()) {
							if (isSymbolicValue(attribute.getValue())) {
								Reference ref2 = findReferenceAtApproxLocation(attribute.getLocation());
								if (ref2 != null)
									reference.addDataflowFromReference(ref2);
							}
						}
					}
				}
			}
		}).visitDocument(htmlDocument);
	}
	
	private Reference findReferenceAtLocation(PositionRange location) {
		return mapLocationToReference.get(location.getStartPosition().toString());
	}
	
	private Reference findReferenceAtApproxLocation(PositionRange location) {
		Position endPosition = location.getEndPosition();
		for (int i = 0; i < 20; i++) {
			Position pos = new Position(endPosition.getFile(), endPosition.getOffset() + i);
			Reference ref = mapLocationToReference.get(pos.toString());
			if (ref != null)
				return ref;
		}
		return null;
	}
	
	
	private boolean isSymbolicValue(String value) {
		return value.matches("1*");
	}
	
}

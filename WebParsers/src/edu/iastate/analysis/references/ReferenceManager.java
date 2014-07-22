package edu.iastate.analysis.references;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import edu.iastate.analysis.references.Reference.ReferenceComparatorByName;
import edu.iastate.analysis.references.Reference.ReferenceComparatorByPosition;
import edu.iastate.analysis.references.detection.DataFlowManager;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
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
	 * Used to record data flows among references
	 */
	private DataFlowManager dataFlowManager = new DataFlowManager();
	
	/**
	 * Adds a reference
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
	
	/**
	 * Gets references
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
	 * Gets dataFlowManager
	 */
	public DataFlowManager getDataFlowManager() {
		return dataFlowManager;
	}
	
	/**
	 * Resolves dataflows
	 */
	public void resolveDataflows(HtmlDocument htmlDocument) {        
		Collection<Reference> references = mapLocationToReference.values();
		
		// Use a map to speed up searching
		HashMap<String, LinkedList<Reference>> mapNameToReferences = new HashMap<String, LinkedList<Reference>>();
		for (Reference reference : references) {
			if (!mapNameToReferences.containsKey(reference.getName()))
				mapNameToReferences.put(reference.getName(), new LinkedList<Reference>());
			mapNameToReferences.get(reference.getName()).add(reference);
		}
		
		for (Reference reference1 : references) {
			if (reference1 instanceof DeclaringReference) {
				// [1] Handle DeclaringReference  
				PositionRange range = getDataFlowManager().getRefLocationsOfDecl((DeclaringReference) reference1);
				if (range != null)
					for (Reference reference2 : findReferencesInRange(range))
						addDataflow(reference1, reference2);
			}
			else {
				if (reference1 instanceof PhpVariableRef || reference1 instanceof JsVariableRef) {
					// [2] Handle RegularReference of types PhpVariableRef or JsVariableRef
					for (Reference reference2 : dataFlowManager.getDeclsOfRef((RegularReference) reference1))
						addDataflow(reference1, reference2);
				}
				else {
					// [3] Handle RegularReference of other types
					for (Reference reference2 : mapNameToReferences.get(reference1.getName()))
						if (reference2 instanceof DeclaringReference && ((RegularReference) reference1).sameEntityAs((DeclaringReference) reference2))
							addDataflow(reference1, reference2);
				}
			}
		}
	}
	
	private LinkedList<Reference> findReferencesInRange(PositionRange range) {
		LinkedList<Reference> references = new LinkedList<Reference>();

		for (int i = 0; i < range.getLength(); i++) {
			Position position = range.getPositionAtRelativeOffset(i);
			Reference reference = mapLocationToReference.get(position.toString());
			if (reference != null)
				references.add(reference);
		}
		
		return references;
	}
	
	private void addDataflow(Reference reference1, Reference reference2) {
		// TODO Consider constraint or not?
		if (ConstraintFactory.createAndConstraint(reference1.getConstraint(), reference2.getConstraint()).isSatisfiable()) 
			reference1.addDataflowFromReference(reference2);
	}
	
//
//		(new HtmlNodeVisitor() {
//			public void visitElement(HtmlElement htmlElement) {
//				super.visitElement(htmlElement);
//				
//				HtmlAttributeValue attributeValue = htmlElement.getAttributeValue("name");
//				if (attributeValue != null) {
//					Reference reference = findReferenceAtLocation(attributeValue.getLocation());
//					if (reference != null) {
//						for (HtmlAttribute attribute : htmlElement.getAttributes()) {
//							if (isSymbolicValue(attribute.getValue())) {
//								Reference ref2 = findReferenceAtApproxLocation(attribute.getLocation());
//								if (ref2 != null)
//									reference.addDataflowFromReference(ref2);
//							}
//						}
//					}
//				}
//			}
//		}).visitDocument(htmlDocument);
//	}
//	
//	private boolean isSymbolicValue(String value) {
//		return value.matches("1*");
//	}
	
}
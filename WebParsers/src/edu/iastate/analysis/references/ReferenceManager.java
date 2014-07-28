package edu.iastate.analysis.references;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import edu.iastate.analysis.references.Reference.ReferenceComparatorByName;
import edu.iastate.analysis.references.Reference.ReferenceComparatorByPosition;
import edu.iastate.analysis.references.detection.DataFlowManager;
import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.dom.nodes.HtmlAttributeValue;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.dom.nodes.HtmlNodeVisitor;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.position.Position;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;

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
		Reference existingReference = findReferenceAtPosition(reference.getStartPosition());
		
		if (existingReference != null) {
			if (existingReference.getConstraint() != reference.getConstraint())
				existingReference.setConstraint(ConstraintFactory.createOrConstraint(existingReference.getConstraint(), reference.getConstraint()));
		}
		else {
			addReferenceAtPosition(reference, reference.getStartPosition());
		}
	}
	
	/**
	 * Adds a reference at a position
	 */
	private void addReferenceAtPosition(Reference reference, Position position) {
		mapLocationToReference.put(position.toString(), reference);
	}
	
	/**
	 * Finds a reference at a position
	 */
	public Reference findReferenceAtPosition(Position position) {
		return mapLocationToReference.get(position.toString());
	}
	
	/**
	 * Finds references at a range
	 */
	public LinkedList<Reference> findReferencesInRange(PositionRange range) {
		LinkedList<Reference> references = new LinkedList<Reference>();

		for (int i = 0; i < range.getLength(); i++) {
			Position position = range.getPositionAtRelativeOffset(i);
			Reference reference = findReferenceAtPosition(position);
			if (reference != null)
				references.add(reference);
		}
		
		return references;
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
		// Get all the references
		Collection<Reference> references = mapLocationToReference.values();
		
		// Use a map to speed up searching
		HashMap<String, LinkedList<Reference>> mapNameToReferences = new HashMap<String, LinkedList<Reference>>();
		for (Reference reference : references) {
			if (!mapNameToReferences.containsKey(reference.getName()))
				mapNameToReferences.put(reference.getName(), new LinkedList<Reference>());
			mapNameToReferences.get(reference.getName()).add(reference);
		}
		
		// Detect data flows for HTML references (between Decl and Ref)
		detectDataFlowsForHtmlReferences(htmlDocument);
		
		/*
		 * Create data flow links among references
		 */
		for (Reference reference1 : references) {
			if (reference1 instanceof DeclaringReference) {
				// [1] Handle data flows between Decl and Ref: Some were detected by PhpVisitor and JavascriptVisitor; Others were detected in detectDataFlowsForHtmlReferences()
				PositionRange range = dataFlowManager.getRefLocationsOfDecl((DeclaringReference) reference1);
				if (range != null)
					for (Reference reference2 : findReferencesInRange(range))
						if (reference1 instanceof PhpVariableDecl && (reference2 instanceof PhpVariableRef || reference2 instanceof PhpRefToHtml || reference2 instanceof PhpRefToSqlTableColumn || reference2 instanceof PhpFunctionCall)
								|| !(reference1 instanceof PhpVariableDecl))
							addDataflow(reference1, reference2);
			}
			else {
				// [2] Handle data flows between Ref and Decl in a single language (PHP or JavaScript), detected by PhpVisitor and JavascriptVisitor
				for (Reference reference2 : dataFlowManager.getDeclsOfRef((RegularReference) reference1))
					addDataflow(reference1, reference2);

				// [3] Handle data flows between Ref and Decl across languages
				for (Reference reference2 : mapNameToReferences.get(reference1.getName()))
					if (reference1 instanceof PhpVariableRef && reference2 instanceof PhpVariableDecl
						|| reference1 instanceof JsVariableRef && reference2 instanceof JsVariableDecl) {
						// Skip if the two references are of the same language
					}
					else if (reference2 instanceof DeclaringReference && ((RegularReference) reference1).sameEntityAs((DeclaringReference) reference2))
						addDataflow(reference1, reference2);
					
				// [4] Handle data flows between PhpRefToHtml and JsObjectFieldDecl
				if (reference1 instanceof PhpRefToHtml && mapNameToReferences.containsKey("value"))
					for (Reference reference2 : mapNameToReferences.get("value"))
						if (reference2 instanceof JsObjectFieldDecl 
								&& ((JsObjectFieldDecl) reference2).getObject() instanceof JsRefToHtmlInput
								&& ((JsRefToHtmlInput) ((JsObjectFieldDecl) reference2).getObject()).getName().equals(reference1.getName()))
							addDataflow(reference1, reference2);	
			}
		}
	}
	
	/**
	 * Detects data flows for HTML references
	 */
	private void detectDataFlowsForHtmlReferences(HtmlDocument htmlDocument) {
		(new HtmlNodeVisitor() {
			public void visitElement(HtmlElement htmlElement) {
				super.visitElement(htmlElement);
				
				HtmlAttributeValue name = htmlElement.getAttributeValue("name");
				if (name != null) {
					Reference reference = findReferenceAtPosition(name.getLocation().getStartPosition());
					if (reference != null) {
						HtmlAttribute value = htmlElement.getAttribute("value");
						if (value != null) {
							Position endPosition = value.getLocation().getEndPosition();
							PositionRange range = new Range(endPosition.getFile(), endPosition.getOffset(), 10);
							dataFlowManager.putMapDeclToRefLocations((DeclaringReference) reference, range);
						}
					}
				}
			}
		}).visitDocument(htmlDocument);
	}
	
	/**
	 * Adds a data flow link between two references
	 */
	private void addDataflow(Reference reference1, Reference reference2) {
		// TODO Consider constraint or not?
		if (ConstraintFactory.createAndConstraint(reference1.getConstraint(), reference2.getConstraint()).isSatisfiable()) 
			reference1.addDataflowFromReference(reference2);
	}
	
}

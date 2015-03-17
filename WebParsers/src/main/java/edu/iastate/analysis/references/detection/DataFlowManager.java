package edu.iastate.analysis.references.detection;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import edu.iastate.analysis.config.AnalysisConfig;
import edu.iastate.analysis.references.DeclaringReference;
import edu.iastate.analysis.references.HtmlDeclOfHtmlInputValue;
import edu.iastate.analysis.references.HtmlFormDecl;
import edu.iastate.analysis.references.HtmlIdDecl;
import edu.iastate.analysis.references.HtmlInputDecl;
import edu.iastate.analysis.references.HtmlQueryDecl;
import edu.iastate.analysis.references.JsDeclOfHtmlInputValue;
import edu.iastate.analysis.references.JsRefToHtmlForm;
import edu.iastate.analysis.references.JsRefToHtmlId;
import edu.iastate.analysis.references.JsRefToHtmlInput;
import edu.iastate.analysis.references.JsRefToHtmlInputValue;
import edu.iastate.analysis.references.PhpFunctionCall;
import edu.iastate.analysis.references.PhpRefToHtml;
import edu.iastate.analysis.references.PhpRefToSqlTableColumn;
import edu.iastate.analysis.references.PhpVariableRef;
import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.RegularReference;
import edu.iastate.analysis.references.Reference.ReferenceComparatorByName;
import edu.iastate.analysis.references.Reference.ReferenceComparatorByPosition;
import edu.iastate.analysis.references.Reference.ReferenceComparatorByType;
import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.position.Position;

/**
 * 
 * @author HUNG
 *
 * This class manages data flows between references. E.g., $y = $z, $x = $y  =>  The data flow is $z -> $y -> $y -> $x
 */
public class DataFlowManager {
	
	private ReferenceManager referenceManager; // Nodes that are managed by this DataFlowManager must be present in the referenceManager
	
	// Set of references that have data flow from a given reference
	private HashMap<Reference, HashSet<Reference>> dataFlowFrom = new HashMap<Reference, HashSet<Reference>>();
	
	// Set of references that have data flow to a given reference
	private HashMap<Reference, HashSet<Reference>> dataFlowTo = new HashMap<Reference, HashSet<Reference>>();
	
	/**
	 * Constructor
	 * @param referenceManager
	 */
	public DataFlowManager(ReferenceManager referenceManager) {
		this.referenceManager = referenceManager;
	}
	
	/*
	 * SETTING DATA FLOWS
	 */
	
	/**
	 * Adds data flow from a DeclaringReference to a RegularReference (with constraint checking)
	 */
	public void addDataFlow(DeclaringReference ref1, RegularReference ref2) {
		if (constraintCompatible(ref1, ref2))
			addDataFlowWithoutConstraintChecking(ref1, ref2);
	}
	
	/**
	 * Adds data flow from a set of DeclaringReferences to a RegularReference (with constraint checking)
	 */
	public void addDataFlow(HashSet<DeclaringReference> refs1, RegularReference ref2) {
		for (DeclaringReference ref1 : refs1)
			addDataFlow(ref1, ref2);
	}
	
	/**
	 * Adds data flow from a RegularReference to a DeclaringReference (with constraint checking)
	 */
	public void addDataFlow(RegularReference ref1, DeclaringReference ref2) {
		if (constraintCompatible(ref1, ref2)) // Normally, ref1 and ref2 should have the same constraint (this check may not be necessary)
			addDataFlowWithoutConstraintChecking(ref1, ref2);
	}
	
	/**
	 * Adds data flow from a set of RegularReferences to a DeclaringReference (with constraint checking)
	 */
	public void addDataFlow(HashSet<RegularReference> refs1, DeclaringReference ref2) {
		for (RegularReference ref1 : refs1)
			addDataFlow(ref1, ref2);
	}
	
	/**
	 * Adds data flow from a reference to another reference (without constraint checking)
	 */
	private void addDataFlowWithoutConstraintChecking(Reference ref1, Reference ref2) {
		if (!dataFlowFrom.containsKey(ref1))
			dataFlowFrom.put(ref1, new HashSet<Reference>());
		dataFlowFrom.get(ref1).add(ref2);
		
		if (!dataFlowTo.containsKey(ref2))
			dataFlowTo.put(ref2, new HashSet<Reference>());
		dataFlowTo.get(ref2).add(ref1);
	}
	
	/**
	 * Removes related data-flow links from and to a reference.
	 * @param reference
	 */
	private void removeLinksWithReference(Reference reference) {
		dataFlowFrom.remove(reference);
		dataFlowTo.remove(reference);
	}
	
	/**
	 * Adds data flows (and references) from another DataFlowManager.
	 * Also resolve any data flows that can exist between the two DataFlowManagers.
	 * @param dataFlowManager
	 */
	public void addDataFlows(DataFlowManager dataFlowManager) {
		ArrayList<Reference> referenceList1 = referenceManager.getReferenceList();
		ArrayList<Reference> referenceList2 = dataFlowManager.referenceManager.getReferenceList();
		
		this.referenceManager.addReferences(dataFlowManager.referenceManager);
		
		for (Reference ref1 : dataFlowManager.dataFlowFrom.keySet()) {
			dataFlowFrom.put(ref1, dataFlowManager.dataFlowFrom.get(ref1));
		}
		for (Reference ref1 : dataFlowManager.dataFlowTo.keySet()) {
			dataFlowTo.put(ref1, dataFlowManager.dataFlowTo.get(ref1));
		}
		
		resolveDataFlowsFromClientCodeToServerCode(referenceList1, referenceList2);
		resolveDataFlowsFromClientCodeToServerCode(referenceList2, referenceList1);
		
		removeDuplicates();
	}
	
	/*
	 * GETTING DATA FLOWS
	 */
	
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
	
	/*
	 * RESOLVING DATA FLOWS
	 */
	
	/**
	 * Resolves data flows
	 */
	public void resolveDataFlows() {
		/*
		 * Data flows within 1 language:
		 * + PHP: Done during symbolic execution
		 * + SQL: Done during symbolic execution
		 * + HTML: Done during parsing VarDOM
		 * + JavaScript: Done during parsing VarDOM. TODO The data flows within each JavaScript code fragment have been resolved,
		 * 		but those across the fragments are not yet resolved. Need to deal with this in the future.
		 */
		
		// Connecting data flows withing 1 language may produce duplicate data flows.
		// Here, we remove those duplicates. (After this step, connecting data flows across languages will not produce any duplicates.)
		removeDuplicates();
		
		/*
		 * Data flows across languages:
		 * a) Def-use:
		 * 		+ SQL -> PHP: Done during symbolic execution
		 * 		+ HTML -> JS: To do next
		 * 		+ HTML/JS -> PHP: To do next
		 * b) Info-flow:
		 * 		+ PHP -> SQL: TODO Future work
		 * 		+ JS -> HTML: TODO Future work
		 * 		+ PHP -> HTML/JS: To do next
		 */
		resolveDataFlowsFromHtmlToJavaScript();
		resolveDataFlowsFromClientCodeToServerCode();
		resolveDataFlowsFromServerCodeToClientCode();
	}
	
	/**
	 * Resolves data flows from HTML to JavaScript
	 */
	private void resolveDataFlowsFromHtmlToJavaScript() {
		ArrayList<Reference> referenceList = referenceManager.getReferenceList();
		HashMap<String, ArrayList<Reference>> referenceNameMap = getReferenceListByName(referenceList); // Use a map of reference names to speed up searching

		for (Reference ref1 : referenceList) {
			String name = ref1.getName();
			
			/*
			 * Connect HtmlFormDecl and JsRefToHtmlForm
			 */
			if (ref1 instanceof HtmlFormDecl) {
				for (Reference ref2 : referenceNameMap.get(name)) {
					if (ref2 instanceof JsRefToHtmlForm)
						addDataFlow((DeclaringReference) ref1, (RegularReference) ref2);
				}
			}
			/*
			 * Connect HtmlIdDecl and JsRefToHtmlId
			 */
			else if (ref1 instanceof HtmlIdDecl) {
				for (Reference ref2 : referenceNameMap.get(name)) {
					if (ref2 instanceof JsRefToHtmlId)
						addDataFlow((DeclaringReference) ref1, (RegularReference) ref2);
				}
			}
			/*
			 * Connect HtmlInputDecl and JsRefToHtmlInput
			 */
			else if (ref1 instanceof HtmlInputDecl) {
				for (Reference ref2 : referenceNameMap.get(name)) {
					if (ref2 instanceof JsRefToHtmlInput && compareFormName((HtmlInputDecl) ref1, (JsRefToHtmlInput) ref2))
						addDataFlow((DeclaringReference) ref1, (RegularReference) ref2);
				}
			}
			/*
			 * Connect HtmlDeclOfHtmlInputValue and JsRefToHtmlInputValue
			 */
			else if (ref1 instanceof HtmlDeclOfHtmlInputValue) {
				for (Reference ref2 : referenceNameMap.get(name)) {
					if (ref2 instanceof JsRefToHtmlInputValue
							&& compareInputName((HtmlDeclOfHtmlInputValue) ref1, (JsRefToHtmlInputValue) ref2)
							&& compareFormName((HtmlDeclOfHtmlInputValue) ref1, (JsRefToHtmlInputValue) ref2))
						addDataFlow((DeclaringReference) ref1, (RegularReference) ref2);
				}
			}
		}
	}
	
	/**
	 * Resolves data flows from the client code to the server code.
	 */
	private void resolveDataFlowsFromClientCodeToServerCode() {
		resolveDataFlowsFromClientCodeToServerCode(referenceManager.getReferenceList(), referenceManager.getReferenceList());
	}
	
	/**
	 * Resolves data flows from the client code to the server code.
	 * The two referenceLists can be the same.
	 * @param referenceListOfClientCode
	 * @param referenceListOfServerCode
	 */
	private void resolveDataFlowsFromClientCodeToServerCode(ArrayList<Reference> referenceListOfClientCode, ArrayList<Reference> referenceListOfServerCode) {
		// TODO Should we create a pseudo node to serve as the transit point of values from client code to server code?
		// That would reduce the number of edges crossing the two sides.

		HashMap<String, ArrayList<Reference>> referenceNameMap = getReferenceListByName(referenceListOfServerCode); // Use a map of reference names to speed up searching
		
		for (Reference ref1 : referenceListOfClientCode) {
			String name = ref1.getName();
			
			/*
			 * Connect HtmlQueryDecl and PhpRefToHtml
			 */
			if (ref1 instanceof HtmlQueryDecl) {
				String submitToPage = getApproxSubmitToPage((HtmlQueryDecl) ref1);
				if (referenceNameMap.containsKey(name))
					for (Reference ref2 : referenceNameMap.get(name)) {
						if (ref2 instanceof PhpRefToHtml && matchSubmitToPageToEntryFile(submitToPage, ref2.getEntryFile()))
							addCrossPageDataFlow((DeclaringReference) ref1, (PhpRefToHtml) ref2);
					}
			}
			/*
			 * Connect HtmlDeclOfHtmlInputValue and PhpRefToHtml
			 */
			else if (ref1 instanceof HtmlDeclOfHtmlInputValue) {
				String submitToPage = getApproxSubmitToPage((HtmlDeclOfHtmlInputValue) ref1);
				String inputName = ((HtmlDeclOfHtmlInputValue) ref1).getHtmlInputDecl().getName();
				
				if (referenceNameMap.containsKey(inputName))
					for (Reference ref2 : referenceNameMap.get(inputName)) {
						if (ref2 instanceof PhpRefToHtml && matchSubmitToPageToEntryFile(submitToPage, ref2.getEntryFile()))
							addCrossPageDataFlow((DeclaringReference) ref1, (PhpRefToHtml) ref2);
					}
			}
			/*
			 * Connect JsDeclOfHtmlInputValue and PhpRefToHtml
			 */
			else if (ref1 instanceof JsDeclOfHtmlInputValue) {
				HashSet<String> submitToPages = getApproxSubmitToPages((JsDeclOfHtmlInputValue) ref1);
				String inputName = ((JsDeclOfHtmlInputValue) ref1).getJsRefToHtmlInput().getName();
				
				if (referenceNameMap.containsKey(inputName))
					for (Reference ref2 : referenceNameMap.get(inputName)) {
						if (ref2 instanceof PhpRefToHtml && matchSubmitToPagesToEntryFile(submitToPages, ref2.getEntryFile()))
							addCrossPageDataFlow((DeclaringReference) ref1, (PhpRefToHtml) ref2);
					}
			}
			/*
			 * Connect HtmlInputDecl (representing value provided by user) and PhpRefToHtml
			 * TODO Need to exclude certain input types (e.g., "hidden")
			 */
			else if (ref1 instanceof HtmlInputDecl) {
				String submitToPage = getApproxSubmitToPage((HtmlInputDecl) ref1);
				String inputName = ((HtmlInputDecl) ref1).getName();
				
				if (referenceNameMap.containsKey(inputName))
					for (Reference ref2 : referenceNameMap.get(inputName)) {
						if (ref2 instanceof PhpRefToHtml && matchSubmitToPageToEntryFile(submitToPage, ref2.getEntryFile()))
							addCrossPageDataFlow((DeclaringReference) ref1, (PhpRefToHtml) ref2);
					}
			}
		}
	}
	
	private void addCrossPageDataFlow(DeclaringReference ref1, PhpRefToHtml ref2) {
		if (AnalysisConfig.CHECK_CONSTRAINTS_FOR_CROSS_PAGE_DATA_FLOWS)
			addDataFlow(ref1, ref2);
		else
			addDataFlowWithoutConstraintChecking(ref1, ref2);
	}
	
	/**
	 * Resolves data flows from the server code and the client code
	 */
	private void resolveDataFlowsFromServerCodeToClientCode() {
		/*
		 * NOTE: The correct algorithm would require every value to have an associated trace.
		 * For example: 
		 *		L1: $x = 'a';
		 *		L2: echo "<input name='input1' value='$x'";
		 * Value $x at line 2 should be ('a': $x:L2 -> $x:L1 -> 'a':L1)
		 * However, currently we don't have that trace so the algorithm below is only an approximate solution.
		 */
		ArrayList<Reference> referenceList = referenceManager.getReferenceList();
		HashMap<String, ArrayList<Reference>> referencePositionMap = getReferenceListByPosition(referenceList); // Use a map of reference positions to speed up searching
		
		for (Reference ref1 : referenceList) {
			/*
			 * Connect a PHP reference and HtmlDeclOfHtmlInputValue
			 */
			if (ref1 instanceof HtmlDeclOfHtmlInputValue) {
				HtmlAttribute attribute = ((HtmlDeclOfHtmlInputValue) ref1).getHtmlAttribute();
				
				HtmlElement htmlElement = attribute.getParentElement();
				ArrayList<HtmlAttribute> attributes = attribute.getParentElement().getAttributes();
				ArrayList<HtmlToken> endBrackets = htmlElement.getOpenTag().getEndBrackets();
				int currentIdx = attributes.indexOf(attribute);
				
				Position currentPos = attribute.getLocation().getStartPosition();
				Position nextPos = null;
				if (attribute.getAttrValEnd() != null)
					nextPos = attribute.getAttrValEnd().getLocation().getStartPosition();
				else {
					for (int i = currentIdx + 1; i < attributes.size(); i++)
						if (attributes.get(i).getLocation().getStartPosition().getOffset() > currentPos.getOffset()) {
							nextPos = attributes.get(i).getLocation().getStartPosition();
							break;
						}
					if (nextPos == null && !endBrackets.isEmpty())
						nextPos = endBrackets.get(0).getLocation().getStartPosition();
				}
				
				if (nextPos == null || !currentPos.getFile().equals(nextPos.getFile()))
					continue;
				
				File file = currentPos.getFile();
				for (int offset = currentPos.getOffset(); offset < nextPos.getOffset(); offset++) {
					Position pos = new Position(file, offset);
					if (referencePositionMap.containsKey(pos.getSignature()))
						for (Reference ref2 : referencePositionMap.get(pos.getSignature()))
							if (ref2 instanceof PhpVariableRef || ref2 instanceof PhpFunctionCall
									|| ref2 instanceof PhpRefToHtml	|| ref2 instanceof PhpRefToSqlTableColumn)
								addDataFlow((RegularReference) ref2, (DeclaringReference) ref1);
				}
			}
		}
	}
	
	/**
	 * Remove data flows that are duplicates of each other
	 */
	private void removeDuplicates() {
		/*
		 * Put references into groups (connected components through data-flow links)
		 */
		ArrayList<Reference> referenceList = referenceManager.getReferenceList();
		HashMap<Reference, Integer> refToGroupId = new HashMap<Reference, Integer>();
		HashMap<Integer, ArrayList<Reference>> groupIdToRefs = new HashMap<Integer, ArrayList<Reference>>();
		
		for (int i = 0; i < referenceList.size(); i++) {
			Reference ref = referenceList.get(i);
			refToGroupId.put(ref, i);
			groupIdToRefs.put(i, new ArrayList<Reference>());
			groupIdToRefs.get(i).add(ref);
		}
		
		for (Reference ref1 : referenceList) {
			int group1Id = refToGroupId.get(ref1);
			for (Reference ref2 : getDataFlowFrom(ref1)) {
				int group2Id = refToGroupId.get(ref2);
				if (group1Id != group2Id) {
					// Merge two groups
					ArrayList<Reference> group1 = groupIdToRefs.get(group1Id);
					for (Reference ref3 : groupIdToRefs.get(group2Id)) {
						refToGroupId.put(ref3, group1Id);
						group1.add(ref3);
					}
					groupIdToRefs.remove(group2Id);
				}
			}
		}
		
		/*
		 * If two groups have the same signature, then remove one of them.
		 */
		HashSet<String> groupSignatures = new HashSet<String>();
		for (int groupId : groupIdToRefs.keySet()) {
			ArrayList<Reference> group = groupIdToRefs.get(groupId);
			String groupSignature = computeGroupSignature(group);
			
			if (groupSignatures.contains(groupSignature))
				removeGroup(group);
			else
				groupSignatures.add(groupSignature);
		}
	}
	
	/**
	 * Computes a signature that uniquely identifies a group of references.
	 */
	private String computeGroupSignature(ArrayList<Reference> groupRefs) {
		Collections.sort(groupRefs, new Reference.ReferenceComparator(new ReferenceComparatorByType(), new ReferenceComparatorByName(), new ReferenceComparatorByPosition()));
		
		StringBuilder str = new StringBuilder();
		for (Reference ref : groupRefs) {
			str.append(ref.getType() + ref.getName() + ref.getLocation().getStartPosition().getSignature());
		}
		return str.toString();
	}
	
	/**
	 * Removes a group of references.
	 */
	private void removeGroup(ArrayList<Reference> refs) {
		for (Reference ref : refs) {
			referenceManager.removeReference(ref);
			removeLinksWithReference(ref);
		}
	}
	
	/*
	 * Utility methods
	 */
	
	/**
	 * Returns references as a map of reference names to speed up searching 
	 */
	private HashMap<String, ArrayList<Reference>> getReferenceListByName(ArrayList<Reference> references) {
		HashMap<String, ArrayList<Reference>> map = new HashMap<String, ArrayList<Reference>>(); 
		for (Reference reference : references) {
			if (!map.containsKey(reference.getName()))
				map.put(reference.getName(), new ArrayList<Reference>());
			map.get(reference.getName()).add(reference);
		}
		return map;
	}
	
	/**
	 * Returns references as a map of reference positions to speed up searching 
	 */
	private HashMap<String, ArrayList<Reference>> getReferenceListByPosition(ArrayList<Reference> references) {
		HashMap<String, ArrayList<Reference>> map = new HashMap<String, ArrayList<Reference>>(); 
		for (Reference reference : references) {
			String position = reference.getStartPosition().getSignature();
			if (!map.containsKey(position))
				map.put(position, new ArrayList<Reference>());
			map.get(position).add(reference);
		}
		return map;
	}
	
	/**
	 * Returns true if there is at least one case where both ref1 and ref2 can exist.
	 */
	private boolean constraintCompatible(Reference ref1, Reference ref2) {
		return ConstraintFactory.createAndConstraint(ref1.getConstraint(), ref2.getConstraint()).isSatisfiable();
	}

	private boolean compareFormName(HtmlInputDecl ref1, JsRefToHtmlInput ref2) {
		String formName1 = ref1.getFormName();
		String formName2 = ref2.getFormName();
		return formName1 != null && formName1.equals(formName2);
	}
	
	private boolean compareFormName(HtmlDeclOfHtmlInputValue ref1, JsRefToHtmlInputValue ref2) {
		String formName1 = ref1.getHtmlInputDecl().getFormName();
		String formName2 = ref2.getJsRefToHtmlInput().getFormName();
		return formName1 != null && formName1.equals(formName2);
	}
	
	private boolean compareInputName(HtmlDeclOfHtmlInputValue ref1, JsRefToHtmlInputValue ref2) {
		String inputName1 = ref1.getHtmlInputDecl().getName();
		String inputName2 = ref2.getJsRefToHtmlInput().getName();
		return inputName1.equals(inputName2);
	}
	
	private String getApproxSubmitToPage(HtmlQueryDecl htmlQueryDecl) {
		String submitToPage = htmlQueryDecl.getSubmitToPage();
		if (submitToPage.isEmpty())
			submitToPage = htmlQueryDecl.getEntryFile().getName();
		return submitToPage;
	}
	
	private String getApproxSubmitToPage(HtmlFormDecl htmlFormDecl) {
		String submitToPage = htmlFormDecl.getSubmitToPage();
		if (submitToPage == null || submitToPage.isEmpty())
			submitToPage = htmlFormDecl.getEntryFile().getName();
		return submitToPage;
	}
	
	private String getApproxSubmitToPage(HtmlInputDecl htmlInputDecl) {
		String submitToPage = htmlInputDecl.getSubmitToPage();
		if (submitToPage == null || submitToPage.isEmpty())
			submitToPage = htmlInputDecl.getEntryFile().getName();
		return submitToPage;
	}
	
	private String getApproxSubmitToPage(HtmlDeclOfHtmlInputValue inputValue) {
		return getApproxSubmitToPage(inputValue.getHtmlInputDecl());
	}
	
	private HashSet<String> getApproxSubmitToPages(JsDeclOfHtmlInputValue inputValue) {
		JsRefToHtmlForm form = inputValue.getJsRefToHtmlInput().getJsRefToHtmlForm();
		HashSet<String> submitToPages = new HashSet<String>();
		
		for (Reference ref : getDataFlowTo(form)) {
			HtmlFormDecl formDecl = (HtmlFormDecl) ref;
			submitToPages.add(getApproxSubmitToPage(formDecl));
		}
		
		if (submitToPages.isEmpty())
			submitToPages.add(inputValue.getEntryFile().getName());
		
		return submitToPages;
	}
	
	/**
	 * Returns true if the submitToPage matches the entryFile
	 */
	private boolean matchSubmitToPageToEntryFile(String submitToPage, File entryFile) {
		// TODO Revise this code
		if (submitToPage.startsWith("./"))
			submitToPage = submitToPage.substring("./".length());
		return entryFile.getAbsolutePath().endsWith(submitToPage);
	}
	
	/**
	 * Returns true if one of the submitToPages matches the entryFile
	 */
	private boolean matchSubmitToPagesToEntryFile(HashSet<String> submitToPages, File entryFile) {
		for (String submitToPage : submitToPages) {
			if (matchSubmitToPageToEntryFile(submitToPage, entryFile))
				return true;
		}
		return false;
	}
	
}

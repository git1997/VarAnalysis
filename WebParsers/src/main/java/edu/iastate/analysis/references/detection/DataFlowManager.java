package edu.iastate.analysis.references.detection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.iastate.analysis.references.DeclaringReference;
import edu.iastate.analysis.references.HtmlDeclOfHtmlInputValue;
import edu.iastate.analysis.references.HtmlFormDecl;
import edu.iastate.analysis.references.HtmlIdDecl;
import edu.iastate.analysis.references.HtmlInputDecl;
import edu.iastate.analysis.references.HtmlQueryDecl;
import edu.iastate.analysis.references.JsDeclOfHtmlInputValue;
import edu.iastate.analysis.references.JsFunctionCall;
import edu.iastate.analysis.references.JsFunctionDecl;
import edu.iastate.analysis.references.JsRefToHtmlForm;
import edu.iastate.analysis.references.JsRefToHtmlId;
import edu.iastate.analysis.references.JsRefToHtmlInput;
import edu.iastate.analysis.references.JsRefToHtmlInputValue;
import edu.iastate.analysis.references.PhpRefToHtml;
import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.RegularReference;

/**
 * 
 * @author HUNG
 *
 */
public class DataFlowManager {
	
	private ReferenceManager referenceManager;
	
	// List of references that have data flow from a given reference.
	// e.g., $y = $z, $x = $y  =>  The data flow is $z -> $y -> $x
	private HashMap<Reference, ArrayList<Reference>> dataFlowFrom = new HashMap<Reference, ArrayList<Reference>>();
	
	// List of references that have data flow to a given reference.
	private HashMap<Reference, ArrayList<Reference>> dataFlowTo = new HashMap<Reference, ArrayList<Reference>>();
	
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
	 * Adds data flow from a DeclaringReference to a RegularReference
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
		ArrayList<Reference> referenceList = referenceManager.getReferenceList();
		HashMap<String, ArrayList<Reference>> referenceNameMap = referenceManager.getReferenceListByName(); // Use a map of reference names to speed up searching
		
		resolveDataFlowsWithinServerCode(referenceList, referenceNameMap);
		resolveDataFlowsWithinClientCode(referenceList, referenceNameMap);
		resolveDataFlowsFromServerCodeToClientCode(referenceList, referenceNameMap);
		resolveDataFlowsFromClientCodeToServerCode(referenceList, referenceNameMap);
	}
	
	/**
	 * Resolves data flows within the server code
	 */
	private void resolveDataFlowsWithinServerCode(ArrayList<Reference> referenceList, HashMap<String, ArrayList<Reference>> referenceNameMap) {
		// Data flows within and across PHP and SQL has been automatically resolved during symbolic execution.
	}
	
	/**
	 * Resolves data flows within the client code
	 */
	private void resolveDataFlowsWithinClientCode(ArrayList<Reference> referenceList, HashMap<String, ArrayList<Reference>> referenceNameMap) {
		// No data flow within HTML
		resolveDataFlowsWithinJavaScriptCode(referenceList, referenceNameMap);
		resolveDataFlowsFromHtmlToJavaScript(referenceList, referenceNameMap);
		resolveDataFlowsFromJavaScriptToHtml(referenceList, referenceNameMap);
	}
	
	/**
	 * Resolves data flows within JavaScript code
	 */
	private void resolveDataFlowsWithinJavaScriptCode(ArrayList<Reference> referenceList, HashMap<String, ArrayList<Reference>> referenceNameMap) {
		// NOTE: The data flows within each JavaScript code fragment have been resolved but those across the fragments are not yet resolved.
		// Therefore, we connect them here. This method should not reconnect data flows within each JavaScript code fragment.
		
		for (Reference ref1 : referenceList) {
			String name = ref1.getName();
			
			/*
			 * Connect JsFunctionDecl and JsFunctionCall
			 * JavaScript functions are not yet connected, both within each code fragment and across code fragments.
			 * @see edu.iastate.analysis.references.detection.JavascriptVisitor.visit(FunctionInvocation)
			 */
			if (ref1 instanceof JsFunctionDecl) {
				for (Reference ref2 : referenceNameMap.get(name)) {
					if (ref2 instanceof JsFunctionCall)
						addDataFlow((DeclaringReference) ref1, (RegularReference) ref2);
				}
			}
		}
	}
	
	/**
	 * Resolves data flows from HTML to JavaScript
	 */
	private void resolveDataFlowsFromHtmlToJavaScript(ArrayList<Reference> referenceList, HashMap<String, ArrayList<Reference>> referenceNameMap) {
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

		// TODO Handle aliasing between HtmlDeclOfHtmlInputValue and JsDeclOfHtmlInputValue. 
	}
	
	/**
	 * Resolves data flows from JavaScript to HTML
	 */
	private void resolveDataFlowsFromJavaScriptToHtml(ArrayList<Reference> referenceList, HashMap<String, ArrayList<Reference>> referenceNameMap) {
		// TODO Handle aliasing between HtmlDeclOfHtmlInputValue and JsDeclOfHtmlInputValue.
	}
		
	/**
	 * Resolves data flows from the server code and the client code
	 */
	private void resolveDataFlowsFromServerCodeToClientCode(ArrayList<Reference> referenceList, HashMap<String, ArrayList<Reference>> referenceNameMap) {
		// TODO Handle generation-and-information-flow here.
	}
	
	/**
	 * Resolves data flows from the client code to the server code
	 */
	private void resolveDataFlowsFromClientCodeToServerCode(ArrayList<Reference> referenceList, HashMap<String, ArrayList<Reference>> referenceNameMap) {
		// TODO Should we create a pseudo node to serve as the transit point of values from client code to server code?
		// That would reduce the number of edges crossing the two sides.
		
		for (Reference ref1 : referenceList) {
			String name = ref1.getName();
			
			/*
			 * Connect HtmlQueryDecl and PhpRefToHtml
			 */
			if (ref1 instanceof HtmlQueryDecl) {
				String submitToPage = getApproxSubmitToPage((HtmlQueryDecl) ref1);
				for (Reference ref2 : referenceNameMap.get(name)) {
					if (ref2 instanceof PhpRefToHtml && matchSubmitToPageToEntryFile(submitToPage, ref2.getEntryFile()))
						addDataFlow((DeclaringReference) ref1, (RegularReference) ref2);
				}
			}
			/*
			 * Connect HtmlDeclOfHtmlInputValue and PhpRefToHtml
			 */
			else if (ref1 instanceof HtmlDeclOfHtmlInputValue) {
				String submitToPage = getApproxSubmitToPage((HtmlDeclOfHtmlInputValue) ref1);
				String inputName = ((HtmlDeclOfHtmlInputValue) ref1).getHtmlInputDecl().getName();
				
				for (Reference ref2 : referenceNameMap.get(inputName)) {
					if (ref2 instanceof PhpRefToHtml && matchSubmitToPageToEntryFile(submitToPage, ref2.getEntryFile()))
						addDataFlow((DeclaringReference) ref1, (RegularReference) ref2);
				}
			}
			/*
			 * Connect JsDeclOfHtmlInputValue and PhpRefToHtml
			 */
			else if (ref1 instanceof JsDeclOfHtmlInputValue) {
				JsDeclOfHtmlInputValue inputValue = (JsDeclOfHtmlInputValue) ref1;
				JsRefToHtmlInput input = inputValue.getJsRefToHtmlInput();
				JsRefToHtmlForm form = input.getJsRefToHtmlForm();
				String inputName = input.getName();

				HashSet<String> submitToPages = new HashSet<String>();
				for (Reference ref3 : getDataFlowTo(form)) {
					HtmlFormDecl formDecl = (HtmlFormDecl) ref3;
					submitToPages.add(getApproxSubmitToPage(formDecl));
				}
				
				for (String submitToPage : submitToPages) {
					for (Reference ref2 : referenceNameMap.get(inputName)) {
						if (ref2 instanceof PhpRefToHtml && matchSubmitToPageToEntryFile(submitToPage, ref2.getEntryFile()))
							addDataFlow((DeclaringReference) ref1, (RegularReference) ref2);
					}
				}
			}
		}
	}
	
	/*
	 * Utility methods
	 */

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
	
	private String getApproxSubmitToPage(HtmlDeclOfHtmlInputValue inputValue) {
		String submitToPage = inputValue.getHtmlInputDecl().getSubmitToPage();
		if (submitToPage == null || submitToPage.isEmpty())
			submitToPage = inputValue.getEntryFile().getName();
		return submitToPage;
	}
	
	/**
	 * Returns true if the submitToPage matches the entryFile
	 */
	private static boolean matchSubmitToPageToEntryFile(String submitToPage, File entryFile) {
		// TODO Revise this code
		return entryFile.getAbsolutePath().endsWith(submitToPage);
	}
	
}

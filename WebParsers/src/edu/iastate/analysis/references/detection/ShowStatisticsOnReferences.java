package edu.iastate.analysis.references.detection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.ReferenceManager;

/**
 * 
 * @author HUNG
 *
 */
public class ShowStatisticsOnReferences {
	
	private String[] referenceTypes = {
		"PhpVariableDecl",
		"PhpVariableRef",
		"PhpRefToHtml",
		"HtmlFormDecl",
		"HtmlInputDecl",
		"HtmlIdDecl",
		"HtmlQueryDecl",
		"JsRefToHtmlForm",
		"JsRefToHtmlInput",
		"JsRefToHtmlId",
		"JsObjectFieldDecl",
		"JsObjectFieldRef",
		"JsFunctionDecl",
		"JsFunctionCall",
		"JsVariableDecl",
		"JsVariableRef",
		"SqlTableColumnDecl",
		"PhpRefToSqlTableColumn",
	};
	
	public String showStatistics(ReferenceManager referenceManager) {
		// TODO Review this code
		HashMap<String, ArrayList<Reference>> referenceLists = new HashMap<String, ArrayList<Reference>>();
		for (String type : referenceTypes)
			referenceLists.put(type, new ArrayList<Reference>());
		
		ArrayList<Reference> references = referenceManager.getReferenceList();
		for (Reference ref : references) {
			String type = ref.getClass().getSimpleName();
			referenceLists.get(type).add(ref);
		}
		
		StringBuilder results = new StringBuilder();
		results.append("Total refs: " + references.size() + System.lineSeparator());
		for (String type : referenceTypes) {
			ArrayList<Reference> referenceList = referenceLists.get(type);
			results.append(type + ": " + referenceList.size() + System.lineSeparator());
			results.append(showStatisticsForReferenceType(referenceList));
		}
		return results.toString();
	}
	
	public String showStatisticsForReferenceType(ArrayList<Reference> references) {
		StringBuilder results = new StringBuilder();
		int dataflowSizeTotal = 0;
		int dataflowLengthTotal = 0;
		int cycleCount = 0;
		int crossFilesCount = 0;
		int crossLanguagesCount = 0;
		
		for (Reference reference : references) {
			HashSet<Reference> dataflowFromRefs = new HashSet<Reference>();
			dataflowFromRefs.add(reference);
			
			HashSet<Reference> newRefs = new HashSet<Reference>();
			newRefs.add(reference);
			
			int length = 0;
			boolean hasCycle = false;
			while (true) {
				HashSet<Reference> nextRefs = new HashSet<Reference>();
				for (Reference ref : newRefs)
					nextRefs.addAll(ref.getDataflowFromReferences());
				
				if (nextRefs.removeAll(dataflowFromRefs))
					hasCycle = true;
				newRefs = nextRefs;
				dataflowFromRefs.addAll(newRefs);
				if (newRefs.isEmpty())
					break;
				else
					length++;
			}
			dataflowSizeTotal += dataflowFromRefs.size();
			dataflowLengthTotal += length;
			if (hasCycle)
				cycleCount++;
			
			if (checkCrossFiles(dataflowFromRefs))
				crossFilesCount++;
			if (checkCrossLanguages(dataflowFromRefs))
				crossLanguagesCount++;
		}
		if (references.size() > 0) {
			results.append("\tAvg dataflow size: " + (float) dataflowSizeTotal / references.size() + System.lineSeparator());
			results.append("\tAvg dataflow length: " + (float) dataflowLengthTotal / references.size() + System.lineSeparator());
			results.append("\tNum cyclic slices: " + cycleCount + System.lineSeparator());
			results.append("\tNum cross-files: " + crossFilesCount + System.lineSeparator());
			results.append("\tNum cross-languages: " + crossLanguagesCount + System.lineSeparator());
		}
		return results.toString();
	}

	private boolean checkCrossFiles(HashSet<Reference> refs) {
		Reference ref = refs.iterator().next();
		String file = ref.getStartPosition().getFilePath();
		for (Reference ref2 : refs)
			if (!ref2.getStartPosition().getFilePath().equals(file))
				return true;
		return false;
	}
	
	private boolean checkCrossLanguages(HashSet<Reference> refs) {
		Reference ref = refs.iterator().next();
		String lang = ref.getClass().getSimpleName().substring(0, 2);
		for (Reference ref2 : refs)
			if (!ref2.getClass().getSimpleName().substring(0, 2).equals(lang))
				return true;
		return false;
	}
	
}

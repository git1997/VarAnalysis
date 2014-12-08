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
 * TODO Review this code
 */
public class ShowStatisticsOnReferences {
	
	private String[] referenceTypes = {
		"PhpVariableDecl",
		"PhpVariableRef",
		"PhpRefToHtml",
		"PhpFunctionDecl",
		"PhpFunctionCall",
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
		return showStatistics(referenceManager.getReferenceList());
	}
	
	public String showStatistics(ArrayList<Reference> references) {
		HashMap<String, ArrayList<Reference>> referenceLists = new HashMap<String, ArrayList<Reference>>();
		for (String type : referenceTypes)
			referenceLists.put(type, new ArrayList<Reference>());
		
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
		results.append("Total refs: " + references.size() + System.lineSeparator());
		results.append(showStatisticsForReferenceType(references));
		return results.toString();
	}
	
	public String showStatisticsForReferenceType(ArrayList<Reference> references) {
		StringBuilder strBuilder = new StringBuilder();
		
		strBuilder.append("(Including size-1 slices)" + System.lineSeparator());
		strBuilder.append(showStatisticsForReferenceType_(references));
		
		strBuilder.append("(Excluding size-1 slices)" + System.lineSeparator());
		ArrayList<Reference> refs = new ArrayList<Reference>();
		for (Reference ref : references)
			if (ref.getDataFlowToReferences().size() > 0)
				refs.add(ref);
		strBuilder.append(showStatisticsForReferenceType_(refs));
		
		return strBuilder.toString();
	}
		
	public String showStatisticsForReferenceType_(ArrayList<Reference> references) {
		StringBuilder results = new StringBuilder();
		
		int totalSlices = references.size();
		int size1SliceCount = 0;
		
		int phpEntities = 0;
		int embeddedEntities = 0;
		
		int sliceSizeTotal = 0;
		int sliceLengthTotal = 0;
		int cycleCount = 0;
		int crossFilesCount = 0;
		int crossLanguagesCount = 0;
		int crossStringsCount = 0;
		int crossFunctionsCount = 0;
		
		ArrayList<Integer> sliceSizes = new ArrayList<Integer>();
		
		for (Reference reference : references) {
			if (reference.getType().startsWith("Php"))
				phpEntities++;
			else
				embeddedEntities++;
			
			HashSet<Reference> slice = new HashSet<Reference>();
			
			HashSet<Reference> unvisitedRefs = new HashSet<Reference>();
			unvisitedRefs.add(reference);
			
			int length = 0;
			boolean hasCycle = false;
			
			while (!unvisitedRefs.isEmpty()) {
				slice.addAll(unvisitedRefs);
				length++;
				
				HashSet<Reference> nextRefs = new HashSet<Reference>();
				for (Reference ref : unvisitedRefs)
					nextRefs.addAll(ref.getDataFlowToReferences());
				
				if (nextRefs.removeAll(slice))
					hasCycle = true;
				unvisitedRefs = nextRefs;
			}
			
			if (slice.size() == 1)
				size1SliceCount++;
			
			sliceSizes.add(slice.size());
			
			sliceSizeTotal += slice.size();
			sliceLengthTotal += length;
			if (hasCycle)
				cycleCount++;
			
			if (checkCrossFiles(slice))
				crossFilesCount++;
			if (checkCrossLanguages(slice))
				crossLanguagesCount++;
			if (checkCrossStrings(slice))
				crossStringsCount++;
			if (checkCrossFunctions(slice))
				crossFunctionsCount++;
		}
		if (totalSlices > 0) {
			results.append("\tNum slices: " + totalSlices + System.lineSeparator());
			results.append("\tNum slice size 1: " + size1SliceCount + System.lineSeparator());
			results.append("\tNum slice size > 1: " + (totalSlices - size1SliceCount) + System.lineSeparator());
			results.append("\tNum PHP entities: " + phpEntities + System.lineSeparator());
			results.append("\tNum Embedded entities: " + embeddedEntities + System.lineSeparator());
			results.append("\tAvg dataflow size: " + (float) (sliceSizeTotal) / (totalSlices) + System.lineSeparator());
			results.append("\tAvg dataflow length: " + (float) (sliceLengthTotal) / (totalSlices) + System.lineSeparator());
			results.append("\tNum cyclic slices: " + cycleCount + System.lineSeparator());
			results.append("\tNum cross-languages: " + crossLanguagesCount + System.lineSeparator());
			results.append("\tNum cross-strings: " + crossStringsCount + System.lineSeparator());
			results.append("\tNum cross-files: " + crossFilesCount + System.lineSeparator());
			results.append("\tNum cross-functions: " + crossFunctionsCount + System.lineSeparator());
			results.append("\t");
			for (int size : sliceSizes)
				results.append(size + " " );
			results.append(System.lineSeparator());
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
	
	private boolean checkCrossStrings(HashSet<Reference> refs) {
		return checkCrossFiles(refs) || checkCrossLanguages(refs);
	}
	
	private boolean checkCrossFunctions(HashSet<Reference> refs) {
		boolean hasFunctionCall = false;
		boolean hasFunctionDecl = false;
		
		for (Reference reference : refs) {
			if (reference.getType().contains("FunctionCall"))
				hasFunctionCall = true;
			if (reference.getType().contains("FunctionDecl"))
				hasFunctionDecl = true;
		}
		
		return hasFunctionCall && hasFunctionDecl;
	}
	
}

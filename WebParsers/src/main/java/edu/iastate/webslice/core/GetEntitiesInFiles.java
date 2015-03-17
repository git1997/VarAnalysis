package edu.iastate.webslice.core;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.detection.ReferenceDetector;
import edu.iastate.analysis.references.detection.ReferenceManager;

/**
 * 
 * @author HUNG
 *
 */
public class GetEntitiesInFiles {
	
	/**
	 * Main method.
	 */
	public static void main(String[] args) {
		String projectPath = SubjectSystems.projectPath;
		List<String> entries = SubjectSystems.projectEntries;
		
		ReferenceManager referenceManager = new ReferenceManager();
		for (String entry : entries) {
			ReferenceManager refManager = new ReferenceDetector().detect(new File(projectPath, entry));
			referenceManager.getDataFlowManager().addDataFlows(refManager.getDataFlowManager());
		}
		
		HashMap<File, Integer> htmlEntitiesInFiles = new HashMap<File, Integer>();
		HashMap<File, Integer> jsEntitiesInFiles = new HashMap<File, Integer>();
		
		for (Reference reference : referenceManager.getReferenceList()) {
			File file = reference.getLocation().getStartPosition().getFile();
			
			if (getLanguage(reference).equals("HTML")) {
				if (!htmlEntitiesInFiles.containsKey(file))
					htmlEntitiesInFiles.put(file, 0);
				htmlEntitiesInFiles.put(file, htmlEntitiesInFiles.get(file) + 1);
			}
			
			if (getLanguage(reference).equals("JS")) {
				if (!jsEntitiesInFiles.containsKey(file))
					jsEntitiesInFiles.put(file, 0);
				jsEntitiesInFiles.put(file, jsEntitiesInFiles.get(file) + 1);
			}
		}
		
		int maxHtmlEntitiesInFile = 0;
		int maxJsEntitiesInFile = 0;
		
		for (Integer cnt : htmlEntitiesInFiles.values()) {
			if (cnt > maxHtmlEntitiesInFile)
				maxHtmlEntitiesInFile = cnt;
		}
		for (Integer cnt : jsEntitiesInFiles.values()) {
			if (cnt > maxJsEntitiesInFile)
				maxJsEntitiesInFile = cnt;
		}
		
		System.out.println(projectPath);
		System.out.println("Max HTML entities in a file: " + maxHtmlEntitiesInFile);
		System.out.println("Max JS entities in a file: " + maxJsEntitiesInFile);
	}
	
	private static String getLanguage(Reference reference) {
		if (reference.getType().startsWith("Php"))
			return "PHP";
		else if (reference.getType().startsWith("Sql"))
			return "SQL";
		else if (reference.getType().startsWith("Html"))
			return "HTML";
		else if (reference.getType().startsWith("Js"))
			return "JS";
		else
			return "Unknown";
	}

}

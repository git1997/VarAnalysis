package edu.iastate.webtesting.bugcoverage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.iastate.symex.util.FileIO;
import edu.iastate.webtesting.evaluation.DebugInfo;
import edu.iastate.webtesting.util_clone.Config;

/**
 * 
 * @author HUNG
 *
 */
public class ComputePhpBugCoverage {
	
	private static final String PHP_ERRORS_RAW_FILE = "/Users/HUNG/Desktop/Web Testing Project/Data/" + Config.SUBJECT_SYSTEM + "/PHP Errors/php-errors-raw.txt";
	private static final String PHP_ERRORS_FILE = "/Users/HUNG/Desktop/Web Testing Project/Data/" + Config.SUBJECT_SYSTEM + "/PHP Errors/php-errors.txt";

	private static Map<String, String> locationToError = new HashMap<String, String>(); // Assuming each location has 1 error
	
	static {
		if (!new File(PHP_ERRORS_FILE).exists()) {
			System.err.println("In ComputePhpBugCoverage.java: Need to create PHP error file first.");
			System.exit(0);
		}
		
		for (String line : FileIO.readLinesFromFile(PHP_ERRORS_FILE)) {
			String location = line.substring(0, line.indexOf(':'));
			String error = line.substring(line.indexOf(':') + 2);
			locationToError.put(location, error);
		}
	}
	
	public static void main(String[] args) {
		Map<String, String> locationToError = new HashMap<String, String>();
		for (String line : FileIO.readLinesFromFile(PHP_ERRORS_RAW_FILE)) {
			String error = line.substring(line.indexOf("] ") + 2, line.lastIndexOf("in /"));
			String file = line.substring(line.lastIndexOf(Config.SUBJECT_SYSTEM) + Config.SUBJECT_SYSTEM.length() + 1, line.lastIndexOf(" on line "));
			String lineNumber = line.substring(line.lastIndexOf(" on line ") + " on line ".length()); 
			
			String location = file + "@" + lineNumber;
			locationToError.put(location, error);
		}

		List<String> errorLocations = new ArrayList<String>(locationToError.keySet());
		Collections.sort(errorLocations);
		
		StringBuilder allErrors = new StringBuilder();
		for (String errorLocation : errorLocations) {
			allErrors.append(errorLocation + ": " + locationToError.get(errorLocation) + System.lineSeparator());
		}
		
		System.out.println(allErrors.toString());
		FileIO.writeStringToFile(allErrors.toString(), PHP_ERRORS_FILE);
	}
	
	public Set<String> compute(String trace) {
		Set<String> bugs = new HashSet<String>();
		
		String[] locations = trace.split("\r?\n");
		for (String location : locations) {
			if (locationToError.containsKey(location))
				bugs.add(location + ": " + locationToError.get(location));
		}
		
		// For debugging
		DebugInfo.phpBugCoverageComputed(bugs);
		
		return bugs;
	}
}

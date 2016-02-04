package edu.iastate.webtesting.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.iastate.symex.datamodel_modified.ReadWriteDataModelCoverageToFromXml;
import edu.iastate.symex.util.FileIO;
import edu.iastate.webtesting.outputcoverage.CModelCoverage;
import edu.iastate.webtesting.outputcoverage.OutputCoverage;
import edu.iastate.webtesting.outputcoverage.DataModelCoverage;

/**
 * 
 * @author HUNG
 *
 */
public class DebugInfo {
	
	public static boolean DEBUG = false; // Set to true to print debugging info
	
	static {
		if (DEBUG)
			FileIO.cleanFolder(Config2.DEBUG_INFO_FOLDER);
	}
	
	private static int currentTestId = 0;
	
	public static void setCurrentTestId(int currentTestId) {
		DebugInfo.currentTestId = currentTestId;
	}
	
	public static void outputCoverageComputed(OutputCoverage outputCoverage) {
		if (!DEBUG)
			return;
		
		DataModelCoverage dataModelCoverage = outputCoverage.getDataModelCoverage();
		CModelCoverage cModelCoverage = outputCoverage.getCModelCoverage();
		new ReadWriteDataModelCoverageToFromXml().writeDataModelCoverageToXmlFile(dataModelCoverage, Config2.DEBUG_INFO_FOLDER + "/[" + currentTestId + "] " + Config2.OUTPUT_COVERAGE_DATA_MODEL_FILE);
		FileIO.writeStringToFile(cModelCoverage.toDebugString(), Config2.DEBUG_INFO_FOLDER + "/[" + currentTestId + "] " + Config2.OUTPUT_COVERAGE_CMODEL_FILE);
	}
	
	public static void stringCoverageComputed(Set<String> stringCoverage) {
		if (!DEBUG)
			return;
		
		List<String> literals = new ArrayList<String>();
		for (String literal : stringCoverage) {
			literals.add(literal.replace("\r", "").replace("\n", " "));
		}
		List<String> sortedLiterals = new ArrayList<String>(literals);
		Collections.sort(sortedLiterals);
		FileIO.writeStringToFile(Utils.listToString(sortedLiterals), Config2.DEBUG_INFO_FOLDER + "/[" + currentTestId + "] " + Config2.STRING_COVERAGE_FILE);
	}
	
	public static void decisionCoverageComputed(Set<String> decisions) {
		if (!DEBUG)
			return;
		
		List<String> sortedDecisions = new ArrayList<String>(decisions);
		Collections.sort(sortedDecisions);
		FileIO.writeStringToFile(Utils.listToString(sortedDecisions), Config2.DEBUG_INFO_FOLDER + "/[" + currentTestId + "] " + Config2.DECISION_COVERAGE_FILE);
	}

	public static void statementCoverageComputed(Set<String> statements) {
		if (!DEBUG)
			return;
		
		List<String> sortedStatements = new ArrayList<String>(statements);
		Collections.sort(sortedStatements);
		FileIO.writeStringToFile(Utils.listToString(sortedStatements), Config2.DEBUG_INFO_FOLDER + "/[" + currentTestId + "] " + Config2.STATEMENT_COVERAGE_FILE);
	}
	
	public static void branchCoverageComputed(Set<String> branches) {
		if (!DEBUG)
			return;
		
		List<String> sortedBranches = new ArrayList<String>(branches);
		Collections.sort(sortedBranches);
		FileIO.writeStringToFile(Utils.listToString(sortedBranches), Config2.DEBUG_INFO_FOLDER + "/[" + currentTestId + "] " + Config2.BRANCH_COVERAGE_FILE);
	}
	
	public static void validationBugCoverageComputed(List<String> clientBugs, List<String> serverBugs, Set<String> bugs) {
		if (!DEBUG)
			return;
		
		List<String> sortedBugs = new ArrayList<String>(bugs);
		Collections.sort(sortedBugs);
		FileIO.writeStringToFile(Utils.listToString(clientBugs), Config2.DEBUG_INFO_FOLDER + "/[" + currentTestId + "] " + Config2.VALIDATION_BUG_COVERAGE_CLIENT_FILE);
		FileIO.writeStringToFile(Utils.listToString(serverBugs), Config2.DEBUG_INFO_FOLDER + "/[" + currentTestId + "] " + Config2.VALIDATION_BUG_COVERAGE_SERVER_FILE);
		FileIO.writeStringToFile(Utils.listToString(sortedBugs), Config2.DEBUG_INFO_FOLDER + "/[" + currentTestId + "] " + Config2.VALIDATION_BUG_COVERAGE_FILE);
	}
	
	public static void spellingBugCoverageComputed(List<String> clientBugs, List<String> serverBugs, Set<String> bugs) {
		if (!DEBUG)
			return;
		
		List<String> sortedBugs = new ArrayList<String>(bugs);
		Collections.sort(sortedBugs);
		FileIO.writeStringToFile(Utils.listToString(clientBugs), Config2.DEBUG_INFO_FOLDER + "/[" + currentTestId + "] " + Config2.SPELLING_BUG_COVERAGE_CLIENT_FILE);
		FileIO.writeStringToFile(Utils.listToString(serverBugs), Config2.DEBUG_INFO_FOLDER + "/[" + currentTestId + "] " + Config2.SPELLING_BUG_COVERAGE_SERVER_FILE);
		FileIO.writeStringToFile(Utils.listToString(sortedBugs), Config2.DEBUG_INFO_FOLDER + "/[" + currentTestId + "] " + Config2.SPELLING_BUG_COVERAGE_FILE);
	}
	
	public static void phpBugCoverageComputed(Set<String> bugs) {
		if (!DEBUG)
			return;
		
		List<String> sortedBugs = new ArrayList<String>(bugs);
		Collections.sort(sortedBugs);
		FileIO.writeStringToFile(Utils.listToString(sortedBugs), Config2.DEBUG_INFO_FOLDER + "/[" + currentTestId + "] " + Config2.PHP_BUG_COVERAGE_FILE);
	}
}

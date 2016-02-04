package edu.iastate.webtesting.evaluation;

import java.io.File;
import java.util.List;
import java.util.Set;

import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.ReadWriteDataModelToFromXml;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.util.FileIO;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.webtesting.bugcoverage.ComputePhpBugCoverage;
import edu.iastate.webtesting.bugcoverage.ComputeSpellingBugCoverage;
import edu.iastate.webtesting.bugcoverage.ComputeValidationBugCoverage;
import edu.iastate.webtesting.codecoverage.ComputeBranchCoverage;
import edu.iastate.webtesting.codecoverage.ComputeStatementCoverage;
import edu.iastate.webtesting.outputcoverage.ComputeDecisionCoverage;
import edu.iastate.webtesting.outputcoverage.ComputeOutputCoverage;
import edu.iastate.webtesting.outputcoverage.ComputeStringCoverage;
import edu.iastate.webtesting.outputcoverage.OutputCoverage;
import edu.iastate.webtesting.tests.TestCase;
import edu.iastate.webtesting.tests.TestSuite;
import edu.iastate.webtesting.tests.TestUniverse;
import edu.iastate.webtesting.util_clone.Config;
import edu.iastate.webtesting.util_clone.XmlReadWrite;
import edu.iastate.webtesting.values_clone.CondValue;

/**
 * 
 * @author HUNG
 *
 */
public class Evaluation {
	
	private static TestUniverse testUniverse = new TestUniverse();
	
	/**
	 * Main entry
	 */
	public static void main(String[] args) {
		readTestUniverse();
		//checkMappingAccuracy();
		
		//computeCorrelationsForSingleTestCases();
		//computeCorrelationsForRandomTestSuitesBySize();
		//compareBests();
	}
	
	private static void readTestUniverse() {
		FileIO.TURN_CACHE_ON = false; // Out of memory error if using cache
		DataModel dataModel = new ReadWriteDataModelToFromXml().readDataModelFromXmlFile(Config2.DATA_MODEL_FILE);
		
		for (File testCaseFolder : new File(Config.TEST_CASE_FOLDER).listFiles()) {
			if (testCaseFolder.getName().startsWith(".") || testCaseFolder.getName().startsWith("[IGNORED"))
				continue;
			
			MyLogger.log(testCaseFolder.getAbsolutePath());
			
			// Read raw data
			int testId = Integer.valueOf(testCaseFolder.getName().substring(testCaseFolder.getName().indexOf('[') + 1, testCaseFolder.getName().indexOf(']')));
			String input = FileIO.readStringFromFile(testCaseFolder + "/" + Config.INPUT_TXT_FILE);
			String output = FileIO.readStringFromFile(testCaseFolder + "/" + Config.OUTPUT_HTML_FILE);
			CondValue cModel = new XmlReadWrite().readCondValueFromXmlFile(new File(testCaseFolder + "/" + Config.CMODEL_XML_FILE));
			String trace = FileIO.readStringFromFile(testCaseFolder + "/" + Config.TRACE_TXT_FILE);
			String branches = FileIO.readStringFromFile(testCaseFolder + "/" + Config.BRANCHES_TXT_FILE);
			
			// For debugging
			DebugInfo.setCurrentTestId(testId);
			
			// Compute output coverage, including data model coverage and cModel coverage
			MyLogger.setLevel(MyLevel.OFF);
			OutputCoverage outputCoverage = new ComputeOutputCoverage().compute(cModel, dataModel, input);
			Set<LiteralNode> dataModelCoverage = outputCoverage.getDataModelCoverage().getMappedLiteralNodes();
			float cModelCoverageMetric = outputCoverage.getCModelCoverage().getMappedRatio();
			MyLogger.setLevel(MyLevel.ALL);
			
			// Compute string coverage
			Set<String> stringCoverage = new ComputeStringCoverage().compute(cModel);
			
			// Compute decision coverage
			Set<String> decisionCoverage = new ComputeDecisionCoverage().compute(outputCoverage.getDataModelCoverage()); 
			
			// Compute statement coverage
			Set<String> statementCoverage = new ComputeStatementCoverage().compute(trace);
			
			// Compute branch coverage
			Set<String> branchCoverage = new ComputeBranchCoverage().compute(branches);
			
			// Compute validation bug coverage
			Set<String> validationBugCoverage = new ComputeValidationBugCoverage().compute(output, cModel);
			
			// Compute spelling bug coverage
			Set<String> spellingBugCoverage = new ComputeSpellingBugCoverage().compute(output, cModel);
			
			// Compute PHP bug coverage
			Set<String> phpBugCoverage = new ComputePhpBugCoverage().compute(trace);
			
			// Update test universe
			TestCase testCase = new TestCase(testId, input, dataModelCoverage, cModelCoverageMetric, stringCoverage, decisionCoverage, statementCoverage, branchCoverage, validationBugCoverage, spellingBugCoverage, phpBugCoverage);
			testUniverse.addTestCase(testCase);
			
			MyLogger.log("\tData Model Coverage: " + dataModelCoverage.size() + " nodes " + testCase.getDataModelCoverageMetric() + " characters");
			MyLogger.log("\tString Coverage: " + stringCoverage.size() + " nodes " + testCase.getStringCoverageMetric() + " characters");
			MyLogger.log("\tDecision Coverage: " + decisionCoverage.size());
			MyLogger.log("\tStatement Coverage: " + statementCoverage.size());
			MyLogger.log("\tBranch Coverage: " + branchCoverage.size());
			MyLogger.log("\tValidation Bug Coverage: " + validationBugCoverage.size());
			MyLogger.log("\tSpelling Bug Coverage: " + spellingBugCoverage.size());
			MyLogger.log("\tPHP Bug Coverage: " + phpBugCoverage.size());
		}
		
		List<LiteralNode> allLiteralNodes = Utils.getSortedLiteralNodesInDataModel(dataModel);
		MyLogger.log("Size of DataModel: " + allLiteralNodes.size() + " nodes " + Utils.countStringLengthOfLiteralNodes(allLiteralNodes) + " characters");
		MyLogger.log("Total Data Model Coverage: " + testUniverse.getDataModelCoverage().size() + " nodes " + testUniverse.getDataModelCoverageMetric() + " characters ");
		MyLogger.log("Total String Coverage: " + testUniverse.getStringCoverage().size() + " nodes " + testUniverse.getStringCoverageMetric() + " characters");
		MyLogger.log("Total Decision Coverage: " + testUniverse.getDecisionCoverage().size());
		MyLogger.log("Total Statement Coverage: " + testUniverse.getStatementCoverage().size());
		MyLogger.log("Total Branch Coverage: " + testUniverse.getBranchCoverage().size());
		MyLogger.log("Total Validation Bug Coverage: " + testUniverse.getValidationBugCoverage().size());
		MyLogger.log("Total Spelling Bug Coverage: " + testUniverse.getSpellingBugCoverage().size());
		MyLogger.log("Total PHP Bug Coverage: " + testUniverse.getPhpBugCoverage().size());
	}
	
	@SuppressWarnings("unused")
	private static void checkMappingAccuracy() {
		MyLogger.log("Test ID\tCModel Cov\t\tDataModelCov / StringCov");
		for (TestCase testCase : testUniverse.getSortedTestCases()) {
			float ratio = (float) testCase.getDataModelCoverageMetric() / testCase.getStringCoverageMetric();
			MyLogger.log(String.format("%4d\t%.2f%15s\t%.2f%19s",
				testCase.getTestId(),
				testCase.getCModelCoverageMetric(), (testCase.getCModelCoverageMetric() < 0.4 ? "[LOW COVERAGE]" : ""),
				ratio, (ratio < 0.4 || ratio > 2.5 ? "[LARGE DIFFERENCE]" : "")));
		}
	}
	
	@SuppressWarnings("unused")
	private static void computeCorrelationsForSingleTestCases() {
		MyLogger.log("Test ID\tData Model Cov\tString Cov\tStatement Cov\tDecision Cov\tBranch Cov\tValBug Cov\tSpellBug Cov\tPhpBug Cov");
		for (TestCase testCase : testUniverse.getSortedTestCases()) {
			MyLogger.log(testCase.getTestId() + "\t" + testCase.getDataModelCoverageMetric() + "\t" + testCase.getStringCoverageMetric() + "\t" + testCase.getStatementCoverage().size() + "\t" + testCase.getDecisionCoverage().size() + "\t" + testCase.getBranchCoverage().size() + "\t" + testCase.getValidationBugCoverage().size() + "\t" + testCase.getSpellingBugCoverage().size() + "\t" + testCase.getPhpBugCoverage().size());
		}
	}
	
	@SuppressWarnings("unused")
	private static void computeCorrelationsForRandomTestSuitesBySize() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("Test Size\tData Model Cov\tString Cov\tStatement Cov\tDecision Cov\tBranch Cov\tValBug Cov\tSpellBug Cov\tPhpBug Cov" + System.lineSeparator());
		for (int i = 1; i <= testUniverse.size(); i++) {
			MyLogger.log("Select random test suites of size: " + i);
			for (int j = 1; j <= 100; j++) {
				TestSuite testSuite = testUniverse.selectRandomTestSuiteBySize(i);
				strBuilder.append(testSuite.size() + "\t" + testSuite.getDataModelCoverageMetric() + "\t" + testSuite.getStringCoverageMetric() + "\t" + testSuite.getStatementCoverage().size() + "\t" + testSuite.getDecisionCoverage().size() + "\t" + testSuite.getBranchCoverage().size() + "\t" + testSuite.getValidationBugCoverage().size() + "\t" + testSuite.getSpellingBugCoverage().size() + "\t" + testSuite.getPhpBugCoverage().size() + System.lineSeparator());
			}
		}
		String resultsFile = "/Users/HUNG/Desktop/results.txt";
		MyLogger.log("Writing results to " + resultsFile);
		FileIO.writeStringToFile(strBuilder.toString(), resultsFile);
	}
	
	@SuppressWarnings("unused")
	private static void compareBests() {
		TestSuite testSuiteForBestDataModelCoverage = new TestSuite();
		TestSuite testSuiteForBestStringCoverage = new TestSuite();
		TestSuite testSuiteForBestStatementCoverage = new TestSuite();
		
		MyLogger.log("Test Size\tData Model Cov\tValBug Cov\tSpellBug Cov\tPhpBug Cov\tStatement Cov\t\tString Cov\tValBug Cov\tSpellBug Cov\tPhpBug Cov\tStatement Cov\t\tStatement Cov\tValBug Cov\tSpellBug Cov\tPhpBug Cov\tData Model Cov\tString Cov");
		for (int k = 1; k <= testUniverse.size(); k++) {
			testSuiteForBestDataModelCoverage = testUniverse.selectNextTestForBestDataModelCoverage(testSuiteForBestDataModelCoverage);
			testSuiteForBestStringCoverage = testUniverse.selectNextTestForBestStringCoverage(testSuiteForBestStringCoverage);
			testSuiteForBestStatementCoverage = testUniverse.selectNextTestForBestStatementCoverage(testSuiteForBestStatementCoverage);
			
			MyLogger.log(k + "\t" 
					+ testSuiteForBestDataModelCoverage.getDataModelCoverageMetric() + "\t" + testSuiteForBestDataModelCoverage.getValidationBugCoverage().size() + "\t" + testSuiteForBestDataModelCoverage.getSpellingBugCoverage().size() + "\t" + testSuiteForBestDataModelCoverage.getPhpBugCoverage().size() + "\t" + testSuiteForBestDataModelCoverage.getStatementCoverage().size() + "\t\t"
					+ testSuiteForBestStringCoverage.getStringCoverageMetric() + "\t" + testSuiteForBestStringCoverage.getValidationBugCoverage().size() + "\t" + testSuiteForBestStringCoverage.getSpellingBugCoverage().size() + "\t" + testSuiteForBestStringCoverage.getPhpBugCoverage().size() + "\t" + testSuiteForBestStringCoverage.getStatementCoverage().size() + "\t\t" 
					+ testSuiteForBestStatementCoverage.getStatementCoverage().size() + "\t" + testSuiteForBestStatementCoverage.getValidationBugCoverage().size() + "\t" + testSuiteForBestStatementCoverage.getSpellingBugCoverage().size() + "\t" + testSuiteForBestStatementCoverage.getPhpBugCoverage().size() + "\t" + testSuiteForBestStatementCoverage.getDataModelCoverageMetric() + "\t" + testSuiteForBestStatementCoverage.getStringCoverageMetric());
		}
	}
}

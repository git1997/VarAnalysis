package edu.iastate.webtesting.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.webtesting.evaluation.Utils;

/**
 * 
 * @author HUNG
 *
 */
public class TestSuite {
	
	protected Set<TestCase> testCases = new HashSet<TestCase>();
	
	private Set<LiteralNode> dataModelCoverage = new HashSet<LiteralNode>();
	private int dataModelCoverageMetric = 0;
	
	private Set<String> stringCoverage = new HashSet<String>();
	private int stringCoverageMetric = 0;
	
	private Set<String> decisionCoverage = new HashSet<String>();
	
	private Set<String> statementCoverage = new HashSet<String>();
	private Set<String> branchCoverage = new HashSet<String>();
	
	private Set<String> validationBugCoverage = new HashSet<String>();
	private Set<String> spellingBugCoverage = new HashSet<String>();
	private Set<String> phpBugCoverage = new HashSet<String>();
	
	public TestSuite clone() {
		TestSuite cloneTestSuite = new TestSuite();
		cloneTestSuite.testCases = new HashSet<TestCase>(testCases);
		cloneTestSuite.dataModelCoverage = new HashSet<LiteralNode>(dataModelCoverage);
		cloneTestSuite.dataModelCoverageMetric = dataModelCoverageMetric;
		cloneTestSuite.stringCoverage = new HashSet<String>(stringCoverage);
		cloneTestSuite.stringCoverageMetric = stringCoverageMetric;
		cloneTestSuite.decisionCoverage = new HashSet<String>(decisionCoverage);
		cloneTestSuite.statementCoverage = new HashSet<String>(statementCoverage);
		cloneTestSuite.branchCoverage = new HashSet<String>(branchCoverage);
		cloneTestSuite.validationBugCoverage = new HashSet<String>(validationBugCoverage);
		cloneTestSuite.spellingBugCoverage = new HashSet<String>(spellingBugCoverage);
		cloneTestSuite.phpBugCoverage = new HashSet<String>(phpBugCoverage);
		return cloneTestSuite;
	}
	
	public void addTestCase(TestCase testCase) {
		testCases.add(testCase);
		
		Set<LiteralNode> newLiteralNodes = testCase.getDataModelCoverage();
		newLiteralNodes.removeAll(dataModelCoverage);
		dataModelCoverage.addAll(newLiteralNodes);
		dataModelCoverageMetric += Utils.countStringLengthOfLiteralNodes(newLiteralNodes);
		
		Set<String> newLiterals = testCase.getStringCoverage();
		newLiterals.removeAll(stringCoverage);
		stringCoverage.addAll(newLiterals);
		stringCoverageMetric += countStringLengthOfLiterals(newLiterals);
		
		decisionCoverage.addAll(testCase.getDecisionCoverage());
		
		statementCoverage.addAll(testCase.getStatementCoverage());
		branchCoverage.addAll(testCase.getBranchCoverage());
		
		validationBugCoverage.addAll(testCase.getValidationBugCoverage());
		spellingBugCoverage.addAll(testCase.getSpellingBugCoverage());
		phpBugCoverage.addAll(testCase.getPhpBugCoverage());
	}
	
	private int countStringLengthOfLiterals(Set<String> literals) {
		int length = 0;
		for (String literal : literals) {
			String string = literal.substring(literal.indexOf('|') + 1); // @see edu.iastate.webtesting.outputcoverage.ComputeStringCoverage.compute(CondValue)
			length += string.length();
		}
		return length;
	}
	
	public Set<TestCase> getTestCases() {
		return new HashSet<TestCase>(testCases);
	}
	
	public List<TestCase> getSortedTestCases() {
		ArrayList<TestCase> tests = new ArrayList<TestCase>(testCases);
		Collections.sort(tests, new Comparator<TestCase>() {

			@Override
			public int compare(TestCase t1, TestCase t2) {
				return t1.getTestId() - t2.getTestId();
			}
		});
		return tests;
	}
	
	public Set<LiteralNode> getDataModelCoverage() {
		return new HashSet<LiteralNode>(dataModelCoverage);
	}
	
	public int getDataModelCoverageMetric() {
		return dataModelCoverageMetric;
	}
	
	public Set<String> getStringCoverage() {
		return new HashSet<String>(stringCoverage);
	}
	
	public int getStringCoverageMetric() {
		return stringCoverageMetric;
	}
	
	public Set<String> getDecisionCoverage() {
		return new HashSet<String>(decisionCoverage);
	}
	
	public Set<String> getStatementCoverage() {
		return new HashSet<String>(statementCoverage);
	}
	
	public Set<String> getBranchCoverage() {
		return new HashSet<String>(branchCoverage);
	}
	
	public Set<String> getValidationBugCoverage() {
		return new HashSet<String>(validationBugCoverage);
	}
	
	public Set<String> getSpellingBugCoverage() {
		return new HashSet<String>(spellingBugCoverage);
	}
	
	public Set<String> getPhpBugCoverage() {
		return new HashSet<String>(phpBugCoverage);
	}
	
	public int size() {
		return testCases.size();
	}
	
	public boolean contains(TestCase testCase) {
		return testCases.contains(testCase);
	}
}

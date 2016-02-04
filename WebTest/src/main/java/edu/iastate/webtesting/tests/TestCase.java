package edu.iastate.webtesting.tests;

import java.util.HashSet;
import java.util.Set;

import edu.iastate.symex.datamodel.nodes.LiteralNode;

/**
 * 
 * @author HUNG
 *
 */
public class TestCase {
	private int testId;
	private String input;
	private Set<LiteralNode> dataModelCoverage;
	private float cModelCoverageMetric;
	private Set<String> stringCoverage;
	private Set<String> decisionCoverage;
	private Set<String> statementCoverage;
	private Set<String> branchCoverage;
	private Set<String> validationBugCoverage;
	private Set<String> spellingBugCoverage;
	private Set<String> phpBugCoverage;
	
	public TestCase(int testId, String input, Set<LiteralNode> dataModelCoverage, float cModelCoverageMetric, Set<String> stringCoverage, Set<String> decisionCoverage, Set<String> statementCoverage, Set<String> branchCoverage, Set<String> validationBugCoverage, Set<String> spellingBugCoverage, Set<String> phpBugCoverage) {
		this.testId = testId;
		this.input = input;
		this.dataModelCoverage = dataModelCoverage;
		this.cModelCoverageMetric = cModelCoverageMetric;
		this.stringCoverage = stringCoverage;
		this.decisionCoverage = decisionCoverage;
		this.statementCoverage = statementCoverage;
		this.branchCoverage = branchCoverage;
		this.validationBugCoverage = validationBugCoverage;
		this.spellingBugCoverage = spellingBugCoverage;
		this.phpBugCoverage = phpBugCoverage;
	}
	
	public int getTestId() {
		return testId;
	}
	
	public String getInput() {
		return input;
	}
	
	public Set<LiteralNode> getDataModelCoverage() {
		return new HashSet<LiteralNode>(dataModelCoverage);
	}
	
	public float getCModelCoverageMetric() {
		return cModelCoverageMetric;
	}
	
	public int getDataModelCoverageMetric() {
		TestSuite testSuite = new TestSuite();
		testSuite.addTestCase(this);
		return testSuite.getDataModelCoverageMetric();
	}
	
	public Set<String> getStringCoverage() {
		return new HashSet<String>(stringCoverage);
	}
	
	public int getStringCoverageMetric() {
		TestSuite testSuite = new TestSuite();
		testSuite.addTestCase(this);
		return testSuite.getStringCoverageMetric();
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
}

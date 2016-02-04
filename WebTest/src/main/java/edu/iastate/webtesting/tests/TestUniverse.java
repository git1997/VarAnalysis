package edu.iastate.webtesting.tests;

import java.util.Random;

/**
 * 
 * @author HUNG
 *
 */
public class TestUniverse extends TestSuite {
	private static Random random = new Random();
	static {
		random.setSeed(100);
	}
	
	public TestSuite selectRandomTestSuite() {
		TestSuite testSuite = new TestSuite();
		for (TestCase testCase : testCases) {
			if (random.nextInt(2) ==  0) {
				testSuite.addTestCase(testCase);
			}
		}
		return testSuite;
	}
	
	public TestSuite selectRandomTestSuiteBySize() {
		return selectRandomTestSuiteBySize(random.nextInt(testCases.size()) + 1);
	}
	
	public TestSuite selectRandomTestSuiteBySize(int size) {
		TestSuite testSuite = new TestSuite();
		int expectedTests = size;
		int availableTests = testCases.size();
		for (TestCase testCase : testCases) {
			if (random.nextInt(availableTests) < expectedTests) {
				testSuite.addTestCase(testCase);
				expectedTests--;
			}
			availableTests--;
		}
		return testSuite;
	}
	
	public TestSuite selectKTestsForBestDataModelCoverage(int k) {
		TestSuite testSuite = new TestSuite();
		for (int i = 1; i <= k; i++) {
			testSuite = selectNextTestForBestDataModelCoverage(testSuite);
		}
		return testSuite;
	}
	
	public TestSuite selectNextTestForBestDataModelCoverage(TestSuite testSuite) {
		TestSuite bestTestSuite = null;
		int bestCov = -1;
		
		for (TestCase testCase : testCases) {
			if (!testSuite.contains(testCase)) {
				TestSuite candidateTestSuite = testSuite.clone();
				candidateTestSuite.addTestCase(testCase);
				int dataModelCoverageMetric = candidateTestSuite.getDataModelCoverageMetric();
				if (dataModelCoverageMetric > bestCov) {
					bestTestSuite = candidateTestSuite;
					bestCov = dataModelCoverageMetric;
				}
			}
		}
		
		return bestTestSuite;
	}
	
	public TestSuite selectKTestsForBestStringCoverage(int k) {
		TestSuite testSuite = new TestSuite();
		for (int i = 1; i <= k; i++) {
			testSuite = selectNextTestForBestStringCoverage(testSuite);
		}
		return testSuite;
	}
	
	public TestSuite selectNextTestForBestStringCoverage(TestSuite testSuite) {
		TestSuite bestTestSuite = null;
		int bestCov = -1;
		
		for (TestCase testCase : testCases) {
			if (!testSuite.contains(testCase)) {
				TestSuite candidateTestSuite = testSuite.clone();
				candidateTestSuite.addTestCase(testCase);
				int stringCoverageMetric = candidateTestSuite.getStringCoverageMetric();
				if (stringCoverageMetric > bestCov) {
					bestTestSuite = candidateTestSuite;
					bestCov = stringCoverageMetric;
				}
			}
		}
		
		return bestTestSuite;
	}
	
	public TestSuite selectKTestsForBestStatementCoverage(int k) {
		TestSuite testSuite = new TestSuite();
		for (int i = 1; i <= k; i++) {
			testSuite = selectNextTestForBestStatementCoverage(testSuite);
		}
		return testSuite;
	}
	
	public TestSuite selectNextTestForBestStatementCoverage(TestSuite testSuite) {
		TestSuite bestTestSuite = null;
		int bestCov = -1;
		
		for (TestCase testCase : testCases) {
			if (!testSuite.contains(testCase)) {
				TestSuite candidateTestSuite = testSuite.clone();
				candidateTestSuite.addTestCase(testCase);
				int statementCoverageMetric = candidateTestSuite.getStatementCoverage().size();
				if (statementCoverageMetric > bestCov) {
					bestTestSuite = candidateTestSuite;
					bestCov = statementCoverageMetric;
				}
			}
		}
		
		return bestTestSuite;
	}
}

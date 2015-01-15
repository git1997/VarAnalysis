package edu.iastate.parsers.test;

import java.io.File;

import org.junit.Test;

import edu.iastate.analysis.references.detection.ReferenceDetector;
import edu.iastate.analysis.references.detection.ReferenceManager;
import edu.iastate.symex.test.GenericTest;

/**
 * 
 * @author HUNG
 *
 */
public class TestWebSlice extends GenericTest {
	
	/**
	 * Main method
	 */
	public static void main(String[] args) {
		TestWebSlice testWebSlice = new TestWebSlice();
		//testWebSlice.enableOracleCreation();
		
		testWebSlice.testGenInfoFlow();
	}

	@Override
	public String getActualOutput(File inputFile) {
		ReferenceManager referenceManager = new ReferenceDetector().detect(inputFile);
		return referenceManager.writeReferenceListToText();
	}
	
	/*
	 * Test methods
	 */
	
	@Test
	public void testAssignment() {
		testFile("WebSlice/testAssignment.php");
	}
	
	@Test
	public void testBranches() {
		testFile("WebSlice/testBranches.php");
	}
	
	@Test
	public void testConstraint() {
		testFile("WebSlice/testConstraint.php");
	}
	
	@Test
	public void testFunction() {
		testFile("WebSlice/testFunction.php");
	}
	
	@Test
	public void testReferenceTypes() {
		testFile("WebSlice/testReferenceTypes.php");
	}
	
	@Test
	public void testDuplicateSlices() {
		testFile("WebSlice/testDuplicateSlices.php");
	}
	
	@Test
	public void testGenInfoFlow() {
		testFile("WebSlice/testGenInfoFlow.php");
	}
	
}

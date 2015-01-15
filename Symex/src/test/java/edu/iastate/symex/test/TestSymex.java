package edu.iastate.symex.test;

import java.io.File;

import org.junit.Test;

import edu.iastate.symex.core.PhpExecuter;
import edu.iastate.symex.datamodel.DataModel;

/**
 * 
 * @author HUNG
 *
 */
public class TestSymex extends GenericTest {
	
	/**
	 * Main method
	 */
	public static void main(String[] args) {
		TestSymex testSymex = new TestSymex();
		//testSymex.enableOracleCreation();
		
		testSymex.testReference();
	}

	@Override
	public String getActualOutput(File inputFile) {
		DataModel dataModel = new PhpExecuter().execute(inputFile);
		return dataModel.toIfdefString();
	}
	
	/*
	 * Test methods
	 */
	
	@Test
	public void testAssignment() {
		testFile("testAssignment.php");
	}
	
	@Test
	public void testScalar() {
		testFile("testScalar.php");
	}
	
	@Test
	public void testIf() {
		testFile("testIf.php");
	}
	
	@Test
	public void testFunction() {
		testFile("testFunction.php");
	}
	
	@Test
	public void testEcho() {
		testFile("testEcho.php");
	}
	
	@Test
	public void testVariableScope() {
		testFile("testVariableScope.php");
	}

	@Test
	public void testGlobalVariable() {
		testFile("testGlobalVariable.php");
	}
	
	@Test
	public void testReferenceVariable() {
		testFile("testReferenceVariable.php");
	}
	
	@Test
	public void testSwitchStatement() {
		testFile("testSwitchStatement.php");
	}
	
	@Test
	public void testFieldAccess() {
		testFile("testFieldAccess.php");
	}
	
	@Test
	public void testArrayAccess() {
		testFile("testArrayAccess.php");
	}
	
	@Test
	public void testReturn() {
		testFile("testReturn.php");
	}
	
	@Test
	public void testLoop() {
		testFile("testLoop.php");
	}
	
	@Test
	public void testReflectionVariable() {
		testFile("testReflectionVariable.php");
	}
	
	@Test
	public void testListVariable() {
		testFile("testListVariable.php");
	}
	
	@Test
	public void testForEach() {
		testFile("testForEach.php");
	}
	
	@Test
	public void testReference() {
		testFile("testReference.php");
	}
	
	@Test
	public void testSchoolMate() {
		testFile("SchoolMate-1.5.4/index.php");
	}
	
}

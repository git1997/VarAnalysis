package edu.iastate.symex.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.WriteDataModelToIfDefs;
import edu.iastate.symex.run.RunSymexForFile;
import edu.iastate.symex.util.FileIO;

/**
 * 
 * @author HUNG
 *
 */
public class TestSymex {
	
	public static boolean testMode = true; // true: to test; false: to generate Oracle
	
	public static void main(String[] args) {
		testMode = true;
		new TestSymex().testFieldAccess();
	}

	private void runFile(String fileName) {
		File inputFile = new File("src/test/resources/input/" + fileName);
		File actualOutputFile = new File("src/test/resources/actual_output/" + fileName.replace(".php", "_php") + "-actual.txt");
		File expectedOutputFile = new File("src/test/resources/expected_output/" + fileName.replace(".php", "_php") + "-expected.txt");
		
		DataModel dataModel = new RunSymexForFile(inputFile).execute();
		String actual = WriteDataModelToIfDefs.convert(dataModel);
		FileIO.writeStringToFile(actual, actualOutputFile);
		
		if (testMode) {
			String expected = FileIO.readStringFromFile(expectedOutputFile);
			assertEquals(expected, actual);
		}
		else {
			FileIO.writeStringToFile(actual, expectedOutputFile);
		}
	}
	
	@Test
	public void testAssignment() {
		runFile("testAssignment.php");
	}
	
	@Test
	public void testScalar() {
		runFile("testScalar.php");
	}
	
	@Test
	public void testIf() {
		runFile("testIf.php");
	}
	
	@Test
	public void testFunction() {
		runFile("testFunction.php");
	}
	
	@Test
	public void testEcho() {
		runFile("testEcho.php");
	}
	
	@Test
	public void testVariableScope() {
		runFile("testVariableScope.php");
	}

	@Test
	public void testGlobalVariable() {
		runFile("testGlobalVariable.php");
	}
	
	@Test
	public void testReferenceVariable() {
		runFile("testReferenceVariable.php");
	}
	
	@Test
	public void testSwitchStatement() {
		runFile("testSwitchStatement.php");
	}
	
	@Test
	public void testFieldAccess() {
		runFile("testFieldAccess.php");
	}
	
	@Test
	public void testArrayAccess() {
		runFile("testArrayAccess.php");
	}
	
	@Test
	public void testReturn() {
		runFile("testReturn.php");
	}
	
	@Test
	public void testLoop() {
		runFile("testLoop.php");
	}
	
	@Test
	public void testReflectionVariable() {
		runFile("testReflectionVariable.php");
	}
	
	@Test
	public void testListVariable() {
		runFile("testListVariable.php");
	}
	
	@Test
	public void testForEach() {
		runFile("testForEach.php");
	}
	
	@Test
	public void testSchoolMate() {
		runFile("SchoolMate-1.5.4/index.php");
	}
	
}

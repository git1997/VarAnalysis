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
		testMode = false;
		new TestSymex().testScalar();
	}

	private void runFile(String inputFilePath, String expectedOutputFilePath) {
		File inputFile = new File("src/test/resources/" + inputFilePath);
		File expectedOutputFile = new File("src/test/resources/" + expectedOutputFilePath);
		
		DataModel dataModel = new RunSymexForFile(inputFile).execute();
		String actual = WriteDataModelToIfDefs.convert(dataModel);
		
		if (testMode) {
			String expected = FileIO.readStringFromFile(expectedOutputFile);
			assertEquals(expected, actual);
		}
		else {
			FileIO.writeStringToFile(actual, expectedOutputFile);
		}
	}
	
	@Test
	public void testFunction() {
		runFile("testFunction.php", "testFunction-expected.txt");
	}
	
	@Test
	public void testGlobalVariable() {
		runFile("testGlobalVariable.php", "testGlobalVariable-expected.txt");
	}
	
	@Test
	public void testIf() {
		runFile("testIf.php", "testIf-expected.txt");
	}
	
	@Test
	public void testScalar() {
		runFile("testScalar.php", "testScalar-expected.txt");
	}
	
	@Test
	public void testSwitchStatement() {
		runFile("testSwitchStatement.php", "testSwitchStatement-expected.txt");
	}

}

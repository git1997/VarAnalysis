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
		new TestSymex().testFieldAccess();
	}

	private void runFile(String fileName) {
		File inputFile = new File("src/test/resources/" + fileName + ".php");
		File expectedOutputFile = new File("src/test/resources/" + fileName + "-expected.txt");
		
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
		runFile("testFunction");
	}
	
	@Test
	public void testGlobalVariable() {
		runFile("testGlobalVariable");
	}
	
	@Test
	public void testIf() {
		runFile("testIf");
	}
	
	@Test
	public void testScalar() {
		runFile("testScalar");
	}
	
	@Test
	public void testSwitchStatement() {
		runFile("testSwitchStatement");
	}
	
	@Test
	public void testFieldAccess() {
		runFile("testFieldAccess");
	}

}

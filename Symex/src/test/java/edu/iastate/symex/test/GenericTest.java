package edu.iastate.symex.test;

import static org.junit.Assert.assertEquals;

import java.io.File;

import edu.iastate.symex.util.FileIO;

/**
 * 
 * @author HUNG
 *
 */
public abstract class GenericTest {
	
	private boolean createOracle; // Set to true to create oracle
	
	public void enableOracleCreation() {
		this.createOracle = true;
	}
	
	public void testFile(String fileName) {
		File inputFile = new File("src/test/resources/input/" + fileName);
		File actualOutputFile = new File("src/test/resources/actual_output/" + fileName.replace(".php", "_php") + "-actual.txt");
		File expectedOutputFile = new File("src/test/resources/expected_output/" + fileName.replace(".php", "_php") + "-expected.txt");
		
		String actual = getActualOutput(inputFile);
		FileIO.writeStringToFile(actual, actualOutputFile);
		
		if (!createOracle) { // Test mode
			String expected = FileIO.readStringFromFile(expectedOutputFile);
			assertEquals(expected, actual);
		}
		else { // Create oracle
			FileIO.writeStringToFile(actual, expectedOutputFile);
		}
	}
	
	public abstract String getActualOutput(File inputFile);

}

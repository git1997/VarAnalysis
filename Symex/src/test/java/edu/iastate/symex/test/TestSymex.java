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
		File inputFile = new File("src/test/resources/testScalar.php");
		//File inputFile = new File("/Work/To-do/Data/Web Projects/Server Code/addressbookv6.2.12/map.php");
		DataModel dataModel = new RunSymexForFile(inputFile).execute();
		System.out.println(WriteDataModelToIfDefs.convert(dataModel));
	}

	private void runFile(String inputFilePath, String outputFilePath) {
		File inputFile = new File("src/test/resources/" + inputFilePath);
		File outputFile = new File("src/test/resources/" + outputFilePath);
		
		DataModel dataModel = new RunSymexForFile(inputFile).execute();
		if (testMode) {
			String actual = WriteDataModelToIfDefs.convert(dataModel);
			String expected = FileIO.readStringFromFile(outputFile);
			assertEquals(actual, expected);
		}
		else {
			String actual = WriteDataModelToIfDefs.convert(dataModel);
			FileIO.writeStringToFile(actual, outputFile);
		}
	}
	
	@Test
	public void testFunction() {
		runFile("testFunction.php", "testFunction-DataModel.txt");
	}
	
	@Test
	public void testGlobalVariable() {
		runFile("testGlobalVariable.php", "testGlobalVariable-DataModel.txt");
	}
	
	@Test
	public void testIf() {
		runFile("testIf.php", "testIf-DataModel.txt");
	}
	
	@Test
	public void testScalar() {
		runFile("testScalar.php", "testScalar-DataModel.txt");
	}

}

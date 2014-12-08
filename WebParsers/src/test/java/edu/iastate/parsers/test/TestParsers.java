package edu.iastate.parsers.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.run.RunHtmlParserForFile;
import edu.iastate.parsers.html.run.WriteHtmlDocumentToIfDefs;
import edu.iastate.symex.util.FileIO;

/**
 * 
 * @author HUNG
 *
 */
public class TestParsers {
	
	public static boolean testMode = true; // true: to test; false: to generate Oracle
	
	public static void main(String[] args) {
		testMode = false;
		new TestParsers().testDocument();
	}

	private void runFile(String inputFilePath, String expectedOutputFilePath) {
		File inputFile = new File("src/test/resources/" + inputFilePath);
		File expectedOutputFile = new File("src/test/resources/" + expectedOutputFilePath);
		
		HtmlDocument htmlDocument = new RunHtmlParserForFile(inputFile).execute();
		String actual = WriteHtmlDocumentToIfDefs.convert(htmlDocument);
		
		if (testMode) {
			String expected = FileIO.readStringFromFile(expectedOutputFile);
			assertEquals(expected, actual);
		}
		else {
			FileIO.writeStringToFile(actual, expectedOutputFile);
		}
	}
	
	@Test
	public void testDocument() {
		runFile("testDocument.php", "testDocument-expected.txt");
	}
	
}

package edu.iastate.parsers.test;

import java.io.File;

import org.junit.Test;

import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.run.RunHtmlParserForFile;
import edu.iastate.parsers.html.run.WriteHtmlDocumentToIfDefs;
import edu.iastate.symex.test.GenericTest;

/**
 * 
 * @author HUNG
 *
 */
public class TestDomParser extends GenericTest {
	
	/**
	 * Main method
	 */
	public static void main(String[] args) {
		TestDomParser testDomParser = new TestDomParser();
		//testDomParser.enableOracleCreation();
		
		testDomParser.testCondition();
	}

	@Override
	public String getActualOutput(File inputFile) {
		HtmlDocument htmlDocument = new RunHtmlParserForFile(inputFile).execute();
		return WriteHtmlDocumentToIfDefs.convert(htmlDocument);
	}
	
	/*
	 * Test methods
	 */
	
	@Test
	public void testCondition() {
		testFile("DomParser/testCondition.php");
	}
	
}

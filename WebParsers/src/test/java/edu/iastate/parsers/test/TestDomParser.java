package edu.iastate.parsers.test;

import java.io.File;

import org.junit.Test;

import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.run.RunHtmlParserForFile;
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
		
		testDomParser.testSimple();
	}

	@Override
	public String getActualOutput(File inputFile) {
		HtmlDocument htmlDocument = new RunHtmlParserForFile(inputFile).execute();
		return htmlDocument.toIfdefString();
	}
	
	/*
	 * Test methods
	 */
	
	@Test
	public void testSimple() {
		testFile("DomParser/testSimple.php");
	}
	
	@Test
	public void testBranching() {
		testFile("DomParser/testBranching.php");
	}
	
}

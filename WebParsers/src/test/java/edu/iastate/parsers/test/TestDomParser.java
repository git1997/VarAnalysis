package edu.iastate.parsers.test;

import java.io.File;

import org.junit.Test;

import edu.iastate.parsers.html.core.PhpExecuterAndParser;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
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
		
		testDomParser.testMultipleParentElements();
	}

	@Override
	public String getActualOutput(File inputFile) {
		HtmlDocument htmlDocument = new PhpExecuterAndParser().executeAndParse(inputFile);
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
	
	@Test
	public void testMultipleOpenTags() {
		testFile("DomParser/testMultipleOpenTags.php");
	}
	
	@Test
	public void testMultipleCloseTags() {
		testFile("DomParser/testMultipleCloseTags.php");
	}
	
	@Test
	public void testMultipleParentElements() {
		testFile("DomParser/testMultipleParentElements.php");
	}
	
	@Test
	public void testSchoolMate() {
		testFile("DomParser/SchoolMate-1.5.4/index.php");
	}
	
}

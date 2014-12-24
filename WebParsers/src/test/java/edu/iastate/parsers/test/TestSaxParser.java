package edu.iastate.parsers.test;

import java.io.File;
import org.junit.Test;

import edu.iastate.parsers.conditional.CondList;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.htmlparser.DataModelToHtmlTokens;
import edu.iastate.parsers.html.htmlparser.HtmlTokensToSaxNodes;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.symex.core.PhpExecuter;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.test.GenericTest;

/**
 * 
 * @author HUNG
 *
 */
public class TestSaxParser extends GenericTest {
	
	/**
	 * Main method
	 */
	public static void main(String[] args) {
		TestSaxParser testSaxParser = new TestSaxParser();
		//testSaxParser.enableOracleCreation();
		
		testSaxParser.testBranching();
	}

	@Override
	public String getActualOutput(File inputFile) {
		DataModel dataModel = new PhpExecuter().execute(inputFile);
		CondList<HtmlToken> tokens = new DataModelToHtmlTokens().lex(dataModel);
		CondList<HtmlSaxNode> saxNodes = new HtmlTokensToSaxNodes().parse(tokens);
		return saxNodes.toIfDefString();
	}
	
	/*
	 * Test methods
	 */
	
	@Test
	public void testSimple() {
		testFile("SaxParser/testSimple.php");
	}
	
	@Test
	public void testBadForm() {
		testFile("SaxParser/testBadForm.php");
	}
	
	@Test
	public void testBranching() {
		testFile("SaxParser/testBranching.php");
	}
	
}

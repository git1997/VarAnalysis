package edu.iastate.parsers.test;

import java.io.File;
import org.junit.Test;

import edu.iastate.parsers.conditional.CondList;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.htmlparser.DataModelToHtmlTokens;
import edu.iastate.symex.core.PhpExecuter;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.test.GenericTest;

/**
 * 
 * @author HUNG
 *
 */
public class TestLexer extends GenericTest {
	
	/**
	 * Main method
	 */
	public static void main(String[] args) {
		TestLexer testLexer = new TestLexer();
		//testLexer.enableOracleCreation();
		
		testLexer.testSimple();
	}

	@Override
	public String getActualOutput(File inputFile) {
		DataModel dataModel = new PhpExecuter().execute(inputFile);
		CondList<HtmlToken> tokens = new DataModelToHtmlTokens().lex(dataModel);
		return tokens.toIfDefString();
	}
	
	/*
	 * Test methods
	 */
	
	@Test 
	public void testSimple() {
		testFile("Lexer/testSimple.php");
	}
	
	@Test
	public void testBadForm() {
		testFile("Lexer/testBadForm.php");
	}
	
}

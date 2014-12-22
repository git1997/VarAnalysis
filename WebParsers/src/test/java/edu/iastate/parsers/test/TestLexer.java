package edu.iastate.parsers.test;

import java.io.File;
import org.junit.Test;

import edu.iastate.parsers.conditional.CondList;
import edu.iastate.parsers.conditional.CondListConcat;
import edu.iastate.parsers.conditional.CondListItem;
import edu.iastate.parsers.conditional.CondListSelect;
import edu.iastate.parsers.html.core.DataModelToHtmlTokens;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.run.RunSymexForFile;
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
		
		testLexer.testTokens();
	}

	@Override
	public String getActualOutput(File inputFile) {
		DataModel dataModel = new RunSymexForFile(inputFile).execute();
		CondList<HtmlToken> tokens = new DataModelToHtmlTokens().lex(dataModel);
		
		StringBuilder strBuilder = new StringBuilder();
		writeTokensToText(tokens, strBuilder);
		return strBuilder.toString();
	}
	
	private void writeTokensToText(CondList<HtmlToken> tokens, StringBuilder strBuilder) {
		if (tokens instanceof CondListConcat<?>) {
			CondListConcat<HtmlToken> concat = (CondListConcat<HtmlToken>) tokens;
			for (CondList<HtmlToken> childNode : concat.getChildNodes())
				writeTokensToText(childNode, strBuilder);
		}
		else if (tokens instanceof CondListSelect<?>) {
			CondListSelect<HtmlToken> select = (CondListSelect<HtmlToken>) tokens;
			String constraint = select.getConstraint().toDebugString();
			
			strBuilder.append(System.lineSeparator() + "#if (" + constraint + ")" + System.lineSeparator());
			writeTokensToText(select.getTrueBranchNode(), strBuilder);
			strBuilder.append(System.lineSeparator() + "#else" + System.lineSeparator());
			writeTokensToText(select.getFalseBranchNode(), strBuilder);
			strBuilder.append(System.lineSeparator() + "#endif" + System.lineSeparator());
		}
		else if (tokens instanceof CondListItem<?>) {
			CondListItem<HtmlToken> item = (CondListItem<HtmlToken>) tokens;
			HtmlToken htmlToken = item.getNode();
			strBuilder.append(htmlToken.toDebugString() + System.lineSeparator());
		}
		else { // if (tokenTree instanceof CondListEmpty<?>)
			// Do nothing
		}
	}
	
	/*
	 * Test methods
	 */
	
	@Test
	public void testTokens() {
		testFile("Lexer/testTokens.php");
	}
	
}

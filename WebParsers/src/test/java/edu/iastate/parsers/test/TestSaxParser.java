package edu.iastate.parsers.test;

import java.io.File;
import org.junit.Test;

import edu.iastate.parsers.conditional.CondList;
import edu.iastate.parsers.conditional.CondListConcat;
import edu.iastate.parsers.conditional.CondListItem;
import edu.iastate.parsers.conditional.CondListSelect;
import edu.iastate.parsers.html.core.DataModelToHtmlTokens;
import edu.iastate.parsers.html.core.HtmlTokensToSaxNodes;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.run.RunSymexForFile;
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
		
		testSaxParser.testSaxNodes();
	}

	@Override
	public String getActualOutput(File inputFile) {
		DataModel dataModel = new RunSymexForFile(inputFile).execute();
		CondList<HtmlToken> tokens = new DataModelToHtmlTokens().lex(dataModel);
		CondList<HtmlSaxNode> saxNodes = new HtmlTokensToSaxNodes().parse(tokens);
		
		StringBuilder strBuilder = new StringBuilder();
		writeSaxNodesToText(saxNodes, strBuilder);
		return strBuilder.toString();
	}
	
	private void writeSaxNodesToText(CondList<HtmlSaxNode> saxNodes, StringBuilder strBuilder) {
		if (saxNodes instanceof CondListConcat<?>) {
			CondListConcat<HtmlSaxNode> concat = (CondListConcat<HtmlSaxNode>) saxNodes;
			for (CondList<HtmlSaxNode> childNode : concat.getChildNodes())
				writeSaxNodesToText(childNode, strBuilder);
		}
		else if (saxNodes instanceof CondListSelect<?>) {
			CondListSelect<HtmlSaxNode> select = (CondListSelect<HtmlSaxNode>) saxNodes;
			String constraint = select.getConstraint().toDebugString();
			
			strBuilder.append(System.lineSeparator() + "#if (" + constraint + ")" + System.lineSeparator());
			writeSaxNodesToText(select.getTrueBranchNode(), strBuilder);
			strBuilder.append(System.lineSeparator() + "#else" + System.lineSeparator());
			writeSaxNodesToText(select.getFalseBranchNode(), strBuilder);
			strBuilder.append(System.lineSeparator() + "#endif" + System.lineSeparator());
		}
		else if (saxNodes instanceof CondListItem<?>) {
			CondListItem<HtmlSaxNode> item = (CondListItem<HtmlSaxNode>) saxNodes;
			HtmlSaxNode htmlSaxNode = item.getNode();
			strBuilder.append(htmlSaxNode.toDebugString() + System.lineSeparator());
		}
		else { // if (saxNodes instanceof CondListEmpty<?>)
			// Do nothing
		}
	}
	
	/*
	 * Test methods
	 */
	
	@Test
	public void testSaxNodes() {
		testFile("SaxParser/testSaxNodes.php");
	}
	
}

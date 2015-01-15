package edu.iastate.parsers.html.htmlparser;

import edu.iastate.parsers.conditional.CondList;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.symex.datamodel.DataModel;

/**
 * 
 * @author HUNG
 *
 */
public class DataModelParser {
	
	/**
	 * Parses a DataModel and returns an HtmlDocument
	 * @param dataModel The dataModel to be parsed
	 */
	public HtmlDocument parse(DataModel dataModel) {
		// Step 1: Convert DataModel into CondList<HtmlToken>
		CondList<HtmlToken> lexResult = new DataModelToHtmlTokens().lex(dataModel);
		
		// Step 2: Convert CondList<HtmlToken> into CondList<HtmlSaxNode>
		CondList<HtmlSaxNode> saxParseResult = new HtmlTokensToSaxNodes().parse(lexResult);
		
		// Step 3: Convert CondList<HtmlSaxNode> to HtmlDocument
		HtmlDocument htmlDocument = new HtmlSaxNodesToHtmlDocument().parse(saxParseResult);
		
		return htmlDocument;
	}
	
}
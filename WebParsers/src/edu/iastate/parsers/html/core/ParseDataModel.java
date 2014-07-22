package edu.iastate.parsers.html.core;

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
public class ParseDataModel {
	
	/**
	 * Parses a data model and returns an HtmlDocument node describing the parse result.
	 * @param dataModel The dataModel to be parsed
	 */
	public HtmlDocument parse(DataModel dataModel) {
		// Step 1: Convert DataModel into CondList<HtmlToken>
		CondList<HtmlToken> lexResult = new DataModelToHtmlTokens().lex(dataModel);
		System.out.println(lexResult.toDebugString());
		
		// Step 2: Convert CondList<HtmlToken> into CondList<HtmlSaxNode>
		CondList<HtmlSaxNode> parseResult = new HtmlTokensToSaxNodes().parse(lexResult);
		System.out.println(parseResult.toDebugString());
		
		// Step 3: Convert CondList<HtmlSaxNode> to HtmlDocument
		HtmlDocument htmlDocument = new HtmlSaxNodesToHtmlDocument().parse(parseResult);
		System.out.println(htmlDocument.toDebugString());
		
		return htmlDocument;
	}
	
}
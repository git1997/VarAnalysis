package edu.iastate.parsers.html.core;

import edu.iastate.parsers.conditional.CondList;
import edu.iastate.parsers.conditional.CondListConcat;
import edu.iastate.parsers.conditional.CondListItem;
import edu.iastate.parsers.conditional.CondListSelect;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.htmlparser.HtmlSaxParser;
import edu.iastate.parsers.html.htmlparser.SaxParserEnv;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlTokensToSaxNodes {
	
	private HtmlSaxParser parser = new HtmlSaxParser();
	private SaxParserEnv env = new SaxParserEnv();
	
	/**
	 * Parse a conditional list of tokens and return a conditional list of HtmlSaxNodes
	 */
	public CondList<HtmlSaxNode> parse(CondList<HtmlToken> tokenList) {
		parseList(tokenList);
		return env.getParseResult();
	}
	
	/**
	 * Parse a general tokenList
	 */
	private void parseList(CondList<HtmlToken> tokenList) {
		if (tokenList instanceof CondListConcat<?>)
			parseConcat((CondListConcat<HtmlToken>) tokenList);
		
		else if (tokenList instanceof CondListSelect<?>)
			parseSelect((CondListSelect<HtmlToken>) tokenList);
		
		else if (tokenList instanceof CondListItem<?>)
			parseToken((CondListItem<HtmlToken>) tokenList);
		
		else { // if (tokenList instanceof CondListEmpty<?>)
			// Do nothing
		}
	}
	
	/**
	 * Parse a Concat
	 */
	private void parseConcat(CondListConcat<HtmlToken> concat) {
		for (CondList<HtmlToken> childNode : concat.getChildNodes())
			parseList(childNode);
	}
	
	/**
	 * Parse a Select
	 */
	private void parseSelect(CondListSelect<HtmlToken> select) {
		/*
		 * Parse the true branch
		 */
		SaxParserEnv trueBranchEnv = new SaxParserEnv(env);
		env = trueBranchEnv;
		parseList(select.getTrueBranchNode());
		env = trueBranchEnv.getOuterScopeEnv();
		
		/*
		 * Parse the false branch
		 */
		SaxParserEnv falseBranchEnv = new SaxParserEnv(env);
		env = falseBranchEnv;
		parseList(select.getFalseBranchNode());
		env = falseBranchEnv.getOuterScopeEnv();
		
		/*
		 * Combine results
		 */
		env.updateAfterParsingBranches(select.getConstraint(), trueBranchEnv, falseBranchEnv);
	}

	/**
	 * Parse an HtmlToken
	 */
	private void parseToken(CondListItem<HtmlToken> item) {
		HtmlToken htmlToken = item.getItem();
		parser.parse(htmlToken, env);
   	}

}

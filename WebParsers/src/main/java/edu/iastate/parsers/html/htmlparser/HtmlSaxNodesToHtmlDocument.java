package edu.iastate.parsers.html.htmlparser;

import edu.iastate.parsers.conditional.CondList;
import edu.iastate.parsers.conditional.CondListConcat;
import edu.iastate.parsers.conditional.CondListItem;
import edu.iastate.parsers.conditional.CondListSelect;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlSaxNodesToHtmlDocument {
	
	private HtmlDomParser parser = new HtmlDomParser();
	private DomParserEnv env = new DomParserEnv();
	
	/**
	 * Parse a conditional list of HtmlSaxNodes and return an HtmlDocument 
	 */
	public HtmlDocument parse(CondList<HtmlSaxNode> saxNodelist) {
		parseList(saxNodelist);
		return env.getParseResult();
	}
	
	/**
	 * Parse a general saxNodeList
	 */
	private void parseList(CondList<HtmlSaxNode> saxNodelist) {
		if (saxNodelist instanceof CondListConcat<?>)
			parseConcat((CondListConcat<HtmlSaxNode>) saxNodelist);
		
		else if (saxNodelist instanceof CondListSelect<?>)
			parseSelect((CondListSelect<HtmlSaxNode>) saxNodelist);
		
		else if (saxNodelist instanceof CondListItem<?>)
			parseToken((CondListItem<HtmlSaxNode>) saxNodelist);
		
		else { // if (saxNodelist instanceof CondListEmpty<?>)
			// Do nothing
		}
	}
	
	/**
	 * Parse a Concat
	 */
	private void parseConcat(CondListConcat<HtmlSaxNode> concat) {
		for (CondList<HtmlSaxNode> childNode : concat.getChildNodes())
			parseList(childNode);
	}
	
	/**
	 * Parse a Select
	 */
	private void parseSelect(CondListSelect<HtmlSaxNode> select) {
		/*
		 * Parse the true branch
		 */
		DomParserEnv trueBranchEnv = new DomParserEnv(env);
		env = trueBranchEnv;
		parseList(select.getTrueBranchNode());
		env = trueBranchEnv.getOuterScopeEnv();
		
		/*
		 * Parse the false branch
		 */
		DomParserEnv falseBranchEnv = new DomParserEnv(env);
		env = falseBranchEnv;
		parseList(select.getFalseBranchNode());
		env = falseBranchEnv.getOuterScopeEnv();
		
		/*
		 * Combine results
		 */
		env.updateAfterParsingBranches(select.getConstraint(), trueBranchEnv, falseBranchEnv);
	}

	/**
	 * Parse an HtmlSaxNode
	 */
	private void parseToken(CondListItem<HtmlSaxNode> item) {
		HtmlSaxNode htmlSaxNode = item.getItem();
		parser.parse(htmlSaxNode, env);
   	}

}

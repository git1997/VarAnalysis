package edu.iastate.parsers.html.core;

import java.util.ArrayList;

import edu.iastate.parsers.conditional.CondList;
import edu.iastate.parsers.conditional.CondListConcat;
import edu.iastate.parsers.conditional.CondListFactory;
import edu.iastate.parsers.conditional.CondListItem;
import edu.iastate.parsers.conditional.CondListSelect;
import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.htmlparser.HtmlSaxParser;
import edu.iastate.parsers.html.sax.nodes.HOpenTag;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.symex.constraints.ConstraintFactory;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlTokensToSaxNodes {
	
	private CondListFactory<HtmlSaxNode> condListFactory = new CondListFactory<HtmlSaxNode>();
	
	/**
	 * Parse a conditional list of tokens and return a conditional list of HtmlSaxNodes
	 */
	public CondList<HtmlSaxNode> parse(CondList<HtmlToken> tokenList) {
		HtmlSaxParser parser = new HtmlSaxParser();
		return parse(tokenList, parser);
	}
	
	/**
	 * Parse a general tokenList
	 */
	private CondList<HtmlSaxNode> parse(CondList<HtmlToken> tokenList, HtmlSaxParser parser) {
		if (tokenList instanceof CondListConcat<?>)
			return parse((CondListConcat<HtmlToken>) tokenList, parser);
		
		else if (tokenList instanceof CondListSelect<?>)
			return parse((CondListSelect<HtmlToken>) tokenList, parser);
		
		else if (tokenList instanceof CondListItem<?>)
			return parse((CondListItem<HtmlToken>) tokenList, parser);
		
		else // if (tokenTree instanceof CondListEmpty<?>)
			return condListFactory.createEmptyCondList();
	}
	
	/**
	 * Parse a Concat
	 */
	private CondList<HtmlSaxNode> parse(CondListConcat<HtmlToken> concat, HtmlSaxParser parser) {
		ArrayList<CondList<HtmlSaxNode>> parseResult = new ArrayList<CondList<HtmlSaxNode>>(); 
		for (CondList<HtmlToken> childNode : concat.getChildNodes())
			parseResult.add(parse(childNode, parser));
		return condListFactory.createCompactConcat(parseResult);
	}
	
	/**
	 * Parse a Select
	 */
	private CondList<HtmlSaxNode> parse(CondListSelect<HtmlToken> select, HtmlSaxParser parser) {
		if (parser.isInsideOpenTag())
			return parseSelectCase1(select, parser);
		else
			return parseSelectCase2(select, parser);
	}
	
	/**
	 * Case 1: Parsing inside an HTML open tag
	 */
	private CondList<HtmlSaxNode> parseSelectCase1(CondListSelect<HtmlToken> select, HtmlSaxParser parser) {
		// TODO Add more error messages here
		HOpenTag lastOpenTag = (HOpenTag) parser.getLastSaxNode();
		HOpenTag lastOpenTagForTrueBranch = lastOpenTag.clone();
		HOpenTag lastOpenTagForFalseBranch = lastOpenTag.clone();
		
		/*
		 * Enter the true branch
		 */
		CondList<HtmlSaxNode> nodesInTrueBranch = null;
		if (select.getTrueBranchNode() != null) {
			parser.setLastSaxNode(lastOpenTagForTrueBranch);
			nodesInTrueBranch = parse(select.getTrueBranchNode(), parser);
		}
		
		/*
		 * Enter the false branch
		 */
		CondList<HtmlSaxNode> nodesInFalseBranch = null;
		if (select.getFalseBranchNode() != null) {
			parser.setLastSaxNode(lastOpenTagForFalseBranch);
			nodesInFalseBranch = parse(select.getFalseBranchNode(), parser);
		}
		
		/*
		 * Combine results
		 */
		parser.setLastSaxNode(lastOpenTag);
		CondList<HtmlSaxNode> mergedResult = condListFactory.createCompactSelect(select.getConstraint(), nodesInTrueBranch, nodesInFalseBranch);
		
		// Combine the attributes in the true branch and false branch and update the original lastOpenTag
		ArrayList<HtmlAttribute> attrsInTrueBranch = lastOpenTagForTrueBranch.getAttributes();
		ArrayList<HtmlAttribute> attrsInFalseBranch = lastOpenTagForFalseBranch.getAttributes();
		
		lastOpenTag.removeAllAttributes();
		int commonAttrs = 0;
		for (int i = 0; i < Math.min(attrsInTrueBranch.size(), attrsInFalseBranch.size()); i++) {
			HtmlAttribute attrInTrueBranch = attrsInTrueBranch.get(i);
			HtmlAttribute attrInFalseBranch = attrsInFalseBranch.get(i);
			if (attrInTrueBranch.getName().equals(attrInFalseBranch.getName())
					&& attrInTrueBranch.getStringValue().equals(attrInFalseBranch.getStringValue())) {
				commonAttrs++;
				lastOpenTag.addAttribute(attrInTrueBranch);
			}
			else
				break;
		}
		for (int i = commonAttrs; i < attrsInTrueBranch.size(); i++) {
			HtmlAttribute attr = attrsInTrueBranch.get(i);
			attr.setConstraint(select.getConstraint());
			lastOpenTag.addAttribute(attr);
		}
		for (int i = commonAttrs; i < attrsInFalseBranch.size(); i++) {
			HtmlAttribute attr = attrsInFalseBranch.get(i);
			attr.setConstraint(ConstraintFactory.createNotConstraint(select.getConstraint()));
			lastOpenTag.addAttribute(attr);
		}
		
		return mergedResult;
	}

	/**
	 * Case 2: Parsing outside an HTML open tag
	 */
	private CondList<HtmlSaxNode> parseSelectCase2(CondListSelect<HtmlToken> select, HtmlSaxParser parser) {
		/*
		 * Enter the true branch
		 */
		CondList<HtmlSaxNode> nodesInTrueBranch = null;
		if (select.getTrueBranchNode() != null)
			nodesInTrueBranch = parse(select.getTrueBranchNode(), parser);
		
		/*
		 * Enter the false branch
		 */
		CondList<HtmlSaxNode> nodesInFalseBranch = null;
		if (select.getFalseBranchNode() != null)
			nodesInFalseBranch = parse(select.getFalseBranchNode(), parser);
		
		/*
		 * Combine results
		 */
		parser.setLastSaxNode(null);
		CondList<HtmlSaxNode> mergedResult = condListFactory.createCompactSelect(select.getConstraint(), nodesInTrueBranch, nodesInFalseBranch);
		return mergedResult;
	}
	
	/**
	 * Parse an HtmlToken
	 */
	private CondList<HtmlSaxNode> parse(CondListItem<HtmlToken> item, HtmlSaxParser parser) {
		HtmlToken htmlToken = item.getNode();
		parser.parse(htmlToken);
		
		CondList<HtmlSaxNode> parseResult = condListFactory.createCondList(parser.getParseResult());
		parser.clearParseResult();
		
		return parseResult;
   	}

}

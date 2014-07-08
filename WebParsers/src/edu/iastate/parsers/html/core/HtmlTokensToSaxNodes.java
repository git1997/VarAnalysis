package edu.iastate.parsers.html.core;

import java.util.ArrayList;

import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.dom.nodes.HtmlConcat;
import edu.iastate.parsers.html.dom.nodes.HtmlNode;
import edu.iastate.parsers.html.dom.nodes.HtmlSelect;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.htmlparser.HtmlSaxParser;
import edu.iastate.parsers.html.sax.nodes.HOpenTag;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.parsers.tree.TreeConcatNode;
import edu.iastate.parsers.tree.TreeLeafNode;
import edu.iastate.parsers.tree.TreeNode;
import edu.iastate.parsers.tree.TreeNodeFactory;
import edu.iastate.parsers.tree.TreeSelectNode;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlTokensToSaxNodes {
	
	private TreeNode<HtmlSaxNode> parseResult = null;
	
	private void updateParseResult(HtmlSaxParser parser) {
		ArrayList<HtmlSaxNode> currentResult = parser.getParseResult();
		TreeNode<HtmlSaxNode> curResult = new TreeNodeFactory<HtmlSaxNode>().createInstanceFromNodes(currentResult);
		parseResult = new TreeNodeFactory<HtmlSaxNode>().createCompactConcatNode(parseResult, curResult);
	}
	
	public TreeNode<HtmlSaxNode> parse(TreeNode<HtmlToken> tokenTree) {
		// Parse the tokenTree and update the parseResult along the way
		HtmlSaxParser parser = new HtmlSaxParser();
		parse(tokenTree, parser);

		// Get the remaining result
		updateParseResult(parser);
		parser.clearParseResult();
		
		return parseResult;
	}
	
	public void parse(TreeNode<HtmlToken> tokenTree, HtmlSaxParser parser) {
		if (tokenTree instanceof TreeConcatNode<?>)
			parse((TreeConcatNode<HtmlToken>) tokenTree, parser);
		
		else if (tokenTree instanceof TreeSelectNode<?>)
			parse((TreeSelectNode<HtmlToken>) tokenTree, parser);
		
		else // if (tokenTree instanceof HtmlTreeLeafNode<?>)
			parse((TreeLeafNode<HtmlToken>) tokenTree, parser);
	}
	
	/**
	 * Parses a ConcatNode
	 */
	private void parse(TreeConcatNode<HtmlToken> concatNode, HtmlSaxParser parser) {
		for (TreeNode<HtmlToken> childNode : concatNode.getChildNodes())
			parse(childNode, parser);
	}
	
	/**
	 * Parses a SelectNode
	 */
	private void parse(TreeSelectNode<HtmlToken> selectNode, HtmlSaxParser parser) {
		// TODO Handle in JavaScript code
		
		// Get result before entering the branches
		updateParseResult(parser);
		
		/*
		 *  Enter the true branch
		 */
		ArrayList<HtmlSaxNode> parseResultInFalseBranch = new ArrayList<HtmlSaxNode>();
		
		parser.clearParseResult();
		parser.setParseResultBeforeBranching(parseResult);
		parser.setParseResultInOtherBranch(parseResultInFalseBranch);
		
		if (selectNode.getTrueBranchNode() != null)
			parse(selectNode.getTrueBranchNode(), parser);
		
		ArrayList<HtmlSaxNode> nodesInTrueBranch = parser.getParseResult();
		parseResult = parser.getParseResultBeforeBranching();
		
		/*
		 * Enter the false branch
		 */
		parser.setParseResult(parseResultInFalseBranch);
		parser.setParseResultBeforeBranching(null);
		
		if (selectNode.getFalseBranchNode() != null)
			parse(selectNode.getFalseBranchNode(), parser);
		
		ArrayList<HtmlSaxNode> nodesInFalseBranch = parser.getParseResult();
		
		/*
		 * Combine results and continue
		 */
		TreeNode<HtmlSaxNode> mergedResult = null;
		
		if (nodesInTrueBranch.size() == 1 && nodesInTrueBranch.get(nodesInTrueBranch.size() - 1) instanceof HOpenTag
				&& nodesInFalseBranch.size() == 1 && nodesInFalseBranch.get(nodesInFalseBranch.size() - 1) instanceof HOpenTag) {
			HOpenTag openTagInTrueBranch = (HOpenTag) nodesInTrueBranch.get(nodesInTrueBranch.size() - 1);
			HOpenTag openTagInFalseBranch = (HOpenTag) nodesInFalseBranch.get(nodesInFalseBranch.size() - 1);
			
			if (openTagInTrueBranch.getType().equals(openTagInFalseBranch.getType()) && openTagInTrueBranch.getLocation() == openTagInFalseBranch.getLocation()) {
				HOpenTag mergedTag = new HOpenTag(openTagInTrueBranch.getType(), openTagInTrueBranch.getLocation());
				for (HtmlAttribute attr : openTagInTrueBranch.getAttributes()) {
					attr.setConstraint(selectNode.getConstraint());
					mergedTag.addAttribute(attr);
				}
				for (HtmlAttribute attr : openTagInFalseBranch.getAttributes()) {
					attr.setConstraint(ConstraintFactory.createNotConstraint(selectNode.getConstraint()));
					mergedTag.addAttribute(attr);
				}
				mergedResult = new TreeLeafNode<HtmlSaxNode>(mergedTag);
			}
		}
		
		if (mergedResult == null)
			mergedResult =  new TreeNodeFactory<HtmlSaxNode>().createInstanceFromBranchingNodes(selectNode.getConstraint(), nodesInTrueBranch, nodesInFalseBranch);
		
		parseResult = new TreeNodeFactory<HtmlSaxNode>().createCompactConcatNode(parseResult, mergedResult);
		
		parser.clearParseResult();
		parser.setParseResultBeforeBranching(null);
		parser.setParseResultInOtherBranch(null);
	}
	

	
	/**
	 * Parses a LeafNode
	 */
	private void parse(TreeLeafNode<HtmlToken> leafNode, HtmlSaxParser parser) {
		parser.parse(leafNode.getNode());
   	}

}

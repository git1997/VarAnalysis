package edu.iastate.parsers.html.core;

import java.util.ArrayList;

import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.htmlparser.HtmlSaxParser;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.parsers.tree.TreeConcatNode;
import edu.iastate.parsers.tree.TreeLeafNode;
import edu.iastate.parsers.tree.TreeNode;
import edu.iastate.parsers.tree.TreeNodeFactory;
import edu.iastate.parsers.tree.TreeSelectNode;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlTokensToSaxNodes {
	
	private TreeNode<HtmlSaxNode> parseResult = null;
	
	private void updateParseResult(HtmlSaxParser parser) {
		ArrayList<HtmlSaxNode> currentResult = parser.getParseResult();
		parseResult = new TreeNodeFactory<HtmlSaxNode>().createInstanceFromNewNodes(parseResult, currentResult);
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
		parser.clearParseResult();
		parse(selectNode.getTrueBranchNode(), parser);
		ArrayList<HtmlSaxNode> nodesInTrueBranch = parser.getParseResult();
		
		/*
		 * Enter the false branch
		 */
		parser.clearParseResult();
		parse(selectNode.getFalseBranchNode(), parser);
		ArrayList<HtmlSaxNode> nodesInFalseBranch = parser.getParseResult();
		
		/*
		 * Combine results and continue
		 */
		parseResult = new TreeNodeFactory<HtmlSaxNode>().createInstanceFromNewBranchingNodes(parseResult, selectNode.getConstraint(), nodesInTrueBranch, nodesInFalseBranch);
		parser.clearParseResult();
	}
	
	/**
	 * Parses a LeafNode
	 */
	private void parse(TreeLeafNode<HtmlToken> leafNode, HtmlSaxParser parser) {
		parser.parse(leafNode.getNode());
   	}

}

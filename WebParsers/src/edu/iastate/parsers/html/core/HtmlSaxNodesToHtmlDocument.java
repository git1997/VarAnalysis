package edu.iastate.parsers.html.core;

import java.util.ArrayList;
import java.util.Stack;

import edu.iastate.parsers.html.dom.nodes.HtmlConcat;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.dom.nodes.HtmlNode;
import edu.iastate.parsers.html.dom.nodes.HtmlSelect;
import edu.iastate.parsers.html.htmlparser.HtmlDomParser;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.parsers.tree.TreeConcatNode;
import edu.iastate.parsers.tree.TreeLeafNode;
import edu.iastate.parsers.tree.TreeNode;
import edu.iastate.parsers.tree.TreeSelectNode;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlSaxNodesToHtmlDocument {
	
	private ArrayList<HtmlNode> parseResult = new ArrayList<HtmlNode>();
	
	public HtmlDocument parse(TreeNode<HtmlSaxNode> saxNodes) {
		// Parse the tokenTree and update the parseResult along the way
		HtmlDomParser parser = new HtmlDomParser();
		parse(saxNodes, parser);
		
		// Get the remaining result
		parseResult.addAll(parser.getParseResult());
		Stack<HtmlElement> htmlStack = parser.saveHtmlStack();
		if (!htmlStack.isEmpty())
			parseResult.add(htmlStack.firstElement());
		
		HtmlDocument htmlDocument = new HtmlDocument();
		for (HtmlNode element : parseResult)
			htmlDocument.addChildNode(element);

		return htmlDocument;
	}
	
	public void parse(TreeNode<HtmlSaxNode> saxNodes, HtmlDomParser parser) {
		if (saxNodes instanceof TreeConcatNode<?>)
			parse((TreeConcatNode<HtmlSaxNode>) saxNodes, parser);
		
		else if (saxNodes instanceof TreeSelectNode<?>)
			parse((TreeSelectNode<HtmlSaxNode>) saxNodes, parser);
		
		else // if (tokenTree instanceof HtmlTreeLeafNode<?>)
			parse((TreeLeafNode<HtmlSaxNode>) saxNodes, parser);
	}
	
	/**
	 * Parses a ConcatNode
	 */
	private void parse(TreeConcatNode<HtmlSaxNode> concatNode, HtmlDomParser parser) {
		for (TreeNode<HtmlSaxNode> childNode : concatNode.getChildNodes())
			parse(childNode, parser);
	}
	
	/**
	 * Parses a SelectNode
	 */
	private void parse(TreeSelectNode<HtmlSaxNode> selectNode, HtmlDomParser parser) {
		// Get result before entering the branches
		parseResult.addAll(parser.getParseResult());
		Stack<HtmlElement> savedStack = parser.saveHtmlStack();
		
		/*
		 *  Enter the true branch
		 */
		parser.clearParseResult();
		parser.clearHtmlStack();
				
		if (selectNode.getTrueBranchNode() != null)
			parse(selectNode.getTrueBranchNode(), parser);
		
		ArrayList<HtmlElement> nodesInTrueBranch = parser.getParseResult();
		
		/*
		 * Enter the false branch
		 */
		parser.clearParseResult();
		parser.clearHtmlStack();
		
		if (selectNode.getFalseBranchNode() != null)
			parse(selectNode.getFalseBranchNode(), parser);
		
		ArrayList<HtmlElement> nodesInFalseBranch = parser.getParseResult();
		
		/*
		 * Combine results and continue
		 */
		parser.restoreHtmlStack(savedStack);
		HtmlNode nodeInTrueBranch = convert(nodesInTrueBranch);
		HtmlNode nodeInFalseBranch = convert(nodesInFalseBranch);
		HtmlSelect select = new HtmlSelect(selectNode.getConstraint(), nodeInTrueBranch, nodeInFalseBranch);
		
		if (!savedStack.isEmpty())
			savedStack.peek().addChildNode(select);
		else
			parseResult.add(select);
		parser.clearParseResult();
	}
	
	private HtmlNode convert(ArrayList<HtmlElement> nodes) {
		if (nodes.isEmpty())
			return null;
		else if (nodes.size() == 1)
			return nodes.get(0);
		else
			return new HtmlConcat(new ArrayList<HtmlNode>(nodes)); 
	}
	
	/**
	 * Parses a LeafNode
	 */
	private void parse(TreeLeafNode<HtmlSaxNode> leafNode, HtmlDomParser parser) {
		parser.parse(leafNode.getNode());
   	}

}

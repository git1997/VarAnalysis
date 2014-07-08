package edu.iastate.parsers.html.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import edu.iastate.parsers.html.dom.nodes.HtmlConcat;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.dom.nodes.HtmlNode;
import edu.iastate.parsers.html.dom.nodes.HtmlSelect;
import edu.iastate.parsers.html.htmlparser.HtmlDomParser;
import edu.iastate.parsers.html.sax.nodes.HOpenTag;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.parsers.tree.TreeConcatNode;
import edu.iastate.parsers.tree.TreeLeafNode;
import edu.iastate.parsers.tree.TreeNode;
import edu.iastate.parsers.tree.TreeSelectNode;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlSaxNodesToHtmlDocument {
	
	public HtmlDocument parse(TreeNode<HtmlSaxNode> saxNodes) {
		HtmlDomParser parser = new HtmlDomParser(); 
		
		// Create a pseudo root element
		HOpenTag rootOpenTag = new HOpenTag("ROOT", PositionRange.UNDEFINED);
		HtmlElement rootElement = new HtmlElement(rootOpenTag);
		parser.pushHtmlStack(rootElement);

		// Parse the saxNodes
		parse(saxNodes, parser);
		
		// Create the HtmlDocument
		HtmlDocument htmlDocument = new HtmlDocument();
		for (HtmlNode element : rootElement.getChildNodes())
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
		Stack<HtmlElement> htmlStack = parser.getHtmlStack();
		HtmlElement lastElement = htmlStack.peek();
		
		/*
		 *  Enter the true branch
		 */
		parser.clearHtmlStack();
		HtmlElement rootElementTrue = new HtmlElement(lastElement.getHtmlOpenTag());
		parser.pushHtmlStack(rootElementTrue);
				
		if (selectNode.getTrueBranchNode() != null)
			parse(selectNode.getTrueBranchNode(), parser);
		boolean emptyStackInTrueBranch = parser.getHtmlStack().isEmpty();
		
		/*
		 * Enter the false branch
		 */
		parser.clearHtmlStack();
		HtmlElement rootElementFalse = new HtmlElement(lastElement.getHtmlOpenTag());
		parser.pushHtmlStack(rootElementFalse);
		
		if (selectNode.getFalseBranchNode() != null)
			parse(selectNode.getFalseBranchNode(), parser);
		boolean emptyStackInFalseBranch = parser.getHtmlStack().isEmpty();
		
		/*
		 * Combine results and continue
		 */
		if (emptyStackInTrueBranch || emptyStackInFalseBranch) {
			HtmlNode select = HtmlSelect.createCompactHtmlNode(selectNode.getConstraint(), rootElementTrue, rootElementFalse);
			htmlStack.pop();
			lastElement = htmlStack.peek();
			lastElement.replaceLastChildNode(select);
		}
		else {
			HtmlNode nodeInTrueBranch = HtmlConcat.createCompactHtmlNode(new ArrayList<HtmlNode>(rootElementTrue.getChildNodes()));
			HtmlNode nodeInFalseBranch = HtmlConcat.createCompactHtmlNode(new ArrayList<HtmlNode>(rootElementFalse.getChildNodes()));
			HtmlNode select = HtmlSelect.createCompactHtmlNode(selectNode.getConstraint(), nodeInTrueBranch, nodeInFalseBranch);
			lastElement.addChildNode(select);
		}
		
		parser.setHtmlStack(htmlStack);
	}
	
	/**
	 * Parses a LeafNode
	 */
	private void parse(TreeLeafNode<HtmlSaxNode> leafNode, HtmlDomParser parser) {
		parser.parse(leafNode.getNode());
   	}

}

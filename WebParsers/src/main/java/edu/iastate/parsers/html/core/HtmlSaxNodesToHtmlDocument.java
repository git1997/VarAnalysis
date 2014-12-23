package edu.iastate.parsers.html.core;

import java.util.Stack;

import edu.iastate.parsers.conditional.CondList;
import edu.iastate.parsers.conditional.CondListConcat;
import edu.iastate.parsers.conditional.CondListItem;
import edu.iastate.parsers.conditional.CondListSelect;
import edu.iastate.parsers.html.dom.nodes.HtmlConcat;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.dom.nodes.HtmlNode;
import edu.iastate.parsers.html.dom.nodes.HtmlSelect;
import edu.iastate.parsers.html.htmlparser.HtmlDomParser;
import edu.iastate.parsers.html.sax.nodes.HOpenTag;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlSaxNodesToHtmlDocument {

	/**
	 * Parse a conditional list of HtmlSaxNodes and return an HtmlDocument
	 */
	public HtmlDocument parse(CondList<HtmlSaxNode> saxNodes) {
		HtmlDomParser parser = new HtmlDomParser(); 
		
		// Use a pseudo root element
		HOpenTag rootOpenTag = new HOpenTag("ROOT", PositionRange.UNDEFINED);
		HtmlElement rootElement = HtmlElement.createHtmlElement(rootOpenTag);
		parser.pushHtmlStack(rootElement);

		// Parse the saxNodes
		parse(saxNodes, parser);
		
		// Create the HtmlDocument
		HtmlDocument htmlDocument = new HtmlDocument();
		for (HtmlNode element : rootElement.getChildNodes())
			htmlDocument.addChildNode(element);
		return htmlDocument;
	}
	
	/**
	 * Parse a general CondList of HtmlSaxNodes
	 */
	private void parse(CondList<HtmlSaxNode> saxNodes, HtmlDomParser parser) {
		if (saxNodes instanceof CondListConcat<?>)
			parse((CondListConcat<HtmlSaxNode>) saxNodes, parser);
		
		else if (saxNodes instanceof CondListSelect<?>)
			parse((CondListSelect<HtmlSaxNode>) saxNodes, parser);
		
		else if (saxNodes instanceof CondListItem<?>)
			parse((CondListItem<HtmlSaxNode>) saxNodes, parser);
		
		else { // if (saxNodes instanceof CondListEmpty<?>)
			// Do nothing
		}
	}
	
	/**
	 * Parse a ConcatNode
	 */
	private void parse(CondListConcat<HtmlSaxNode> concatNode, HtmlDomParser parser) {
		for (CondList<HtmlSaxNode> childNode : concatNode.getChildNodes())
			parse(childNode, parser);
	}
	
	/**
	 * Parse a SelectNode
	 * TODO Revise this code
	 */
	private void parse(CondListSelect<HtmlSaxNode> selectNode, HtmlDomParser parser) {
		Stack<HtmlElement> lastHtmlStack = parser.getHtmlStack();
		HtmlElement lastElement = lastHtmlStack.peek();
		
		/*
		 * Enter the true branch
		 */
		parser.clearHtmlStack();
		HtmlElement rootElementTrue = HtmlElement.createHtmlElement(lastElement.getOpenTag());
		parser.pushHtmlStack(rootElementTrue);
				
		if (selectNode.getTrueBranchNode() != null)
			parse(selectNode.getTrueBranchNode(), parser);
		HtmlElement rootElementTrueAfter = !parser.isEmptyHtmlStack() ? parser.getFirstElementInHtmlStack() : null;
		
		/*
		 * Enter the false branch
		 */
		parser.clearHtmlStack();
		HtmlElement rootElementFalse = HtmlElement.createHtmlElement(lastElement.getOpenTag());
		parser.pushHtmlStack(rootElementFalse);
		
		if (selectNode.getFalseBranchNode() != null)
			parse(selectNode.getFalseBranchNode(), parser);
		HtmlElement rootElementFalseAfter = !parser.isEmptyHtmlStack() ? parser.getFirstElementInHtmlStack() : null;
		
		/*
		 * Combine results
		 */
		
		// Handle well-formed HTML
		HtmlNode nodeInTrueBranch1 = HtmlConcat.createCompactConcat(rootElementTrue.getChildNodes());
		HtmlNode nodeInFalseBranch1 = HtmlConcat.createCompactConcat(rootElementFalse.getChildNodes());
		HtmlNode select1 = HtmlSelect.createCompactSelect(selectNode.getConstraint(), nodeInTrueBranch1, nodeInFalseBranch1);
		if (select1 != null)
			lastHtmlStack.peek().addChildNode(select1);
		
		// [Optional] Handle the case where one opening tag is closed in two different branches
		if (rootElementTrueAfter != rootElementTrue && rootElementFalseAfter != rootElementFalse && lastHtmlStack.size() >= 2) {
			HtmlNode select2 = HtmlSelect.createCompactSelect(selectNode.getConstraint(), rootElementTrue, rootElementFalse);
			lastHtmlStack.pop();
			lastHtmlStack.peek().replaceLastChildNode(select2);
		}
					
		// Handle ill-formed HTML
		HtmlNode nodeInTrueBranch3 = (rootElementTrueAfter != rootElementTrue ? rootElementTrueAfter : null);
		HtmlNode nodeInFalseBranch3 = (rootElementFalseAfter != rootElementFalse ? rootElementFalseAfter : null);
		HtmlNode select3 = HtmlSelect.createCompactSelect(selectNode.getConstraint(), nodeInTrueBranch3, nodeInFalseBranch3);
		if (select3 != null)
			lastHtmlStack.peek().addChildNode(select3);
		
		parser.setHtmlStack(lastHtmlStack);
	}
	
	/**
	 * Parse an HtmlSaxNode
	 */
	private void parse(CondListItem<HtmlSaxNode> saxNode, HtmlDomParser parser) {
		parser.parse(saxNode.getItem());
   	}

}

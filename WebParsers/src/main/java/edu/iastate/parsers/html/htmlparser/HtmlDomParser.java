package edu.iastate.parsers.html.htmlparser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Stack;

import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.dom.nodes.HtmlText;
import edu.iastate.parsers.html.sax.nodes.HCloseTag;
import edu.iastate.parsers.html.sax.nodes.HOpenTag;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.parsers.html.sax.nodes.HText;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlDomParser {
	
	private Stack<HtmlElement> htmlStack = new Stack<HtmlElement>();

	public Stack<HtmlElement> getHtmlStack() {
		Stack<HtmlElement> clonedStack = new Stack<HtmlElement>();
		clonedStack.addAll(htmlStack);
		return clonedStack;
	}
	
	public void setHtmlStack(Stack<HtmlElement> savedStack) {
		this.htmlStack = savedStack;
	}
	
	public void clearHtmlStack() {
		htmlStack = new Stack<HtmlElement>();
	}
	
	public void pushHtmlStack(HtmlElement element) {
		htmlStack.push(element);
	}
	
	public boolean isEmptyHtmlStack() {
		return htmlStack.isEmpty();
	}
	
	public HtmlElement peekHtmlStack() {
		return htmlStack.peek();
	}
	
	public HtmlElement getFirstElementInHtmlStack() {
		return htmlStack.firstElement();
	}
	
	public void parse(HtmlSaxNode saxNode) {
		/*
		 * Handle ill-formed HTML, e.g. <br> may not have closing tag
		 */
		if (!htmlStack.isEmpty() && selfClosingTags.contains(htmlStack.peek().getType())) {
			if (!(saxNode instanceof HCloseTag)
					|| (saxNode instanceof HCloseTag && htmlStack.peek().getType() != ((HCloseTag) saxNode).getType()))
				htmlStack.pop();
		}
		
		if (saxNode instanceof HOpenTag) {
			HtmlElement htmlElement = HtmlElement.createHtmlElement((HOpenTag) saxNode);
			if (!htmlStack.isEmpty()) {
				htmlStack.peek().addChildNode(htmlElement);
			}
			htmlStack.add(htmlElement);
		}
		
		else if (saxNode instanceof HCloseTag) {
			if (!htmlStack.isEmpty()) {
				HtmlElement htmlElement = htmlStack.peek();
				if (htmlElement.getType().equals(((HCloseTag) saxNode).getType())) {
					htmlStack.pop();
				}
				else {
					// TODO Handle mismatching tags here
				}
			}
		}
		
		else { // if (saxNode instanceof HText)
			if (!htmlStack.isEmpty()) {
				HtmlElement htmlElement = htmlStack.peek();
				htmlElement.addChildNode(new HtmlText((HText) saxNode));
			}
		}
	}
	
	/**
	 * List of self-closing tags 
	 */
	private static HashSet<String> selfClosingTags = new HashSet<String>(Arrays.asList(new String[]{"br", "empty", "input"}));
	
}

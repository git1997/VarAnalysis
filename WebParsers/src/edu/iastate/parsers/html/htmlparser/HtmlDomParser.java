package edu.iastate.parsers.html.htmlparser;


import java.util.ArrayList;
import java.util.Stack;

import edu.iastate.parsers.html.dom.nodes.HtmlElement;
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
	
	private ArrayList<HtmlElement> parseResult = new ArrayList<HtmlElement>();
	
	public Stack<HtmlElement> saveHtmlStack() {
		Stack<HtmlElement> savedStack = new Stack<HtmlElement>();
		savedStack.addAll(htmlStack);
		return savedStack;
	}
	
	public void restoreHtmlStack(Stack<HtmlElement> savedStack) {
		this.htmlStack = savedStack;
	}
	
	public void clearHtmlStack() {
		htmlStack = new Stack<HtmlElement>();
	}
	
	public ArrayList<HtmlElement> getParseResult() {
		return new ArrayList<HtmlElement>(parseResult);
	}
	
	public void clearParseResult() {
		parseResult = new ArrayList<HtmlElement>();
	}
	
	public void parse(HtmlSaxNode saxNode) {
		if (saxNode instanceof HOpenTag) {
			HtmlElement htmlElement = new HtmlElement((HOpenTag) saxNode);
			if (!htmlStack.isEmpty())
				htmlStack.peek().addChildNode(htmlElement);
			htmlStack.add(htmlElement);
		}
		else if (saxNode instanceof HCloseTag) {
			if (!htmlStack.isEmpty()) {
				HtmlElement htmlElement = htmlStack.peek();
				if (htmlElement.getType().equals(((HCloseTag) saxNode).getType())) {
					htmlStack.pop();
					if (htmlStack.isEmpty())
						parseResult.add(htmlElement);
				}
			}
		}
		else if (saxNode instanceof HText) {
			if (!htmlStack.isEmpty()) {
				HtmlElement htmlElement = htmlStack.peek();
				htmlElement.setHtmlText((HText) saxNode);
			}
		}
	}
	
}

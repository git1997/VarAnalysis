package edu.iastate.parsers.html.htmlparser;

import java.util.ArrayList;
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
		Stack<HtmlElement> savedStack = new Stack<HtmlElement>();
		savedStack.addAll(htmlStack);
		return savedStack;
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
//					if (htmlStack.isEmpty())
//						parseResult.add(htmlElement);
				}
			}
		}
		else if (saxNode instanceof HText) {
			if (!htmlStack.isEmpty()) {
				HtmlElement htmlElement = htmlStack.peek();
				//htmlElement.setHtmlText((HText) saxNode);
				htmlElement.addChildNode(new HtmlText((HText) saxNode));
			}
		}
	}
	
}

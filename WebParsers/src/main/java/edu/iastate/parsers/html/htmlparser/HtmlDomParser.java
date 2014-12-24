package edu.iastate.parsers.html.htmlparser;

import java.util.Arrays;
import java.util.HashSet;

import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.dom.nodes.HtmlText;
import edu.iastate.parsers.html.sax.nodes.HCloseTag;
import edu.iastate.parsers.html.sax.nodes.HOpenTag;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.parsers.html.sax.nodes.HText;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlDomParser {
	
	/**
	 * Parses the htmlSaxNode
	 */
	public void parse(HtmlSaxNode saxNode, DomParserEnv env) {
		/*
		 * Handle self-closed tags (e.g., <br> or <emtpy>)
		 */
		if (isSelfClosedTag(env.getCurrentHtmlElementType())) {
			if (saxNode instanceof HCloseTag && env.closeTagIsValid((HCloseTag) saxNode)) {
				env.addCloseTagToCurrentHtmlElement((HCloseTag) saxNode);
				env.popHtmlStack();
				return;
			}
			env.popHtmlStack();
		}
		
		/*
		 * HOpenTag
		 */
		if (saxNode instanceof HOpenTag) {
			HtmlElement htmlElement = HtmlElement.createHtmlElement((HOpenTag) saxNode);
			env.pushHtmlStack(htmlElement);
			if (htmlElement.getOpenTag().isSelfClosed())
				env.popHtmlStack();
		}
		/*
		 * HCloseTag
		 */
		else if (saxNode instanceof HCloseTag) {
			HCloseTag closeTag = (HCloseTag) saxNode;
			if (env.closeTagIsValid(closeTag)) {
				env.addCloseTagToCurrentHtmlElement(closeTag);
				env.popHtmlStack();
			}
			else {
				MyLogger.log(MyLevel.USER_EXCEPTION, "In HtmlDomParser.java: Encountered CloseTag " + closeTag.getType() + " when in OpenTag " + env.getCurrentHtmlElementType());
			}
		}
		/*
		 * HText
		 */
		else {
			HtmlText htmlText = new HtmlText((HText) saxNode);
			env.addHtmlText(htmlText);
		}
	}
	
	/**
	 * List of self-closing tags 
	 */
	private static HashSet<String> selfClosedTags = new HashSet<String>(Arrays.asList(new String[]{"br", "empty", "input"}));
	
	private static boolean isSelfClosedTag(String tagType) {
		return selfClosedTags.contains(tagType);
	}
	
}

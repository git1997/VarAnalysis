package edu.iastate.parsers.html.dom.nodes;

import java.util.ArrayList;

import edu.iastate.parsers.html.sax.nodes.HOpenTag;
import edu.iastate.parsers.html.sax.nodes.HText;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlElement extends HtmlNode {

	private HOpenTag htmlOpenTag;											// The openTag of this HtmlElement
	
	private ArrayList<HtmlNode> childNodes = new ArrayList<HtmlNode>();	// Its child nodes, type Choice or HtmlElement
	
	private HText htmlText = null;											// Its text
	
	/**
	 * Constructor
	 * @param openTag
	 */
	public HtmlElement(HOpenTag htmlOpenTag) {
		super(htmlOpenTag.getLocation());
		this.htmlOpenTag = htmlOpenTag;
	}
	
	public HOpenTag getHtmlOpenTag() {
		return htmlOpenTag;
	}
	
	public void addChildNode(HtmlNode childNode) {
		childNodes.add(childNode);
	}
	
	public ArrayList<HtmlNode> getChildNodes() {
		return new ArrayList<HtmlNode>(childNodes);
	}
	
	public void setHtmlText(HText htmlText) {
		this.htmlText = htmlText;
	}
	
	public HText getHtmlText() {
		return htmlText;
	}
	
	public String getType() {
		return htmlOpenTag.getType();
	}
	
	@Override
	public String toDebugString() {
		StringBuilder str = new StringBuilder();
		str.append("HtmlElement" + System.lineSeparator());
		str.append("\t" + htmlOpenTag.toDebugString() + System.lineSeparator());
		str.append("\tChild nodes:" + System.lineSeparator());
		for (HtmlNode child : childNodes)
			str.append(child.toDebugString() + System.lineSeparator());
		return str.toString();
	}
	
}

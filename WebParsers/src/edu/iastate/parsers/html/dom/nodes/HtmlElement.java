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
	
	private HtmlElement parentElement = null;	// The parent element

	private HOpenTag htmlOpenTag;											// The openTag of this HtmlElement
	
	private ArrayList<HtmlNode> childNodes = new ArrayList<HtmlNode>();		// Its child nodes, type Choice or HtmlElement
	
	private HText htmlText = null;											// Its text
	
	/**
	 * Constructor
	 * @param openTag
	 */
	public HtmlElement(HOpenTag htmlOpenTag) {
		super(htmlOpenTag.getLocation());
		this.htmlOpenTag = htmlOpenTag;
		
		for (HtmlAttribute attr : htmlOpenTag.getAttributes())
			attr.setParentElement(this);
	}
	
	public HOpenTag getHtmlOpenTag() {
		return htmlOpenTag;
	}
	
	public void addChildNode(HtmlNode childNode) {
		childNodes.add(childNode);
	}
	
	public void replaceLastChildNode(HtmlNode lastChildNode) {
		childNodes.set(childNodes.size() - 1, lastChildNode);
	}
	
	public ArrayList<HtmlNode> getChildNodes() {
		return new ArrayList<HtmlNode>(childNodes);
	}
	
//	public void setHtmlText(HText htmlText) {
//		this.htmlText = htmlText;
//	}
	
	public HtmlText getHtmlText() {
		// TODO Fix here
		if (!childNodes.isEmpty() && childNodes.get(0) instanceof HtmlText)
			return (HtmlText) childNodes.get(0);
		else
			return null;
	}
	
	public String getType() {
		return htmlOpenTag.getType();
	}
	
	public ArrayList<HtmlAttribute> getAttributes() {
		return htmlOpenTag.getAttributes();
	}
	
	public HtmlAttributeValue getAttributeValue(String attributeName) {
		return htmlOpenTag.getAttributeValue(attributeName);
	}
	
	/**
	 * Sets parentElement - Private access: Should only be called from HtmlElement.addChildElement
	 * @param element
	 */
	private void setParentElement(HtmlElement element) {
		this.parentElement = element;
	}
	
	public HtmlElement getParentElement() {
		return parentElement;
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

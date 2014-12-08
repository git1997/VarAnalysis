package edu.iastate.parsers.html.dom.nodes;

import java.util.ArrayList;

import edu.iastate.parsers.html.sax.nodes.HCloseTag;
import edu.iastate.parsers.html.sax.nodes.HOpenTag;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlElement extends HtmlNode {
	
	protected HtmlElement parentElement = null;	// The parent element

	protected HOpenTag openTag;					// The openTag of this HtmlElement
	protected HCloseTag closeTag = null;			// The closeTag of this HtmlElement, can be null
	
	protected ArrayList<HtmlNode> childNodes = new ArrayList<HtmlNode>();	// Its child nodes
	
	/**
	 * Protected constructor
	 * @param openTag
	 */
	protected HtmlElement(HOpenTag openTag) {
		super(openTag.getLocation());
		this.openTag = openTag;
		
		for (HtmlAttribute attr : openTag.getAttributes())
			attr.setParentElement(this);
	}
	
	public static HtmlElement createHtmlElement(HOpenTag openTag) {
		if (openTag.getType().equals("form"))
			return new HtmlForm(openTag);

		else if (openTag.getType().equals("input") || openTag.getType().equals("select") || openTag.getType().equals("textarea"))
			return new HtmlInput(openTag);
		
		else if (openTag.getType().equals("script"))
			return new HtmlScript(openTag);
		
		else
			return new HtmlElement(openTag);
	}
	
	/*
	 * Set properties
	 */
	
	/**
	 * Sets parentElement - Private access: Should only be called from HtmlElement.addChildNode
	 * @param parentElement
	 */
	private void setParentElement(HtmlElement parentElement) {
		this.parentElement = parentElement;
	}
	
	public void setCloseTag(HCloseTag closeTag) {
		this.closeTag = closeTag;
	}
	
	public void addChildNode(HtmlNode childNode) {
		childNodes.add(childNode);
		
		if (childNode instanceof HtmlElement)
			((HtmlElement) childNode).setParentElement(this);
	}
	
	public void replaceLastChildNode(HtmlNode lastChildNode) {
		childNodes.set(childNodes.size() - 1, lastChildNode);
	}
	
	/*
	 * Get properties
	 */
	
	public HtmlElement getParentElement() {
		return parentElement;
	}
	
	public HOpenTag getOpenTag() {
		return openTag;
	}
	
	public HCloseTag getCloseTag() {
		return closeTag;
	}
	
	public ArrayList<HtmlNode> getChildNodes() {
		return new ArrayList<HtmlNode>(childNodes);
	}
	
	public String getType() {
		return openTag.getType();
	}
	
	public ArrayList<HtmlAttribute> getAttributes() {
		return openTag.getAttributes();
	}
	
	public HtmlAttribute getAttribute(String attributeName) {
		return openTag.getAttribute(attributeName);
	}
	
	public HtmlAttributeValue getAttributeValue(String attributeName) {
		return openTag.getAttributeValue(attributeName);
	}
	
	@Override
	public String toDebugString() {
		StringBuilder str = new StringBuilder();
		str.append("HtmlElement" + System.lineSeparator());
		str.append("\t" + openTag.toDebugString() + System.lineSeparator());
		str.append("\tChild nodes:" + System.lineSeparator());
		for (HtmlNode child : childNodes)
			str.append(child.toDebugString() + System.lineSeparator());
		return str.toString();
	}
	
}

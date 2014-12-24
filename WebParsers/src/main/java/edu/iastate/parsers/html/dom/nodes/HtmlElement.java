package edu.iastate.parsers.html.dom.nodes;

import java.util.ArrayList;

import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.sax.nodes.HCloseTag;
import edu.iastate.parsers.html.sax.nodes.HOpenTag;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlElement extends HtmlNode {

	protected HOpenTag openTag;	 // The openTag of this HtmlElement
	
	protected HtmlElement parentElement = null;	// The parent element
	
	protected ArrayList<HtmlNode> childNodes = new ArrayList<HtmlNode>();	// Its child nodes
	
	protected ArrayList<HCloseTag> closeTags = new ArrayList<HCloseTag>();	// The closeTag of this HtmlElement, there could be multiple closeTags (although it rarely happens)
	
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
	
	public void addChildNode(HtmlNode childNode) {
		childNodes.add(childNode);
		
		if (childNode instanceof HtmlElement)
			((HtmlElement) childNode).setParentElement(this);
	}
	
	public void removeLastChildNodes(int count) {
		for (int i = 1; i <= count; i++)
			removeLastChildNode();
	}
	
	public void removeLastChildNode() {
		HtmlNode lastChildNode = childNodes.get(childNodes.size() - 1); 
		if (lastChildNode instanceof HtmlElement)
			((HtmlElement) lastChildNode).setParentElement(null);
		
		childNodes.remove(childNodes.size() - 1);
	}
	
	public void addCloseTag(HCloseTag closeTag) {
		closeTags.add(closeTag);
	}
	
	/*
	 * Get properties
	 */
	
	public HOpenTag getOpenTag() {
		return openTag;
	}
	
	public HtmlElement getParentElement() {
		return parentElement;
	}
	
	public ArrayList<HtmlNode> getChildNodes() {
		return new ArrayList<HtmlNode>(childNodes);
	}
	
	public ArrayList<HCloseTag> getCloseTags() {
		return new ArrayList<HCloseTag>(closeTags);
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
	
	public String getAttributeStringValue(String attributeName) {
		return openTag.getAttributeStringValue(attributeName);
	}
	
	public ArrayList<HtmlToken> getEndBrackets() {
		return openTag.getEndBrackets();
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

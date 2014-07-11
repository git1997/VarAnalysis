package edu.iastate.parsers.html.dom.nodes;

import java.util.ArrayList;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlDocument extends HtmlNode {

	private ArrayList<HtmlNode> childNodes = new ArrayList<HtmlNode>();
	
	/**
	 * Constructor
	 */
	public HtmlDocument() {
		super(PositionRange.UNDEFINED);
	}
	
	public void addChildNode(HtmlNode childNode) {
		childNodes.add(childNode);
	}
	
	public ArrayList<HtmlNode> getChildNodes() {
		return new ArrayList<HtmlNode>(childNodes);
	}

	@Override
	public String toDebugString() {
		StringBuilder str = new StringBuilder();
		for (HtmlNode child : childNodes)
			str.append(child.toDebugString() + System.lineSeparator());
		return str.toString();
	}

}

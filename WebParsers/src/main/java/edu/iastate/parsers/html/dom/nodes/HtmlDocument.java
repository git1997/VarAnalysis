package edu.iastate.parsers.html.dom.nodes;

import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlDocument {

	private ArrayList<HtmlNode> topNodes = new ArrayList<HtmlNode>();
	
	/**
	 * Constructor
	 * @param topNodes The top nodes in this HtmlDocument
	 */
	public HtmlDocument(ArrayList<HtmlNode> topNodes) {
		this.topNodes = topNodes;
	}
	
	public ArrayList<HtmlNode> getTopNodes() {
		return new ArrayList<HtmlNode>(topNodes);
	}

	public String toDebugString() {
		StringBuilder str = new StringBuilder();
		for (HtmlNode topNode : topNodes)
			str.append(topNode.toDebugString() + System.lineSeparator());
		return str.toString();
	}

}

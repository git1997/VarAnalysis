package edu.iastate.parsers.html.dom.nodes;

import java.util.ArrayList;

import edu.iastate.parsers.html.core.WriteHtmlDocumentToIfDefs;

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
	
	/**
	 * Writes the HtmlDocument to #ifdef format
	 */
	public String toIfdefString() {
		return WriteHtmlDocumentToIfDefs.convert(this);
	}

	public String toDebugString() {
		StringBuilder str = new StringBuilder();
		for (HtmlNode topNode : topNodes)
			str.append(topNode.toDebugString() + System.lineSeparator());
		return str.toString();
	}

}

package edu.iastate.parsers.html.dom.nodes;

import java.util.ArrayList;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlConcat extends HtmlNode {
	
	private ArrayList<HtmlNode> childNodes;
	
	public HtmlConcat(ArrayList<HtmlNode> childNodes) {
		super(PositionRange.UNDEFINED);
		this.childNodes = childNodes;
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

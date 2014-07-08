package edu.iastate.parsers.html.dom.nodes;

import java.util.ArrayList;

import edu.iastate.symex.constraints.Constraint;
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
	
	public static HtmlNode createCompactHtmlNode(ArrayList<HtmlNode> childNodes) {
		if (childNodes.isEmpty())
			return null;
		else if (childNodes.size() == 1)
			return childNodes.get(childNodes.size() - 1);
		else
			return new HtmlConcat(childNodes);
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

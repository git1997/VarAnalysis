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
	
	/**
	 * Private constructor
	 */
	private HtmlConcat(ArrayList<HtmlNode> childNodes) {
		super(PositionRange.UNDEFINED);
		this.childNodes = childNodes;
	}
	
	public static HtmlNode createCompactHtmlNode(ArrayList<HtmlNode> childNodes) {
		ArrayList<HtmlNode> compactChildNodes = new ArrayList<HtmlNode>();
		appendChildNodes(compactChildNodes, childNodes);
		
		if (compactChildNodes.isEmpty())
			return null;
		else if (compactChildNodes.size() == 1)
			return compactChildNodes.get(0);
		else
			return new HtmlConcat(compactChildNodes);
	}
	
	public static HtmlNode createCompactHtmlNode(HtmlNode childNode1, HtmlNode childNode2) {
		ArrayList<HtmlNode> childNodes = new ArrayList<HtmlNode>(2);
		childNodes.add(childNode1);
		childNodes.add(childNode2);
		return createCompactHtmlNode(childNodes);
	}
	
	private static void appendChildNodes(ArrayList<HtmlNode> compactChildNodes, ArrayList<HtmlNode> childNodes) {
		for (HtmlNode childNode : childNodes)
			appendChildNode(compactChildNodes, childNode);
	}

	private static void appendChildNode(ArrayList<HtmlNode> compactChildNodes, HtmlNode childNode) {
		if (childNode instanceof HtmlConcat)
			appendChildNodes(compactChildNodes, ((HtmlConcat) childNode).getChildNodes());
		else 
			compactChildNodes.add(childNode);
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

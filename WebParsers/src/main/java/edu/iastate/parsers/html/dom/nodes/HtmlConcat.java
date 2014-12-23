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
	 * Private constructor.
	 * @param childNodes childNodes must be compact (no child nodes of type CondListConcat) and contain at least 2 elements
	 */
	private HtmlConcat(ArrayList<HtmlNode> childNodes) {
		super(PositionRange.UNDEFINED);
		this.childNodes = childNodes;
	}
	
	/**
	 * Creates an HtmlConcat
	 * @param childNodes childNodes can be empty (but must not be null)
	 */
	public static HtmlNode createCompactConcat(ArrayList<HtmlNode> childNodes) {
		ArrayList<HtmlNode> compactChildNodes = new ArrayList<HtmlNode>();
		appendChildNodes(compactChildNodes, childNodes);
		
		if (compactChildNodes.isEmpty())
			return HtmlEmpty.EMPTY;
		else if (compactChildNodes.size() == 1)
			return compactChildNodes.get(0);
		else
			return new HtmlConcat(compactChildNodes);
	}
	
	private static void appendChildNodes(ArrayList<HtmlNode> compactChildNodes, ArrayList<HtmlNode> childNodes) {
		for (HtmlNode childNode : childNodes)
			appendChildNode(compactChildNodes, childNode);
	}

	private static void appendChildNode(ArrayList<HtmlNode> compactChildNodes, HtmlNode childNode) {
		if (childNode instanceof HtmlConcat)
			appendChildNodes(compactChildNodes, ((HtmlConcat) childNode).getChildNodes());
		else if (!(childNode instanceof HtmlEmpty))
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

package edu.iastate.parsers.html.dom.nodes;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 
 * @author HUNG
 *
 */
public abstract class HtmlNode {
	
	protected HashSet<HtmlNode> parentNodes = new HashSet<HtmlNode>(); // The parent nodes (an HtmlNode can have mutiple parents, but they must never form a loop)
	
	protected ArrayList<HtmlNode> childNodes = new ArrayList<HtmlNode>(); // Its child nodes
	
	/*
	 * Set properties
	 */
	
	/**
	 * Adds a childNode
	 * @param childNode
	 */
	public void addChildNode(HtmlNode childNode) {
		childNodes.add(childNode);
		childNode.parentNodes.add(this);
	}
	
	/**
	 * Removes the last childNode
	 */
	public void removeLastChildNode() {
		HtmlNode lastChildNode = childNodes.remove(childNodes.size() - 1);
		lastChildNode.parentNodes.remove(this);
	}
	
	public void removeLastChildNodes(int count) {
		for (int i = 1; i <= count; i++)
			removeLastChildNode();
	}
	
	/*
	 * Get properties
	 */
	
	public HashSet<HtmlNode> getParentNodes() {
		return new HashSet<HtmlNode>(parentNodes);
	}
	
	public ArrayList<HtmlNode> getChildNodes() {
		return new ArrayList<HtmlNode>(childNodes);
	}
	
	public HashSet<HtmlNode> getAncestorNodes() {
		HashSet<HtmlNode> ancestorNodes = new HashSet<HtmlNode>();
		for (HtmlNode parentNode : parentNodes) {
			ancestorNodes.add(parentNode);
			ancestorNodes.addAll(parentNode.getAncestorNodes());
		}
		return ancestorNodes;
	}
	
	/**
	 * Used for debugging
	 */
	public abstract String toDebugString();
	
}

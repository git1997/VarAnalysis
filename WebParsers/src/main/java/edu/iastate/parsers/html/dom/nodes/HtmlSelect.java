package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.symex.constraints.Constraint;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlSelect extends HtmlNode {
	
	private Constraint constraint;
	private HtmlNode trueBranchNode;	// Can be null
	private HtmlNode falseBranchNode;	// Can be null
	
	/**
	 * Private constructor
	 */
	private HtmlSelect(Constraint constraint, HtmlNode trueBranchNode, HtmlNode falseBranchNode) {
		super(constraint.getLocation());
		this.constraint = constraint;
		this.trueBranchNode = trueBranchNode;
		this.falseBranchNode = falseBranchNode;
	}
	
	public static HtmlNode createCompactHtmlNode(Constraint constraint, HtmlNode trueBranchNode, HtmlNode falseBranchNode) {
		if (trueBranchNode == null && falseBranchNode == null)
			return null;
		else
			return new HtmlSelect(constraint, trueBranchNode, falseBranchNode);
	}
	
	public Constraint getConstraint() {
		return constraint;
	}
	
	/**
	 * Returns the True branch node, can be null.
	 */
	public HtmlNode getTrueBranchNode() {
		return trueBranchNode;
	}

	/**
	 * Returns the False branch node, can be null.
	 */
	public HtmlNode getFalseBranchNode() {
		return falseBranchNode;
	}
	
	@Override
	public String toDebugString() {
		String retString = System.lineSeparator() + "#if (" + constraint + ")" + System.lineSeparator()
				+ (trueBranchNode != null ? trueBranchNode.toDebugString() : "null") + System.lineSeparator()
				+ "#else" + System.lineSeparator()
				+ (falseBranchNode != null ? falseBranchNode.toDebugString() : "null") + System.lineSeparator()
				+ "#endif" + System.lineSeparator();
		return retString;
	}

}

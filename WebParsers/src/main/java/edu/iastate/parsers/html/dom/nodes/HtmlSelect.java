package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.symex.constraints.Constraint;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlSelect extends HtmlNode {
	
	private Constraint constraint;
	
	/**
	 * Private constructor
	 */
	private HtmlSelect(Constraint constraint, HtmlNode trueBranchNode, HtmlNode falseBranchNode) {
		this.constraint = constraint;
		this.addChildNode(trueBranchNode);
		this.addChildNode(falseBranchNode);
	}
	
	/**
	 * Creates an HtmlSelect
	 */
	public static HtmlNode createCompactSelect(Constraint constraint, HtmlNode trueBranchNode, HtmlNode falseBranchNode) {
		if (trueBranchNode instanceof HtmlEmpty && falseBranchNode instanceof HtmlEmpty)
			return HtmlEmpty.EMPTY;
		
		return new HtmlSelect(constraint, trueBranchNode, falseBranchNode);
	}
	
	public Constraint getConstraint() {
		return constraint;
	}
	
	public HtmlNode getTrueBranchNode() {
		return childNodes.get(0);
	}

	public HtmlNode getFalseBranchNode() {
		return childNodes.get(1);
	}
	
	@Override
	public String toDebugString() {
		String retString = System.lineSeparator() + "#if (" + constraint.toDebugString() + ")" + System.lineSeparator()
				+ getTrueBranchNode().toDebugString() + System.lineSeparator()
				+ "#else" + System.lineSeparator()
				+ getFalseBranchNode().toDebugString() + System.lineSeparator()
				+ "#endif" + System.lineSeparator();
		return retString;
	}

}

package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.symex.constraints.Constraint;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlSelect extends HtmlNode {
	
	private Constraint constraint;
	private HtmlNode trueBranchNode;
	private HtmlNode falseBranchNode;
	
	/**
	 * Private constructor
	 */
	private HtmlSelect(Constraint constraint, HtmlNode trueBranchNode, HtmlNode falseBranchNode) {
		this.constraint = constraint;
		this.trueBranchNode = trueBranchNode;
		this.falseBranchNode = falseBranchNode;
	}
	
	/**
	 * Creates an HtmlSelect
	 */
	public static HtmlNode createCompactSelect(Constraint constraint, HtmlNode trueBranchNode, HtmlNode falseBranchNode) {
		return new HtmlSelect(constraint, trueBranchNode, falseBranchNode);
	}
	
	public Constraint getConstraint() {
		return constraint;
	}
	
	public HtmlNode getTrueBranchNode() {
		return trueBranchNode;
	}

	public HtmlNode getFalseBranchNode() {
		return falseBranchNode;
	}
	
	@Override
	public String toDebugString() {
		String retString = System.lineSeparator() + "#if (" + constraint.toDebugString() + ")" + System.lineSeparator()
				+ trueBranchNode.toDebugString() + System.lineSeparator()
				+ "#else" + System.lineSeparator()
				+ falseBranchNode.toDebugString() + System.lineSeparator()
				+ "#endif" + System.lineSeparator();
		return retString;
	}

}

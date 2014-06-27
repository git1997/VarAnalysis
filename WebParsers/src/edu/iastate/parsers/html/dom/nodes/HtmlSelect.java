package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlSelect extends HtmlNode {
	
	private Constraint constraint;
	private HtmlNode trueBranchNode;
	private HtmlNode falseBranchNode;
	
	public HtmlSelect(Constraint constraint, HtmlNode trueBranchNode, HtmlNode falseBranchNode) {
		super(PositionRange.UNDEFINED);
		this.constraint = constraint;
		this.trueBranchNode = trueBranchNode;
		this.falseBranchNode = falseBranchNode;
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
		String retString = System.lineSeparator() + "#if (" + constraint + ")" + System.lineSeparator()
				+ (trueBranchNode != null ? trueBranchNode.toDebugString() : "null") + System.lineSeparator()
				+ "#else" + System.lineSeparator()
				+ (falseBranchNode != null ? falseBranchNode.toDebugString() : "null") + System.lineSeparator()
				+ "#endif" + System.lineSeparator();
		return retString;
	}

}

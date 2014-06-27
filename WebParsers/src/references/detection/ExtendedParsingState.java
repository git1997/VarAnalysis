package references.detection;


import java.util.Stack;

import constraints.AndConstraint;
import constraints.AtomicConstraint;
import constraints.Constraint;
import constraints.NotConstraint;

import datamodel.nodes.ext.SelectNode;
import deprecated.html.elements.HtmlTag;
import edu.iastate.parsers.html.htmlparser.ParsingState;


/**
 * ExtendedParsingState is a ParsingState with the 'constraint' property added 
 * to indicate the path constraints of the code the HTML Parser is parsing.
 * 
 * @author HUNG
 *
 */
public class ExtendedParsingState extends ParsingState {
	
	private Constraint constraint;
	
	/**
	 * Saves the state
	 */
	public ExtendedParsingState save() {
		ExtendedParsingState clonedState = new ExtendedParsingState();
		clonedState.lexicalState = lexicalState;
		clonedState.htmlStack = new Stack<HtmlTag>();
		clonedState.htmlStack.addAll(htmlStack);
		clonedState.constraint = constraint;
		return clonedState;
	}
	
	/*
	 * Update constraints
	 */
	
	public void addConstraint(SelectNode selectNode, boolean isTrueBranch) {	
		Constraint newConstraint = (isTrueBranch ? new AtomicConstraint(selectNode) : new NotConstraint(new AtomicConstraint(selectNode)));
		if (constraint == null)
			constraint = newConstraint;
		else
			constraint = new AndConstraint(constraint, newConstraint);
	}
	
	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}
	
	/*
	 * Get properties
	 */
	
	public Constraint getConstraint() {
		return constraint;
	}
	
}

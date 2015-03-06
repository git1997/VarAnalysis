package edu.iastate.symex.core;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.EqualConstraint;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.php.nodes.ExpressionNode;
import edu.iastate.symex.php.nodes.ScalarNode;
import edu.iastate.symex.php.nodes.VariableBaseNode;

/**
 * 
 * @author HUNG
 *
 */
public class BranchEnv extends Env {
	
	private Constraint constraint;
	
	/**
	 * Constructor
	 */
	public BranchEnv(Env outerScopeEnv, Constraint constraint) {
		super(outerScopeEnv);
		this.constraint = constraint;
		
		// Handle constraints with the form "$x == some_value"
		handleEqualConstraint();
	}
	
	/**
	 * Handle constraints with the form "$x == some_value".
	 * In that case, assign $x with some_value in the branchEnv.
	 */
	private void handleEqualConstraint() {
		if (constraint instanceof EqualConstraint) {
			ExpressionNode left = ((EqualConstraint) constraint).getLeftExpression();
			ExpressionNode right = ((EqualConstraint) constraint).getRightExpression();
			if (left instanceof VariableBaseNode && right instanceof ScalarNode) {
				// TODO This action causes the expressions to be re-evaluated. Haven't considered its side effects yet.
				PhpVariable phpVariable = ((VariableBaseNode) left).createVariablePossiblyWithNull(this);
				DataNode value = right.execute(this);
				if (phpVariable != null)
					this.writeVariable(phpVariable, value);
			}
		}
	}
	
	public Constraint getConstraint() {
		return constraint;
	}

}

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
	
	// These fields are used to support assigning variables' values for constraints with the form "$x == some_value"
	// @see edu.iastate.symex.core.BranchEnv.assignVariableValuesOnConstraint()
	private PhpVariable phpVariable = null;
	private DataNode originalValue = null;
	private DataNode newValue = null;
	
	/**
	 * Constructor
	 */
	public BranchEnv(Env outerScopeEnv, Constraint constraint) {
		super(outerScopeEnv);
		this.constraint = constraint;
	}
	
	/**
	 * Handle constraints with the form "$x == some_value".
	 * In that case, assign $x with some_value in the branchEnv.
	 */
	public void assignVariableValuesOnConstraint() {
		if (constraint instanceof EqualConstraint) {
			ExpressionNode left = ((EqualConstraint) constraint).getLeftExpression();
			ExpressionNode right = ((EqualConstraint) constraint).getRightExpression();
			
			if (left instanceof VariableBaseNode && right instanceof ScalarNode) {
				// TODO This action causes the expressions to be re-evaluated. Haven't considered its side effects yet.
				PhpVariable phpVariable = ((VariableBaseNode) left).createVariablePossiblyWithNull(this);
				if (phpVariable != null) {
					DataNode value = right.execute(this);

					this.phpVariable = phpVariable;
					this.originalValue = phpVariable.getValue();
					this.newValue = value;
					
					this.writeVariable(phpVariable, value);
				}
			}
		}
	}
	
	/**
	 * Undo assigning variables' values.
	 * Without doing this, after executing a branch such as if ($x == 1) {}, value of $x will be Select($x == 1, 1, UNSET)
	 * where as we want $x's value to remain as Symbolic.
	 * @see edu.iastate.symex.core.BranchEnv.assignVariableValuesOnConstraint()
	 */
	public void undoAssignVariableValuesOnConstraint() {
		if (phpVariable != null && phpVariable.getValue() == newValue)
			this.writeVariable(phpVariable, originalValue);
	}
	
	public Constraint getConstraint() {
		return constraint;
	}

}

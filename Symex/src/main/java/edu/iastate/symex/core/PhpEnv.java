package edu.iastate.symex.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;

/**
 * 
 * @author HUNG
 *
 */
public abstract class PhpEnv extends Env {
	
	/*
	 * Manage variables in the current scope
	 */
	private HashMap<String, PhpVariable> variableTable = new HashMap<String, PhpVariable>();
	
	/*
	 * Collect output values and return values at return statements.
	 * The final output value will include those output values PLUS the output value from the normal flow.
	 * The final return value will include those return values.
	 */
	private ValueSet outputAtReturns = new ValueSet();
	private ValueSet valueAtReturns = new ValueSet();
	
	/**
	 * Constructor
	 * @param outerScopeEnv
	 */
	public PhpEnv(Env outerScopeEnv) {
		super(outerScopeEnv);
	}
	
	/**
	 * Puts a variable in the CURRENT scope.
	 * Protected method. Should be called by Env only.
	 * NOTE: variableName and phpVariable.getName() do not necessarily match.
	 * E.g., function foo(&$x) {} foo($y). We have map("x" => PhpVariable("y", value_of_y))
	 * @param variableName
	 * @param phpVariable
	 */
	protected void putVariableInCurrentScope(String variableName, PhpVariable phpVariable) {
		variableTable.put(variableName, phpVariable);
	}

	/**
	 * Gets a variable from the CURRENT scope.
	 * Protected method. Should be called by Env only.
	 * NOTE: variableName and phpVariable.getName() do not necessarily match.
	 * E.g., function foo(&$x) {} foo($y). We have map("x" => PhpVariable("y", value_of_y))
	 * @param variableName
	 */
	protected PhpVariable getVariableFromCurrentScope(String variableName) {
		return variableTable.get(variableName);
	}
	
	/**
	 * Returns the set of variables MANAGED by the CURRENT scope.
	 * Protected method. Should be called by Env only.
	 * @see edu.iastate.symex.core.FunctionEnv.getVariablesCreatedFromCurrentScope()
	 */
	protected HashSet<PhpVariable> getVariablesFromCurrentScope() {
		return new HashSet<PhpVariable>(variableTable.values());
	}
	
	/*
	 * Mange the output value
	 */
	
	protected ValueSet getOutputAtReturns_() {
		return outputAtReturns;
	}
	
	protected void collectOutputAtReturn_(Constraint constraint, DataNode value) {
		outputAtReturns.addValue(constraint, value);
	}
	
	protected void clearOutputAtReturns_() {
		outputAtReturns = new ValueSet();
	}
	
	/*
	 * Manage the return value
	 */
	
	protected ValueSet getValueAtReturns_() {
		return valueAtReturns;
	}
	
	protected void collectValueAtReturn_(Constraint constraint, DataNode value) {
		valueAtReturns.addValue(constraint, value);
	}
	
	protected void clearValueAtReturns_() {
		valueAtReturns = new ValueSet();
	}
	
	/*
	 * Handle output values and return values when executing a file
	 * @see FileNode.execute(env)
	 */
	
	protected Object backupOutputAtReturns_() {
		return outputAtReturns;
	}
	
	protected void restoreOutputAtReturns_(Object value) {
		outputAtReturns = (ValueSet) value;
	}
	
	
	protected Object backupValueAtReturns_() {
		return valueAtReturns;
	}
	
	protected void restoreValueAtReturns_(Object value) {
		valueAtReturns = (ValueSet) value;
	}
	
}

/**
 * 
 * @author HUNG
 * ValueSet is a collection of (Constraint, DataNode) pairs, 
 * it is used to record output values (at exit statements) and return values (at return statements)
 */

class ValueSet {
	
	private ArrayList<Constraint> constraints = new ArrayList<Constraint>();
	private ArrayList<DataNode> values = new ArrayList<DataNode>();
	
	/**
	 * Adds a value under a constraint to the valueSet
	 */
	public void addValue(Constraint constraint, DataNode value) {
		constraints.add(constraint);
		values.add(value);
	}
	
	/**
	 * Returns the constraint that is not covered by existing constraints in the ValueSet.
	 * E.g., if ValueSet contains constraints A, B, then this methods returns !(A | B)
	 * NOTE: This method may hang because the combined constraint can be large.
	 */
	public Constraint getUncoveredConstraint() {
		Constraint constraint = Constraint.FALSE;
		for (Constraint c : constraints)
			constraint = ConstraintFactory.createOrConstraint(constraint, c);
		return ConstraintFactory.createNotConstraint(constraint);
	}
	
	/**
	 * Returns a value representing the valueSet, or NULL if the valueSet is empty.
	 * (The last constraint in the valueSet is assumed to add up to TRUE, i.e. constrain[1] | constraint[2] | ... | constraint[n] == TRUE)
	 */
	public DataNode getValue() {
		if (values.isEmpty())
			return null;
		
		DataNode retValue = values.get(values.size() - 1);
		for (int i = values.size() - 2; i >= 0; i--) {
			retValue = DataNodeFactory.createCompactSelectNode(constraints.get(i), values.get(i), retValue);
		}
		return retValue;
	}
	
}

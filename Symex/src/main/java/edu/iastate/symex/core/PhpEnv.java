package edu.iastate.symex.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.SpecialNode;

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
	 * Manage the return values (by a function call or include statement).
	 * The return values are composed of all the return values at return statements.
	 */
	private ValueSet returnValue = new ValueSet();
	
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
	 * Manage the return value
	 */
	
	protected DataNode getReturnValue_() {
		return returnValue.getValue();
	}
	
	protected void addReturnValue_(Constraint constraint, DataNode value) {
		returnValue.addValue(constraint, value);
	}
	
	/*
	 * Handle return values for include statements
	 * @see IncludeNode.execute(env)
	 */
	
	public Object backupReturnValue_() {
		return returnValue;
	}
	
	public void removeReturnValue_() {
		returnValue = new ValueSet();
	}
	
	public void restoreReturnValue_(Object value) {
		returnValue = (ValueSet) value;
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
	 * Returns a value representing the valueSet.
	 * (The last constraint in the valueSet is assumed to add up to TRUE, i.e. constrain[1] | constraint[2] | ... | constraint[n] == TRUE)
	 */
	public DataNode getValue() {
		int size = values.size();
		DataNode retValue = SpecialNode.UnsetNode.UNSET;
		for (int i = size - 1; i >= 0; i--) {
			Constraint constraint = constraints.get(i);
			DataNode value = values.get(i);
			if (retValue == SpecialNode.UnsetNode.UNSET)
				retValue = value;
			else
				retValue = DataNodeFactory.createCompactSelectNode(constraint, value, retValue);
		}
		return retValue;
	}
	
}

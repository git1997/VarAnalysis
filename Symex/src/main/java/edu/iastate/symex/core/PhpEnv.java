package edu.iastate.symex.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
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
	 * Manage the output value
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
	 * (All constraints in the valueSet are assumed to add up to TRUE, i.e. constrain[1] | constraint[2] | ... | constraint[n] == TRUE)
	 */
	public DataNode getValue() {
		if (values.isEmpty())
			return null;
		
		int size = values.size();
		return getValue(values.toArray(new DataNode[size]), constraints.toArray(new Constraint[size]), size);
	}
	
	private DataNode getValue(DataNode[] values, Constraint[] constraints, int size) {
		if (size == 1)
			return values[0];
		
		// combinedValue[i] is the combined value of values[i - 1] and values[i]
		DataNode[] combinedValue = new DataNode[size];
		for (int i = size - 1; i > 0; i--) {
			combinedValue[i] = DataNodeFactory.createCompactSelectNode(constraints[i - 1], values[i - 1], values[i]);
		}
		
		// ratio[i] is the compression ratio if values[i - 1] and values[i] are combined
		// max is the maximum ratio
		double max = 0;
		for (int i = size - 1; i > 0; i--) {
			double ratio = (double) countConcatChildNodes(combinedValue[i]) / (countConcatChildNodes(values[i - 1]) + countConcatChildNodes(values[i]));
			if (ratio > max)
				max = ratio;
		}
		
		// Combine those pairs with compression ratio exceeding a threshold
		// Put the combined pairs and uncombined elements into a new array
		int size_ = 0;
		DataNode[] values_ = new DataNode[size];
		Constraint[] constraints_ = new Constraint[size];
		
		for (int i = size - 1; i > 0; i--) {
			combinedValue[i] = DataNodeFactory.createCompactSelectNode(constraints[i - 1], values[i - 1], values[i]);
			double ratio = (double) countConcatChildNodes(combinedValue[i]) / (countConcatChildNodes(values[i - 1]) + countConcatChildNodes(values[i]));
			if (ratio > max * 0.99) {
				values[i - 1] = combinedValue[i];
				// TODO The statement below is correct but might cause explosion of constraints, so let's comment it out for now, 
				//   and use an approximate constraint.
				//constraints[i - 1] = ConstraintFactory.createOrConstraint(constraints[i - 1], constraints[i]);
				constraints[i - 1] = constraints[i];
			}
			else {
				values_[size_] = values[i];
				constraints_[size_] = constraints[i];
				size_++;
			}
		}
		values_[size_] = values[0];
		constraints_[size_] = constraints[0];
		size_++;
		
		// Reverse the arrays
		for (int i = 0; i <= size_ / 2 - 1; i++) {
			DataNode tmpValue = values_[i];
			values_[i] = values_[size_ - i - 1];
			values_[size_ - i - 1] = tmpValue;
			
			Constraint tmpConstraint = constraints_[i];
			constraints_[i] = constraints_[size_ - i - 1];
			constraints_[size_ - i - 1] = tmpConstraint;
		}
		
		// Do it recursively for the new arrays
		return getValue(values_, constraints_, size_);
	}
	
	/**
	 * Returns the number of child nodes in a dataNode if it is a ConcatNode; otherwise return 1
	 */
	private int countConcatChildNodes(DataNode dataNode) {
		if (dataNode instanceof ConcatNode)
			return ((ConcatNode) dataNode).getChildNodes().size();
		else
			return 1;
	}
	
}

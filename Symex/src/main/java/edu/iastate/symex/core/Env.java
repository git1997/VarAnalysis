package edu.iastate.symex.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.iastate.symex.config.SymexConfig;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.datamodel.nodes.ArrayNode;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.ObjectNode;
import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode.UnsetNode;
import edu.iastate.symex.instrumentation.WebAnalysis;
import edu.iastate.symex.php.nodes.ClassDeclarationNode;
import edu.iastate.symex.php.nodes.FileNode;
import edu.iastate.symex.php.nodes.FunctionDeclarationNode;
import edu.iastate.symex.position.Range;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 * 
 * Env contains information during run time about variables, functions, classes, files, etc.
 * 
 */
public abstract class Env {
	
	private Env outerScopeEnv; // The Env in the outer scope, can be null if it is already the outermost Env (GlobalEnv)
	
	/*
	 * Manage global variables in the current scope
	 * (Variables that are declared with the global keyword)
	 */
	private HashSet<String> globalVariables = new HashSet<String>();
	
	private static String SPECIAL_VARIABLE_OUTPUT = "[OUTPUT]";	// A special global variable representing the output value
	
	/*
	 * Manage variables that have been modified in the branches during symbolic execution.
	 * Depending on the specific algorithm, dirtyVariables might store either the original or the updated values of modified variables.
	 */
	private HashMap<PhpVariable, DataNode> dirtyVariables = new HashMap<PhpVariable, DataNode>();
	
	/**
	 * Constructor
	 * @param outerScopeEnv
	 */
	public Env(Env outerScopeEnv) {
		this.outerScopeEnv = outerScopeEnv;
		
		// Set global variables
		setGlobalVariable(SPECIAL_VARIABLE_OUTPUT);
	}
	
	/*
	 * MANAGE ENVs
	 */
	
	/**
	 * Returns the GlobalEnv
	 */
	private GlobalEnv getGlobalEnv() {
		Env env = this;
		while (!(env instanceof GlobalEnv))
			env = env.outerScopeEnv;
		return (GlobalEnv) env;
	}

	/**
	 * Returns the (inner-most) PhpEnv
	 */
	private PhpEnv getPhpEnv() {
		Env env = this;
		while (!(env instanceof PhpEnv))
			env = env.outerScopeEnv;
		return (PhpEnv) env;
	}
	
	/*
	 * MANAGE VARIABLES
	 */
	
	/**
	 * Creates a variable from its name.
	 * IMPORTANT: The variable's value must be set shortly after the creation of the variable.
	 * @param name
	 */
	public PhpVariable createVariable(String name) {
		return new PhpVariable(name);
	}
	
	/**
	 * Writes a value to a variable. The variable may have an existing value.
	 * Also, the variable will be marked as dirty (as part of the solution for symbolic execution)
	 * @param phpVariable
	 */
	public void writeVariable(PhpVariable phpVariable, DataNode value) {
		// Record dirty variables
		if (!dirtyVariables.containsKey(phpVariable))
			dirtyVariables.put(phpVariable, phpVariable.getValue());
		
		phpVariable.setValue(value);
	}
	
	/**
	 * Puts a variable to the env.
	 * NOTE: name and phpVariable.getName() do not necessarily match.
	 * E.g., function foo(&$x) {} foo($y). We have map("x" => PhpVariable("y", value_of_y))
	 * @param name
	 * @param phpVariable
	 */
	public void putVariable(String name, PhpVariable phpVariable) {
		if (isGlobalVariable(name))
			getGlobalEnv().putVariableInCurrentScope(name, phpVariable);
		else
			getPhpEnv().putVariableInCurrentScope(name, phpVariable);
	}
	
	/**
	 * Gets a variable from the env.
	 * NOTE: name and phpVariable.getName() do not necessarily match.
	 * E.g., function foo(&$x) {} foo($y). We have map("x" => PhpVariable("y", value_of_y))
	 * @param name
	 */
	public PhpVariable getVariable(String name) {
		if (isGlobalVariable(name))
			return getGlobalEnv().getVariableFromCurrentScope(name);
		else
			return getPhpEnv().getVariableFromCurrentScope(name);
	}
	
	/**
	 * Get a variable or put one if it doesn't already exist.
	 * @see getVariable, putVariable
	 * @param name
	 */
	public PhpVariable getOrPutVariable(String name) {
		PhpVariable phpVariable = getVariable(name);
		if (phpVariable == null) {
			phpVariable = createVariable(name);
			putVariable(name, phpVariable);
		}
		return phpVariable;
	}
	
	/**
	 * Either get or put the variable, then write to its value.
	 * @see getVariable, putVariable, writeVariable
	 * @param name
	 * @param value
	 */
	public void getOrPutThenWriteVariable(String name, DataNode value) {
		PhpVariable phpVariable = getOrPutVariable(name);
		writeVariable(phpVariable, value);
	}
	
	/**
	 * Reads the value of a variable. 
	 * Returns UNSET if the variable does not exist.
	 * @param name
	 */
	public DataNode readVariable(String name) {
		PhpVariable phpVariable = getVariable(name);
		if (phpVariable != null)
			return phpVariable.getValue();
		else {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In Env.java: Reading an undefined variable (" + name + ").");
			return SpecialNode.UnsetNode.UNSET;
		}
	}
	
	/**
	 * Either get or put the variable, then read its value.
	 * @see getVariable, putVariable
	 * @param name
	 */
	public DataNode getOrPutThenReadVariable(String name) {
		PhpVariable phpVariable = getOrPutVariable(name);
		return phpVariable.getValue();
	}
	
	/*
	 * MANAGE GLOBAL VARIABLES
	 */
	
	public void setGlobalVariable(String variableName) {
		globalVariables.add(variableName);
	}
	
	public boolean isGlobalVariable(String variableName) {
		return globalVariables.contains(variableName);
	}
	
	public HashSet<String> getGlobalVariables() {
		return new HashSet<String>(globalVariables);
	}
	
	/*
	 * MANAGE PREDEFINED CONSTANTS
	 */

	/**
	 * Sets the value of a predefined constant.
	 */
	public void setPredefinedConstantValue(String constantName,	DataNode constantValue) {
		setGlobalVariable(constantName);
		getOrPutThenWriteVariable(constantName, constantValue);
	}

	/**
	 * Returns the value of a predefined constant.
	 */
	public DataNode getPredefinedConstantValue(String constantName) {
		/* Handle PHP keywords */ 
		if (constantName.toUpperCase().equals("TRUE"))
			return SpecialNode.BooleanNode.TRUE;
		else if (constantName.toUpperCase().equals("FALSE"))
			return SpecialNode.BooleanNode.FALSE;
		else if (constantName.toUpperCase().equals("NULL"))
			return DataNodeFactory.createLiteralNode("");

		/* Handle PHP system constants */
		else if (constantName.toUpperCase().equals("__FILE__"))
			return DataNodeFactory.createLiteralNode(peekFileFromStack().getAbsolutePath());

		/* Other cases */
		else {
			setGlobalVariable(constantName);
			return readVariable(constantName);
		}
	}
	
	/*
	 * MANAGE ARRAYS & OBJECTS
	 */
	
	/**
	 * Get an array element or put one if it doesn't already exist.
	 * @see getOrPutVariable
	 */
	public PhpVariable getOrPutArrayElement(ArrayNode array, String key) {
		PhpVariable phpVariable = array.getElement(key);
		if (phpVariable == null) {
			phpVariable = createVariable(key);
			array.putElement(key, phpVariable);
		}
		return phpVariable;
	}
	
	/**
	 * Either get or put the array element, then write to its value.
	 * @see getOrPutThenWriteVariable
	 */
	public void getOrPutThenWriteArrayElement(ArrayNode array, String key, DataNode value) {
		PhpVariable phpVariable = getOrPutArrayElement(array, key);
		writeVariable(phpVariable, value);
	}
	
	/**
	 * Either get or put the array element, then read its value.
	 * @see getOrPutThenReadVariable
	 */
	public DataNode getOrPutThenReadArrayElement(ArrayNode array, String key) {
		PhpVariable phpVariable = getOrPutArrayElement(array, key);
		return phpVariable.getValue();
	}
	
	/**
	 * Get an object field or put one if it doesn't already exist.
	 * @see getOrPutVariable
	 */
	public PhpVariable getOrPutObjectField(ObjectNode object, String fieldName) {
		PhpVariable phpVariable = object.getField(fieldName);
		if (phpVariable == null) {
			phpVariable = createVariable(fieldName);
			object.putField(fieldName, phpVariable);
		}
		return phpVariable;
	}
	
	/**
	 * Either get or put the object field, then write to its value.
	 * @see getOrPutThenWriteVariable
	 */
	public void getOrPutThenWriteObjectField(ObjectNode object, String fieldName, DataNode value) {
		PhpVariable phpVariable = getOrPutObjectField(object, fieldName);
		writeVariable(phpVariable, value);
	}
	
	/**
	 * Either get or put the object field, then read its value.
	 * @see getOrPutThenReadVariable
	 */
	public DataNode getOrPutThenReadObjectField(ObjectNode object, String fieldName) {
		PhpVariable phpVariable = getOrPutObjectField(object, fieldName);
		return phpVariable.getValue();
	}
	
	/*
	 * MANAGE OUTPUT & RETURN VALUES
	 */
	
	/**
	 * Gets the current output
	 */
	public DataNode getCurrentOutput() {
		return getOrPutThenReadVariable(SPECIAL_VARIABLE_OUTPUT);
	}
	
	/**
	 * Sets the current output
	 */
	private void setCurrentOutput(DataNode value) {
		getOrPutThenWriteVariable(SPECIAL_VARIABLE_OUTPUT, value);
	}

	/**
	 * Appends string values to the current output (used by echo/print)
	 */
	public void appendOutput(ArrayList<DataNode> stringValues) {
		for (DataNode stringValue : stringValues)
			appendOutput(stringValue);
	}
	
	/**
	 * Appends a string value to the current output (used by echo/print)
	 */
	public void appendOutput(DataNode stringValue) {
		DataNode oldValue = getCurrentOutput();
		
		DataNode newValue;
		if (oldValue == SpecialNode.UnsetNode.UNSET)
			newValue = stringValue;
		else
			newValue = DataNodeFactory.createCompactConcatNode(oldValue, stringValue);
		
		setCurrentOutput(newValue);
	}
	
	/**
	 * Collects the output value at some exit statement
	 */
	public void collectOutputAtExit() {
		getGlobalEnv().collectOutputAtExit_(getConjunctedConstraintUpToGlobalEnvScope(), getCurrentOutput());
	}
	
	/**
	 * Collects the output value at some return statement
	 */
	public void collectOutputAtReturn() {
		getPhpEnv().collectOutputAtReturn_(getConjunctedConstraintUpToPhpEnvScope(), getCurrentOutput());
	}
	
	/**
	 * Collectes the return value at some return statement
	 */
	public void collectValueAtReturn(DataNode value) {
		getPhpEnv().collectValueAtReturn_(getConjunctedConstraintUpToPhpEnvScope(), value);
	}
	
	/**
	 * Merges the current output value in the normal flow with the output values collected at exit statements.
	 * Note that the normal flow may not exist (all flows end with exit). 
	 */
	public void mergeCurrentOutputWithOutputAtExits() {
		DataNode outputAtExits = getGlobalEnv().getOutputAtExits_().getValue();
		if (outputAtExits != null) {
			// TODO The correct way to compute the constraint is as follows:
			// 			Constraint constraint = getGlobalEnv().getOutputAtExits_().getUncoveredConstraint();
			// Then, we need to check whether constraint is satisfiable (constraint == FALSE means that there's no normal flow).
			// However, getUncoveredConstraint() often hangs when there are too many constraints.
			// As a work-around, let's create a simple constraint representing the normal case, and assume that the normal flow exists (which is often true).
			Constraint constraint = ConstraintFactory.createAtomicConstraint("NORMAL_OUTPUT", Range.UNDEFINED);

			DataNode mergedOutput = DataNodeFactory.createCompactSelectNode(constraint, getCurrentOutput(), outputAtExits);
			setCurrentOutput(mergedOutput);
			getGlobalEnv().clearOutputAtExits_();
		}
	}
	
	/**
	 * Merges the current output value in the normal flow with the output values collected at return statements.
	 * Note that the normal flow may not exist (all flows end with return). 
	 */
	public void mergeCurrentOutputWithOutputAtReturns() {
		DataNode outputAtReturns = getPhpEnv().getOutputAtReturns_().getValue();
		if (outputAtReturns != null) {
			// TODO Need to verify whether calling getUncoveredConstraint() slows down the execution significantly.
			// If so, we need to do some optimization here (e.g., checking whether the output is really changed at return statements).
			Constraint constraint = getPhpEnv().getOutputAtReturns_().getUncoveredConstraint();
			
			DataNode mergedOutput = (constraint.isSatisfiable() ? DataNodeFactory.createCompactSelectNode(constraint, getCurrentOutput(), outputAtReturns) : outputAtReturns);
			setCurrentOutput(mergedOutput);
			getPhpEnv().clearOutputAtReturns_();
		}
	}
	
	/**
	 * Returns all return values collected at return statements
	 */
	public DataNode getReturnValue() {
		DataNode returnValue = getPhpEnv().getValueAtReturns_().getValue();
		if (returnValue == null)
			return SpecialNode.UnsetNode.UNSET;
		else
			return returnValue;
	}
	
	/*
	 * Handle output values and return values when executing a file
	 * @see FileNode.execute(env)
	 */
	
	public Object backupOutputAtReturns() {
		return getPhpEnv().backupOutputAtReturns_();
	}
	
	public void clearOutputAtReturns() {
		getPhpEnv().clearOutputAtReturns_();
	}
	
	public void restoreOutputAtReturns(Object value) {
		getPhpEnv().restoreOutputAtReturns_(value);
	}
	
	public Object backupValueAtReturns() {
		return getPhpEnv().backupValueAtReturns_();
	}
	
	public void clearValueAtReturns() {
		getPhpEnv().clearValueAtReturns_();
	}
	
	public void restoreValueAtReturns(Object value) {
		getPhpEnv().restoreValueAtReturns_(value);
	}
	
	/*
	 * MANAGE FUNCTIONS, CLASSES, and FILES
	 */

	public void putFunction(String functionName, FunctionDeclarationNode phpFunction) {
		getGlobalEnv().putFunction_(functionName, phpFunction);
	}

	public FunctionDeclarationNode getFunction(String functionName) {
		return getGlobalEnv().getFunction_(functionName);
	}

	public void putClass(String className, ClassDeclarationNode phpClass) {
		getGlobalEnv().putClass_(className, phpClass);
	}

	public ClassDeclarationNode getClass(String className) {
		return getGlobalEnv().getClass_(className);
	}

	public void putFile(File file, FileNode phpFile) {
		getGlobalEnv().putFile_(file, phpFile);
	}

	public FileNode getFile(File file) {
		return getGlobalEnv().getFile_(file);
	}
	
	/*
	 * Manage invoked functions and included files
	 */

	public void pushFunctionToStack(String functionName) {
		getGlobalEnv().pushFunctionToStack_(functionName);
	}

	public String peekFunctionFromStack() {
		return getGlobalEnv().peekFunctionFromStack_();
	}

	public String popFunctionFromStack() {
		return getGlobalEnv().popFunctionFromStack_();
	}

	public boolean containsFunctionInStack(String functionName) {
		return getGlobalEnv().containsFunctionInStack_(functionName);
	}

	public ArrayList<String> getFunctionStack() {
		return new ArrayList<String>(getGlobalEnv().getFunctionStack_());
	}
	
	public void pushFileToStack(File file) {
		getGlobalEnv().pushFileToStack_(file);
	}

	public File peekFileFromStack() {
		return getGlobalEnv().peekFileFromStack_();
	}

	public File popFileFromStack() {
		return getGlobalEnv().popFileFromStack_();
	}

	public boolean containsFileInStack(File file) {
		return getGlobalEnv().containsFileInStack_(file);
	}

	public ArrayList<File> getFileStack() {
		return getGlobalEnv().getFileStack_();
	}
	
	public HashSet<File> getInvokedFiles() {
		return new HashSet<File>(getGlobalEnv().getInvokedFiles_());
	}
	
	/**
	 * Resolves a file based on a value representing the file path.
	 * Returns null if the file cannot be resolved
	 * @param value
	 */
	public File resolveFile(DataNode value) {
		// TODO Get the file path.
		// The correct statements should be as follows:
		//		String filePath = value.getExactStringValueOrNull();
		//		if (filePath == null)
		//			return null;
		// However, we don't want to miss any files, so we currently get an approximate copy of the input DataNode.
		String filePath = value.getStringValueFromLiteralNodes();
		if (filePath.isEmpty())
			return null;
		
		// Standardize the file path
		filePath = filePath.replace('\\', File.separatorChar).replace('/', File.separatorChar);
		
		// Case 1: filePath is absolute
		File file = new File(filePath);
		if (file.isFile())
			return file;

		// Case 2: filePath is relative
		for (File invokedFile : getFileStack()) {
			file = new File(invokedFile.getParent(), filePath);
			if (file.isFile())
				return file;
		}
		
		return null;
	}
	
	/*
	 * MANAGE CONSTRAINTS
	 */
	
	/**
	 * Returns the conjuncted constraints of the current scope and its enclosing scopes, up to the GlobalEnv scope.
	 */
	public Constraint getConjunctedConstraintUpToGlobalEnvScope() {
		if (this instanceof GlobalEnv)
			return Constraint.TRUE;
		
		Constraint outerConstraint = outerScopeEnv.getConjunctedConstraintUpToGlobalEnvScope();
		if (this instanceof BranchEnv)
			return ConstraintFactory.createAndConstraint(outerConstraint, ((BranchEnv) this).getConstraint());
		else
			return outerConstraint;
	}

	/**
	 * Returns the conjuncted constraints of the current scope and its enclosing scopes, up to the (inner-most) PhpEnv scope.
	 */
	public Constraint getConjunctedConstraintUpToPhpEnvScope() {
		if (this instanceof PhpEnv)
			return Constraint.TRUE;
		
		Constraint outerConstraint = outerScopeEnv.getConjunctedConstraintUpToPhpEnvScope();
		// Here, 'this' must be instanceof BranchEnv
		return ConstraintFactory.createAndConstraint(outerConstraint, ((BranchEnv) this).getConstraint());
	}
	
	/*
	 * MANAGE DIRTY VARIABLES & UPDATE ENV DURING EXECUTION
	 */
	
	/**
	 * Backtracks the current Env after executing a branch/loop/function.
	 * 
	 * IDEA: During branch/loop/function execution, dirtyVariables contain the original values of variables in the outer scope,
	 *	 whereas the variables themselves contain new values updated in the current scope.
	 * After the execution, we swap this property. That is, the returned dirtyVariables now contain
	 *	 new values, and the variables themselves are restored to their original values.
	 * @param innerScopeEnv
	 * @return dirtyVariables containing the updated values of the variables after executing the branch/loop/function
	 */
	public HashMap<PhpVariable, DataNode> backtrackAfterExecution(Env innerScopeEnv) {
		HashMap<PhpVariable, DataNode> dirtyVariables = new HashMap<PhpVariable, DataNode>();
		for (PhpVariable variable : innerScopeEnv.dirtyVariables.keySet()) {
			DataNode value = variable.getValue();
			variable.setValue(innerScopeEnv.dirtyVariables.get(variable));
			dirtyVariables.put(variable, value);
		}
		return dirtyVariables;
	}
	
	/**
	 * Updates the current Env after executing two branches.
	 * 
	 * IDEA: At this point, the variables' values have been restored to their original values before entering the branches, 
	 *   (by calling Env.backtrackAfterExecution(Env) prior to calling this method).
	 *   Now dirtyVarsInTrueBranch and dirtyVarsInFalseBranch contain modified values in the branches.
	 *   This method will combine modified values in the branches to update the values of dirty variables after executing the two branches. 
	 * @param constraint
	 * @param dirtyVarsInTrueBranch
	 * @param dirtyVarsInFalseBranch
	 */
	public void updateAfterBranchExecution(Constraint constraint, HashMap<PhpVariable, DataNode> dirtyVarsInTrueBranch, HashMap<PhpVariable, DataNode>  dirtyVarsInFalseBranch) {
		HashSet<PhpVariable> variables = new HashSet<PhpVariable>();
		variables.addAll(dirtyVarsInTrueBranch.keySet());
		variables.addAll(dirtyVarsInFalseBranch.keySet());
		
		for (PhpVariable variable : variables) {
			DataNode valueInTrueBranch = dirtyVarsInTrueBranch.containsKey(variable) ? dirtyVarsInTrueBranch.get(variable) : variable.getValue();
			DataNode valueInFalseBranch = dirtyVarsInFalseBranch.containsKey(variable) ? dirtyVarsInFalseBranch.get(variable) : variable.getValue();
			DataNode compactSelectNode = DataNodeFactory.createCompactSelectNode(constraint, valueInTrueBranch, valueInFalseBranch);
			writeVariable(variable, compactSelectNode);	// This method also updates dirtyVariables for the current Env
		}
	}
	
	public void updateWithOneBranchOnly(HashMap<PhpVariable, DataNode> dirtyVarsInBranch) {
		for (PhpVariable variable : dirtyVarsInBranch.keySet()) {
			writeVariable(variable, dirtyVarsInBranch.get(variable)); // This method also updates dirtyVariables for the current Env
		}
	}
	
	/**
	 * Updates the current Env after executing a loop.
	 * 
	 * IDEA: At this point, the variables' values have been restored to their original values before entering the loop, 
	 *   (by calling Env.backtrackAfterExecution(Env) prior to calling this method).
	 *   Now dirtyVarsInLoop contains modified values in the loop.
	 *   This method will compare the original values and modified values to update the values of dirty variables after executing the loop.
	 * @param loopEnv
	 */
	public void updateAfterLoopExecution(BranchEnv loopEnv, HashMap<PhpVariable, DataNode> dirtyVarsInLoop) {
		Constraint constraint = loopEnv.getConstraint();
		
		for (PhpVariable variable : dirtyVarsInLoop.keySet()) {
			DataNode valueBeforeLoop = variable.getValue();
			DataNode valueInsideLoop = dirtyVarsInLoop.get(variable);
			
			DataNode appendedStringValue = getAppendedStringValue(valueBeforeLoop, valueInsideLoop);
			
			if (appendedStringValue != null) {
				RepeatNode repeatNode = DataNodeFactory.createRepeatNode(constraint, appendedStringValue);
				DataNode valueAfterLoop;
				if (valueBeforeLoop == SpecialNode.UnsetNode.UNSET)
					valueAfterLoop = repeatNode;
				else
					valueAfterLoop = DataNodeFactory.createCompactConcatNode(valueBeforeLoop, repeatNode);
				
				writeVariable(variable, valueAfterLoop); // This method also updates dirtyVariables for the current Env
			}
			else {
				// Disregard modifications in the loop
			}			
		}
	}

	/**
	 * Returns the DataNode that is appended to the stringValueBeforeLoop to get
	 * the stringValueAfterLoop. It is expected that the first child nodes of
	 * the stringValueAfterLoop are the child nodes of the
	 * stringValueBeforeLoop. If it is not so, then we don't know how to handle
	 * it nicely yet, let's return null.
	 */
	private DataNode getAppendedStringValue(DataNode stringValueBeforeLoop, DataNode stringValueAfterLoop) {
		if (stringValueBeforeLoop instanceof UnsetNode)
			return stringValueAfterLoop;

		if (!(stringValueAfterLoop instanceof ConcatNode))
			return null;

		ArrayList<DataNode> stringValuesBeforeLoop = new ArrayList<DataNode>();
		ArrayList<DataNode> stringValuesAfterLoop = new ArrayList<DataNode>();

		if (stringValueBeforeLoop instanceof ConcatNode)
			stringValuesBeforeLoop.addAll(((ConcatNode) stringValueBeforeLoop).getChildNodes());
		else
			stringValuesBeforeLoop.add(stringValueBeforeLoop);
		stringValuesAfterLoop = ((ConcatNode) stringValueAfterLoop).getChildNodes();

		boolean checkPrefix = true; // True if stringValuesBeforeLoop form the
									// prefix of stringValuesAfterLoop
		for (int i = 0; i < stringValuesBeforeLoop.size(); i++) {
			if (i == stringValuesAfterLoop.size()
					|| stringValuesBeforeLoop.get(i) != stringValuesAfterLoop.get(i)) {
				checkPrefix = false;
				break;
			}
		}
		if (!checkPrefix || stringValuesBeforeLoop.size() == stringValuesAfterLoop.size())
			return null;

		DataNode appendedStringValue;
		if (stringValuesBeforeLoop.size() + 1 == stringValuesAfterLoop.size())
			appendedStringValue = stringValuesAfterLoop.get(stringValuesAfterLoop.size() - 1);
		else {
			ArrayList<DataNode> childNodes = new ArrayList<DataNode>();
			for (int i = stringValuesBeforeLoop.size(); i < stringValuesAfterLoop.size(); i++)
				childNodes.add(stringValuesAfterLoop.get(i));
			appendedStringValue = DataNodeFactory.createCompactConcatNode(childNodes);
		}
		return appendedStringValue;
	}
	
	/**
	 * Updates the current Env after executing a function.
	 *
	 * IDEA: At this point, the variables' values have been restored to their original values before entering the function, 
	 *   (by calling Env.backtrackAfterExecution(Env) prior to calling this method).
	 *   Now dirtyVarsInFunction contains modified values in the function. Note that these might include local variables in the function.
	 *   This method will propagate modified values of non-local dirty variables to the current Env.
	 * @param functionEnv
	 */
	public void updateAfterFunctionExecution(FunctionEnv functionEnv, HashMap<PhpVariable, DataNode> dirtyVarsInFunction) {
		// Consider only variables that are dirty and not local to the functionEnv
		HashSet<PhpVariable> localVarsInFunction = functionEnv.getVariablesCreatedFromCurrentScope();
		HashSet<PhpVariable> nonLocalDirtyVarsInFunction = new HashSet<PhpVariable>(dirtyVarsInFunction.keySet());
		nonLocalDirtyVarsInFunction.removeAll(localVarsInFunction);
		
		for (PhpVariable variable : nonLocalDirtyVarsInFunction) {
			DataNode valueInFunction = dirtyVarsInFunction.get(variable);
			writeVariable(variable, valueInFunction); // This method also updates dirtyVariables for the current Env
		}
		
		/*
		 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB ANALYSIS CODE
		if (WebAnalysis.isEnabled())
			WebAnalysis.onFunctionInvocationFinished(new HashSet<PhpVariable>(nonLocalDirtyVarsInFunction), this);
		// END OF WEB ANALYSIS CODE
	}
	
	/*
	 * FINISHING EXECUTION
	 */
	
	/**
	 * Performs a few tasks when the execution is finished.
	 */
	public void finishExecution() {
		if (SymexConfig.COLLECT_OUTPUTS_FROM_EXIT_STATEMENTS)
			mergeCurrentOutputWithOutputAtExits();
	}
	
}

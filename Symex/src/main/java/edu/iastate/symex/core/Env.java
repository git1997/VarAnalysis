package edu.iastate.symex.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.iastate.symex.analysis.WebAnalysis;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.datamodel.nodes.ArrayNode;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.ObjectNode;
import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;
import edu.iastate.symex.php.nodes.ClassDeclarationNode;
import edu.iastate.symex.php.nodes.FileNode;
import edu.iastate.symex.php.nodes.FunctionDeclarationNode;
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
	 * Gets the final output
	 */
	public DataNode getFinalOutput() {
		return getGlobalEnv().getFinalOutput_();
	}
	
	/**
	 * Adds an output value at some exit statement to the final output
	 */
	public void addOutputAtExitToFinalOutput() {
		getGlobalEnv().addOuptutAtExitToFinalOutput_(getConjunctedConstraintUpToGlobalEnvScope(), getCurrentOutput());
	}
	
	/**
	 * Adds the output value in the normal flow to the final output
	 */
	public void addNormalOutputToFinalOutput() {
		getGlobalEnv().addNormalOutputToFinalOutput_(getCurrentOutput());
	}
	
	/**
	 * Gets the return value
	 */
	public DataNode getReturnValue() {
		return getPhpEnv().getReturnValue_();
	}
	
	/**
	 * Adds a return value (at some return statement)
	 */
	public void addReturnValue(DataNode value) {
		getPhpEnv().addReturnValue_(getConjunctedConstraintUpToPhpEnvScope(), value);
	}
	
	/*
	 * Handle return values for include statements
	 * @see IncludeNode.execute(env)
	 */
	
	public Object backupReturnValue() {
		return getPhpEnv().backupReturnValue_();
	}
	
	public void removeReturnValue() {
		getPhpEnv().removeReturnValue_();
	}
	
	public void restoreReturnValue(Object value) {
		getPhpEnv().restoreReturnValue_(value);
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
	 * Returns a copy of the dirtyVariables map
	 */
	protected HashMap<PhpVariable, DataNode> copyDirtyVariables() {
		return new HashMap<PhpVariable, DataNode>(dirtyVariables);
	}
	
	/**
	 * Returns the set of dirtyVariables
	 */
	protected HashSet<PhpVariable> getDirtyVariables() {
		return new HashSet<PhpVariable>(dirtyVariables.keySet());
	}
	
	/**
	 * Backtracks the current Env after executing a branch.
	 * 
	 * IDEA: During branch execution, dirtyVariables contain old values of variables before the branch,
	 *	whereas the variables themselves contain new values updated in the branch.
	 *	After branch execution, we swap this property. That is, the returned dirtyVariables now contain
	 *	new values, and the variables themselves are restored to the old values.
	 * @param branchEnv
	 * @return dirtyVariables containing the updated values of the variables after executing the branch
	 */
	public HashMap<PhpVariable, DataNode> backtrackAfterBranchExecution(BranchEnv branchEnv) {
		HashMap<PhpVariable, DataNode> dirtyVarsInBranch = branchEnv.copyDirtyVariables();
		for (PhpVariable variable : new HashSet<PhpVariable>(dirtyVarsInBranch.keySet())) { // Get a new HashSet since the map is updated in the loop
			DataNode value = variable.getValue();
			variable.setValue(dirtyVarsInBranch.get(variable));
			dirtyVarsInBranch.put(variable, value);
		}
		return dirtyVarsInBranch;
	}
	
	/**
	 * Updates the current Env after executing two branches
	 * 
	 * IDEA: At this point, the variables'values have been restored to their values before entering the branches, see backtrackAfterBranchExecution(BranchEnv).
	 * Now dirtyVarsInTrueBranch and dirtyVarsInFalseBranch contain modified values in the branches.
	 * This method will combine the old values and dirty values in the branches to update the values of variables after executing the two branches. 
	 * @param constraint
	 * @param dirtyVarsInTrueBranch
	 * @param dirtyVarsInFalseBranch
	 * @paramm trueBranchRetValue
	 * @param falseBranchRetValue
	 */
	public void updateAfterBranchExecution(Constraint constraint, HashMap<PhpVariable, DataNode> dirtyVarsInTrueBranch, HashMap<PhpVariable, DataNode>  dirtyVarsInFalseBranch, DataNode trueBranchRetValue, DataNode falseBranchRetValue) {
		/*
		 * Handle return/exit statements in the branches.
		 * For an ifStatement: E; if (C) { A; return; } else { B; } D;
		 * the best transformation is
		 * 		=> E; if (C) A; else { B; D; }
		 * However, currently we can probably only use an approximate transformation as follows
		 * 		=> E; B; D; (disregard A)
		 */
		if (isTerminated(trueBranchRetValue) && !isTerminated(falseBranchRetValue)) {
			updateWithOneBranchOnly(dirtyVarsInFalseBranch);
			return;
		}
		else if (isTerminated(falseBranchRetValue) && !isTerminated(trueBranchRetValue)) {
			updateWithOneBranchOnly(dirtyVarsInTrueBranch);
			return;
		}
		
		/*
		 * Handle regular cases
		 */
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
	
	private void updateWithOneBranchOnly(HashMap<PhpVariable, DataNode> dirtyVarsInBranch) {
		for (PhpVariable variable : dirtyVarsInBranch.keySet())
			writeVariable(variable, dirtyVarsInBranch.get(variable)); // This method also updates dirtyVariables for the current Env
	}
	
	/**
	 * Returns true if the branch is terminated by EXIT/RETURN statements.
	 */
	private boolean isTerminated(DataNode branchRetValue) {
		return branchRetValue == SpecialNode.ControlNode.EXIT || branchRetValue == SpecialNode.ControlNode.RETURN;
	}
	
	/**
	 * Updates the current Env after executing a loop.
	 * 
	 * IDEA: dirtyVarsInLoop contain original values of modified variables after the execution of the loop.
	 * This method will compare the old values and modified values in the loop to update the values of variables after executing the loop.
	 * @param loopEnv
	 */
	public void updateAfterLoopExecution(BranchEnv loopEnv) {
		Constraint constraint = loopEnv.getConstraint();
		HashMap<PhpVariable, DataNode> dirtyVarsInLoop = loopEnv.copyDirtyVariables();
		
		for (PhpVariable variable : dirtyVarsInLoop.keySet()) {
			DataNode valueBeforeLoop = dirtyVarsInLoop.get(variable);
			DataNode valueInsideLoop = variable.getValue();
			
			DataNode appendedStringValue = getAppendedStringValue(valueBeforeLoop, valueInsideLoop);
			DataNode valueAfterLoop;
			
			if (appendedStringValue != null) {
				RepeatNode repeatNode = DataNodeFactory.createRepeatNode(constraint, appendedStringValue);
				if (valueBeforeLoop == SpecialNode.UnsetNode.UNSET)
					valueAfterLoop = repeatNode;
				else
					valueAfterLoop = DataNodeFactory.createCompactConcatNode(valueBeforeLoop, repeatNode);
			}
			else
				valueAfterLoop = valueBeforeLoop;
			
			writeVariable(variable, valueAfterLoop); // This method also updates dirtyVariables for the current Env
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
		if (stringValueBeforeLoop == null)
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
	 * IDEA: dirtyVarsInFunction contain original values of modified variables after the execution of the function.
	 * These might include local variables in the function, we disregard those variables.
	 * For the remaining variables (dirty and not local to the function), their values are already updated (by the function),
	 * we now only have to mark them as dirty values for the current Env.
	 * @param functionEnv
	 */
	public void updateAfterFunctionExecution(FunctionEnv functionEnv) {
		HashMap<PhpVariable, DataNode> dirtyVarsMapInFunction = functionEnv.copyDirtyVariables();
		HashSet<PhpVariable> dirtyVarsInFunction = functionEnv.getDirtyVariables();
		HashSet<PhpVariable> localVarsInFunction = functionEnv.getVariablesCreatedFromCurrentScope();
		
		// Consider only variables that are dirty and not local to the functionEnv
		HashSet<PhpVariable> nonLocalDirtyVarsInFunction = dirtyVarsInFunction;
		nonLocalDirtyVarsInFunction.removeAll(localVarsInFunction);
		
		for (PhpVariable variable : nonLocalDirtyVarsInFunction) {
			// Update the set of dirtyVariables for the current scope
			if (!this.dirtyVariables.containsKey(variable))
				this.dirtyVariables.put(variable, dirtyVarsMapInFunction.get(variable));
		}
		
		/*
		 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB ANALYSIS CODE
		if (WebAnalysis.entityDetectionListener != null)
			WebAnalysis.onFunctionInvocationFinished(nonLocalDirtyVarsInFunction, this);
		// END OF WEB ANALYSIS CODE
	}
	
	/*
	 * FINISHING EXECUTION
	 */
	
	/**
	 * Performs a few tasks when the execution is finished.
	 */
	public void finishExecution() {
		addNormalOutputToFinalOutput();
	}
	
}

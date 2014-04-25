package edu.iastate.symex.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.php.elements.PhpClass;
import edu.iastate.symex.php.elements.PhpFile;
import edu.iastate.symex.php.elements.PhpFunction;
import edu.iastate.symex.php.elements.PhpVariable;
import edu.iastate.symex.php.nodes.ExpressionNode;
import edu.iastate.symex.php.nodes.FormalParameterNode;
import edu.iastate.symex.php.nodes.ScalarNode;
import edu.iastate.symex.php.nodes.VariableNode;
import edu.iastate.symex.util.StringUtils;

/**
 * 
 * @author HUNG
 * 
 */
public class Env {

	/*
	 * Manage the scopes of elements
	 */
	public enum ScopeType {
		PROGRAM, FUNCTION, BRANCH
	}

	private ScopeType scopeType; // The scope type of the current env
	private Env outerScopeenv; // Manages the elements in
														// the outer scope

	/*
	 * Manage the variables in the current scope
	 */
	private HashMap<String, PhpVariable> variableTable = new HashMap<String, PhpVariable>();
	private static String SPECIAL_VARIABLE_OUTPUT = "[OUTPUT]"; // Used
																// throughout
																// the execution
	private static String SPECIAL_VARIABLE_FINAL_OUTPUT = "[FINAL_OUTPUT]"; // Used
																			// in
																			// PROGRAM
																			// scope
																			// only
	private static String SPECIAL_VARIABLE_RETURN = "[RETURN]"; // Used in
																// PROGRAM/FUNCTION
																// scope only

	/*
	 * Manage global variables
	 */
	private HashSet<String> globalVariableNames = new HashSet<String>(); // Used
																			// in
																			// PROGRAM/FUNCTION
																			// scope
																			// only

	/*
	 * Manage the declarations of functions, classes, and files, so that they
	 * only need to be parsed once.
	 */
	private static File workingDirectory = new File(".");
	private static HashMap<String, PhpFunction> functionTable = new HashMap<String, PhpFunction>();
	private static HashMap<String, PhpClass> classTable = new HashMap<String, PhpClass>();
	private static HashMap<File, PhpFile> fileTable = new HashMap<File, PhpFile>();

	/*
	 * These fields are used to prevent recursive function/program invocation
	 */
	private Stack<String> functionStack = new Stack<String>(); // Used in
																// PROGRAM scope
																// only
	private Stack<File> fileStack = new Stack<File>(); // Used in PROGRAM scope
														// only
	private HashSet<File> invokedFiles = new HashSet<File>(); // Used in PROGRAM
																// scope only

	/*
	 * These fields are used to handle return/exit statements.
	 */
	private LiteralNode conditionString = null;
	private boolean isTrueBranch = true;
	private boolean hasReturnStatement = false;
	private boolean hasExitStatement = false;

	/**
	 * Resets the static fields every time the main program is executed to save
	 * memory space and prevent caching.
	 */
	public static void resetStaticFields() {
		functionTable = new HashMap<String, PhpFunction>();
		classTable = new HashMap<String, PhpClass>();
		fileTable = new HashMap<File, PhpFile>();
	}

	/**
	 * Constructor PROGRAM scope.
	 */
	public Env() {
		this.scopeType = ScopeType.PROGRAM;
		this.outerScopeenv = null;
	}

	/**
	 * Constructor FUNCTION scope
	 */
	public Env(Env outerScopeenv,
			String functionName) {
		this.scopeType = ScopeType.FUNCTION;
		this.outerScopeenv = outerScopeenv;
	}

	/**
	 * Constructor BRANCH scope
	 */
	public Env(Env outerScopeenv,
			LiteralNode conditionString, boolean isTrueBranch) {
		this.scopeType = ScopeType.BRANCH;
		this.outerScopeenv = outerScopeenv;

		this.conditionString = conditionString;
		this.isTrueBranch = isTrueBranch;
	}

	/*
	 * Get the env from different scopes
	 */

	/**
	 * Returns the env that contains this env and has a
	 * PROGRAM/FUNCTION scope, or returns itself if it already has a
	 * PROGRAM/FUNCTION scope.
	 */
	public Env getFunctionScopeenv() {
		if (scopeType == ScopeType.PROGRAM || scopeType == ScopeType.FUNCTION)
			return this;
		else
			return outerScopeenv.getFunctionScopeenv();
	}

	/**
	 * Returns the outermost env (the env of the program).
	 */
	public Env getProgramScopeenv() {
		if (scopeType == ScopeType.PROGRAM)
			return this;
		else
			return outerScopeenv.getProgramScopeenv();
	}

	/*
	 * Manage VARIABLES. Typically, a write access will be done on the current
	 * scope's element manager, whereas a read access will be done on a function
	 * scope element manager.
	 */

	/**
	 * Puts a variable in the CURRENT scope
	 * 
	 * @param variableName
	 * @param phpElement
	 */
	public void putVariableInCurrentScope(PhpVariable phpVariable) {
		variableTable.put(phpVariable.getName(), phpVariable);
	}

	/**
	 * Gets a variable from the CURRENT scope
	 * 
	 * @param variableName
	 * @return
	 */
	private PhpVariable getVariableFromCurrentScope(String variableName) {
		return variableTable.get(variableName);
	}

	/**
	 * Gets a variable from a FUNCTION scope (used for non-global variables)
	 * 
	 * @param variableName
	 * @return
	 */
	public PhpVariable getVariableFromFunctionScope(String variableName) {
		PhpVariable phpVariable = this
				.getVariableFromCurrentScope(variableName);
		if (phpVariable != null)
			return phpVariable;
		else if (scopeType == ScopeType.PROGRAM
				|| scopeType == ScopeType.FUNCTION)
			return null;
		else
			return outerScopeenv
					.getVariableFromFunctionScope(variableName);
	}

	/**
	 * Gets a variable from the PROGRAM scope (used for global variables)
	 * 
	 * @param variableName
	 * @return
	 */
	private PhpVariable getVariableFromProgramScope(String variableName) {
		PhpVariable phpVariable = this
				.getVariableFromCurrentScope(variableName);
		if (phpVariable != null)
			return phpVariable;
		else if (scopeType == ScopeType.PROGRAM)
			return null;
		else
			return outerScopeenv
					.getVariableFromProgramScope(variableName);
	}

	/*
	 * Manage GLOBAL VARIABLES
	 */

	/**
	 * Adds a global variable.
	 */
	public void addGlobalVariable(VariableNode globalVariableNode) {
		String globalVariableName = globalVariableNode
				.getResolvedVariableNameOrNull(this);
		PhpVariable globalVariable = new PhpVariable(globalVariableName);
		PhpVariable referredGlobalVariable = this
				.getVariableFromProgramScope(globalVariableName);
		if (referredGlobalVariable != null)
			globalVariable.setDataNode(referredGlobalVariable.getDataNode());
		else
			globalVariable.setDataNode(new SymbolicNode(globalVariableNode));
		this.putVariableInCurrentScope(globalVariable);
		this.getFunctionScopeenv().globalVariableNames
				.add(globalVariableName);
	}

	/**
	 * Returns all global variables in the function
	 */
	public HashSet<String> getGlobalVariableNames() {
		return new HashSet<String>(
				this.getFunctionScopeenv().globalVariableNames);
	}

	/*
	 * Manage PREDEFINED CONSTANTS
	 */

	/**
	 * Sets the value of a predefined constant.
	 */
	public void setPredefinedConstantValue(String constantName,
			DataNode constantValue) {
		PhpVariable phpConstant = new PhpVariable(constantName);
		phpConstant.setDataNode(constantValue);
		this.getProgramScopeenv().putVariableInCurrentScope(
				phpConstant);
	}

	/**
	 * Returns the value of a predefined constant.
	 */
	public DataNode getPredefinedConstantValue(ScalarNode scalarNode) {
		String constantName = scalarNode.getSourceCode();
		PhpVariable phpConstant = this.getProgramScopeenv()
				.getVariableFromCurrentScope(constantName);

		/* Get the value if it has been defined */
		if (phpConstant != null)
			return phpConstant.getDataNode();

		/* Handle PHP keywords */
		else if (constantName.toUpperCase().equals("TRUE"))
			return DataNodeFactory.createLiteralNode("TRUE");
		else if (constantName.toUpperCase().equals("FALSE"))
			return DataNodeFactory.createLiteralNode("FALSE");
		else if (constantName.toUpperCase().equals("NULL"))
			return DataNodeFactory.createLiteralNode("");

		/* Handle PHP system constants */
		else if (constantName.toUpperCase().equals("__FILE__"))
			return DataNodeFactory.createLiteralNode(getWorkingDirectory()
					+ StringUtils.getFileSystemSlash() + peekFileFromStack());

		/* Else, return a symbolic value */
		else
			return new SymbolicNode(scalarNode);
	}

	/*
	 * Manage FUNCTIONS, CLASSES, and FILES.
	 */

	public void setWorkingDirectory(File projectFolder_) {
		workingDirectory = projectFolder_;
	}

	public File getWorkingDirectory() {
		return workingDirectory;
	}

	public void putFunction(String functionName, PhpFunction phpFunction) {
		Env.functionTable.put(functionName, phpFunction);
	}

	public PhpFunction getFunction(String functionName) {
		return Env.functionTable.get(functionName);
	}

	public void putClass(String className, PhpClass phpClass) {
		Env.classTable.put(className, phpClass);
	}

	public PhpClass getClass(String className) {
		return Env.classTable.get(className);
	}

	public void putFile(File fileName, PhpFile phpFile) {
		Env.fileTable.put(fileName, phpFile);
	}

	public PhpFile getFile(File fileName) {
		return Env.fileTable.get(fileName);
	}

	/*
	 * Manage invoked functions and included files
	 */

	public void pushFunctionToStack(String functionName) {
		this.getProgramScopeenv().functionStack.push(functionName);
	}

	public String peekFunctionFromStack() {
		return this.getProgramScopeenv().functionStack.peek();
	}

	public void popFunctionFromStack() {
		this.getProgramScopeenv().functionStack.pop();
	}

	public boolean containsFunctionInStack(String functionName) {
		return this.getProgramScopeenv().functionStack
				.contains(functionName);
	}

	public ArrayList<String> getFunctionStack() {
		return new ArrayList<String>(
				this.getProgramScopeenv().functionStack);
	}

	public void pushFileToStack(File fileName) {
		this.getProgramScopeenv().fileStack.push(fileName);
		addInvokedFiles(fileName);
	}

	public File peekFileFromStack() {
		return this.getProgramScopeenv().fileStack.peek();
	}

	public void popFileFromStack() {
		this.getProgramScopeenv().fileStack.pop();
	}

	public boolean containsFileInStack(File fileName) {
		return this.getProgramScopeenv().fileStack
				.contains(fileName);
	}

	public ArrayList<File> getFileStack() {
		return new ArrayList<File>(
				this.getProgramScopeenv().fileStack);
	}

	public void addInvokedFiles(File fileName) {
		this.getProgramScopeenv().invokedFiles.add(fileName);
	}

	public HashSet<File> getInvokedFiles() {
		return new HashSet<File>(
				this.getProgramScopeenv().invokedFiles);
	}

	/*
	 * Manage constraints
	 */

	/**
	 * Returns the set of constraints for the current scope.
	 */
	public ArrayList<Constraint> getConstraints() {
		ArrayList<Constraint> constraints = new ArrayList<Constraint>();

		if (scopeType == ScopeType.BRANCH) {
			constraints.add(new Constraint(conditionString, isTrueBranch));
			constraints.addAll(outerScopeenv.getConstraints());
		}

		return constraints;
	}

	/*
	 * Update env when executing the program.
	 */

	/**
	 * Updates the env after executing some branches.
	 */
	public void updateWithBranches(LiteralNode conditionString,
			Env trueBranchenv,
			Env falseBranchenv) {
		// If a branch has a return/exit statement, update the variables in the
		// current scope with the other branch
		boolean trueBranchTerminated = (trueBranchenv != null && (trueBranchenv
				.hasReturnStatement() || trueBranchenv
				.hasExitStatement()));
		boolean falseBranchTerminated = (falseBranchenv != null && (falseBranchenv
				.hasReturnStatement() || falseBranchenv
				.hasExitStatement()));

		if (trueBranchTerminated || falseBranchTerminated) {
			if (trueBranchTerminated && !falseBranchTerminated
					&& falseBranchenv != null)
				this.updateVariableTable(falseBranchenv);
			else if (!trueBranchTerminated && falseBranchTerminated
					&& trueBranchenv != null)
				this.updateVariableTable(trueBranchenv);
			return;
		}

		// Else, update the variables in the current scope considering their
		// values in both branches.
		HashSet<String> variableNamesInTrueBranch = (trueBranchenv != null ? trueBranchenv
				.getRegularVariableNames() : new HashSet<String>());
		HashSet<String> variableNamesInFalseBranch = (falseBranchenv != null ? falseBranchenv
				.getRegularVariableNames() : new HashSet<String>());
		HashSet<String> variableNamesInEitherBranch = new HashSet<String>(
				variableNamesInTrueBranch);
		variableNamesInEitherBranch.addAll(variableNamesInFalseBranch);

		for (String variableName : variableNamesInEitherBranch) {
			PhpVariable variableInTrueBranch = (trueBranchenv != null ? trueBranchenv
					.getVariableFromFunctionScope(variableName) : this
					.getVariableFromFunctionScope(variableName));
			PhpVariable variableInFalseBranch = (falseBranchenv != null ? falseBranchenv
					.getVariableFromFunctionScope(variableName) : this
					.getVariableFromFunctionScope(variableName));

			DataNode dataNodeInTrueBranch = (variableInTrueBranch != null ? variableInTrueBranch.getDataNode() : null);
			DataNode dataNodeInFalseBranch = (variableInFalseBranch != null ? variableInFalseBranch.getDataNode() : null);
			DataNode compactSelectNode = DataNodeFactory.createCompactSelectNode(conditionString, dataNodeInTrueBranch, dataNodeInFalseBranch);

			PhpVariable phpVariable = new PhpVariable(variableName);
			phpVariable.setDataNode(compactSelectNode);
			this.putVariableInCurrentScope(phpVariable);
		}

		// Also, update the output in the current scope considering its values
		// in both branches.
		if (trueBranchenv != null
				&& trueBranchenv.containsSpecialVariableOutput()
				|| falseBranchenv != null
				&& falseBranchenv.containsSpecialVariableOutput()) {
			PhpVariable variableInTrueBranch = (trueBranchenv != null ? trueBranchenv
					.getVariableFromProgramScope(SPECIAL_VARIABLE_OUTPUT)
					: this.getVariableFromProgramScope(SPECIAL_VARIABLE_OUTPUT));
			PhpVariable variableInFalseBranch = (falseBranchenv != null ? falseBranchenv
					.getVariableFromProgramScope(SPECIAL_VARIABLE_OUTPUT)
					: this.getVariableFromProgramScope(SPECIAL_VARIABLE_OUTPUT));

			DataNode dataNodeInTrueBranch = (variableInTrueBranch != null ? variableInTrueBranch.getDataNode() : null);
			DataNode dataNodeInFalseBranch = (variableInFalseBranch != null ? variableInFalseBranch.getDataNode() : null);
			DataNode compactSelectNode = DataNodeFactory.createCompactSelectNode(conditionString, dataNodeInTrueBranch, dataNodeInFalseBranch);

			PhpVariable phpVariable = new PhpVariable(SPECIAL_VARIABLE_OUTPUT);
			phpVariable.setDataNode(compactSelectNode);
			this.putVariableInCurrentScope(phpVariable);
		}
	}

	/**
	 * Updates the variableTable with the one in one of the branches.
	 */
	private void updateVariableTable(Env branchenv) {
		// Update regular variables
		for (String variableName : branchenv
				.getRegularVariableNames()) {
			PhpVariable phpVariable = branchenv
					.getVariableFromCurrentScope(variableName);
			this.putVariableInCurrentScope(phpVariable);
		}
		// Update output
		if (branchenv.containsSpecialVariableOutput()) {
			PhpVariable phpVariable = branchenv
					.getVariableFromCurrentScope(SPECIAL_VARIABLE_OUTPUT);
			this.putVariableInCurrentScope(phpVariable);
		}
	}

	/**
	 * Updates the env after executing a loop.
	 */
	public void updateWithLoop(LiteralNode conditionString,
			Env loopenv) {
		// Update regular variables
		HashSet<String> variableNamesInsideLoop = loopenv
				.getRegularVariableNames();
		for (String variableName : variableNamesInsideLoop) {
			PhpVariable variableBeforeLoop = this
					.getVariableFromFunctionScope(variableName);
			PhpVariable variableInsideLoop = loopenv
					.getVariableFromCurrentScope(variableName);

			DataNode dataNodeBeforeLoop = (variableBeforeLoop != null ? variableBeforeLoop
					.getDataNode() : null);
			DataNode dataNodeAfterLoop = variableInsideLoop.getDataNode();

			DataNode appendedStringValue = getAppendedStringValue(
					dataNodeBeforeLoop, dataNodeAfterLoop);
			if (appendedStringValue != null) {
				PhpVariable phpVariable = new PhpVariable(variableName);
				if (dataNodeBeforeLoop != null)
					phpVariable.appendStringValue(dataNodeBeforeLoop);

				RepeatNode repeatNode = new RepeatNode(conditionString,
						appendedStringValue);
				phpVariable.appendStringValue(repeatNode);
				this.putVariableInCurrentScope(phpVariable);
			}
		}
		// Update output
		if (loopenv.containsSpecialVariableOutput()) {
			PhpVariable variableBeforeLoop = this
					.getVariableFromProgramScope(SPECIAL_VARIABLE_OUTPUT);
			PhpVariable variableInsideLoop = loopenv
					.getVariableFromCurrentScope(SPECIAL_VARIABLE_OUTPUT);

			DataNode dataNodeBeforeLoop = (variableBeforeLoop != null ? variableBeforeLoop
					.getDataNode() : null);
			DataNode dataNodeAfterLoop = variableInsideLoop.getDataNode();
			DataNode appendedStringValue = getAppendedStringValue(
					dataNodeBeforeLoop, dataNodeAfterLoop);

			if (appendedStringValue != null) {
				PhpVariable phpVariable = new PhpVariable(
						SPECIAL_VARIABLE_OUTPUT);
				if (dataNodeBeforeLoop != null)
					phpVariable.appendStringValue(dataNodeBeforeLoop);

				RepeatNode repeatNode = new RepeatNode(conditionString,
						appendedStringValue);
				phpVariable.appendStringValue(repeatNode);
				this.putVariableInCurrentScope(phpVariable);
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
	private DataNode getAppendedStringValue(DataNode stringValueBeforeLoop,
			DataNode stringValueAfterLoop) {
		if (stringValueBeforeLoop == null)
			return stringValueAfterLoop;

		if (!(stringValueAfterLoop instanceof ConcatNode))
			return null;

		ArrayList<DataNode> stringValuesBeforeLoop = new ArrayList<DataNode>();
		ArrayList<DataNode> stringValuesAfterLoop = new ArrayList<DataNode>();

		if (stringValueBeforeLoop instanceof ConcatNode)
			stringValuesBeforeLoop.addAll(((ConcatNode) stringValueBeforeLoop)
					.getChildNodes());
		else
			stringValuesBeforeLoop.add(stringValueBeforeLoop);
		stringValuesAfterLoop = ((ConcatNode) stringValueAfterLoop)
				.getChildNodes();

		boolean checkPrefix = true; // True if stringValuesBeforeLoop form the
									// prefix of stringValuesAfterLoop
		for (int i = 0; i < stringValuesBeforeLoop.size(); i++) {
			if (i == stringValuesAfterLoop.size()
					|| stringValuesBeforeLoop.get(i) != stringValuesAfterLoop
							.get(i)) {
				checkPrefix = false;
				break;
			}
		}
		if (!checkPrefix
				|| stringValuesBeforeLoop.size() == stringValuesAfterLoop
						.size())
			return null;

		DataNode appendedStringValue;
		if (stringValuesBeforeLoop.size() + 1 == stringValuesAfterLoop.size())
			appendedStringValue = stringValuesAfterLoop
					.get(stringValuesAfterLoop.size() - 1);
		else {
			ArrayList<DataNode> childNodes = new ArrayList<DataNode>();
			for (int i = stringValuesBeforeLoop.size(); i < stringValuesAfterLoop.size(); i++)
				childNodes.add(stringValuesAfterLoop.get(i));
			appendedStringValue = DataNodeFactory.createCompactConcatNode(childNodes);
		}
		return appendedStringValue;
	}

	/**
	 * Updates the env after executing a function.
	 */
	public void updateAfterFunctionExecution(
			Env functionenv,
			ArrayList<FormalParameterNode> formalParameterNodes,
			ArrayList<ExpressionNode> argumentExpressionNodes) {
		// Update reference parameters
		for (FormalParameterNode formalParameterNode : formalParameterNodes) {
			if (formalParameterNode.isReference()) {
				int parameterIndex = formalParameterNodes
						.indexOf(formalParameterNode);
				String parameterName = formalParameterNode
						.getResolvedParameterNameOrNull(null);

				if (parameterIndex >= argumentExpressionNodes.size()) {
					break;
				}
				if (!(argumentExpressionNodes.get(parameterIndex) instanceof VariableNode)) {
					MyLogger.log(
							MyLevel.TODO,
							"In env.updateAfterFunctionExecution: Reference parameter is not of type VariableNode.");
					continue;
				}

				String referencedVariableName = ((VariableNode) argumentExpressionNodes
						.get(parameterIndex)).getResolvedVariableNameOrNull(null);
				PhpVariable phpVariable = new PhpVariable(
						referencedVariableName);
				phpVariable.setDataNode(functionenv
						.getVariableFromCurrentScope(parameterName)
						.getDataNode());
				this.putVariableInCurrentScope(phpVariable);
			}
		}

		// Update global variables
		for (String globalVariableName : functionenv
				.getGlobalVariableNames()) {
			PhpVariable variableInsideFunction = functionenv
					.getVariableFromCurrentScope(globalVariableName);
			if (variableInsideFunction != null) {
				PhpVariable phpVariable = new PhpVariable(globalVariableName);
				phpVariable.setDataNode(variableInsideFunction.getDataNode());
				this.putVariableInCurrentScope(phpVariable);
			}
		}

		// Update output
		if (functionenv.containsSpecialVariableOutput()) {
			PhpVariable phpVariable = new PhpVariable(SPECIAL_VARIABLE_OUTPUT);
			phpVariable.setDataNode(functionenv
					.getVariableFromCurrentScope(SPECIAL_VARIABLE_OUTPUT)
					.getDataNode());
			this.putVariableInCurrentScope(phpVariable);
		}
	}

	/*
	 * Append string values to output
	 */

	/**
	 * Appends string values to the current output. This function is used by the
	 * "echo" statement and the "print" function invocation.
	 */
	public void appendOutput(ArrayList<DataNode> resolvedExpressionNodes) {
		PhpVariable newOutputVariable = new PhpVariable(SPECIAL_VARIABLE_OUTPUT);
		PhpVariable oldOutputVariable = this
				.getVariableFromProgramScope(SPECIAL_VARIABLE_OUTPUT);

		if (oldOutputVariable != null)
			newOutputVariable
					.appendStringValue(oldOutputVariable.getDataNode());
		for (DataNode stringValue : resolvedExpressionNodes)
			newOutputVariable.appendStringValue(stringValue);

		this.putVariableInCurrentScope(newOutputVariable);
	}

	/**
	 * Appends a string value to the current output.
	 */
	public void appendOutput(DataNode resolvedExpressionNode) {
		ArrayList<DataNode> resolvedExpressionNodes = new ArrayList<DataNode>();
		resolvedExpressionNodes.add(resolvedExpressionNode);
		this.appendOutput(resolvedExpressionNodes);
	}

	/*
	 * These methods are used to handle return/exit statements
	 */

	public void setHasReturnStatement(boolean hasReturnStatement) {
		this.hasReturnStatement = hasReturnStatement;
	}

	public void setHasExitStatement(boolean hasExitStatement) {
		this.hasExitStatement = hasExitStatement;
	}

	public boolean hasReturnStatement() {
		return this.hasReturnStatement;
	}

	public boolean hasExitStatement() {
		return this.hasExitStatement;
	}

	/**
	 * During the execution, the final output values will be collected from the
	 * current output values at exit points, plus the output value in the normal
	 * flow.
	 */
	public void addCurrentOutputToFinalOutput() {
		PhpVariable currentOutput = this.getCurrentOutput();
		if (currentOutput == null)
			return;

		PhpVariable finalOutput = this.getProgramScopeenv()
				.getVariableFromCurrentScope(SPECIAL_VARIABLE_FINAL_OUTPUT);
		if (finalOutput == null) {
			PhpVariable finalOutputVariable = new PhpVariable(
					SPECIAL_VARIABLE_FINAL_OUTPUT);
			finalOutputVariable.setDataNode(currentOutput.getDataNode());
			this.getProgramScopeenv().putVariableInCurrentScope(
					finalOutputVariable);
		} else {
			DataNode selectNode;
			if (isTrueBranch)
				selectNode = DataNodeFactory.createCompactSelectNode(conditionString, currentOutput.getDataNode(), finalOutput.getDataNode());
			else
				selectNode = DataNodeFactory.createCompactSelectNode(conditionString, finalOutput.getDataNode(), currentOutput.getDataNode());
			finalOutput.setDataNode(selectNode);
		}
	}

	/**
	 * Similar to final output values, the return values of a function are
	 * composed of all the return values at return statements.
	 */
	public void addReturnValue(DataNode currentReturnValue) {
		PhpVariable finalReturn = this.getFunctionScopeenv()
				.getVariableFromCurrentScope(SPECIAL_VARIABLE_RETURN);
		if (finalReturn == null) {
			PhpVariable finalReturnVariable = new PhpVariable(
					SPECIAL_VARIABLE_RETURN);
			finalReturnVariable.setDataNode(currentReturnValue);
			this.getFunctionScopeenv().putVariableInCurrentScope(
					finalReturnVariable);
		} else {
			DataNode selectNode;
			if (isTrueBranch)
				selectNode = DataNodeFactory.createCompactSelectNode(conditionString, currentReturnValue, finalReturn.getDataNode());
			else
				selectNode = DataNodeFactory.createCompactSelectNode(conditionString, finalReturn.getDataNode(), currentReturnValue);
			finalReturn.setDataNode(selectNode);
		}
	}

	/**
	 * Temporarily removes the RETURN variable.
	 * 
	 * @see servergraph.nodes.IncludeNode.execute(env)
	 */
	public void removeReturnValue() {
		this.getFunctionScopeenv().variableTable
				.remove(SPECIAL_VARIABLE_RETURN);
	}

	/*
	 * Utility functions
	 */

	public HashSet<String> getAllVariableNames() {
		return new HashSet<String>(variableTable.keySet());
	}

	public HashSet<PhpVariable> getAllVariables() {
		return new HashSet<PhpVariable>(variableTable.values());
	}

	public HashSet<String> getRegularVariableNames() {
		HashSet<String> variableNames = getAllVariableNames();
		variableNames.remove(SPECIAL_VARIABLE_OUTPUT);
		variableNames.remove(SPECIAL_VARIABLE_FINAL_OUTPUT);
		variableNames.remove(SPECIAL_VARIABLE_RETURN);
		return variableNames;
	}

	public boolean containsSpecialVariableOutput() {
		return variableTable.containsKey(SPECIAL_VARIABLE_OUTPUT);
	}

	public PhpVariable getCurrentOutput() {
		return this.getVariableFromProgramScope(SPECIAL_VARIABLE_OUTPUT);
	}

	public PhpVariable getFinalOutput() {
		return this.getProgramScopeenv()
				.getVariableFromCurrentScope(SPECIAL_VARIABLE_FINAL_OUTPUT);
	}

	public PhpVariable getReturnValue() {
		return this.getFunctionScopeenv()
				.getVariableFromCurrentScope(SPECIAL_VARIABLE_RETURN);
	}

	public HashSet<PhpFunction> getAllFunctions() {
		return new HashSet<PhpFunction>(functionTable.values());
	}

}
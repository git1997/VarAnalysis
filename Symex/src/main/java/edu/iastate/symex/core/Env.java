package edu.iastate.symex.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;
import edu.iastate.symex.php.nodes.ClassDeclarationNode;
import edu.iastate.symex.php.nodes.ExpressionNode;
import edu.iastate.symex.php.nodes.FileNode;
import edu.iastate.symex.php.nodes.FormalParameterNode;
import edu.iastate.symex.php.nodes.FunctionDeclarationNode;
import edu.iastate.symex.php.nodes.VariableNode;

/**
 * Env contains information during run time about variables, classes, methods, etc.
 * @author HUNG
 * 
 */
public abstract class Env {

	protected Env outerScopeEnv; 	// The Env in the outer scope, can be null if it is already the outermost Env (GlobalEnv)

	/*
	 * Manage the variables in the current scope
	 */
	private HashMap<String, PhpVariable> variableTable = new HashMap<String, PhpVariable>();
	private static String SPECIAL_VARIABLE_OUTPUT = "[OUTPUT]"; // Used throughout the execution (GLOBAL/FUNCTION/BRANCH scopes)
	private static String SPECIAL_VARIABLE_FINAL_OUTPUT = "[FINAL_OUTPUT]"; // Used in GLOBAL scope only
	private static String SPECIAL_VARIABLE_RETURN = "[RETURN]"; // Used in GLOBAL/FUNCTION scopes only

	/*
	 * Manage global variables in the current scope
	 * (Variables that are declared with the global keyword)
	 */
	private HashSet<String> globalVariables = new HashSet<String>(); 

	/*
	 * These fields are used to handle return/exit statements.
	 */
	private boolean hasReturnStatement = false;
	private boolean hasExitStatement = false;

	/**
	 * Constuctor
	 * @param outerScopeEnv
	 */
	public Env(Env outerScopeEnv) {
		this.outerScopeEnv = outerScopeEnv;
	}
	
	/*
	 * Get the env from different scopes
	 */

	/**
	 * Returns the global Env.
	 */
	public GlobalEnv getGlobalEnv() {
		if (this instanceof GlobalEnv)
			return (GlobalEnv) this;
		else
			return outerScopeEnv.getGlobalEnv();
	}
	
	/**
	 * Returns the global or function env that contains this env.
	 */
	public Env getGlobalOrFunctionEnv() {
		if (this instanceof GlobalEnv || this instanceof FunctionEnv)
			return this;
		else
			return outerScopeEnv.getGlobalOrFunctionEnv();
	}

	/*
	 * Manage VARIABLES.
	 * Typically, a write access will be done on the current scope's env,
	 * whereas a read access will be done on a global/function scope's env.
	 */
	
	/**
	 * Writes a variable
	 * @param phpVariable
	 */
	public void writeVariable(PhpVariable phpVariable) {
		putVariableInCurrentScope(phpVariable);
	}
	
	/**
	 * Reads a variable from its name
	 * @param name
	 */
	public PhpVariable readVariable(String name) {
		return getVariableFromGlobalOrFunctionScope(name); 
	}

	/**
	 * Puts a variable in the CURRENT scope
	 * @param phpVariable
	 */
	protected void putVariableInCurrentScope(PhpVariable phpVariable) {
		variableTable.put(phpVariable.getName(), phpVariable);
	}

	/**
	 * Gets a variable from the CURRENT scope
	 * @param variableName
	 */
	protected PhpVariable getVariableFromCurrentScope(String variableName) {
		return variableTable.get(variableName);
	}

	/**
	 * Gets a variable from the first enclosing Env that has GLOBAL/FUNCTION scope
	 * @param variableName
	 */
	protected PhpVariable getVariableFromGlobalOrFunctionScope(String variableName) {
		PhpVariable phpVariable = getVariableFromCurrentScope(variableName);
		if (phpVariable != null)
			return phpVariable;
		else if (this instanceof GlobalEnv || this instanceof FunctionEnv)
			return null;
		else
			return outerScopeEnv.getVariableFromGlobalOrFunctionScope(variableName);
	}

	/**
	 * Gets a variable from the first enclosing Env that has GLOBAL scope
	 * @param variableName
	 */
	protected PhpVariable getVariableFromGlobalScope(String variableName) {
		PhpVariable phpVariable = getVariableFromCurrentScope(variableName);
		if (phpVariable != null)
			return phpVariable;
		else if (this instanceof GlobalEnv)
			return null;
		else
			return outerScopeEnv.getVariableFromGlobalScope(variableName);
	}

	/**
	 * Adds a global variable in the current Env
	 */
	public void addGlobalVariable(String variableName) {
		globalVariables.add(variableName);
		
		PhpVariable globalVariable = new PhpVariable(variableName);
		PhpVariable referredGlobalVariable = readVariable(variableName);
		if (referredGlobalVariable != null)
			globalVariable.setDataNode(referredGlobalVariable.getDataNode());
		else
			globalVariable.setDataNode(DataNodeFactory.createSymbolicNode());
		writeVariable(globalVariable);
	}
	
	/**
	 * Gets the set of global variables in the current Env
	 */
	public HashSet<String> getGlobalVariables() {
		return new HashSet<String>(globalVariables);
	}

	/*
	 * Manage PREDEFINED CONSTANTS
	 */

	/**
	 * Sets the value of a predefined constant in the Global Env.
	 */
	public void setPredefinedConstantValue(String constantName,	DataNode constantValue) {
		PhpVariable phpConstant = new PhpVariable(constantName);
		phpConstant.setDataNode(constantValue);
		getGlobalEnv().putVariableInCurrentScope(phpConstant);
	}

	/**
	 * Returns the value of a predefined constant from the Global Env, or null if not found.
	 */
	public DataNode getPredefinedConstantValue(String constantName) {
		PhpVariable phpConstant = getGlobalEnv().getVariableFromCurrentScope(constantName);

		/* Get the value if it has been defined */
		if (phpConstant != null)
			return phpConstant.getDataNode();

		/* Handle PHP keywords */ 
		else if (constantName.toUpperCase().equals("TRUE"))
			return SpecialNode.BooleanNode.TRUE;
		else if (constantName.toUpperCase().equals("FALSE"))
			return SpecialNode.BooleanNode.FALSE;
		else if (constantName.toUpperCase().equals("NULL"))
			return DataNodeFactory.createLiteralNode("");

		/* Handle PHP system constants */
		else if (constantName.toUpperCase().equals("__FILE__"))
			return DataNodeFactory.createLiteralNode(peekFileFromStack().getAbsolutePath());

		/* Else, return null */
		else
			return null;
	}	
	
	/*
	 * Manage FUNCTIONS, CLASSES, and FILES.
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

	/*
	 * Manage constraints
	 */
	
	/**
	 * Returns the conjuncted constraints of the current scope and its enclosing scopes.
	 */
	public Constraint getConjunctedConstraint() {
		if (this instanceof BranchEnv) {
			Constraint outerConstraint = outerScopeEnv.getConjunctedConstraint();
			Constraint thisConstraint = ((BranchEnv) this).getConstraint();
			return ConstraintFactory.createAndConstraint(outerConstraint, thisConstraint);
		}
		else if (outerScopeEnv != null)
			return outerScopeEnv.getConjunctedConstraint();
		else
			return Constraint.TRUE;
	}
	
	/**
	 * Returns the conjuncted constraints of the current scope and its enclosing scopes
	 * up to its first enclosing Function/Global scope.
	 */
	public Constraint getConjunctedConstraintUpToGlobalOrFunctionScope() {
		if (this instanceof BranchEnv) {
			Constraint outerConstraint = outerScopeEnv.getConjunctedConstraintUpToGlobalOrFunctionScope();
			Constraint thisConstraint = ((BranchEnv) this).getConstraint();
			return ConstraintFactory.createAndConstraint(outerConstraint, thisConstraint);
		}
		else
			return Constraint.TRUE;
	}

	/*
	 * Update Env when executing the program.
	 */

	/**
	 * Updates the env after executing some branches.
	 */
	public void updateWithBranches(Constraint constraint, Env trueBranchEnv, Env falseBranchEnv) {
		// If a branch has a return/exit statement, update the variables in the
		// current scope with the other branch
		boolean trueBranchTerminated = (trueBranchEnv != null && (trueBranchEnv.hasReturnStatement() || trueBranchEnv.hasExitStatement()));
		boolean falseBranchTerminated = (falseBranchEnv != null && (falseBranchEnv.hasReturnStatement() || falseBranchEnv.hasExitStatement()));

		if (trueBranchTerminated || falseBranchTerminated) {
			if (trueBranchTerminated && !falseBranchTerminated && falseBranchEnv != null)
				this.updateVariableTableWithOneBranch(falseBranchEnv);
			else if (!trueBranchTerminated && falseBranchTerminated && trueBranchEnv != null)
				this.updateVariableTableWithOneBranch(trueBranchEnv);
			return;
		}

		// Else, update the variables in the current scope considering their
		// values in both branches.
		HashSet<String> variableNamesInTrueBranch = (trueBranchEnv != null ? trueBranchEnv.getRegularVariableNames() : new HashSet<String>());
		HashSet<String> variableNamesInFalseBranch = (falseBranchEnv != null ? falseBranchEnv.getRegularVariableNames() : new HashSet<String>());
		HashSet<String> variableNamesInEitherBranch = new HashSet<String>(variableNamesInTrueBranch);
		variableNamesInEitherBranch.addAll(variableNamesInFalseBranch);

		for (String variableName : variableNamesInEitherBranch) {
			PhpVariable variableInTrueBranch = (trueBranchEnv != null ? trueBranchEnv.getVariableFromGlobalOrFunctionScope(variableName) : this.getVariableFromGlobalOrFunctionScope(variableName));
			PhpVariable variableInFalseBranch = (falseBranchEnv != null ? falseBranchEnv.getVariableFromGlobalOrFunctionScope(variableName) : this.getVariableFromGlobalOrFunctionScope(variableName));

			DataNode dataNodeInTrueBranch = (variableInTrueBranch != null ? variableInTrueBranch.getDataNode() : null);
			DataNode dataNodeInFalseBranch = (variableInFalseBranch != null ? variableInFalseBranch.getDataNode() : null);
			DataNode compactSelectNode = DataNodeFactory.createCompactSelectNode(constraint, dataNodeInTrueBranch, dataNodeInFalseBranch);

			PhpVariable phpVariable = new PhpVariable(variableName);
			phpVariable.setDataNode(compactSelectNode);
			this.putVariableInCurrentScope(phpVariable);
		}

		// Also, update the output in the current scope considering its values
		// in both branches.
		if (trueBranchEnv != null && trueBranchEnv.containsSpecialVariableOutput()
				|| falseBranchEnv != null && falseBranchEnv.containsSpecialVariableOutput()) {
			PhpVariable variableInTrueBranch = (trueBranchEnv != null ? trueBranchEnv.getVariableFromGlobalScope(SPECIAL_VARIABLE_OUTPUT)
					: this.getVariableFromGlobalScope(SPECIAL_VARIABLE_OUTPUT));
			PhpVariable variableInFalseBranch = (falseBranchEnv != null ? falseBranchEnv.getVariableFromGlobalScope(SPECIAL_VARIABLE_OUTPUT)
					: this.getVariableFromGlobalScope(SPECIAL_VARIABLE_OUTPUT));

			DataNode dataNodeInTrueBranch = (variableInTrueBranch != null ? variableInTrueBranch.getDataNode() : null);
			DataNode dataNodeInFalseBranch = (variableInFalseBranch != null ? variableInFalseBranch.getDataNode() : null);
			DataNode compactSelectNode = DataNodeFactory.createCompactSelectNode(constraint, dataNodeInTrueBranch, dataNodeInFalseBranch);

			PhpVariable phpVariable = new PhpVariable(SPECIAL_VARIABLE_OUTPUT);
			phpVariable.setDataNode(compactSelectNode);
			this.putVariableInCurrentScope(phpVariable);
		}
	}

	/**
	 * Updates the variableTable with the one in one of the branches.
	 */
	private void updateVariableTableWithOneBranch(Env branchEnv) {
		// Update regular variables
		for (String variableName : branchEnv.getRegularVariableNames()) {
			PhpVariable phpVariable = branchEnv.getVariableFromCurrentScope(variableName);
			this.putVariableInCurrentScope(phpVariable);
		}
		// Update output
		if (branchEnv.containsSpecialVariableOutput()) {
			PhpVariable phpVariable = branchEnv.getVariableFromCurrentScope(SPECIAL_VARIABLE_OUTPUT);
			this.putVariableInCurrentScope(phpVariable);
		}
	}

	/**
	 * Updates the Env after executing a loop.
	 */
	public void updateWithLoop(Constraint constraint, Env loopEnv) {
		// Update regular variables
		HashSet<String> variableNamesInsideLoop = loopEnv.getRegularVariableNames();
		for (String variableName : variableNamesInsideLoop) {
			PhpVariable variableBeforeLoop = this.getVariableFromGlobalOrFunctionScope(variableName);
			PhpVariable variableInsideLoop = loopEnv.getVariableFromCurrentScope(variableName);

			DataNode dataNodeBeforeLoop = (variableBeforeLoop != null ? variableBeforeLoop.getDataNode() : null);
			DataNode dataNodeAfterLoop = variableInsideLoop.getDataNode();

			DataNode appendedStringValue = getAppendedStringValue(dataNodeBeforeLoop, dataNodeAfterLoop);
			if (appendedStringValue != null) {
				PhpVariable phpVariable = new PhpVariable(variableName);
				if (dataNodeBeforeLoop != null)
					phpVariable.appendStringValue(dataNodeBeforeLoop);

				RepeatNode repeatNode = DataNodeFactory.createRepeatNode(constraint, appendedStringValue);
				phpVariable.appendStringValue(repeatNode);
				this.putVariableInCurrentScope(phpVariable);
			}
		}
		// Update output
		if (loopEnv.containsSpecialVariableOutput()) {
			PhpVariable variableBeforeLoop = this.getVariableFromGlobalScope(SPECIAL_VARIABLE_OUTPUT);
			PhpVariable variableInsideLoop = loopEnv.getVariableFromCurrentScope(SPECIAL_VARIABLE_OUTPUT);

			DataNode dataNodeBeforeLoop = (variableBeforeLoop != null ? variableBeforeLoop.getDataNode() : null);
			DataNode dataNodeAfterLoop = variableInsideLoop.getDataNode();
			DataNode appendedStringValue = getAppendedStringValue(dataNodeBeforeLoop, dataNodeAfterLoop);

			if (appendedStringValue != null) {
				PhpVariable phpVariable = new PhpVariable(SPECIAL_VARIABLE_OUTPUT);
				if (dataNodeBeforeLoop != null)
					phpVariable.appendStringValue(dataNodeBeforeLoop);

				RepeatNode repeatNode = DataNodeFactory.createRepeatNode(constraint, appendedStringValue);
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
	 * Updates the Env after executing a function.
	 */
	public void updateAfterFunctionExecution(Env functionEnv, ArrayList<FormalParameterNode> formalParameterNodes, ArrayList<ExpressionNode> argumentExpressionNodes) {
		// Update reference parameters
		for (FormalParameterNode formalParameterNode : formalParameterNodes) {
			if (formalParameterNode.isReference()) {
				int parameterIndex = formalParameterNodes.indexOf(formalParameterNode);
				String parameterName = formalParameterNode.getResolvedParameterNameOrNull(null);

				if (parameterIndex >= argumentExpressionNodes.size()) {
					break;
				}
				if (!(argumentExpressionNodes.get(parameterIndex) instanceof VariableNode)) {
					MyLogger.log(MyLevel.TODO, "In Env.updateAfterFunctionExecution: Reference parameter is not of type VariableNode.");
					continue;
				}

				String referencedVariableName = ((VariableNode) argumentExpressionNodes.get(parameterIndex)).getResolvedVariableNameOrNull(null);
				PhpVariable phpVariable = new PhpVariable(referencedVariableName);
				phpVariable.setDataNode(functionEnv.getVariableFromCurrentScope(parameterName).getDataNode());
				this.putVariableInCurrentScope(phpVariable);
			}
		}

		// Update global variables
		for (String globalVariableName : functionEnv.getGlobalVariables()) {
			PhpVariable variableInsideFunction = functionEnv.getVariableFromCurrentScope(globalVariableName);
			if (variableInsideFunction != null) {
				PhpVariable phpVariable = new PhpVariable(globalVariableName);
				phpVariable.setDataNode(variableInsideFunction.getDataNode());
				this.putVariableInCurrentScope(phpVariable);
			}
		}

		// Update output
		if (functionEnv.containsSpecialVariableOutput()) {
			PhpVariable phpVariable = new PhpVariable(SPECIAL_VARIABLE_OUTPUT);
			phpVariable.setDataNode(functionEnv.getVariableFromCurrentScope(SPECIAL_VARIABLE_OUTPUT).getDataNode());
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
		PhpVariable oldOutputVariable = readVariable(SPECIAL_VARIABLE_OUTPUT);

		if (oldOutputVariable != null)
			newOutputVariable.appendStringValue(oldOutputVariable.getDataNode());
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

		PhpVariable finalOutput = this.getGlobalEnv().getVariableFromCurrentScope(SPECIAL_VARIABLE_FINAL_OUTPUT);
		if (finalOutput == null) {
			PhpVariable finalOutputVariable = new PhpVariable(SPECIAL_VARIABLE_FINAL_OUTPUT);
			finalOutputVariable.setDataNode(currentOutput.getDataNode());
			this.getGlobalEnv().putVariableInCurrentScope(finalOutputVariable);
		} else {
			DataNode selectNode;
			Constraint constraint = getConjunctedConstraint();
			selectNode = DataNodeFactory.createCompactSelectNode(constraint, currentOutput.getDataNode(), finalOutput.getDataNode());
			finalOutput.setDataNode(selectNode);
		}
	}

	/**
	 * Similar to final output values, the return values of a function are
	 * composed of all the return values at return statements.
	 */
	public void addReturnValue(DataNode currentReturnValue) {
		PhpVariable finalReturn = this.getGlobalOrFunctionEnv().getVariableFromCurrentScope(SPECIAL_VARIABLE_RETURN);
		if (finalReturn == null) {
			PhpVariable finalReturnVariable = new PhpVariable(SPECIAL_VARIABLE_RETURN);
			finalReturnVariable.setDataNode(currentReturnValue);
			this.getGlobalOrFunctionEnv().putVariableInCurrentScope(finalReturnVariable);
		} else {
			DataNode selectNode;
			Constraint constraint = getConjunctedConstraintUpToGlobalOrFunctionScope();
			selectNode = DataNodeFactory.createCompactSelectNode(constraint, currentReturnValue, finalReturn.getDataNode());
			finalReturn.setDataNode(selectNode);
		}
	}

	/**
	 * Temporarily removes the RETURN variable.
	 * 
	 * @see IncludeNode.execute(env)
	 */
	public void removeReturnValue() {
		this.getGlobalOrFunctionEnv().variableTable.remove(SPECIAL_VARIABLE_RETURN);
	}

	/*
	 * Utility functions
	 */

	public HashSet<String> getAllVariableNames() {
		return new HashSet<String>(variableTable.keySet());
	}

//	public HashSet<PhpVariable> getAllVariables() {
//		return new HashSet<PhpVariable>(variableTable.values());
//	}

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
		return this.getVariableFromGlobalScope(SPECIAL_VARIABLE_OUTPUT);
	}

	public PhpVariable getFinalOutput() {
		return this.getGlobalEnv().getVariableFromCurrentScope(SPECIAL_VARIABLE_FINAL_OUTPUT);
	}

	public PhpVariable getReturnValue() {
		return this.getGlobalOrFunctionEnv().getVariableFromCurrentScope(SPECIAL_VARIABLE_RETURN);
	}

}
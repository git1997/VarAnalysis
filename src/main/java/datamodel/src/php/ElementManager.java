package php;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import php.elements.PhpClass;
import php.elements.PhpFile;
import php.elements.PhpFunction;
import php.elements.PhpVariable;
import php.nodes.ExpressionNode;
import php.nodes.FormalParameterNode;
import php.nodes.ScalarNode;
import php.nodes.VariableNode;
import util.StringUtils;
import logging.MyLevel;
import logging.MyLogger;
import datamodel.nodes.DataNode;
import datamodel.nodes.ConcatNode;
import datamodel.nodes.LiteralNode;
import datamodel.nodes.RepeatNode;
import datamodel.nodes.SelectNode;
import datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class ElementManager {
	
	/*
	 * Manage the scopes of elements
	 */
	public enum ScopeType { 
		PROGRAM, FUNCTION, BRANCH
	}
	private ScopeType scopeType;						// The scope type of the current elementManager
	private ElementManager outerScopeElementManager;	// Manages the elements in the outer scope

	/*
	 * Manage the variables in the current scope
	 */
	private HashMap<String, PhpVariable> variableTable = new HashMap<String, PhpVariable>();
	private static String SPECIAL_VARIABLE_OUTPUT 		= "[OUTPUT]";		// Used throughout the execution
	private static String SPECIAL_VARIABLE_FINAL_OUTPUT = "[FINAL_OUTPUT]";	// Used in PROGRAM scope only
	private static String SPECIAL_VARIABLE_RETURN 		= "[RETURN]"; 		// Used in PROGRAM/FUNCTION scope only
	
	/*
	 * Manage global variables
	 */
	private HashSet<String> globalVariableNames = new HashSet<String>();	// Used in PROGRAM/FUNCTION scope only
	
	/*
	 * Manage the declarations of functions, classes, and files, so that they only need to be parsed once.
	 */
	private static String projectFolder = "";
	private static HashMap<String, PhpFunction> functionTable = new HashMap<String, PhpFunction>();
	private static HashMap<String, PhpClass> classTable = new HashMap<String, PhpClass>();
	private static HashMap<String, PhpFile> fileTable = new HashMap<String, PhpFile>();
	
	/*
	 * These fields are used to prevent recursive function/program invocation
	 */
	private Stack<String> functionStack = new Stack<String>();		// Used in PROGRAM scope only
	private Stack<String> fileStack = new Stack<String>();			// Used in PROGRAM scope only
	private HashSet<String> invokedFiles = new HashSet<String>();	// Used in PROGRAM scope only
	
	/*
	 * These fields are used to handle return/exit statements.
	 */
	private LiteralNode conditionString = null;
	private boolean isTrueBranch = true;
	private boolean hasReturnStatement = false;
	private boolean hasExitStatement = false;
	
	/**
	 * Resets the static fields every time the main program is executed to save memory space and prevent caching.
	 */
	public static void resetStaticFields() {
		functionTable = new HashMap<String, PhpFunction>();
		classTable = new HashMap<String, PhpClass>();
		fileTable = new HashMap<String, PhpFile>();
	}
	
	/**
	 * Constructor
	 * PROGRAM scope.
	 */
	public ElementManager() {
		this.scopeType = ScopeType.PROGRAM;
		this.outerScopeElementManager = null;
	}
	
	/**
	 * Constructor
	 * FUNCTION scope
	 */
	public ElementManager(ElementManager outerScopeElementManager, String functionName) {
		this.scopeType = ScopeType.FUNCTION;
		this.outerScopeElementManager = outerScopeElementManager;
	}
	
	/**
	 * Constructor
	 * BRANCH scope
	 */
	public ElementManager(ElementManager outerScopeElementManager, LiteralNode conditionString, boolean isTrueBranch) {
		this.scopeType = ScopeType.BRANCH;
		this.outerScopeElementManager = outerScopeElementManager;
		
		this.conditionString = conditionString;
		this.isTrueBranch = isTrueBranch;
	}
	
	/*
	 * Get the elementManager from different scopes
	 */
	
	/**
	 * Returns the elementManager that contains this elementManager and has a PROGRAM/FUNCTION scope, 
	 * or returns itself if it already has a PROGRAM/FUNCTION scope.  
	 */
	public ElementManager getFunctionScopeElementManager() {
		if (scopeType == ScopeType.PROGRAM || scopeType == ScopeType.FUNCTION)
			return this;
		else
			return outerScopeElementManager.getFunctionScopeElementManager();
	}
	
	/**
	 * Returns the outermost elementManager (the elementManager of the program).  
	 */
	public ElementManager getProgramScopeElementManager() {
		if (scopeType == ScopeType.PROGRAM)
			return this;
		else
			return outerScopeElementManager.getProgramScopeElementManager();
	}
	
	/*
	 * Manage VARIABLES.
	 * Typically, a write access will be done on the current scope's element manager,
	 * whereas a read access will be done on a function scope element manager.
	 */
	
	/**
	 * Puts a variable in the CURRENT scope
	 * @param variableName
	 * @param phpElement
	 */
	public void putVariableInCurrentScope(PhpVariable phpVariable) {
		variableTable.put(phpVariable.getName(), phpVariable);
	}
	
	/**
	 * Gets a variable from the CURRENT scope
	 * @param variableName
	 * @return
	 */	
	private PhpVariable getVariableFromCurrentScope(String variableName) {
		return variableTable.get(variableName);
	}
	
	/**
	 * Gets a variable from a FUNCTION scope (used for non-global variables)
	 * @param variableName
	 * @return
	 */	
	public PhpVariable getVariableFromFunctionScope(String variableName) {
		PhpVariable phpVariable = this.getVariableFromCurrentScope(variableName);
		if (phpVariable != null)
			return phpVariable;
		else if (scopeType == ScopeType.PROGRAM || scopeType == ScopeType.FUNCTION)
			return null;
		else
			return outerScopeElementManager.getVariableFromFunctionScope(variableName);
	}
	
	/**
	 * Gets a variable from the PROGRAM scope (used for global variables)
	 * @param variableName
	 * @return
	 */	
	private PhpVariable getVariableFromProgramScope(String variableName) {
		PhpVariable phpVariable = this.getVariableFromCurrentScope(variableName);
		if (phpVariable != null)
			return phpVariable;
		else if (scopeType == ScopeType.PROGRAM)
			return null;
		else
			return outerScopeElementManager.getVariableFromProgramScope(variableName);
	}
	
	/*
	 * Manage GLOBAL VARIABLES
	 */
	
	/**
	 * Adds a global variable.
	 */
	public void addGlobalVariable(VariableNode globalVariableNode) {
		String globalVariableName = globalVariableNode.resolveVariableName(this);
		PhpVariable globalVariable = new PhpVariable(globalVariableName);
		PhpVariable referredGlobalVariable = this.getVariableFromProgramScope(globalVariableName);
		if (referredGlobalVariable != null)
			globalVariable.setDataNode(referredGlobalVariable.getDataNode());
		else
			globalVariable.setDataNode(new SymbolicNode(globalVariableNode));
		this.putVariableInCurrentScope(globalVariable);
		this.getFunctionScopeElementManager().globalVariableNames.add(globalVariableName);
	}
	
	/**
	 * Returns all global variables in the function
	 */
	public HashSet<String> getGlobalVariableNames() {
		return new HashSet<String>(this.getFunctionScopeElementManager().globalVariableNames);
	}
	
	/*
	 * Manage PREDEFINED CONSTANTS
	 */

	/**
	 * Sets the value of a predefined constant.
	 */
	public void setPredefinedConstantValue(String constantName, DataNode constantValue) {
		PhpVariable phpConstant = new PhpVariable(constantName);
		phpConstant.setDataNode(constantValue);
		this.getProgramScopeElementManager().putVariableInCurrentScope(phpConstant);
	}
	
	/**
	 * Returns the value of a predefined constant.
	 */
	public DataNode getPredefinedConstantValue(ScalarNode scalarNode) {
		String constantName = scalarNode.getStringValue();
		PhpVariable phpConstant = this.getProgramScopeElementManager().getVariableFromCurrentScope(constantName);
		
		/* Get the value if it has been defined */
		if (phpConstant != null)
			return phpConstant.getDataNode();
		
		/* Handle PHP keywords */
		else if (constantName.toUpperCase().equals("TRUE"))
			return new LiteralNode("TRUE");
		else if (constantName.toUpperCase().equals("FALSE"))
			return new LiteralNode("FALSE");
		else if (constantName.toUpperCase().equals("NULL"))
			return new LiteralNode("");
		
		/* Handle PHP system constants */
		else if (constantName.toUpperCase().equals("__FILE__"))
			return new LiteralNode(getProjectFolder() + StringUtils.getFileSystemSlash() + peekFileFromStack());
		
		/* Else, return a symbolic value */
		else
			return new SymbolicNode(scalarNode);
	}
	
	/*
	 * Manage FUNCTIONS, CLASSES, and FILES.
	 */
	
	public void setProjectFolder(String projectFolder_) {
		projectFolder = projectFolder_;
	}
	
	public String getProjectFolder() {
		return projectFolder;
	}
		
	public void putFunction(String functionName, PhpFunction phpFunction) {
		ElementManager.functionTable.put(functionName, phpFunction);
	}
	
	public PhpFunction getFunction(String functionName) {
		return ElementManager.functionTable.get(functionName);
	}
	
	public void putClass(String className, PhpClass phpClass) {
		ElementManager.classTable.put(className, phpClass);
	}
	
	public PhpClass getClass(String className) {
		return ElementManager.classTable.get(className);
	}
	
	public void putFile(String fileName, PhpFile phpFile) {
		ElementManager.fileTable.put(fileName, phpFile);
	}
	
	public PhpFile getFile(String fileName) {
		return ElementManager.fileTable.get(fileName);
	}
	
	/*
	 * Manage invoked functions and included files
	 */
	
	public void pushFunctionToStack(String functionName) {
		this.getProgramScopeElementManager().functionStack.push(functionName);
	}
	
	public String peekFunctionFromStack() {
		return this.getProgramScopeElementManager().functionStack.peek();
	}
	
	public void popFunctionFromStack() {
		this.getProgramScopeElementManager().functionStack.pop();
	}
		
	public boolean containsFunctionInStack(String functionName) {
		return this.getProgramScopeElementManager().functionStack.contains(functionName);
	}
	
	public ArrayList<String> getFunctionStack() {
		return new ArrayList<String>(this.getProgramScopeElementManager().functionStack);
	}
	
	public void pushFileToStack(String fileName) {
		this.getProgramScopeElementManager().fileStack.push(fileName);
		addInvokedFiles(fileName);
	}
	
	public String peekFileFromStack() {
		return this.getProgramScopeElementManager().fileStack.peek();
	}
	
	public void popFileFromStack() {
		this.getProgramScopeElementManager().fileStack.pop();
	}
	
	public boolean containsFileInStack(String fileName) {
		return this.getProgramScopeElementManager().fileStack.contains(fileName);
	}
	
	public ArrayList<String> getFileStack() {
		return new ArrayList<String>(this.getProgramScopeElementManager().fileStack);
	}
	
	public void addInvokedFiles(String fileName) {
		this.getProgramScopeElementManager().invokedFiles.add(fileName);
	}
	
	public HashSet<String> getInvokedFiles() {
		return new HashSet<String>(this.getProgramScopeElementManager().invokedFiles);
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
			constraints.addAll(outerScopeElementManager.getConstraints());
		}
		
		return constraints;
	}
	
	/*
	 * Update ElementManager when executing the program.
	 */
	
	/**
	 * Updates the elementManager after executing some branches.
	 */
	public void updateWithBranches(LiteralNode conditionString, ElementManager trueBranchElementManager, ElementManager falseBranchElementManager) {		
		// If a branch has a return/exit statement, update the variables in the current scope with the other branch
		boolean trueBranchTerminated = (trueBranchElementManager != null && (trueBranchElementManager.hasReturnStatement() || trueBranchElementManager.hasExitStatement()));
		boolean falseBranchTerminated = (falseBranchElementManager != null && (falseBranchElementManager.hasReturnStatement() || falseBranchElementManager.hasExitStatement()));
		
		if (trueBranchTerminated || falseBranchTerminated) {
			if (trueBranchTerminated && !falseBranchTerminated && falseBranchElementManager != null)
				this.updateVariableTable(falseBranchElementManager);
			else if (!trueBranchTerminated && falseBranchTerminated && trueBranchElementManager != null)
				this.updateVariableTable(trueBranchElementManager);
			return;
		}
		
		// Else, update the variables in the current scope considering their values in both branches.
		HashSet<String> variableNamesInTrueBranch = (trueBranchElementManager != null ? trueBranchElementManager.getRegularVariableNames() : new HashSet<String>());
		HashSet<String> variableNamesInFalseBranch = (falseBranchElementManager != null ? falseBranchElementManager.getRegularVariableNames() : new HashSet<String>());
		HashSet<String> variableNamesInEitherBranch = new HashSet<String>(variableNamesInTrueBranch);
		variableNamesInEitherBranch.addAll(variableNamesInFalseBranch);
		
		for (String variableName : variableNamesInEitherBranch) {
			PhpVariable variableInTrueBranch = (trueBranchElementManager != null ? trueBranchElementManager.getVariableFromFunctionScope(variableName) : this.getVariableFromFunctionScope(variableName));
			PhpVariable variableInFalseBranch = (falseBranchElementManager != null ? falseBranchElementManager.getVariableFromFunctionScope(variableName) : this.getVariableFromFunctionScope(variableName));
			
			DataNode dataNodeInTrueBranch = (variableInTrueBranch != null ? variableInTrueBranch.getDataNode() : null);
			DataNode dataNodeInFalseBranch = (variableInFalseBranch != null ? variableInFalseBranch.getDataNode() : null);
			DataNode compactSelectNode = new SelectNode(conditionString, dataNodeInTrueBranch, dataNodeInFalseBranch).compact();
			
			PhpVariable phpVariable = new PhpVariable(variableName);
			phpVariable.setDataNode(compactSelectNode);
			this.putVariableInCurrentScope(phpVariable);
		}
		
		// Also, update the output in the current scope considering its values in both branches.
		if (trueBranchElementManager != null && trueBranchElementManager.containsSpecialVariableOutput() || falseBranchElementManager != null && falseBranchElementManager.containsSpecialVariableOutput()) {
			PhpVariable variableInTrueBranch = (trueBranchElementManager != null ? trueBranchElementManager.getVariableFromProgramScope(SPECIAL_VARIABLE_OUTPUT) : this.getVariableFromProgramScope(SPECIAL_VARIABLE_OUTPUT));
			PhpVariable variableInFalseBranch = (falseBranchElementManager != null ? falseBranchElementManager.getVariableFromProgramScope(SPECIAL_VARIABLE_OUTPUT) : this.getVariableFromProgramScope(SPECIAL_VARIABLE_OUTPUT));
			
			DataNode dataNodeInTrueBranch = (variableInTrueBranch != null ? variableInTrueBranch.getDataNode() : null);
			DataNode dataNodeInFalseBranch = (variableInFalseBranch != null ? variableInFalseBranch.getDataNode() : null);
			DataNode compactSelectNode = new SelectNode(conditionString, dataNodeInTrueBranch, dataNodeInFalseBranch).compact();
			
			PhpVariable phpVariable = new PhpVariable(SPECIAL_VARIABLE_OUTPUT);
			phpVariable.setDataNode(compactSelectNode);
			this.putVariableInCurrentScope(phpVariable);
		}
	}
	
	/**
	 * Updates the variableTable with the one in one of the branches.
	 */
	private void updateVariableTable(ElementManager branchElementManager) {
		// Update regular variables
		for (String variableName : branchElementManager.getRegularVariableNames()) {
			PhpVariable phpVariable = branchElementManager.getVariableFromCurrentScope(variableName);
			this.putVariableInCurrentScope(phpVariable);
		}
		// Update output
		if (branchElementManager.containsSpecialVariableOutput()) {
			PhpVariable phpVariable = branchElementManager.getVariableFromCurrentScope(SPECIAL_VARIABLE_OUTPUT);
			this.putVariableInCurrentScope(phpVariable);
		}
	}
	
	/**
	 * Updates the elementManager after executing a loop.
	 */
	public void updateWithLoop(LiteralNode conditionString, ElementManager loopElementManager) {
		// Update regular variables
		HashSet<String> variableNamesInsideLoop = loopElementManager.getRegularVariableNames();
		for (String variableName : variableNamesInsideLoop) {
			PhpVariable variableBeforeLoop = this.getVariableFromFunctionScope(variableName);
			PhpVariable variableInsideLoop = loopElementManager.getVariableFromCurrentScope(variableName);
			
			DataNode dataNodeBeforeLoop = (variableBeforeLoop != null ? variableBeforeLoop.getDataNode() : null);
			DataNode dataNodeAfterLoop = variableInsideLoop.getDataNode();
			
			DataNode appendedStringValue = getAppendedStringValue(dataNodeBeforeLoop, dataNodeAfterLoop);
			if (appendedStringValue != null) {			
				PhpVariable phpVariable = new PhpVariable(variableName);
				if (dataNodeBeforeLoop != null)
					phpVariable.appendStringValue(dataNodeBeforeLoop);
				
				RepeatNode repeatNode = new RepeatNode(conditionString, appendedStringValue);
				phpVariable.appendStringValue(repeatNode);
				this.putVariableInCurrentScope(phpVariable);
			}
		}		
		// Update output
		if (loopElementManager.containsSpecialVariableOutput()) {
			PhpVariable variableBeforeLoop = this.getVariableFromProgramScope(SPECIAL_VARIABLE_OUTPUT);
			PhpVariable variableInsideLoop = loopElementManager.getVariableFromCurrentScope(SPECIAL_VARIABLE_OUTPUT);
			
			DataNode dataNodeBeforeLoop = (variableBeforeLoop != null ? variableBeforeLoop.getDataNode() : null);
			DataNode dataNodeAfterLoop = variableInsideLoop.getDataNode();
			DataNode appendedStringValue = getAppendedStringValue(dataNodeBeforeLoop, dataNodeAfterLoop);
			
			if (appendedStringValue != null) {		
				PhpVariable phpVariable = new PhpVariable(SPECIAL_VARIABLE_OUTPUT);
				if (dataNodeBeforeLoop != null)
					phpVariable.appendStringValue(dataNodeBeforeLoop);
				
				RepeatNode repeatNode = new RepeatNode(conditionString, appendedStringValue);
				phpVariable.appendStringValue(repeatNode);
				this.putVariableInCurrentScope(phpVariable);
			}
		}
	}
	
	/**
	 * Returns the DataNode that is appended to the stringValueBeforeLoop to get the stringValueAfterLoop.
	 * It is expected that the first child nodes of the stringValueAfterLoop are the child nodes of the stringValueBeforeLoop.
	 * If it is not so, then we don't know how to handle it nicely yet, let's return null.
	 */
	private DataNode getAppendedStringValue(DataNode stringValueBeforeLoop, DataNode stringValueAfterLoop) {
		if (stringValueBeforeLoop == null)
			return stringValueAfterLoop;
		
		if ( !(stringValueAfterLoop instanceof ConcatNode) )
			return null;
		
		ArrayList<DataNode> stringValuesBeforeLoop = new ArrayList<DataNode>();
		ArrayList<DataNode> stringValuesAfterLoop = new ArrayList<DataNode>();
		
		if (stringValueBeforeLoop instanceof ConcatNode)
			stringValuesBeforeLoop.addAll(((ConcatNode) stringValueBeforeLoop).getChildNodes());
		else
			stringValuesBeforeLoop.add(stringValueBeforeLoop);
		stringValuesAfterLoop = ((ConcatNode) stringValueAfterLoop).getChildNodes();
			
		boolean checkPrefix = true;	// True if stringValuesBeforeLoop form the prefix of stringValuesAfterLoop
		for (int i = 0; i < stringValuesBeforeLoop.size(); i++) {
			if (i == stringValuesAfterLoop.size() || stringValuesBeforeLoop.get(i) != stringValuesAfterLoop.get(i)) {
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
			ConcatNode concatNode = new ConcatNode();
			for (int i = stringValuesBeforeLoop.size(); i < stringValuesAfterLoop.size(); i++)
				concatNode.appendChildNode(stringValuesAfterLoop.get(i));
			appendedStringValue = concatNode;
		}
		return appendedStringValue;
	}
	
	/**
	 * Updates the elementManager after executing a function.
	 */
	public void updateAfterFunctionExecution(ElementManager functionElementManager, ArrayList<FormalParameterNode> formalParameterNodes, ArrayList<ExpressionNode> argumentExpressionNodes) {
		// Update reference parameters
		for (FormalParameterNode formalParameterNode : formalParameterNodes) {
			if (formalParameterNode.isReference()) {
				int parameterIndex = formalParameterNodes.indexOf(formalParameterNode);
				String parameterName = formalParameterNode.resolveParameterName(null);
				
				if (parameterIndex >= argumentExpressionNodes.size()) {
					break;
				}
				if (!(argumentExpressionNodes.get(parameterIndex) instanceof VariableNode)) {
					MyLogger.log(MyLevel.TODO, "In ElementManager.updateAfterFunctionExecution: Reference parameter is not of type VariableNode.");
					continue;
				}
				
				String referencedVariableName = ((VariableNode) argumentExpressionNodes.get(parameterIndex)).resolveVariableName(null);
				PhpVariable phpVariable = new PhpVariable(referencedVariableName);
				phpVariable.setDataNode(functionElementManager.getVariableFromCurrentScope(parameterName).getDataNode());
				this.putVariableInCurrentScope(phpVariable);
			}
		}
		
		// Update global variables
		for (String globalVariableName : functionElementManager.getGlobalVariableNames()) {
			PhpVariable variableInsideFunction = functionElementManager.getVariableFromCurrentScope(globalVariableName);
			if (variableInsideFunction != null) {
				PhpVariable phpVariable = new PhpVariable(globalVariableName); 
				phpVariable.setDataNode(variableInsideFunction.getDataNode());
				this.putVariableInCurrentScope(phpVariable);
			}
		}
		
		// Update output
		if (functionElementManager.containsSpecialVariableOutput()) {
			PhpVariable phpVariable = new PhpVariable(SPECIAL_VARIABLE_OUTPUT); 
			phpVariable.setDataNode(functionElementManager.getVariableFromCurrentScope(SPECIAL_VARIABLE_OUTPUT).getDataNode());
			this.putVariableInCurrentScope(phpVariable);
		}
	}
	
	/*
	 * Append string values to output
	 */
	
	/**
	 * Appends string values to the current output.
	 * This function is used by the "echo" statement and the "print" function invocation.
	 */
	public void appendOutput(ArrayList<DataNode> resolvedExpressionNodes) {
		PhpVariable newOutputVariable = new PhpVariable(SPECIAL_VARIABLE_OUTPUT);
		PhpVariable oldOutputVariable = this.getVariableFromProgramScope(SPECIAL_VARIABLE_OUTPUT);
		
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
	 * current output values at exit points, plus the output value in the normal flow.
	 */
	public void addCurrentOutputToFinalOutput() {
		PhpVariable currentOutput = this.getCurrentOutput();
		if (currentOutput == null)
			return;
		
		PhpVariable finalOutput = this.getProgramScopeElementManager().getVariableFromCurrentScope(SPECIAL_VARIABLE_FINAL_OUTPUT);
		if (finalOutput == null) {
			PhpVariable finalOutputVariable = new PhpVariable(SPECIAL_VARIABLE_FINAL_OUTPUT);
			finalOutputVariable.setDataNode(currentOutput.getDataNode());
			this.getProgramScopeElementManager().putVariableInCurrentScope(finalOutputVariable);
		}
		else {
			SelectNode selectNode;
			if (isTrueBranch)
				selectNode = new SelectNode(conditionString, currentOutput.getDataNode(), finalOutput.getDataNode());
			else
				selectNode = new SelectNode(conditionString, finalOutput.getDataNode(), currentOutput.getDataNode());
			finalOutput.setDataNode(selectNode);
		}
	}
	
	/**
	 * Similar to final output values, the return values of a function
	 * are composed of all the return values at return statements.
	 */
	public void addReturnValue(DataNode currentReturnValue) {		
		PhpVariable finalReturn = this.getFunctionScopeElementManager().getVariableFromCurrentScope(SPECIAL_VARIABLE_RETURN);
		if (finalReturn == null) {
			PhpVariable finalReturnVariable = new PhpVariable(SPECIAL_VARIABLE_RETURN);
			finalReturnVariable.setDataNode(currentReturnValue);
			this.getFunctionScopeElementManager().putVariableInCurrentScope(finalReturnVariable);
		}
		else {
			SelectNode selectNode;
			if (isTrueBranch)
				selectNode = new SelectNode(conditionString, currentReturnValue, finalReturn.getDataNode());
			else
				selectNode = new SelectNode(conditionString, finalReturn.getDataNode(), currentReturnValue);
			finalReturn.setDataNode(selectNode);
		}
	}
	
	/**
	 * Temporarily removes the RETURN variable.
	 * @see servergraph.nodes.IncludeNode.execute(ElementManager)
	 */
	public void removeReturnValue() {
		this.getFunctionScopeElementManager().variableTable.remove(SPECIAL_VARIABLE_RETURN);
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
		return this.getProgramScopeElementManager().getVariableFromCurrentScope(SPECIAL_VARIABLE_FINAL_OUTPUT);
	}
	
	public PhpVariable getReturnValue() {
		return this.getFunctionScopeElementManager().getVariableFromCurrentScope(SPECIAL_VARIABLE_RETURN);
	}
	
	public HashSet<PhpFunction> getAllFunctions() {
		return new HashSet<PhpFunction>(functionTable.values());
	}
	
}
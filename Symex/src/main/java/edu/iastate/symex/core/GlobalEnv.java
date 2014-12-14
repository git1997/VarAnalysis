package edu.iastate.symex.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import edu.iastate.symex.config.SymexConfig;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.SpecialNode;
import edu.iastate.symex.php.nodes.ClassDeclarationNode;
import edu.iastate.symex.php.nodes.FileNode;
import edu.iastate.symex.php.nodes.FunctionDeclarationNode;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class GlobalEnv extends PhpEnv {
	
	/*
	 * Working directory
	 */
	private File workingDirectory = null; // [Optional] The project folder containing the PHP file to be executed
	
	/*
	 * Manage the declarations of functions, classes, and files,
	 * so that they only need to be parsed once.
	 */
	private HashMap<String, FunctionDeclarationNode> functionTable = new HashMap<String, FunctionDeclarationNode>();
	private HashMap<String, ClassDeclarationNode> classTable = new HashMap<String, ClassDeclarationNode>();
	private HashMap<File, FileNode> fileTable = new HashMap<File, FileNode>();
	
	/*
	 * The stacks of executed files and functions.
	 * (These fields can be used to prevent recursive function/file invocation)
	 */
	private Stack<String> functionStack = new Stack<String>();
	private Stack<File> fileStack = new Stack<File>();
	private HashSet<File> invokedFiles = new HashSet<File>(); // May contain files that are not in the fileStack (already executed)

	/*
	 * Manage the output.
	 * During execution, the final output will be collected from
	 * the output value in the normal flow, plus the output values at exit statements.
	 */
	private DataNode normalOutput = SpecialNode.UnsetNode.UNSET;
	private ValueSet outputAtExits = new ValueSet(); // Collection of output values at exit statements
	
	/**
	 * Constructor
	 */
	public GlobalEnv() {
		super(null);
	}

	/*
	 * Working directory
	 */

	public void setWorkingDirectory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public File getWorkingDirectory() {
		return workingDirectory;
	}

	/*
	 * Manage functions, classes, and files
	 */

	protected void putFunction_(String functionName, FunctionDeclarationNode phpFunction) {
		functionTable.put(functionName, phpFunction);
	}

	protected FunctionDeclarationNode getFunction_(String functionName) {
		return functionTable.get(functionName);
	}

	protected void putClass_(String className, ClassDeclarationNode phpClass) {
		classTable.put(className, phpClass);
	}

	protected ClassDeclarationNode getClass_(String className) {
		return classTable.get(className);
	}

	protected void putFile_(File fileName, FileNode phpFile) {
		fileTable.put(fileName, phpFile);
	}

	protected FileNode getFile_(File fileName) {
		return fileTable.get(fileName);
	}
	
	/*
	 * Manage invoked functions and included files
	 */

	protected void pushFunctionToStack_(String functionName) {
		functionStack.push(functionName);
	}

	protected String peekFunctionFromStack_() {
		return functionStack.peek();
	}

	protected String popFunctionFromStack_() {
		return functionStack.pop();
	}

	protected boolean containsFunctionInStack_(String functionName) {
		return functionStack.contains(functionName);
	}

	protected ArrayList<String> getFunctionStack_() {
		return new ArrayList<String>(functionStack);
	}
	
	protected void pushFileToStack_(File file) {
		fileStack.push(file);
		addInvokedFiles_(file); // Update invokedFiles every time a new file is pushed to stack
	}

	protected File peekFileFromStack_() {
		return fileStack.peek();
	}

	protected File popFileFromStack_() {
		return fileStack.pop();
	}

	protected boolean containsFileInStack_(File file) {
		return fileStack.contains(file);
	}

	protected ArrayList<File> getFileStack_() {
		return new ArrayList<File>(fileStack);
	}
	
	protected void addInvokedFiles_(File file) {
		invokedFiles.add(file);
	}
	
	/**
	 * Returns invoked files.
	 * NOTE: Invoked files may contain files that are not in the fileStack (already executed)
	 */
	protected HashSet<File> getInvokedFiles_() {
		return new HashSet<File>(invokedFiles);
	}
	
	/*
	 * Manage the output
	 */
	
	protected DataNode getFinalOutput_() {
		DataNode outputAtExitsValue = outputAtExits.getValue();
		if (outputAtExitsValue != SpecialNode.UnsetNode.UNSET && SymexConfig.COLLECT_OUTPUTS_FROM_EXIT_STATEMENTS) {
			// TODO The correct statement for constraint should be as follows:
			// 		Constraint constraint = outputAtExits.getUncoveredConstraint();
			// However, getUncoveredConstraint() often hangs when there are too many constraints.
			// As a work-around, let's create a simple constraint representing the normal case:
			Constraint constraint = ConstraintFactory.createAtomicConstraint("NORMAL_OUTPUT", PositionRange.UNDEFINED);
			
			return DataNodeFactory.createCompactSelectNode(constraint, normalOutput, outputAtExitsValue);
		}
		else
			return normalOutput;
	}
	
	protected void addOuptutAtExitToFinalOutput_(Constraint constraint, DataNode value) {
		outputAtExits.addValue(constraint, value);
	}
	
	protected void addNormalOutputToFinalOutput_(DataNode value) {
		 normalOutput = value;
	}
	
}

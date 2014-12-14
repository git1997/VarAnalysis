package edu.iastate.symex.php.nodes;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.FunctionInvocation;
import org.eclipse.php.internal.core.ast.nodes.InfixExpression;

import edu.iastate.symex.analysis.WebAnalysis;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.core.BranchEnv;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.FunctionEnv;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.ObjectNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;

/**
 * 
 * @author HUNG
 *
 */
public class FunctionInvocationNode extends VariableBaseNode {

	private ExpressionNode name;	// The name of the function
	private ArrayList<ExpressionNode> arguments = new ArrayList<ExpressionNode>();
	
	/*
	Represents function invocation. Holds the function name and the invocation parameters. 

	e.g. foo(),
	 $a(),
	 foo($a, 'a', 12)
	*/
	public FunctionInvocationNode(FunctionInvocation functionInvocation) {
		super(functionInvocation);
		this.name = ExpressionNode.createInstance(functionInvocation.getFunctionName().getName());
		for (Expression parameter : functionInvocation.parameters()) {
			ExpressionNode argument = ExpressionNode.createInstance(parameter);
			this.arguments.add(argument);
		}
	}
	
	/**
	 * Resolves the name of the function.
	 */
	public String getResolvedFunctionNameOrNull(Env env) {
		return name.getResolvedNameOrNull(env);
	}
	
	@Override
	public PhpVariable createVariablePossiblyWithNull(Env env) {
		MyLogger.log(MyLevel.TODO, "In FunctionInvocationNode.java: Don't know how to create a variable from a function invocation.");
		return null;
	}
	
	@Override
	public DataNode execute(Env env) {		
		return this.execute(env, null);
	}
		
	/**
	 * Executes the functionInvocation on an object (the object can be null)
	 * @param env
	 * @param object
	 */
	public DataNode execute(Env env, ObjectNode object) {
		/*
		 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB ANALYSIS CODE
		WebAnalysis.onFunctionInvocationExecute((FunctionInvocation) this.getAstNode(), env);
		// END OF WEB ANALYSIS CODE
		
		/*
		 * Get the function name
		 */
		String functionName = getResolvedFunctionNameOrNull(env);
		if (functionName == null)
			return DataNodeFactory.createSymbolicNode(this);
		
		// TODO [AdhocCode] In squirrelmail, some function calls are misspelled.
		// The following code is temporarily used to fixed that bug. Should be removed later.
		// BEGIN OF ADHOC CODE
//		if (functionName.equals("sqGetGlobalVar")) 
//			functionName = "sqgetGlobalVar";
		// END OF ADHOC CODE
		
		/*
		 * Avoid recursive function calling
		 */
		if (env.containsFunctionInStack(functionName))
			return DataNodeFactory.createSymbolicNode(this);
		
		/*
		 * Get the argument values
		 */
		ArrayList<DataNode> argumentValues = new ArrayList<DataNode>();
		for (ExpressionNode argument : arguments) {
			argumentValues.add(argument.execute(env));
		}
				
		/*
		 * For some standard PHP functions, process them separately.
		 */
		if (functionName.equals("print"))
			return php_print(argumentValues, env);
		else if (functionName.equals("define"))
			return php_define(argumentValues, env);
		else if (functionName.equals("dirname"))
			return php_dirname(argumentValues, env);
		else if (functionName.equals("file_exists"))
			return php_file_exists(argumentValues, env);
		else if (functionName.equals("exit") || functionName.equals("die")) // Equivalent								
			return php_exit(argumentValues, env);
		else if (functionName.equals("strtolower"))
			return php_strtolower(argumentValues, env);
		else if (functionName.equals("htmlspecialchars"))
			return php_htmlspecialchars(argumentValues, env);
		else if (functionName.equals("urlencode"))
			return php_urlencode(argumentValues, env);
		else if (functionName.equals("mysql_query"))
			return php_mysql_query(argumentValues, env);
		else if (functionName.equals("mysql_fetch_array") || functionName.equals("mysql_fetch_assoc")) // Equivalent
			return php_mysql_fetch_array(argumentValues, env);
		
		/*
		 * Get the function
		 */
		FunctionDeclarationNode function = null;
		if (object == null) {
			if (env.getFunction(functionName) != null)
				function = env.getFunction(functionName);
		}
		else {
			function = object.getClassDeclarationNode().getFunction(functionName);
		}
		if (function == null)
			return DataNodeFactory.createSymbolicNode(this);		
		
		/*
		 * Prepare to execute the function
		 */
					
		// Set up a new scope
		FunctionEnv functionEnv = new FunctionEnv(env, functionName);
		
		// Set up parameters
		ArrayList<FormalParameterNode> parameters = function.getFormalParameters();
		for (FormalParameterNode parameter : parameters) {
			String parameterName = parameter.getResolvedParameterNameOrNull(functionEnv);
			int parameterIndex = parameters.indexOf(parameter);
			
			if (parameterName == null)
				continue;
			
			if (parameterIndex >= arguments.size()) {
				DataNode parameterValue;
				if (parameter.getDefaultValue() != null)
					parameterValue = parameter.getDefaultValue().execute(functionEnv);
				else
					parameterValue = DataNodeFactory.createSymbolicNode(parameter);
				
				functionEnv.getOrPutThenWriteVariable(parameterName, parameterValue);
				continue;
			}
			
			// Handle reference parameters
			if (parameter.isReference()) {
				ExpressionNode argument = arguments.get(parameterIndex);
				if (argument instanceof VariableBaseNode) {
					VariableBaseNode variableBase = (VariableBaseNode) argument;
					PhpVariable phpVariable = variableBase.createVariablePossiblyWithNull(env);
					
					if (phpVariable != null) {
						functionEnv.putVariable(parameterName, phpVariable);
						continue;
					}
					// Else, handle reference parameteres as if they are regular ones.
				}
				// Else, handle reference parameteres as if they are regular ones.
			} 
			
			// Handle regular parameters
			DataNode argumentValue = argumentValues.get(parameterIndex);
			functionEnv.getOrPutThenWriteVariable(parameterName, argumentValue);
		}
		
		/*
		 * Execute the function
		 */
		
		functionEnv.pushFunctionToStack(functionName);
		//MyLogger.log(MyLevel.PROGRESS, "Executing files " + env.getFileStack() + " functions " + env.getFunctionStack() + " ...");
		
		function.getBody().execute(functionEnv);
		
		//MyLogger.log(MyLevel.PROGRESS, "Done with files " + env.getFileStack() + " functions " + env.getFunctionStack() + ".");
		functionEnv.popFunctionFromStack();
		
		/*
		 * Finish up
		 */

		// Update the env
		env.updateAfterFunctionExecution(functionEnv);
		
		// Get the return value of the function.
		DataNode returnValue = functionEnv.getReturnValue();
		if (returnValue != SpecialNode.UnsetNode.UNSET)
			return returnValue;
		else
			return DataNodeFactory.createSymbolicNode(this);
	}
	
	/*
	 * IMPLEMENTING STANDARD PHP FUNCTIONS
	 */
	
	/**
	 * Implements the standard PHP function: print
	 */
	private DataNode php_print(ArrayList<DataNode> arguments, Env env) {	
		env.appendOutput(arguments);
		return DataNodeFactory.createSymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: define
	 */
	private DataNode php_define(ArrayList<DataNode> arguments, Env env) {	
		if (arguments.size() == 2) {
			String constantName = arguments.get(0).getExactStringValueOrNull();
			DataNode constantValue = arguments.get(1);
			if (constantName != null)
				env.setPredefinedConstantValue(constantName, constantValue);
		}
		return DataNodeFactory.createSymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: dirname
	 */
	private DataNode php_dirname(ArrayList<DataNode> arguments, Env env) {
		if (arguments.size() == 1) {
			String fileName = arguments.get(0).getExactStringValueOrNull();
			if (fileName != null) {
				String dirName = new File(fileName).getParent();
				if (dirName != null)
					return DataNodeFactory.createLiteralNode(dirName);
			}
		}
		return DataNodeFactory.createSymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: file_exists
	 */
	private DataNode php_file_exists(ArrayList<DataNode> arguments, Env env) {
		if (arguments.size() == 1) {
			File file = env.resolveFile(arguments.get(0));
			if (file != null)
				return SpecialNode.BooleanNode.TRUE;
		}
		return DataNodeFactory.createSymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: exit
	 */
	private DataNode php_exit(ArrayList<DataNode> arguments, Env env) {
		// [ADHOC CODE] TODO Work-around to handle statments such as below
		// 		mysql_connect(...) or die("Could not connect: " . mysql_error())
		if (this.getAstNode().getParent() instanceof InfixExpression
				&& ((InfixExpression) this.getAstNode().getParent()).getOperator() == InfixExpression.OP_STRING_OR) {
			Constraint constraint = ConstraintFactory.createAtomicConstraint(this.getSourceCode(), this.getLocation());
			BranchEnv branchEnv = new BranchEnv(env, constraint);
			
			branchEnv.appendOutput(arguments);
			branchEnv.addOutputAtExitToFinalOutput();
		
			env.backtrackAfterBranchExecution(branchEnv);
			return SpecialNode.ControlNode.EXIT;
		}
		// [END OF ADHOC CODE]
		
		env.appendOutput(arguments);
		env.addOutputAtExitToFinalOutput();
		return SpecialNode.ControlNode.EXIT;
	}
	
	/**
	 * Implements the standard PHP function: strtolower
	 */
	private DataNode php_strtolower(ArrayList<DataNode> arguments, Env env) {	
		if (arguments.size() == 1 && arguments.get(0) instanceof LiteralNode) {
			String str = arguments.get(0).getExactStringValueOrNull();
			if (str != null)
				return DataNodeFactory.createLiteralNode(str.toLowerCase()); // TODO Add positionRange
		}
		return DataNodeFactory.createSymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: htmlspecialchars
	 */
	private DataNode php_htmlspecialchars(ArrayList<DataNode> arguments, Env env) {	
		if (arguments.size() == 1)
			return arguments.get(0);
		else
			return DataNodeFactory.createSymbolicNode(this);
		
	}

	/**
	 * Implements the standard PHP function: urlencode
	 */
	private DataNode php_urlencode(ArrayList<DataNode> arguments, Env env) {	
		if (arguments.size() == 1)
			return arguments.get(0);
		else
			return DataNodeFactory.createSymbolicNode(this);		
	}
	
	/**
	 * Implements the standard PHP function: mysql_query
	 */
	private DataNode php_mysql_query(ArrayList<DataNode> arguments, Env env) {
		/*
		 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB ANALYSIS CODE
		if (WebAnalysis.entityDetectionListener != null)
			return WebAnalysis.onMysqlQuery((FunctionInvocation) this.getAstNode(), arguments.get(0), env);
		// END OF WEB ANALYSIS CODE
		
		return DataNodeFactory.createSymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: mysql_fetch_array
	 */
	private DataNode php_mysql_fetch_array(ArrayList<DataNode> arguments, Env env) {
		/*
		 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB ANALYSIS CODE
		if (WebAnalysis.entityDetectionListener != null)
			return WebAnalysis.onMysqlFetchArray((FunctionInvocation) this.getAstNode(), arguments.get(0), env);
		// END OF WEB ANALYSIS CODE
		
		return DataNodeFactory.createSymbolicNode(this);
	}
	
}
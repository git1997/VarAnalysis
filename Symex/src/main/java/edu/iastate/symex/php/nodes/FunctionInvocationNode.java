package edu.iastate.symex.php.nodes;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.FunctionInvocation;

import edu.iastate.symex.config.SymexConfig;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.ObjectNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.php.elements.PhpVariable;

/**
 * 
 * @author HUNG
 *
 */
public class FunctionInvocationNode extends VariableBaseNode {

	private ExpressionNode name;	// The name of the function
	private ArrayList<ExpressionNode> parameters = new ArrayList<ExpressionNode>();
	
	/*
	Represents function invocation. Holds the function name and the invocation parameters. 

	e.g. foo(),
	 $a(),
	 foo($a, 'a', 12)
	*/
	public FunctionInvocationNode(FunctionInvocation functionInvocation) {
		super(functionInvocation);
		this.name = ExpressionNode.createInstance(functionInvocation.getFunctionName().getName());
		for (Expression expression : functionInvocation.parameters()) {
			ExpressionNode expressionNode = ExpressionNode.createInstance(expression);
			this.parameters.add(expressionNode);
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
	 * @param ojbectNode
	 * @return
	 */
	public DataNode execute(Env env, ObjectNode objectNode) {
		/*
		 * Get the function name
		 */
		String functionName = getResolvedFunctionNameOrNull(env);		
		
		// TODO: [AdhocCode] In squirrelmail, some function calls are misspelled.
		// The following code is temporarily used to fixed that bug. Should be removed later.
		// BEGIN OF ADHOC CODE
		if (functionName.equals("sqGetGlobalVar")) 
			functionName = "sqgetGlobalVar";
		// END OF ADHOC CODE
		
		// Avoid recursive function calling
		if (env.containsFunctionInStack(functionName))
			return new SymbolicNode(this);
		
		/*
		 * For some standard PHP functions, process them separately.
		 */
		if (functionName.equals("isset") || functionName.equals("empty"))
			return php_isset(parameters, env);	
			
		/*
		 * Get the argument values
		 */
		ArrayList<DataNode> argumentValues = new ArrayList<DataNode>();
		for (ExpressionNode argumentExpressionNode : parameters) {
			argumentValues.add(argumentExpressionNode.execute(env));
		}
				
		/*
		 * For some standard PHP functions, process them separately.
		 * Note that argumentExpressionNodes (of type ExpressionNode)
		 * 		have now been resolved to argumentValues (of type DataNode)
		 */
		if (functionName.equals("exit") || functionName.equals("die")) 										
			return php_exit(argumentValues, env);
			// TODO Handle die or not?
			// Should not handle the "die" function because "die" indicates something abnormal happened,
		 	// whereas with "exit", the developer's intention is to create different versions.
		else if (functionName.equals("print"))
			return php_print(argumentValues, env);
		else if (functionName.equals("define"))
			return php_define(argumentValues, env);
		else if (functionName.equals("htmlspecialchars"))
			return php_htmlspecialchars(argumentValues, env);
		else if (functionName.equals("urlencode"))
			return php_urlencode(argumentValues, env);
		else if (functionName.equals("dirname"))
			return php_dirname(argumentValues, env);
		else if (functionName.equals("strtolower"))
			return php_strtolower(argumentValues, env);
		else if (functionName.equals("mysql_query"))
			return php_mysql_query(argumentValues, env, this);
		else if (functionName.equals("mysql_fetch_array") || functionName.equals("mysql_fetch_assoc"))
			return php_mysql_fetch_array(argumentValues, env);
		
		/*
		 * Get the function node, return a SymbolicNode if not found.
		 */
		FunctionDeclarationNode functionNode = null;
		if (objectNode == null) {
			if (env.getFunction(functionName) != null)
				functionNode = env.getFunction(functionName).getFunctionDeclarationNode();
		}
		else {
			functionNode = objectNode.getClassDeclarationNode().getFunction(functionName);
		}
		if (functionNode == null)
			return new SymbolicNode(this);		
		
		/*
		 * Prepare to execute the function
		 */
					
		// Set up a new scope for the execution of the function
		Env functionenv = new Env(env, functionName);
		
		// Set up parameters
		ArrayList<FormalParameterNode> formalParameterNodes = functionNode.getFormalParameters();
		for (FormalParameterNode formalParameterNode : formalParameterNodes) {
			int parameterIndex = formalParameterNodes.indexOf(formalParameterNode);
			String parameterName = formalParameterNode.getResolvedParameterNameOrNull(functionenv);
			DataNode parameterValue;
			if (parameterIndex < argumentValues.size())
				parameterValue = argumentValues.get(parameterIndex);
			else {
				if (formalParameterNode.getDefaultValue() != null)
					parameterValue = formalParameterNode.getDefaultValue().execute(functionenv);
				else
					parameterValue = new SymbolicNode(formalParameterNode);
			}			
			PhpVariable phpVariable = new PhpVariable(parameterName);
			phpVariable.setDataNode(parameterValue);
			functionenv.putVariableInCurrentScope(phpVariable);
		}
		
		/*
		 * Execute the function
		 */
		functionenv.pushFunctionToStack(functionName);
		//MyLogger.log(MyLevel.PROGRESS, "Executing files " + env.getFileStack() + " functions " + env.getFunctionStack() + " ...");
		
		functionNode.getBody().execute(functionenv);
		
		//MyLogger.log(MyLevel.PROGRESS, "Done with files " + env.getFileStack() + " functions " + env.getFunctionStack() + ".");
		functionenv.popFunctionFromStack();
		
		/*
		 * Finish up
		 */
		
		// Update the env
		env.updateAfterFunctionExecution(functionenv, formalParameterNodes, parameters);
		
		// Get the return value of the function.
		PhpVariable returnValue = functionenv.getReturnValue();
		if (returnValue != null)
			return returnValue.getDataNode();
		else
			return new SymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: exit
	 */
	private DataNode php_exit(ArrayList<DataNode> parameterValues, Env env) {	
		env.setHasExitStatement(true);
		if (SymexConfig.COLLECT_OUTPUTS_FROM_EXIT_STATEMENTS)
			env.addCurrentOutputToFinalOutput();
		return new SymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: print
	 */
	private DataNode php_print(ArrayList<DataNode> parameterValues, Env env) {	
		env.appendOutput(parameterValues);
		return new SymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: define
	 */
	private DataNode php_define(ArrayList<DataNode> parameterValues, Env env) {	
		if (parameterValues.size() == 2) {
			String constantName = parameterValues.get(0).getApproximateStringValue();
			DataNode constantValue = parameterValues.get(1);
			env.setPredefinedConstantValue(constantName, constantValue);
		}
		return new SymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: htmlspecialchars
	 */
	private DataNode php_htmlspecialchars(ArrayList<DataNode> parameterValues, Env env) {	
		if (parameterValues.size() == 1)
			return parameterValues.get(0);
		else
			return new SymbolicNode(this);
		
	}

	/**
	 * Implements the standard PHP function: urlencode
	 */
	private DataNode php_urlencode(ArrayList<DataNode> parameterValues, Env env) {	
		if (parameterValues.size() == 1)
			return parameterValues.get(0);
		else
			return new SymbolicNode(this);		
	}
	
	/**
	 * Implements the standard PHP function: dirname
	 */
	private DataNode php_dirname(ArrayList<DataNode> parameterValues, Env env) {	
		if (parameterValues.size() == 1) {
			String fileName = parameterValues.get(0).getApproximateStringValue();
			String dirName = new File(fileName).getParent();
 			return (dirName != null ? DataNodeFactory.createLiteralNode(dirName) : new SymbolicNode(this));
		}
		else
			return new SymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: strtolower
	 */
	private DataNode php_strtolower(ArrayList<DataNode> parameterValues, Env env) {	
		if (parameterValues.size() == 1 && parameterValues.get(0) instanceof LiteralNode) {
			String str = parameterValues.get(0).getApproximateStringValue();
			return DataNodeFactory.createLiteralNode(str.toLowerCase()); // TODO Add positionRange
		}
		else
			return new SymbolicNode(this);
	}
	
	/*
	 * The following code is used from BabelRef to identify mysql_query function calls
	 */
	// BEGIN OF BABELREF CODE
	public interface IMysqlQueryStatementListener {
		public void mysqlQueryStatementFound(DataNode dataNode, String scope);
	}
	
	public static IMysqlQueryStatementListener mysqlQueryStatementListener = null;
	// END OF BABELREF CODE
	
	/**
	 * Implements the standard PHP function: mysql_query
	 */
	private DataNode php_mysql_query(ArrayList<DataNode> parameterValues, Env env, Object scope) {
		/*
		 * The following code is used from BabelRef to identify mysql_query function calls
		 */
		// BEGIN OF BABELREF CODE
		if (mysqlQueryStatementListener != null && parameterValues.size() == 1) {
			mysqlQueryStatementListener.mysqlQueryStatementFound(parameterValues.get(0), "mysql_query_" + scope.hashCode()); // @see edu.iastate.symex.php.nodes.ArrayAccessNode.execute(env)
		}
		
		if (mysqlQueryStatementListener != null)
			return DataNodeFactory.createLiteralNode("mysql_query_" + scope.hashCode()); // @see edu.iastate.symex.php.nodes.ArrayAccessNode.execute(env)
		// END OF BABELREF CODE
		
		return new SymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: mysql_fetch_array
	 */
	private DataNode php_mysql_fetch_array(ArrayList<DataNode> parameterValues, Env env) {
		/*
		 * The following code is used from BabelRef to handle mysql_fetch_array function calls
		 */
		// BEGIN OF BABELREF CODE
		if (mysqlQueryStatementListener != null && parameterValues.size() == 1)
			return parameterValues.get(0);	// @see edu.iastate.symex.php.nodes.ArrayAccessNode.execute(env)
		// END OF BABELREF CODE
		
		return new SymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: isset
	 */
	private DataNode php_isset(ArrayList<ExpressionNode> argumentExpressionNodes, Env env) {
		/*
		 * The following code is used from BabelRef to identify PHP variable entities.
		 */
		// BEGIN OF BABELREF CODE
//		if (VariableNode.variableDeclListener != null) {
//			ExpressionNode argumentExpressionNode = argumentExpressionNodes.get(0);
//			if (argumentExpressionNode instanceof VariableNode)
//				((VariableNode) argumentExpressionNode).variableDeclFound(env);
//		}
		// END OF BABELREF CODE
		
		return new SymbolicNode(this);
	}
	
}
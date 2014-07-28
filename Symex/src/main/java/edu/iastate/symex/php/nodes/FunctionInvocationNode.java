package edu.iastate.symex.php.nodes;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.FunctionInvocation;

import edu.iastate.symex.analysis.WebAnalysis;
import edu.iastate.symex.config.SymexConfig;
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
		
		// TODO: [AdhocCode] In squirrelmail, some function calls are misspelled.
		// The following code is temporarily used to fixed that bug. Should be removed later.
		// BEGIN OF ADHOC CODE
		if (functionName.equals("sqGetGlobalVar")) 
			functionName = "sqgetGlobalVar";
		// END OF ADHOC CODE
		
		// Avoid recursive function calling
		if (env.containsFunctionInStack(functionName))
			return DataNodeFactory.createSymbolicNode(this);
		
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
		if (functionName.equals("exit") || functionName.equals("die")) // Equivalent								
			return php_exit(argumentValues, env);
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
			return php_mysql_query(argumentValues, env);
		else if (functionName.equals("mysql_fetch_array") || functionName.equals("mysql_fetch_assoc"))
			return php_mysql_fetch_array(argumentValues, env);
		
		/*
		 * Get the function node, return a SymbolicNode if not found.
		 */
		FunctionDeclarationNode functionNode = null;
		if (objectNode == null) {
			if (env.getFunction(functionName) != null)
				functionNode = env.getFunction(functionName);
		}
		else {
			functionNode = objectNode.getClassDeclarationNode().getFunction(functionName);
		}
		if (functionNode == null)
			return DataNodeFactory.createSymbolicNode(this);		
		
		/*
		 * Prepare to execute the function
		 */
					
		// Set up a new scope for the execution of the function
		FunctionEnv functionEnv = new FunctionEnv(env, functionName);
		
		// Set up parameters
		ArrayList<FormalParameterNode> formalParameterNodes = functionNode.getFormalParameters();
		for (FormalParameterNode formalParameterNode : formalParameterNodes) {
			int parameterIndex = formalParameterNodes.indexOf(formalParameterNode);
			String parameterName = formalParameterNode.getResolvedParameterNameOrNull(functionEnv);
			DataNode parameterValue;
			if (parameterIndex < argumentValues.size())
				parameterValue = argumentValues.get(parameterIndex);
			else {
				if (formalParameterNode.getDefaultValue() != null)
					parameterValue = formalParameterNode.getDefaultValue().execute(functionEnv);
				else
					parameterValue = DataNodeFactory.createSymbolicNode(formalParameterNode);
			}			
			PhpVariable phpVariable = new PhpVariable(parameterName);
			phpVariable.setDataNode(parameterValue);
			functionEnv.writeVariable(phpVariable);
		}
		
		/*
		 * Execute the function
		 */
		functionEnv.pushFunctionToStack(functionName);
		//MyLogger.log(MyLevel.PROGRESS, "Executing files " + env.getFileStack() + " functions " + env.getFunctionStack() + " ...");
		
		functionNode.getBody().execute(functionEnv);
		
		//MyLogger.log(MyLevel.PROGRESS, "Done with files " + env.getFileStack() + " functions " + env.getFunctionStack() + ".");
		functionEnv.popFunctionFromStack();
		
		/*
		 * Finish up
		 */
		
		// Update the env
		env.updateAfterFunctionExecution(functionEnv, formalParameterNodes, parameters);
		
		// Get the return value of the function.
		PhpVariable returnValue = functionEnv.getReturnValue();
		if (returnValue != null)
			return returnValue.getDataNode();
		else
			return DataNodeFactory.createSymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: exit
	 */
	private DataNode php_exit(ArrayList<DataNode> parameterValues, Env env) {	
		// TODO How should we handle exit?
		if (SymexConfig.COLLECT_OUTPUTS_FROM_EXIT_STATEMENTS) {
			env.setHasExitStatement(true);
			env.addCurrentOutputToFinalOutput();
		}
		return SpecialNode.ControlNode.EXIT;
	}
	
	/**
	 * Implements the standard PHP function: print
	 */
	private DataNode php_print(ArrayList<DataNode> parameterValues, Env env) {	
		env.appendOutput(parameterValues);
		return DataNodeFactory.createSymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: define
	 */
	private DataNode php_define(ArrayList<DataNode> parameterValues, Env env) {	
		if (parameterValues.size() == 2) {
			String constantName = parameterValues.get(0).getExactStringValueOrNull();
			DataNode constantValue = parameterValues.get(1);
			if (constantName != null)
				env.setPredefinedConstantValue(constantName, constantValue);
		}
		return DataNodeFactory.createSymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: htmlspecialchars
	 */
	private DataNode php_htmlspecialchars(ArrayList<DataNode> parameterValues, Env env) {	
		if (parameterValues.size() == 1)
			return parameterValues.get(0);
		else
			return DataNodeFactory.createSymbolicNode(this);
		
	}

	/**
	 * Implements the standard PHP function: urlencode
	 */
	private DataNode php_urlencode(ArrayList<DataNode> parameterValues, Env env) {	
		if (parameterValues.size() == 1)
			return parameterValues.get(0);
		else
			return DataNodeFactory.createSymbolicNode(this);		
	}
	
	/**
	 * Implements the standard PHP function: dirname
	 */
	private DataNode php_dirname(ArrayList<DataNode> parameterValues, Env env) {	
		if (parameterValues.size() == 1) {
			String fileName = parameterValues.get(0).getExactStringValueOrNull();
			if (fileName != null) {
				String dirName = new File(fileName).getParent();
				if (dirName != null)
					return DataNodeFactory.createLiteralNode(dirName);
			}
		}
		return DataNodeFactory.createSymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: strtolower
	 */
	private DataNode php_strtolower(ArrayList<DataNode> parameterValues, Env env) {	
		if (parameterValues.size() == 1 && parameterValues.get(0) instanceof LiteralNode) {
			String str = parameterValues.get(0).getExactStringValueOrNull();
			if (str != null)
				return DataNodeFactory.createLiteralNode(str.toLowerCase()); // TODO Add positionRange
		}
		return DataNodeFactory.createSymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: mysql_query
	 */
	private DataNode php_mysql_query(ArrayList<DataNode> parameterValues, Env env) {
		/*
		 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB ANALYSIS CODE
		if (WebAnalysis.entityDetectionListener != null)
			return WebAnalysis.onMysqlQuery((FunctionInvocation) this.getAstNode(), parameterValues.get(0), env);
		// END OF WEB ANALYSIS CODE
		
		return DataNodeFactory.createSymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: mysql_fetch_array
	 */
	private DataNode php_mysql_fetch_array(ArrayList<DataNode> parameterValues, Env env) {
		/*
		 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB ANALYSIS CODE
		if (WebAnalysis.entityDetectionListener != null)
			return WebAnalysis.onMysqlFetchArray((FunctionInvocation) this.getAstNode(), parameterValues.get(0), env);
		// END OF WEB ANALYSIS CODE
		
		return DataNodeFactory.createSymbolicNode(this);
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
		
		return DataNodeFactory.createSymbolicNode(this);
	}
	
}
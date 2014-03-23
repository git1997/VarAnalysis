package php.nodes;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.FunctionInvocation;

import config.DataModelConfig;
import php.ElementManager;
import php.elements.PhpVariable;
import util.logging.MyLevel;
import util.logging.MyLogger;
import datamodel.nodes.DataNode;
import datamodel.nodes.LiteralNode;
import datamodel.nodes.ObjectNode;
import datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class FunctionInvocationNode extends VariableBaseNode {

	private ExpressionNode functionNameExpressionNode;	// The name of the function
	private String functionName = null;
	
	private ArrayList<ExpressionNode> argumentExpressionNodes = new ArrayList<ExpressionNode>();
	
	/*
	Represents function invocation. Holds the function name and the invocation parameters. 

	e.g. foo(),
	 $a(),
	 foo($a, 'a', 12)
	*/
	public FunctionInvocationNode(FunctionInvocation functionInvocation) {
		super(functionInvocation);
		this.functionNameExpressionNode = ExpressionNode.createInstance(functionInvocation.getFunctionName().getName());
		for (Expression expression : functionInvocation.parameters()) {
			ExpressionNode expressionNode = ExpressionNode.createInstance(expression);
			this.argumentExpressionNodes.add(expressionNode);
		}
	}
	
	/**
	 * Resolves the name of the function.
	 */
	public String resolveFunctionName(ElementManager elementManager) {
		if (functionName == null)
			functionName = functionNameExpressionNode.resolveName(elementManager);
		return functionName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.VariableBaseNode#createVariablePossiblyWithNull(servergraph.ElementManager)
	 */
	@Override
	public PhpVariable createVariablePossiblyWithNull(ElementManager elementManager) {
		MyLogger.log(MyLevel.TODO, "In FunctionInvocationNode.java: Don't know how to create a variable from a function invocation.");
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {		
		return this.execute(elementManager, null);
	}
		
	/**
	 * Executes the functionInvocation on an object (the object can be null)
	 * @param elementManager
	 * @param ojbectNode
	 * @return
	 */
	public DataNode execute(ElementManager elementManager, ObjectNode objectNode) {
		/*
		 * Get the function name
		 */
		String functionName = resolveFunctionName(elementManager);		
		
		// TODO: [AdhocCode] In squirrelmail, some function calls are misspelled.
		// The following code is temporarily used to fixed that bug. Should be removed later.
		// BEGIN OF ADHOC CODE
		if (functionName.equals("sqGetGlobalVar")) 
			functionName = "sqgetGlobalVar";
		// END OF ADHOC CODE
		
		// Avoid recursive function calling
		if (elementManager.containsFunctionInStack(functionName))
			return new SymbolicNode(this);
		
		/*
		 * For some standard PHP functions, process them separately.
		 */
		if (functionName.equals("isset") || functionName.equals("empty"))
			return php_isset(argumentExpressionNodes, elementManager);	
			
		/*
		 * Get the argument values
		 */
		ArrayList<DataNode> argumentValues = new ArrayList<DataNode>();
		for (ExpressionNode argumentExpressionNode : argumentExpressionNodes) {
			argumentValues.add(argumentExpressionNode.execute(elementManager));
		}
				
		/*
		 * For some standard PHP functions, process them separately.
		 * Note that argumentExpressionNodes (of type ExpressionNode)
		 * 		have now been resolved to argumentValues (of type DataNode)
		 */
		if (functionName.equals("exit") || functionName.equals("die")) 										
			return php_exit(argumentValues, elementManager);
			// TODO Handle die or not?
			// Should not handle the "die" function because "die" indicates something abnormal happened,
		 	// whereas with "exit", the developer's intention is to create different versions.
		else if (functionName.equals("print"))
			return php_print(argumentValues, elementManager);
		else if (functionName.equals("define"))
			return php_define(argumentValues, elementManager);
		else if (functionName.equals("htmlspecialchars"))
			return php_htmlspecialchars(argumentValues, elementManager);
		else if (functionName.equals("urlencode"))
			return php_urlencode(argumentValues, elementManager);
		else if (functionName.equals("dirname"))
			return php_dirname(argumentValues, elementManager);
		else if (functionName.equals("strtolower"))
			return php_strtolower(argumentValues, elementManager);
		else if (functionName.equals("mysql_query"))
			return php_mysql_query(argumentValues, elementManager, this);
		else if (functionName.equals("mysql_fetch_array") || functionName.equals("mysql_fetch_assoc"))
			return php_mysql_fetch_array(argumentValues, elementManager);
		
		/*
		 * Get the function node, return a SymbolicNode if not found.
		 */
		FunctionDeclarationNode functionNode = null;
		if (objectNode == null) {
			if (elementManager.getFunction(functionName) != null)
				functionNode = elementManager.getFunction(functionName).getFunctionDeclarationNode();
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
		ElementManager functionElementManager = new ElementManager(elementManager, functionName);
		
		// Set up parameters
		ArrayList<FormalParameterNode> formalParameterNodes = functionNode.getFormalParameterNodes();
		for (FormalParameterNode formalParameterNode : formalParameterNodes) {
			int parameterIndex = formalParameterNodes.indexOf(formalParameterNode);
			String parameterName = formalParameterNode.resolveParameterName(functionElementManager);
			DataNode parameterValue;
			if (parameterIndex < argumentValues.size())
				parameterValue = argumentValues.get(parameterIndex);
			else {
				if (formalParameterNode.getDefaultValue() != null)
					parameterValue = formalParameterNode.getDefaultValue().execute(functionElementManager);
				else
					parameterValue = new SymbolicNode(formalParameterNode);
			}			
			PhpVariable phpVariable = new PhpVariable(parameterName);
			phpVariable.setDataNode(parameterValue);
			functionElementManager.putVariableInCurrentScope(phpVariable);
		}
		
		/*
		 * Execute the function
		 */
		functionElementManager.pushFunctionToStack(functionName);
		//MyLogger.log(MyLevel.PROGRESS, "Executing files " + elementManager.getFileStack() + " functions " + elementManager.getFunctionStack() + " ...");
		
		functionNode.getBodyNode().execute(functionElementManager);
		
		//MyLogger.log(MyLevel.PROGRESS, "Done with files " + elementManager.getFileStack() + " functions " + elementManager.getFunctionStack() + ".");
		functionElementManager.popFunctionFromStack();
		
		/*
		 * Finish up
		 */
		
		// Update the elementManager
		elementManager.updateAfterFunctionExecution(functionElementManager, formalParameterNodes, argumentExpressionNodes);
		
		// Get the return value of the function.
		PhpVariable returnValue = functionElementManager.getReturnValue();
		if (returnValue != null)
			return returnValue.getDataNode();
		else
			return new SymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: exit
	 */
	private DataNode php_exit(ArrayList<DataNode> parameterValues, ElementManager elementManager) {	
		elementManager.setHasExitStatement(true);
		if (DataModelConfig.COLLECT_OUTPUTS_FROM_EXIT_STATEMENTS)
			elementManager.addCurrentOutputToFinalOutput();
		return new SymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: print
	 */
	private DataNode php_print(ArrayList<DataNode> parameterValues, ElementManager elementManager) {	
		elementManager.appendOutput(parameterValues);
		return new SymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: define
	 */
	private DataNode php_define(ArrayList<DataNode> parameterValues, ElementManager elementManager) {	
		if (parameterValues.size() == 2) {
			String constantName = parameterValues.get(0).getApproximateStringValue();
			DataNode constantValue = parameterValues.get(1);
			elementManager.setPredefinedConstantValue(constantName, constantValue);
		}
		return new SymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: htmlspecialchars
	 */
	private DataNode php_htmlspecialchars(ArrayList<DataNode> parameterValues, ElementManager elementManager) {	
		if (parameterValues.size() == 1)
			return parameterValues.get(0);
		else
			return new SymbolicNode(this);
		
	}

	/**
	 * Implements the standard PHP function: urlencode
	 */
	private DataNode php_urlencode(ArrayList<DataNode> parameterValues, ElementManager elementManager) {	
		if (parameterValues.size() == 1)
			return parameterValues.get(0);
		else
			return new SymbolicNode(this);		
	}
	
	/**
	 * Implements the standard PHP function: dirname
	 */
	private DataNode php_dirname(ArrayList<DataNode> parameterValues, ElementManager elementManager) {	
		if (parameterValues.size() == 1) {
			String fileName = parameterValues.get(0).getApproximateStringValue();
			String dirName = new File(fileName).getParent();
 			return (dirName != null ? new LiteralNode(dirName) : new SymbolicNode(this));
		}
		else
			return new SymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: strtolower
	 */
	private DataNode php_strtolower(ArrayList<DataNode> parameterValues, ElementManager elementManager) {	
		if (parameterValues.size() == 1 && parameterValues.get(0) instanceof LiteralNode) {
			String str = parameterValues.get(0).getApproximateStringValue();
			return new LiteralNode(str.toLowerCase(), this.getLocation());
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
	private DataNode php_mysql_query(ArrayList<DataNode> parameterValues, ElementManager elementManager, Object scope) {
		/*
		 * The following code is used from BabelRef to identify mysql_query function calls
		 */
		// BEGIN OF BABELREF CODE
		if (mysqlQueryStatementListener != null && parameterValues.size() == 1) {
			mysqlQueryStatementListener.mysqlQueryStatementFound(parameterValues.get(0), "mysql_query_" + scope.hashCode()); // @see php.nodes.ArrayAccessNode.execute(ElementManager)
		}
		
		if (mysqlQueryStatementListener != null)
			return new LiteralNode("mysql_query_" + scope.hashCode()); // @see php.nodes.ArrayAccessNode.execute(ElementManager)
		// END OF BABELREF CODE
		
		return new SymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: mysql_fetch_array
	 */
	private DataNode php_mysql_fetch_array(ArrayList<DataNode> parameterValues, ElementManager elementManager) {
		/*
		 * The following code is used from BabelRef to handle mysql_fetch_array function calls
		 */
		// BEGIN OF BABELREF CODE
		if (mysqlQueryStatementListener != null && parameterValues.size() == 1)
			return parameterValues.get(0);	// @see php.nodes.ArrayAccessNode.execute(ElementManager)
		// END OF BABELREF CODE
		
		return new SymbolicNode(this);
	}
	
	/**
	 * Implements the standard PHP function: isset
	 */
	private DataNode php_isset(ArrayList<ExpressionNode> argumentExpressionNodes, ElementManager elementManager) {
		/*
		 * The following code is used from BabelRef to identify PHP variable entities.
		 */
		// BEGIN OF BABELREF CODE
		if (VariableNode.variableDeclListener != null) {
			ExpressionNode argumentExpressionNode = argumentExpressionNodes.get(0);
			if (argumentExpressionNode instanceof VariableNode)
				((VariableNode) argumentExpressionNode).variableDeclFound(elementManager);
		}
		// END OF BABELREF CODE
		
		return new SymbolicNode(this);
	}
	
}
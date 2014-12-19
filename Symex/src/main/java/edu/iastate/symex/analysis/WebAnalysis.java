package edu.iastate.symex.analysis;

import java.util.HashSet;

import org.eclipse.php.internal.core.ast.nodes.ArrayAccess;
import org.eclipse.php.internal.core.ast.nodes.Assignment;
import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.FormalParameter;
import org.eclipse.php.internal.core.ast.nodes.FunctionDeclaration;
import org.eclipse.php.internal.core.ast.nodes.FunctionInvocation;
import org.eclipse.php.internal.core.ast.nodes.ReturnStatement;
import org.eclipse.php.internal.core.ast.nodes.Variable;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.php.nodes.ArrayAccessNode;

/**
 * 
 * @author HUNG
 *
 */
public class WebAnalysis {
	
	public static IEntityDetectionListener entityDetectionListener = null; // Usage of this interface must check for null before calling
	
	/*
	 * Interfaces
	 */
	
	public interface IEntityDetectionListener {
		
		/*
		 * Handle variables 
		 */
		public void onAssignmentExecute(Assignment assignment, PhpVariable phpVariable, Env env);
		
		public void onVariableExecute(Variable variable, PhpVariable phpVariable, Env env);
		
		/*
		 * Handle array access (e.g., $_REQUEST['input'] or $sql_row['name']) 
		 */
		public void onArrayAccessExecute(ArrayAccess arrayAccess, ArrayAccessNode arrayAccessNode, DataNode arrayNode, DataNode keyNode, Env env);
		
		/*
		 * Handle functions
		 */
		public void onFunctionDeclarationExecute(FunctionDeclaration functionDeclaration, Env env);
		
		public void onFunctionInvocationExecute(FunctionInvocation functionInvocation, Env env);
		
		public void onFunctionInvocationParameterPassing(FormalParameter parameter, PhpVariable phpVariable, Expression argument, Env env);
		
		public void onReturnStatementExecute(ReturnStatement returnStatement, Env env);
		
		public void onFunctionInvocationFinished(HashSet<PhpVariable> nonLocalDirtyVariablesInFunction, Env env);
		
		/*
		 * Handle SQL queries (e.g., mysql_query("SELECT name FROM products"))
		 * Track the propagation of SQL table columns, as in 
		 * 		mysql_query("SELECT name FROM products");
		 * 		$product = mysql_fetch_array($result);
		 * 		echo $product['name']
		 */
		public DataNode onMysqlQuery(FunctionInvocation functionInvocation, DataNode argumentValue, Env env);
		
		public DataNode onMysqlFetchArray(FunctionInvocation functionInvocation, DataNode argumentValue, Env env);
		
		/*
		 * Handle branches
		 */
		public void onTrueBranchExecutionStarted(Env env);
		
		public void onFalseBranchExecutionStarted(Env env);
		
		public void onBothBranchesExecutionFinished(HashSet<PhpVariable> dirtyVariablesInTrueBranch, HashSet<PhpVariable> dirtyVariablesInFalseBranch, Env env);
		
	}
	
	/*
	 * Methods
	 */
	
	public static void onAssignmentExecute(Assignment assignment, PhpVariable phpVariableDecl, Env env) {
		entityDetectionListener.onAssignmentExecute(assignment, phpVariableDecl, env);
	}
	
	public static void onVariableExecute(Variable variable, PhpVariable phpVariable, Env env) {
		entityDetectionListener.onVariableExecute(variable, phpVariable, env);
	}
	
	public static void onArrayAccessExecute(ArrayAccess arrayAccess, ArrayAccessNode arrayAccessNode, DataNode arrayNode, DataNode keyNode, Env env) {
		entityDetectionListener.onArrayAccessExecute(arrayAccess, arrayAccessNode, arrayNode, keyNode, env);
	}
	
	public static void onFunctionDeclarationExecute(FunctionDeclaration functionDeclaration, Env env) {
		entityDetectionListener.onFunctionDeclarationExecute(functionDeclaration, env);
	}
	
	public static void onFunctionInvocationExecute(FunctionInvocation functionInvocation, Env env) {
		entityDetectionListener.onFunctionInvocationExecute(functionInvocation, env);
	}
	
	public static void onFunctionInvocationParameterPassing(FormalParameter parameter, PhpVariable phpVariable, Expression argument, Env env) {
		entityDetectionListener.onFunctionInvocationParameterPassing(parameter, phpVariable, argument, env);
	}
	
	public static void onReturnStatementExecute(ReturnStatement returnStatement, Env env) {
		entityDetectionListener.onReturnStatementExecute(returnStatement, env);
	}
	
	public static void onFunctionInvocationFinished(HashSet<PhpVariable> nonLocalDirtyVariablesInFunction, Env env) {
		entityDetectionListener.onFunctionInvocationFinished(nonLocalDirtyVariablesInFunction, env);
	}
	
	public static DataNode onMysqlQuery(FunctionInvocation functionInvocation, DataNode argumentValue, Env env) {
		return entityDetectionListener.onMysqlQuery(functionInvocation, argumentValue, env);
	}
	
	public static DataNode onMysqlFetchArray(FunctionInvocation functionInvocation, DataNode argumentValue, Env env) {
		return entityDetectionListener.onMysqlFetchArray(functionInvocation, argumentValue, env);
	}
	
	public static void onTrueBranchExecutionStarted(Env env) {
		entityDetectionListener.onTrueBranchExecutionStarted(env);
	}
	
	public static void onFalseBranchExecutionStarted(Env env) {
		entityDetectionListener.onFalseBranchExecutionStarted(env);
	}
	
	public static void onBothBranchesExecutionFinished(HashSet<PhpVariable> dirtyVariablesInTrueBranch, HashSet<PhpVariable> dirtyVariablesInFalseBranch, Env env) {
		entityDetectionListener.onBothBranchesExecutionFinished(dirtyVariablesInTrueBranch, dirtyVariablesInFalseBranch, env);
	}

}

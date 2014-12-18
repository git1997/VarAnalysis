package edu.iastate.symex.analysis;

import java.util.HashSet;

import org.eclipse.php.internal.core.ast.nodes.ArrayAccess;
import org.eclipse.php.internal.core.ast.nodes.Assignment;
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
	
	public static IEntityDetectionListener entityDetectionListener = null;
	
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
		
		public void onReturnStatementExecute(ReturnStatement returnStatement, Env env);
		
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
		if (entityDetectionListener != null)
			entityDetectionListener.onAssignmentExecute(assignment, phpVariableDecl, env);
	}
	
	public static void onVariableExecute(Variable variable, PhpVariable phpVariable, Env env) {
		if (entityDetectionListener != null)
			entityDetectionListener.onVariableExecute(variable, phpVariable, env);
	}
	
	public static void onArrayAccessExecute(ArrayAccess arrayAccess, ArrayAccessNode arrayAccessNode, DataNode arrayNode, DataNode keyNode, Env env) {
		if (entityDetectionListener != null)
			entityDetectionListener.onArrayAccessExecute(arrayAccess, arrayAccessNode, arrayNode, keyNode, env);
	}
	
	public static void onFunctionDeclarationExecute(FunctionDeclaration functionDeclaration, Env env) {
		if (entityDetectionListener != null)
			entityDetectionListener.onFunctionDeclarationExecute(functionDeclaration, env);
	}
	
	public static void onFunctionInvocationExecute(FunctionInvocation functionInvocation, Env env) {
		if (entityDetectionListener != null)
			entityDetectionListener.onFunctionInvocationExecute(functionInvocation, env);
	}
	
	public static void onReturnStatementExecute(ReturnStatement returnStatement, Env env) {
		if (entityDetectionListener != null)
			entityDetectionListener.onReturnStatementExecute(returnStatement, env);
	}
	
	public static DataNode onMysqlQuery(FunctionInvocation functionInvocation, DataNode argumentValue, Env env) {
		if (entityDetectionListener != null)
			return entityDetectionListener.onMysqlQuery(functionInvocation, argumentValue, env);
		else
			return null;
	}
	
	public static DataNode onMysqlFetchArray(FunctionInvocation functionInvocation, DataNode argumentValue, Env env) {
		if (entityDetectionListener != null)
			return entityDetectionListener.onMysqlFetchArray(functionInvocation, argumentValue, env);
		else
			return null;
	}
	
	public static void onTrueBranchExecutionStarted(Env env) {
		if (entityDetectionListener != null)
			entityDetectionListener.onTrueBranchExecutionStarted(env);
	}
	
	public static void onFalseBranchExecutionStarted(Env env) {
		if (entityDetectionListener != null)
			entityDetectionListener.onFalseBranchExecutionStarted(env);
	}
	
	public static void onBothBranchesExecutionFinished(HashSet<PhpVariable> dirtyVariablesInTrueBranch, HashSet<PhpVariable> dirtyVariablesInFalseBranch, Env env) {
		if (entityDetectionListener != null)
			entityDetectionListener.onBothBranchesExecutionFinished(dirtyVariablesInTrueBranch, dirtyVariablesInFalseBranch, env);
	}

}

package edu.iastate.symex.analysis;

import org.eclipse.php.internal.core.ast.nodes.ArrayAccess;
import org.eclipse.php.internal.core.ast.nodes.Assignment;
import org.eclipse.php.internal.core.ast.nodes.FunctionInvocation;
import org.eclipse.php.internal.core.ast.nodes.Variable;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;

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
		
		/**
		 * Used to identify declarations of PHP variables 
		 */
		public void onAssignmentExecute(Assignment assignment, Env env);
		
		/**
		 * Used to identify PHP variables 
		 */
		public void onVariableExecute(Variable variable, Env env);
		
		/**
		 * Used to identify $_REQUEST['input'] or $sql_row['name'] variables 
		 */
		public void onArrayAccessExecute(ArrayAccess arrayAccess, Env env);
		
		/**
		 * Used to identify SQL table columns, as in mysql_query("SELECT name FROM products");
		 */
		public DataNode onMysqlQuery(FunctionInvocation functionInvocation, DataNode argumentValue, Env env);
		
		/**
		 * Used to track the propagation of SQL table columns, as in 
		 * 		mysql_query("SELECT name FROM products");
		 * 		$product = mysql_fetch_array($result);
		 * 		echo $product['name']
		 */
		public DataNode onMysqlFetchArray(FunctionInvocation functionInvocation, DataNode argumentValue, Env env);
		
	}
	
	/*
	 * Methods
	 */
	
	public static void onAssignmentExecute(Assignment assignment, Env env) {
		if (entityDetectionListener != null)
			entityDetectionListener.onAssignmentExecute(assignment, env);
	}
	
	public static void onVariableExecute(Variable variable, Env env) {
		if (entityDetectionListener != null)
			entityDetectionListener.onVariableExecute(variable, env);
	}
	
	public static void onArrayAccessExecute(ArrayAccess arrayAccess, Env env) {
		if (entityDetectionListener != null)
			entityDetectionListener.onArrayAccessExecute(arrayAccess, env);
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

}

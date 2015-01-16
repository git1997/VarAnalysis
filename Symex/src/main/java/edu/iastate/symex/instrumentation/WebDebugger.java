package edu.iastate.symex.instrumentation;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.php.nodes.StatementNode;

/**
 * 
 * @author HUNG
 *
 */
public class WebDebugger {
	
	private static IListener listener = null;
	
	public static void setListener(IListener listener) {
		WebDebugger.listener = listener;
	}
	
	public static boolean isEnabled() {
		return listener != null;
	}
	
	/*
	 * Interface
	 */
	
	public interface IListener {
		
		public void onStatementExecuteStart(StatementNode statement, Env env);
		
		public void onStatementExecuteEnd(StatementNode statement, Env env);
		
	}
	
	/*
	 * Methods
	 */
	
	public static void onStatementExecuteStart(StatementNode statement, Env env) {
		listener.onStatementExecuteStart(statement, env);
	}
	
	public static void onStatementExecuteEnd(StatementNode statement, Env env) {
		listener.onStatementExecuteEnd(statement, env);
	}

}

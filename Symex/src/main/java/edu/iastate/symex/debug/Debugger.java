package edu.iastate.symex.debug;

import java.io.File;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpExecuter;
import edu.iastate.symex.instrumentation.WebDebugger;
import edu.iastate.symex.php.nodes.StatementNode;

/**
 * 
 * @author HUNG
 *
 */
public class Debugger implements WebDebugger.IListener {
	
	private Trace trace = new Trace();

	/**
	 * Executes PHP code and returns debug information
	 * @param file The file to be executed
	 */
	public DebugInfo debug(File file) {
		DebugInfo debugInfo = new DebugInfo();
		
		WebDebugger.setListener(this);
		new PhpExecuter().execute(file);
		WebDebugger.setListener(null);
		
		debugInfo.setTrace(trace);
		return debugInfo;
	}

	@Override
	public void onStatementExecuteStart(StatementNode statement, Env env) {
		trace.push(statement);
	}

	@Override
	public void onStatementExecuteEnd(StatementNode statement, Env env) {
		trace.pop();
	}

}

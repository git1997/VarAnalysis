package edu.iastate.symex.debug;

import java.io.File;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpExecuter;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.instrumentation.WebDebugger;
import edu.iastate.symex.php.nodes.StatementNode;
import edu.iastate.symex.position.PositionRange;

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
		DataModel dataModel = new PhpExecuter().execute(file);
		WebDebugger.setListener(null);
		
		debugInfo.setDataModel(dataModel);
		debugInfo.setTrace(trace);
		return debugInfo;
	}
	
	@Override
	public void onStatementExecuteStart(StatementNode statement, Env env) {
		trace.push(statement);
		
		/*
		 * Debugging code
		 */
		if (checkLocation(statement.getLocation(), new String[] {"file.php", "1"}))
			System.out.println();
	}

	@Override
	public void onStatementExecuteEnd(StatementNode statement, Env env) {
		trace.pop();
	}
	
	/**
	 * Returns true if the location is at some given line in a file
	 */
	private boolean checkLocation(PositionRange location, String[] fileAndLineInfo) {
		return location.getStartPosition().getFilePath().endsWith(fileAndLineInfo[0]) 
			&& location.getStartPosition().getLine() == Integer.valueOf(fileAndLineInfo[1]); 
	}

}

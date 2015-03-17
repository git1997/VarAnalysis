package edu.iastate.webslice.core;

import java.io.File;
import java.util.List;

import edu.iastate.analysis.references.detection.ReferenceDetector;
import edu.iastate.analysis.references.detection.ReferenceManager;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.instrumentation.WebDebugger;
import edu.iastate.symex.php.nodes.StatementNode;
import edu.iastate.symex.util.Timer;
import edu.iastate.webslice.core.ShowStatisticsOnReferences;

/**
 * 
 * @author HUNG
 *
 */
public class Evaluation {
	
	/**
	 * Main method.
	 */
	public static void main(String[] args) {
		String projectPath = SubjectSystems.projectPath;
		List<String> entries = SubjectSystems.projectEntries;
		
		Timer timer = new Timer();
		CountExecutedStatements countExecutedStatements = new CountExecutedStatements();
		WebDebugger.setListener(countExecutedStatements);
		
		ReferenceManager referenceManager = new ReferenceManager();
		for (String entry : entries) {
			ReferenceManager refManager = new ReferenceDetector().detect(new File(projectPath, entry));
			referenceManager.getDataFlowManager().addDataFlows(refManager.getDataFlowManager());
		}
		
		WebDebugger.setListener(null);
		int executedStatements = countExecutedStatements.getCount();
		float time = timer.getElapsedSeconds();
		
		System.out.println(new ShowStatisticsOnReferences().showStatistics(referenceManager));
		
		System.out.println("========== EXECUTION SUMMARY ==========");
		System.out.println("Entries: " + entries.size());
		System.out.println("Executed statements: " + executedStatements);
		System.out.println("Time: " + time);
		System.out.println(entries.size() + "\t" + executedStatements + "\t" + time);
	}
	
	/**
	 * This class is used to count executed statements.
	 */
	static class CountExecutedStatements implements WebDebugger.IListener {
		
		private int count = 0;

		@Override
		public void onStatementExecuteStart(StatementNode statement, Env env) {
			count++;
		}

		@Override
		public void onStatementExecuteEnd(StatementNode statement, Env env) {
		}
		
		public int getCount() {
			return count;
		}
		
	}
	
}

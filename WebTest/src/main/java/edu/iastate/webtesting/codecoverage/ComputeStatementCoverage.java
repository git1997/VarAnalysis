package edu.iastate.webtesting.codecoverage;

import java.util.HashSet;
import java.util.Set;

import edu.iastate.webtesting.evaluation.DebugInfo;

/**
 * 
 * @author HUNG
 *
 */
public class ComputeStatementCoverage {
	
	public Set<String> compute(String trace) {
		Set<String> statements = new HashSet<String>();
		for (String statement : trace.split("\r?\n")) {
			statements.add(statement);
		}
		
		// For debugging
		DebugInfo.statementCoverageComputed(statements);
		
		return statements;
	}
}

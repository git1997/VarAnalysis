package edu.iastate.webtesting.codecoverage;

import java.util.HashSet;
import java.util.Set;

import edu.iastate.webtesting.evaluation.DebugInfo;

/**
 * 
 * @author HUNG
 *
 */
public class ComputeBranchCoverage {
	
	public Set<String> compute(String branchesTrace) {
		Set<String> branches = new HashSet<String>();
		for (String branch : branchesTrace.split("\r?\n")) {
			branches.add(branch);
		}
		
		// For debugging
		DebugInfo.branchCoverageComputed(branches);
		
		return branches;
	}
}

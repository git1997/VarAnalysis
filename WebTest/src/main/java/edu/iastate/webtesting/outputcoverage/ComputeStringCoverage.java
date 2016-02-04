package edu.iastate.webtesting.outputcoverage;

import java.util.HashSet;
import java.util.Set;

import edu.iastate.webtesting.evaluation.DebugInfo;
import edu.iastate.webtesting.evaluation.Utils;
import edu.iastate.webtesting.util_clone.CodeLocation;
import edu.iastate.webtesting.values_clone.CondValue;
import edu.iastate.webtesting.values_clone.Literal;

/**
 * 
 * @author HUNG
 *
 */
public class ComputeStringCoverage {
	
	public Set<String> compute(CondValue cModel) {
		Set<String> outputCoverage = new HashSet<String>();
		for (CondValue condValue : Utils.flattenConcat(cModel)) {
			if (condValue instanceof Literal) {
				Literal literal = (Literal) condValue;
				if (literal.getLocation() != CodeLocation.UNDEFINED) // Ignore literals from unknown sources
					outputCoverage.add(Utils.codeLocationToString(literal.getLocation()) + "|" + literal.getStringValue());
			}
		}
		
		// For debugging
		DebugInfo.stringCoverageComputed(outputCoverage);
				
		return outputCoverage;
	}
}

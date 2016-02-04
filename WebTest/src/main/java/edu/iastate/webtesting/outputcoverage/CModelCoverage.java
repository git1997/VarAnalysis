package edu.iastate.webtesting.outputcoverage;

import java.util.List;
import java.util.Set;

import edu.iastate.webtesting.evaluation.Utils;
import edu.iastate.webtesting.util_clone.CodeLocation;
import edu.iastate.webtesting.values_clone.CondValue;
import edu.iastate.webtesting.values_clone.Literal;

/**
 * 
 * @author HUNG
 *
 */
public class CModelCoverage {
	private CondValue cModel;
	private List<CondValue> condValues;
	private Set<Literal> mappedLiterals;
	
	public CModelCoverage(CondValue cModel, List<CondValue> condValues, Set<Literal> mappedLiterals) {
		this.cModel = cModel;
		this.condValues = condValues;
		this.mappedLiterals = mappedLiterals;
	}

	/**
	 * Returns the ratio between the length of mapped literals and the length of the cModel.
	 * This indicates the quality of the mapping from CModel to DataModel. @see edu.iastate.webtesting.outputcoverage.ComputeOutputCoverage.compute(CondValue, DataModel)
	 */
	public float getMappedRatio() {
		int totalLength = 0;
		int mappedLength = 0;
		for (CondValue condValue : Utils.flattenConcat(cModel)) {
			if (condValue instanceof Literal) {
				Literal literal = (Literal) condValue;
				if (literal.getLocation() != CodeLocation.UNDEFINED) { // Ignore literals from unknown sources
					totalLength  += literal.getStringValue().length();
					if (mappedLiterals.contains(literal))
						mappedLength += literal.getStringValue().length();
				}
			}
		}
		return (float) mappedLength / totalLength;
		
		//return (float) Utils.countStringLengthOfLiterals(mappedLiterals) / cModel.getConcreteStringValue().length(); // Also count literals from unknown sources
	}
	
	/*
	 * Utility methods
	 */
	
	public String toDebugString() {
		StringBuilder str = new StringBuilder();
		str.append("Mapped Literals: " + mappedLiterals.size() + " / " + condValues.size() + " literals, "
						+ Utils.countStringLengthOfLiterals(mappedLiterals) + " / " + cModel.getConcreteStringValue().length() + " characters" + System.lineSeparator());
		for (CondValue condValue : condValues) {
			if (mappedLiterals.contains(condValue))
				str.append("[Mapped]");
			else
				str.append("[UnMapped]");
			str.append(" " + condValue.getClass().getSimpleName() + " " + condValue.getConcreteStringValue().replace("\r", "").replace("\n", " ") + System.lineSeparator());
		}
		return str.toString();
	}
}

package edu.iastate.webtesting.mapper;

import java.util.ArrayList;
import java.util.List;

import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.webtesting.values_clone.CondValue;
import edu.iastate.webtesting.values_clone.CondValueFactory;
import edu.iastate.webtesting.values_clone.Literal;

/**
 * 
 * @author HUNG
 *
 */
public class ConcatNodeMapper extends DataNodeMapper {

	MappingResult map(List<CondValue> condValues, ConcatNode concatNode, boolean strictMapping) {
		List<DataNode> nonConcatNodes = concatNode.getChildNodes();
		return map(condValues, nonConcatNodes, strictMapping);
	}
	
	// @param condValues.size() >= 1 
	// @param nonConcatNodes.size() >= 2
	private MappingResult map(List<CondValue> condValues, List<DataNode> nonConcatNodes, boolean strictMapping) {
		MappingResult result = new MappingResult();
		
		// Find pivots
		List<Fragment> pivots = findPivots(condValues, nonConcatNodes, strictMapping);
		
		// Only proceed when there are pivots
		if (pivots.isEmpty())
			return result;
		
		// Map the pivots
		for (Fragment pivot : pivots) {
			Literal literal = (Literal) condValues.get(pivot.start1);
			LiteralNode literalNode = (LiteralNode) nonConcatNodes.get(pivot.start2);
			result.addMapping(literal, literalNode);

			// TODO Work-around to map [pivot.start1, pivot.end1] to [pivot.start2, pivot.end2]
			// This could lead to result.getMappedLiterals() containing several literals that are not in the original condValues list (they are clones of some literals in the list)
			for (int i = pivot.start1 + 1; i <= pivot.end1; i++) {
				result.addMapping((Literal) condValues.get(i), literalNode);
			}
			for (int j = pivot.start2 + 1; j <= pivot.end2; j++) {
				Literal clonedLiteral = CondValueFactory.createLiteral(literal.getStringValue(), literal.getLocation());
				result.addMapping(clonedLiteral, (LiteralNode) nonConcatNodes.get(j));
			}
		}
		
		// Identify unmapped fragments
		List<Fragment> unmappedFragments = identifyUnmappedFragments(condValues, nonConcatNodes, pivots);
		
		// Map the fragments
		for (int i = 0; i < unmappedFragments.size(); i++) {
			Fragment fragment = unmappedFragments.get(i);
			List<CondValue> subCondValues = condValues.subList(fragment.start1, fragment.end1 + 1);
			
			MappingResult subResult;
			boolean strict = (i < unmappedFragments.size() - 1 ? true : strictMapping);
			// TODO Could try to map Literals to SymbolicNodes here
			
			if (fragment.start2 == fragment.end2)
				subResult = map(subCondValues, nonConcatNodes.get(fragment.start2), strict);
			else
				subResult = map(subCondValues, nonConcatNodes.subList(fragment.start2, fragment.end2 + 1), strict);
			result.addMappingResult(subResult);
		}
		
		return result;
	}
	
	private List<Fragment> findPivots(List<CondValue> condValues, List<DataNode> nonConcatNodes, boolean strictMapping) {
		List<Fragment> pivots = new ArrayList<Fragment>();
		int offset1 = 0;
		int offset2 = 0;
		while (true) {
			Fragment pivot = findNextPivot(condValues, offset1, nonConcatNodes, offset2, strictMapping);
			if (pivot == null)
				break;
			pivots.add(pivot);
			offset1 = pivot.end1 + 1;
			offset2 = pivot.end2 + 1;
		}
		return pivots;
	}
	
	private Fragment findNextPivot(List<CondValue> condValues, int offset1, List<DataNode> nonConcatNodes, int offset2, boolean strictMapping) {
		/*
		 * Attempt the simple case where a pivot's length = 1.
		 */
		boolean foundPivot = false;
		int start1 = -1;
		int start2 = -1;
		for (int i = offset1; i < condValues.size(); i++) {
			CondValue condValue = condValues.get(i);
			String condValueStr = getExactStringValueOrNull(condValue);
			
			for (int j = offset2; j < nonConcatNodes.size(); j++) {
				DataNode nonConcatNode = nonConcatNodes.get(j);
				String nonConcatNodeStr = nonConcatNode.getExactStringValueOrNull();
				
				if (condValueStr != null && nonConcatNodeStr != null & condValueStr.equals(nonConcatNodeStr)) {
					boolean unique = true;
					if (strictMapping)
						for (int i2 = offset1; i2 < condValues.size(); i2++) {
							CondValue condValue2 = condValues.get(i2);
							String condValueStr2 = getExactStringValueOrNull(condValue2);
							if (i != i2 && condValueStr2 != null && nonConcatNodeStr != null & condValueStr2.equals(nonConcatNodeStr)) {
								unique = false;
								break;
							}
						}
					if (unique)
						for (int j2 = offset2; j2 < nonConcatNodes.size(); j2++) {
							DataNode nonConcatNode2 = nonConcatNodes.get(j2);
							String nonConcatNodeStr2 = nonConcatNode2.getExactStringValueOrNull();
							if (j != j2 && condValueStr != null && nonConcatNodeStr2 != null & condValueStr.equals(nonConcatNodeStr2)) {
								unique = false;
								break;
							}
						}
					if (unique) {
						foundPivot = true;
						start2 = j;
						break;
					}
				}
			}
			if (foundPivot) {
				start1 = i;
				break;
			}
		}
		if (foundPivot) { // Found the pivot
			return new Fragment(start1, start1, start2, start2);
		}
		
		/*
		 * Attempt the more complex case where a pivot's length > 1.
		 * Step 1: Find positions start1 and start2 in the two sequences where the corresponding elements are left-aligned.
		 */
		boolean foundPivotCandidate = false;
		start1 = -1;
		start2 = -1;
		for (int i = offset1; i < condValues.size(); i++) {
			CondValue condValue = condValues.get(i);
			
			for (int j = offset2; j < nonConcatNodes.size(); j++) {
				DataNode nonConcatNode = nonConcatNodes.get(j);	
				
				if (leftAligned(condValue, nonConcatNode)) {
					boolean unique = true;
					if (strictMapping)
						for (int i2 = offset1; i2 < condValues.size(); i2++) {
							CondValue condValue2 = condValues.get(i2);
							if (i != i2 && leftAligned(condValue2, nonConcatNode)) {
								unique = false;
								break;
							}
						}
					if (unique)
						for (int j2 = offset2; j2 < nonConcatNodes.size(); j2++) {
							DataNode nonConcatNode2 = nonConcatNodes.get(j2);
							if (j != j2 && leftAligned(condValue, nonConcatNode2)) {
								unique = false;
								break;
							}
						}
					if (unique) {
						foundPivotCandidate = true;
						start2 = j;
						break;
					}
				}
			}
			if (foundPivotCandidate) {
				start1 = i;
				break;
			}
		}
		
		/*
		 * Step 2: Find positions end1 and end2 in the two sequences
		 *   such that [start1, end1]'s string value == [start2, end2]'s string value.
		 */
		if (foundPivotCandidate) {
			int end1 = start1;
			int end2 = start2;
			String str1 = getExactStringValueOrNull(condValues.get(start1)); // str1 is not null
			String str2 = nonConcatNodes.get(start2).getExactStringValueOrNull(); // str2 is not null
			while (str1.length() < str2.length() && end1 < condValues.size() - 1) {
				end1++;
				String nextStr1 = getExactStringValueOrNull(condValues.get(end1));
				if (nextStr1 != null)
					str1 += nextStr1;
				else
					break;
			}
			while (str1.length() > str2.length() && end2 < nonConcatNodes.size() - 1) {
				end2++;
				String nextStr2 = nonConcatNodes.get(end2).getExactStringValueOrNull();
				if (nextStr2 != null)
					str2 += nextStr2;
				else
					break;
			}
			if (str1.equals(str2)) { // Found the pivot
				return new Fragment(start1, end1, start2, end2);
			}
			else {
				reportMapError(nonConcatNodes.get(start2), "Found unique left-aligned nodes but could not find exact match");
				return findNextPivot(condValues, start1 + 1, nonConcatNodes, offset2, strictMapping);
			}
		}
		
		// If pivots are not found, return null.
		return null;
	}
	
	private List<Fragment> identifyUnmappedFragments(List<CondValue> condValues, List<DataNode> nonConcatNodes, List<Fragment> pivots) {
		List<Fragment> fragments = new ArrayList<Fragment>();
		
		int start1 = 0;
		int start2 = 0;
		for (Fragment pivot : pivots) {			
			int end1 = pivot.start1 - 1;
			int end2 = pivot.start2 - 1;
			
			if (start1 <= end1 & start2 <= end2)
				fragments.add(new Fragment(start1, end1, start2, end2));
			
			start1 = pivot.end1 + 1;
			start2 = pivot.end2 + 1;
		}
		int end1 = condValues.size() - 1;
		int end2 = nonConcatNodes.size() - 1;
		if (start1 <= end1 & start2 <= end2)
			fragments.add(new Fragment(start1, end1, start2, end2));
		
		return fragments;
	}
	
	private class Fragment {
		private int start1;
		private int end1; // inclusive
		private int start2;
		private int end2; // inclusive
		
		public Fragment(int start1, int end1, int start2, int end2) {
			this.start1 = start1;
			this.end1 = end1;
			this.start2 = start2;
			this.end2 = end2;
		}
	}
	
	/*
	 * Utility methods
	 */
	
	private boolean leftAligned(CondValue condValue, DataNode nonConcatNode) {
		String str1 = nonConcatNode.getExactStringValueOrNull();
		String str2 = getExactStringValueOrNull(condValue);
		return (str1 != null && str2 != null 
					&& (str1.isEmpty() && str2.isEmpty()
						|| !str2.isEmpty() && str1.startsWith(str2) 
						|| !str1.isEmpty() && str2.startsWith(str1)));
	}
	
	private String getExactStringValueOrNull(CondValue nonConcatValue) {
		if (nonConcatValue instanceof Literal)
			return ((Literal) nonConcatValue).getStringValue();
		else
			return null;
	}
}

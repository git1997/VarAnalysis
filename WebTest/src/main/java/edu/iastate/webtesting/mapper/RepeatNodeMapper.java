package edu.iastate.webtesting.mapper;

import java.util.List;

import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.webtesting.values_clone.CondValue;
import edu.iastate.webtesting.values_clone.Literal;

/**
 * 
 * @author HUNG
 *
 */
public class RepeatNodeMapper extends DataNodeMapper {

	MappingResult map(List<CondValue> condValues, RepeatNode repeatNode, boolean strictMapping) {
		MappingResult result = new MappingResult();
		
		int nextElementToMap = 0;
		while (nextElementToMap < condValues.size()) {
			// Map 1 iteration of the loop
			List<CondValue> subList = condValues.subList(nextElementToMap, condValues.size());
			MappingResult subResult = map(subList, repeatNode.getChildNode(), false);
			result.addMappingResult(subResult);
			
			// Break if no progress is made
			int lastElementToMap = nextElementToMap;
			nextElementToMap = getFirstOffsetOfLastUnmappedRegion(condValues, result);
			if (nextElementToMap == lastElementToMap)
				break;
		}
		
		return result;
	}
	
	/**
	 * Returns the first offset of the last region that is not mapped yet.
	 * (Returns list.size() if all elements have been mapped.)
	 */
	private int getFirstOffsetOfLastUnmappedRegion(List<CondValue> list, MappingResult mappingResult) {
		for (int i = list.size() - 1; i >= 0; i--)
			if (list.get(i) instanceof Literal && mappingResult.isMapped((Literal) list.get(i)))
				return i + 1;
		return 0;
	}
}

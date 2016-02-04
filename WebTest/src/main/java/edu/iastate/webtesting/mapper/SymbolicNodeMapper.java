package edu.iastate.webtesting.mapper;

import java.util.List;

import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.webtesting.values_clone.CondValue;

/**
 * 
 * @author HUNG
 *
 */
public class SymbolicNodeMapper extends DataNodeMapper {

	MappingResult map(List<CondValue> condValues, SymbolicNode symbolicNode, boolean strictMapping) {
		MappingResult result = new MappingResult();
		
		// TODO Add mappings for SymbolicNode as well
		
		return result;
	}
}

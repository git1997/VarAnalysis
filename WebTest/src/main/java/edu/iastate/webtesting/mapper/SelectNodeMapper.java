package edu.iastate.webtesting.mapper;

import java.util.List;

import edu.iastate.symex.datamodel.nodes.SelectNode;
import edu.iastate.webtesting.values_clone.CondValue;

/**
 * 
 * @author HUNG
 *
 */
public class SelectNodeMapper extends DataNodeMapper {

	MappingResult map(List<CondValue> condValues, SelectNode selectNode, boolean strictMapping) {
		// Try both branches
		MappingResult resultInTrueBranch = map(condValues, selectNode.getNodeInTrueBranch(), strictMapping);
		MappingResult resultInFalseBranch = map(condValues, selectNode.getNodeInFalseBranch(), strictMapping);
		
		// Return the better result
		if (resultInTrueBranch.getMappedLiterals().size() >= resultInFalseBranch.getMappedLiterals().size())
			return resultInTrueBranch;
		else
			return resultInFalseBranch;
	}
}

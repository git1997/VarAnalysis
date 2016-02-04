package edu.iastate.webtesting.mapper;

import java.util.List;

import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.webtesting.values_clone.CondValue;
import edu.iastate.webtesting.values_clone.Literal;

/**
 * 
 * @author HUNG
 *
 */
public class LiteralNodeMapper extends DataNodeMapper {

	MappingResult map(List<CondValue> condValues, LiteralNode literalNode, boolean strictMapping) {
		MappingResult result = new MappingResult();
		
		CondValue firstValue = condValues.get(0);
		if (firstValue instanceof Literal) {
			Literal literal = (Literal) firstValue;
			if (literal.getStringValue().equals(literalNode.getStringValue())) {
				result.addMapping(literal, literalNode);
			}
			else
				reportMapError(literalNode, "Expected " + literalNode.getStringValue().replace("\r", "").replace("\n", " ") + " but found " + literal.getStringValue().replace("\r", "").replace("\n", " "));
		}
		else 
			reportMapError(literalNode, "Expected Literal but found " + firstValue.getClass().getSimpleName());
		
		if (strictMapping && condValues.size() > 1)
			reportMapError(literalNode, "Mapping more than 1 condValue to 1 literalNode");
		
		return result;
	}
}

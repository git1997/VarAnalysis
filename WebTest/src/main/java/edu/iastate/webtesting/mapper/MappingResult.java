package edu.iastate.webtesting.mapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.webtesting.values_clone.Literal;

/**
 * 
 * @author HUNG
 *
 */
public class MappingResult {
	
	private HashMap<Literal, LiteralNode> map = new HashMap<Literal, LiteralNode>();
	
	public void addMapping(Literal literal, LiteralNode literalNode) {
		map.put(literal, literalNode);
	}
	
	public void addMappingResult(MappingResult mappingResult) {
		for (Entry<Literal, LiteralNode> entry : mappingResult.map.entrySet())
			addMapping(entry.getKey(), entry.getValue());
	}
	
	public Set<Literal> getMappedLiterals() {
		return new HashSet<Literal>(map.keySet());
	}
	
	public Set<LiteralNode> getMappedLiteralNodes() {
		return new HashSet<LiteralNode>(map.values());
	}
	
	public boolean isMapped(Literal literal) {
		return map.containsKey(literal);
	}
}

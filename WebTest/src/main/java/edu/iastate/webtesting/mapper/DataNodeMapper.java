package edu.iastate.webtesting.mapper;

import java.util.List;

import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.symex.datamodel.nodes.SelectNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.webtesting.values_clone.CondValue;

/**
 * 
 * @author HUNG
 *
 */
public abstract class DataNodeMapper {
	
	private static ConcatNodeMapper concatNodeMapper = new ConcatNodeMapper();
	private static LiteralNodeMapper literalNodeMapper = new LiteralNodeMapper();
	private static RepeatNodeMapper repeatNodeMapper = new RepeatNodeMapper();
	private static SelectNodeMapper selectNodeMapper = new SelectNodeMapper();
	private static SymbolicNodeMapper symbolicNodeMapper = new SymbolicNodeMapper();
	
	/**
	 * Maps a list of CondValues (size >= 1) to a DataNode
	 * @param strictMapping		true if the entire list should be mapped; false if only the left-most part of the list should be mapped
	 */
	public static MappingResult map(List<CondValue> condValues, DataNode dataNode, boolean strictMapping) {
		if (dataNode instanceof ConcatNode) {
			return concatNodeMapper.map(condValues, (ConcatNode) dataNode, strictMapping);
		}
		else if (dataNode instanceof LiteralNode) {
			return literalNodeMapper.map(condValues, (LiteralNode) dataNode, strictMapping);
		}
		else if (dataNode instanceof RepeatNode) {
			return repeatNodeMapper.map(condValues, (RepeatNode) dataNode, strictMapping);
		}
		else if (dataNode instanceof SelectNode) {
			return selectNodeMapper.map(condValues, (SelectNode) dataNode, strictMapping);
		}
		else if (dataNode instanceof SymbolicNode) {
			return symbolicNodeMapper.map(condValues, (SymbolicNode) dataNode, strictMapping);
		}
		else {
			reportMapError(dataNode, "Unexpected dataNode.");
			return new MappingResult();
		}
	}
	
	/*
	 * Utility methods
	 */
	
	/**
	 * Reports an error during mapping
	 */
	static void reportMapError(DataNode dataNode, String message) {
		MyLogger.log("Error while mapping " + dataNode.getClass().getSimpleName() + ": " + message);
	}
}

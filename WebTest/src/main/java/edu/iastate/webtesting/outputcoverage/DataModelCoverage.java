package edu.iastate.webtesting.outputcoverage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.symex.datamodel.nodes.SelectNode;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.webtesting.evaluation.Utils;

/**
 * 
 * @author HUNG
 *
 */
public class DataModelCoverage {
	private DataModel dataModel;
	private Set<LiteralNode> mappedLiteralNodes;
	private Map<DataNode, Float> coverageMap;
	
	public DataModelCoverage(DataModel dataModel, Set<LiteralNode> mappedLiteralNodes) {
		this.dataModel = dataModel;
		this.mappedLiteralNodes = mappedLiteralNodes;
		this.coverageMap = computeCoverageMap(dataModel, mappedLiteralNodes);
	}
	
	public DataModel getDataModel() {
		return dataModel;
	}
	
	public Set<LiteralNode> getMappedLiteralNodes() {
		return new HashSet<LiteralNode>(mappedLiteralNodes);
	}
	
	public Map<DataNode, Float> getCoverageMap() {
		return new HashMap<DataNode, Float>(coverageMap);
	}
	
	public Set<DataNode> getCoveredNodes() {
		return new HashSet<DataNode>(coverageMap.keySet());
	}
	
	public void addCoverage(DataModelCoverage dataModelCoverage) {
		if (dataModel != dataModelCoverage.dataModel) {
			MyLogger.log("In DataModelCoverage.java: DataModels must be the same. Quitting now...");
			System.exit(0);
		}
		
		mappedLiteralNodes.addAll(dataModelCoverage.mappedLiteralNodes);
		coverageMap = computeCoverageMap(dataModel, mappedLiteralNodes); // Recompute coverage map
	}
	
	private Map<DataNode, Float> computeCoverageMap(DataModel dataModel, Set<LiteralNode> mappedLiteralNodes) {
		Map<DataNode, Float> coverageMap = new HashMap<DataNode, Float>();
		computeCoverageMap_(dataModel.getRoot(), mappedLiteralNodes, coverageMap);
		return coverageMap;
	}
	
	private float computeCoverageMap_(DataNode dataNode, Set<LiteralNode> mappedLiteralNodes, Map<DataNode, Float> coverageMap) {
		float coveredLength = 0;
		float totalLength = 0;
		
		if (dataNode instanceof ConcatNode) {
			ConcatNode concatNode = ((ConcatNode) dataNode);
			List<DataNode> childNodes = concatNode.getChildNodes();
			for (DataNode childNode : childNodes) {
				float length = computeCoverageMap_(childNode, mappedLiteralNodes, coverageMap);
				if (coverageMap.containsKey(childNode))
					coveredLength += length * coverageMap.get(childNode);
				totalLength += length; 
			}
		}
		else if (dataNode instanceof LiteralNode) {
			LiteralNode literalNode = (LiteralNode) dataNode;
			int length = literalNode.getStringValue().length();
			if (mappedLiteralNodes.contains(literalNode))
				coveredLength += length;
			totalLength += length;
		}
		else if (dataNode instanceof RepeatNode) {
			RepeatNode repeatNode = (RepeatNode) dataNode;
			DataNode childNode = repeatNode.getChildNode(); 
			float length = computeCoverageMap_(childNode, mappedLiteralNodes, coverageMap);
			if (coverageMap.containsKey(childNode))
				coveredLength += length * coverageMap.get(childNode);
			totalLength += length;
		}
		else if (dataNode instanceof SelectNode) {
			SelectNode selectNode = (SelectNode) dataNode;
			DataNode trueChild = selectNode.getNodeInTrueBranch();
			DataNode falseChild = selectNode.getNodeInFalseBranch();
			float length1 = computeCoverageMap_(trueChild, mappedLiteralNodes, coverageMap);
			float length2 = computeCoverageMap_(falseChild, mappedLiteralNodes, coverageMap);
			if (coverageMap.containsKey(trueChild))
				coveredLength += length1 * coverageMap.get(trueChild);
			if (coverageMap.containsKey(falseChild))
				coveredLength += length2 * coverageMap.get(falseChild);
			totalLength += length1;
			totalLength += length2;
		}
		else {
			// Do nothing for other nodes
		}
		
		if (coveredLength > 0)
			coverageMap.put(dataNode, coveredLength / totalLength);
		return totalLength;
	}
	
	/*
	 * Utility methods
	 */
	
	public String toDebugString() {
		List<LiteralNode> sortedLiteralNodes = Utils.getSortedLiteralNodesInDataModel(dataModel);
		List<LiteralNode> sortedMappedLiteralNodes = new ArrayList<LiteralNode>(sortedLiteralNodes); 
		sortedMappedLiteralNodes.retainAll(mappedLiteralNodes);
		
		StringBuilder str = new StringBuilder();
		str.append("Mapped Literal Nodes: " + mappedLiteralNodes.size() + " / " + sortedLiteralNodes.size() + " literal nodes, "
						+ Utils.countStringLengthOfLiteralNodes(mappedLiteralNodes) + " / " + Utils.countStringLengthOfLiteralNodes(sortedLiteralNodes) + " characters" + System.lineSeparator());
		for (LiteralNode literalNode : sortedMappedLiteralNodes) {
			str.append(literalNode.getStringValue().replace("\r", "").replace("\n", " ") + System.lineSeparator());
		}
		return str.toString();
	}
}

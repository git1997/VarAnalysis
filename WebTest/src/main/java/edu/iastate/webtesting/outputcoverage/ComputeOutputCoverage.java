package edu.iastate.webtesting.outputcoverage;

import java.util.List;
import java.util.Set;

import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.SelectNode;
import edu.iastate.webtesting.evaluation.DebugInfo;
import edu.iastate.webtesting.evaluation.Utils;
import edu.iastate.webtesting.mapper.DataNodeMapper;
import edu.iastate.webtesting.mapper.MappingResult;
import edu.iastate.webtesting.values_clone.CondValue;
import edu.iastate.webtesting.values_clone.Literal;

/**
 * 
 * @author HUNG
 *
 */
public class ComputeOutputCoverage {

	public OutputCoverage compute(CondValue cModel, DataModel dataModel, String input) {
		// Get the DataModel of the corresponding phpFile
		DataNode rootNode = (input != null ? searchForRootDataNode(dataModel, input) : dataModel.getRoot());
		
		// Perform mapping
		List<CondValue> condValues = Utils.flattenConcat(cModel);
		MappingResult mappingResult = DataNodeMapper.map(condValues, rootNode, true);
		
		// Compute CModel coverage
		Set<Literal> mappedLiterals = mappingResult.getMappedLiterals();
		mappedLiterals.retainAll(condValues); // TODO Due to the algorithm in edu.iastate.webtesting.mapper.ConcatNodeMapper.map(List<CondValue>, List<DataNode>, boolean), mappedLiterals may contain a few elements that are not in the original condValues list
		CModelCoverage cModelCoverage = new CModelCoverage(cModel, condValues, mappedLiterals);
		
		// Compute DataModel coverage
		Set<LiteralNode> mappedLiteralNodes = mappingResult.getMappedLiteralNodes();
		DataModelCoverage dataModelCoverage = new DataModelCoverage(dataModel, mappedLiteralNodes);
		
		// Coverage result
		OutputCoverage outputCoverage = new OutputCoverage(cModelCoverage, dataModelCoverage);
		
		// For debugging
		DebugInfo.outputCoverageComputed(outputCoverage);
				
		return outputCoverage;
	}
	
	/**
	 * Searches for the sub-DataModel within the DataModel that represents the output of the PHP file indicated in parameter input.
	 * @see edu.iastate.webtesting.evaluation.RunSymexForProject
	 * @param dataModel
	 * @param input
	 * @return
	 */
	private DataNode searchForRootDataNode(DataModel dataModel, String input) {
		DataNode rootNode = dataModel.getRoot();
		String phpFile = input.substring(0, input.indexOf(".php") + ".php".length());
		return searchForRootDataNode(rootNode, phpFile);
	}
	
	private DataNode searchForRootDataNode(DataNode rootNode, String phpFile) {
		if (rootNode instanceof SelectNode) {
			SelectNode selectNode = (SelectNode) rootNode;
			if (selectNode.getConstraint().toDebugString().endsWith(".php")) {
				if (selectNode.getConstraint().toDebugString().equals(phpFile))
					return selectNode.getNodeInTrueBranch();
				else
					return searchForRootDataNode(selectNode.getNodeInFalseBranch(), phpFile);
			}
		}

		return rootNode;
	}
}
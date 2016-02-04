package edu.iastate.webtesting.outputcoverage;

import java.util.HashSet;
import java.util.Set;

import edu.iastate.symex.datamodel.DataModelVisitor;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SelectNode;
import edu.iastate.webtesting.evaluation.DebugInfo;

/**
 * 
 * @author HUNG
 *
 */
public class ComputeDecisionCoverage {
	
	public Set<String> compute(DataModelCoverage dataModelCoverage) {
		final Set<String> decisionCoverage = new HashSet<String>();
		final Set<DataNode> coveredNodes = dataModelCoverage.getCoveredNodes();
		dataModelCoverage.getDataModel().getRoot().accept(new DataModelVisitor() {
				
			public boolean visitSelectNode(SelectNode selectNode) {
				if (coveredNodes.contains(selectNode.getNodeInTrueBranch())) {
					decisionCoverage.add(selectNode.getConstraint().toDebugString() + ":" + selectNode.getConstraint().getLocation().getStartPosition().toDebugString() + ":" + selectNode.hashCode() + ":True");
				}
				if (coveredNodes.contains(selectNode.getNodeInFalseBranch())
						&& !(selectNode.getNodeInFalseBranch() instanceof SelectNode)) { // ADHOC Work-around to fix the way Decision Cov is counted at a SwitchStatement
					decisionCoverage.add(selectNode.getConstraint().toDebugString() + ":" + selectNode.getConstraint().getLocation().getStartPosition().toDebugString() + ":" + selectNode.hashCode() + ":False");
				}
				return true;
			}
		});
		
		// For debugging
		DebugInfo.decisionCoverageComputed(decisionCoverage);
				
		return decisionCoverage;
	}
}

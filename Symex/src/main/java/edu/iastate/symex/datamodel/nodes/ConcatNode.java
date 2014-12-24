package edu.iastate.symex.datamodel.nodes;

import java.util.ArrayList;
import edu.iastate.symex.datamodel.DataModelVisitor;

/**
 * 
 * @author HUNG
 *
 */
public class ConcatNode extends DataNode {
	
	private ArrayList<DataNode> childNodes = new ArrayList<DataNode>(); // childNodes must be compact (no child nodes of type ConcatNode)

	/**
	 * Protected constructor, called from DataNodeFactory only.
	 * @param childNodes childNodes must be compact (no child nodes of type ConcatNode) and contain at least 2 elements
	 */
	protected ConcatNode(ArrayList<DataNode> childNodes) {
		for (DataNode childNode : childNodes) {
			if (checkAndUpdateDepth(childNode))
				this.childNodes.add(childNode);
		}
	}
	
	public ArrayList<DataNode> getChildNodes() {
		return new ArrayList<DataNode>(childNodes);
	}
	
	@Override
	public String getExactStringValueOrNull() {
		StringBuilder string = new StringBuilder();
		for (DataNode childNode : childNodes) {
			String childValue = childNode.getExactStringValueOrNull();
			if (childValue == null)
				return null;
			string.append(childValue);
		}
		return string.toString();
	}
	
	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		boolean continueVisit = dataModelVisitor.visitConcatNode(this);
		if (!continueVisit)
			return;
		
		for (DataNode v: childNodes)
			v.accept(dataModelVisitor);
	}
	
}

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
	 * Package-level constructor, called from DataNodeFactory only.
	 * @param childNodes childNodes must be compact (no child nodes of type ConcatNode)
	 */
	ConcatNode(ArrayList<DataNode> childNodes) {
		for (DataNode childNode : childNodes) {
			if (checkAndUpdateDepth(childNode))
				this.childNodes.add(childNode);
		}
	}
	
	public ArrayList<DataNode> getChildNodes() {
		return new ArrayList<DataNode>(childNodes);
	}
	
	@Override
	public String getApproximateStringValue() {
		StringBuilder string = new StringBuilder();
		for (DataNode childNode : childNodes) {
			string.append(childNode.getApproximateStringValue());
		}
		return string.toString();
	}
	
	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitConcatNode(this);
		for (DataNode v: childNodes)
			v.accept(dataModelVisitor);
	}
	
}

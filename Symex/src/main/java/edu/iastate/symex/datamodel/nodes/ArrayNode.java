package edu.iastate.symex.datamodel.nodes;

import java.util.HashMap;
import edu.iastate.symex.datamodel.DataModelVisitor;

/**
 * 
 * @author HUNG
 *
 */
public class ArrayNode extends DataNode {
	
	private HashMap<String, DataNode> elementTable = new HashMap<String, DataNode>();
	
	public DataNode getElement(String key) {
		return elementTable.get(key);
	}
	
	public void setElement(String key, DataNode dataNode) {
		if (checkAndUpdateDepth(dataNode))
			elementTable.put(key, dataNode);
	}
	
	@Override
	public String getApproximateStringValue() {
		StringBuilder string = new StringBuilder();
		string.append("Array(");
		for (String key : elementTable.keySet()) {
			string.append(key);
			string.append("=>");
			string.append(elementTable.get(key).getApproximateStringValue());
		}
		string.append(")");
		return string.toString();
	}
	
	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitArrayNode(this);
		for (DataNode v: elementTable.values())
			v.accept(dataModelVisitor);
	}

}

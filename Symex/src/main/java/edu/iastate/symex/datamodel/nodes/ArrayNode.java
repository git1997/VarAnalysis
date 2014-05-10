package edu.iastate.symex.datamodel.nodes;

import java.util.HashMap;
import edu.iastate.symex.datamodel.DataModelVisitor;

/**
 * 
 * @author HUNG
 * Note that ArrayNode is mutable.
 *
 */
public class ArrayNode extends DataNode {
	
	private HashMap<String, DataNode> map = new HashMap<String, DataNode>();
	
	/**
	 * Protected constructor, called from DataNodeFactory only.
	 */
	protected ArrayNode() {
	}
	
	public DataNode getElement(String key) {
		return map.get(key);
	}
	
	public void setElement(String key, DataNode dataNode) {
		if (checkAndUpdateDepth(dataNode))
			map.put(key, dataNode);
	}
	
	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitArrayNode(this);
		for (DataNode node : map.values())
			node.accept(dataModelVisitor);
	}

}

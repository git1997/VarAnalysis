package edu.iastate.symex.datamodel.nodes;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.DataModelVisitor;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 * 
 * Note that ArrayNode is mutable and depth cannot be tracked.
 * @see edu.iastate.symex.datamodel.nodes.ObjectNode
 *
 */
public class ArrayNode extends DataNode {
	
	/*
	 * Used LinkedHashMap to preserve the order of inserted elements 
	 * (which is necessary for a list assignment, e.g., list($a, $b) = array(1, 2))
	 */
	private LinkedHashMap<String, PhpVariable> map = new LinkedHashMap<String, PhpVariable>();
	
	/**
	 * Protected constructor, called from DataNodeFactory only.
	 */
	protected ArrayNode() {
	}

	/**
	 * This method modifies the array. It should be called from Env only.
	 */
	public void putElement(String key, PhpVariable phpVariable) {
		map.put(key, phpVariable);
	}
	
	public PhpVariable getElement(String key) {
		return map.get(key);
	}
	
	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		boolean continueVisit = dataModelVisitor.visitArrayNode(this);
		if (!continueVisit)
			return;
		
		for (PhpVariable variable : map.values())
			variable.getValue().accept(dataModelVisitor);
	}
	
	public DataNode getElementValue(String key) {
		PhpVariable phpVariable = getElement(key);
		if (phpVariable != null)
			return phpVariable.getValue();
		else {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In ArrayNode: Reading an undefined element of key [" + key + "].");
			return SpecialNode.UnsetNode.UNSET;
		}
	}
	
	public ArrayList<String> getKeys() {
		return new ArrayList<String>(map.keySet());
	}
	
	public ArrayList<DataNode> getElementValues() {
		ArrayList<DataNode> elementValues = new ArrayList<DataNode>();
		for (String key : map.keySet())
			elementValues.add(getElementValue(key));
		return elementValues;
	}
	
	/**
	 * Generate the next key for the case when a value is appended to the array (e.g., $x[] = '1')
	 */
	public String generateNextKey() {
		int i = map.size();
		while (map.containsKey(String.valueOf(i)))
			i++;
		return String.valueOf(i);
	}

}

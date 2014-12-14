package edu.iastate.symex.core;

import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;

/**
 * 
 * @author HUNG
 * 
 * A PhpVariable represents one of the following: 
 * 		(1) A regular PHP variable (e.g., $x)
 * 		(2) An array element (e.g, $x['foo'])
 * 		(3) An object field (e.g., $x->foo)
 * @see {@link edu.iastate.symex.datamodel.nodes.ArrayNode}, {@link edu.iastate.symex.datamodel.nodes.ObjectNode} 
 *
 */
public class PhpVariable {
	
	private String name;	// The name of the phpVariable
	
	private DataNode value;	// The value of the phpVariable, represented by a dataNode.
	
	/**
	 * Protected constructor. Should be called by Env only.
	 * IMPORTANT: The variable's value must be set shortly after the creation of the variable.
	 * @param name
	 */
	protected PhpVariable(String name) {
		this.name = name;
		this.value = SpecialNode.UnsetNode.UNSET;
	}
	
	/**
	 * Returns the name of the variable
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the value of the variable. 
	 * Returns UNSET if its value has not been set.
	 */
	public DataNode getValue() {
		return value;
	}
	
	/**
	 * Protected method. Should be called by Env only.
	 * Sets the value of the variable.
	 * @param value
	 */
	protected void setValue(DataNode value) {
		this.value = value;
	}
	
}

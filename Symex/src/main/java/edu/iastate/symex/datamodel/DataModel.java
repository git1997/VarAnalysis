package edu.iastate.symex.datamodel;

import edu.iastate.symex.datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class DataModel {

	private DataNode root;
	
	/**
	 * Constructor
	 * @param root
	 */
	public DataModel(DataNode root) {
		this.root = root;
	}
	
	/**
	 * Returns the root DataNode of the data model.
	 */
	public DataNode getRoot() {
		return root;
	}
	
	/**
	 * Writes the DataModel to #ifdef format
	 */
	public String toIfdefString() {
		return WriteDataModelToIfDefs.convert(this);
	}
	
}
package edu.iastate.symex.datamodel.nodes;

import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.datamodel.DataModelVisitor;

/**
 * 
 * @author HUNG
 *
 */
public class LiteralNode extends DataNode {

	private String stringValue;		// The string value of this literal node
	
	private PositionRange location;	// The location of this literal node in the source code (can be undefined if the value is dynamically generated)
	
	/**
	 * Protected constructor, called from DataNodeFactory only.
	 */ 
	protected LiteralNode(String stringValue, PositionRange location) { 
		this.stringValue = stringValue; 
		this.location = location;
	}
	
	public String getStringValue() {
		return stringValue;
	}
	
	public PositionRange getLocation() {
		return location;
	}
	
	@Override
	public String getExactStringValueOrNull() {
		return stringValue;
	}
	
	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitLiteralNode(this);
	}

}
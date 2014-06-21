package edu.iastate.symex.datamodel.nodes;

import edu.iastate.symex.position.Region;
import edu.iastate.symex.datamodel.DataModelVisitor;

/**
 * 
 * @author HUNG
 *
 */
public class LiteralNode extends DataNode {

	private Region region;			// The position of this literal node in the source code (can be undefined if the value is dynamically generated)

	private String stringValue;		// The string value of this literal node
	
	/**
	 * Protected constructor, called from DataNodeFactory only.
	 */ 
	protected LiteralNode(Region region, String stringValue) { 
		this.region = region; 
		this.stringValue = stringValue;
	}
	
	public Region getRegion() {
		return region;
	}
	
	public String getStringValue() {
		return stringValue;
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
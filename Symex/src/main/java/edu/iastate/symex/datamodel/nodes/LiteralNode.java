package edu.iastate.symex.datamodel.nodes;

import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.datamodel.DataModelVisitor;

/**
 * 
 * @author HUNG
 *
 */
public class LiteralNode extends DataNode {

	private PositionRange positionRange;	// The position of this literal node in the source code (can be undefined if the value is dynamically generated)

	private String stringValue;				// The string value of this literal node
	
	/*
	 * Constructors
	 */
	
	public LiteralNode(PositionRange positionRange, String stringValue) { 
		this.positionRange = positionRange; 
		this.stringValue = stringValue;
	}
	
	public PositionRange getPositionRange() {
		return positionRange;
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
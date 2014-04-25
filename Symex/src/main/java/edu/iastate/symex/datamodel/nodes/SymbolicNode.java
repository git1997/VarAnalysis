package edu.iastate.symex.datamodel.nodes;

import edu.iastate.symex.php.nodes.PhpNode;
import edu.iastate.symex.datamodel.DataModelVisitor;

/**
 * 
 * @author HUNG
 *
 */
public class SymbolicNode extends DataNode {

	private PhpNode phpNode = null;			// The PhpNode which has unresolved value.
	
	private SymbolicNode parentNode = null;	// To support the tracing of unresolved values.
	
	/*
	 * Constructors
	 */
	
	public SymbolicNode() {		
	}
	
	public SymbolicNode(PhpNode phpNode) {
		this.phpNode = phpNode;
	}

	public SymbolicNode(PhpNode phpNode, SymbolicNode parentNode) {
		this.phpNode = phpNode;
		this.parentNode = parentNode;
	}

	public PhpNode getPhpNode() {
		return phpNode;
	}
	
	public SymbolicNode getParentNode() {
		return parentNode;
	}
	
	public String getSymbolicValue() {
		return "__SYMBOLIC_" + this.hashCode() + "__";
	}
	
	public static String getSymbolicValueRegularExpression() {
		return "__SYMBOLIC_\\d+__";
	}
	
	@Override
	public String getApproximateStringValue() {
		return this.getSymbolicValue();
	}
	
	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitSymbolicNode(this);
	}
	
}

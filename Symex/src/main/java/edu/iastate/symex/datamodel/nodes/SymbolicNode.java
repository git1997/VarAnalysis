package edu.iastate.symex.datamodel.nodes;

import edu.iastate.symex.php.nodes.PhpNode;
import edu.iastate.symex.datamodel.DataModelVisitor;

/**
 * 
 * @author HUNG
 *
 */
public class SymbolicNode extends DataNode {

	private PhpNode phpNode = null;			// The PhpNode which has unresolved value, can be null.
	
	private SymbolicNode parentNode = null;	// To support the tracing of unresolved values, can be null.
	
	/*
	 * Constructors
	 */
	
	/**
	 * Protected constructor, called from DataNodeFactory only.
	 * @param phpNode The PhpNode which has unresolved value, can be null.
	 * @param parentNode To support the tracing of unresolved values, can be null.
	 */
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
	
	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitSymbolicNode(this);
	}
	
}

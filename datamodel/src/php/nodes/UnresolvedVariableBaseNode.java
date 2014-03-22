package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.VariableBase;

import php.ElementManager;
import php.elements.PhpVariable;

import datamodel.nodes.DataNode;
import datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class UnresolvedVariableBaseNode extends VariableBaseNode {

	/**
	 * Constructor
	 */
	public UnresolvedVariableBaseNode(VariableBase variableBase) {
		super(variableBase);
	}

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		return new SymbolicNode(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.VariableBaseNode#createVariablePossiblyWithNull(servergraph.ElementManager)
	 */
	@Override
	public PhpVariable createVariablePossiblyWithNull(ElementManager elementManager) {
		return null;
	}

}

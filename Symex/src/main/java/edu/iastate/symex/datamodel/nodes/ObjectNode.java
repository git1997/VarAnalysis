package edu.iastate.symex.datamodel.nodes;

import edu.iastate.symex.datamodel.DataModelVisitor;
import edu.iastate.symex.php.nodes.ClassDeclarationNode;

/**
 * 
 * @author HUNG
 *
 */
public class ObjectNode extends DataNode {
	
	private ClassDeclarationNode classDeclarationNode;
	
	/**
	 * Protected constructor, called from DataNodeFactory only.
	 * @param classDeclarationNode
	 */
	protected ObjectNode(ClassDeclarationNode classDeclarationNode) {
		this.classDeclarationNode = classDeclarationNode;
	}

	public ClassDeclarationNode getClassDeclarationNode() {
		return classDeclarationNode;
	}
	
	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitObjectNode(this);
	}

}

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
	 * Constructor
	 * @param classDeclarationNode
	 */
	public ObjectNode(ClassDeclarationNode classDeclarationNode) {
		this.classDeclarationNode = classDeclarationNode;
	}

	public ClassDeclarationNode getClassDeclarationNode() {
		return classDeclarationNode;
	}
	
	public String getSymbolicValue() {
		return "__OBJECT_" + this.hashCode() + "__";
	}
	
	public static String getSymbolicValueRegularExpression() {
		return "__OBJECT_\\d+__";
	}
	
	@Override
	public String getApproximateStringValue() {
		return getSymbolicValue();
	}

	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitObjectNode(this);
	}

}

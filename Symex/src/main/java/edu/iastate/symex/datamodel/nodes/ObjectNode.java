package edu.iastate.symex.datamodel.nodes;

import java.util.HashMap;

import edu.iastate.symex.datamodel.DataModelVisitor;
import edu.iastate.symex.php.nodes.ClassDeclarationNode;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class ObjectNode extends DataNode {
	
	private ClassDeclarationNode classDeclarationNode;
	
	private HashMap<String, DataNode> fieldValues = new HashMap<String, DataNode>();
	
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
	
	public void putFieldValue(String name, DataNode value) {
		fieldValues.put(name, value);
	}
	
	public DataNode getFieldValue(String name) {
		if (fieldValues.containsKey(name))
			return fieldValues.get(name);
		else {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In ObjectNode.java: Object " + classDeclarationNode.getName() + " does not have field " + name);
			return DataNodeFactory.createSymbolicNode();
		}
	}
	
	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitObjectNode(this);
	}

}

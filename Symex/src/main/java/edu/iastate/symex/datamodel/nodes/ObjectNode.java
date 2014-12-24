package edu.iastate.symex.datamodel.nodes;

import java.util.HashMap;

import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.DataModelVisitor;
import edu.iastate.symex.php.nodes.ClassDeclarationNode;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 * 
 * Note that ObjectNode is mutable and depth cannot be tracked.
 * @see edu.iastate.symex.datamodel.nodes.ArrayNode
 *
 */
public class ObjectNode extends DataNode {
	
	private ClassDeclarationNode classDeclarationNode;
	
	private HashMap<String, PhpVariable> map = new HashMap<String, PhpVariable>();
	
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
	
	/**
	 * This method modifies the object. It should be called from Env only.
	 */
	public void putField(String fieldName, PhpVariable phpVariable) {
		map.put(fieldName, phpVariable);
	}
	
	public PhpVariable getField(String fieldName) {
		return map.get(fieldName);
	}
	
	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		boolean continueVisit = dataModelVisitor.visitObjectNode(this);
		if (!continueVisit)
			return;
		
		for (PhpVariable variable : map.values())
			variable.getValue().accept(dataModelVisitor);
	}
	
	public DataNode getFieldValue(String fieldName) {
		PhpVariable phpVariable = getField(fieldName);
		if (phpVariable != null)
			return phpVariable.getValue();
		else {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In ObjectNode: Reading an undefined field (" + fieldName + ").");
			return SpecialNode.UnsetNode.UNSET;
		}
	}

}

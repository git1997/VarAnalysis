package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ArrayAccess;

import php.ElementManager;
import php.elements.PhpArrayElement;
import php.elements.PhpVariable;

import datamodel.nodes.ArrayNode;
import datamodel.nodes.DataNode;
import datamodel.nodes.LiteralNode;
import datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class ArrayAccessNode extends VariableBaseNode {

	private VariableBaseNode arrayVariableBaseNode;
	private ExpressionNode indexExpressionNode = null;
	
	/*
	Holds a variable and an index that point to array or hashtable 

	e.g. $a[],
	 $a[1],
	 $a[$b],
	 $a{'name'} 
	*/
	public ArrayAccessNode(ArrayAccess arrayAccess) {
		super(arrayAccess);
		arrayVariableBaseNode = VariableBaseNode.createInstance(arrayAccess.getName());
		if (arrayAccess.getIndex() != null)
			indexExpressionNode = ExpressionNode.createInstance(arrayAccess.getIndex());
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		// The following code is used from BabelRef to identify $_REQUEST['var'] or $sql_row['name'] variables 
		// BEGIN OF BABELREF CODE
		if (requestVariableListener != null || sqlTableColumnListener != null)
			babelrefCheckArrayAccessNode(elementManager);
		
		if (VariableNode.variableRefListener != null && arrayVariableBaseNode instanceof VariableNode && !((VariableNode) arrayVariableBaseNode).resolveVariableName(elementManager).startsWith("_"))
			((VariableNode) arrayVariableBaseNode).variableRefFound(elementManager);
		// END OF BABELREF CODE
		
		if (arrayVariableBaseNode instanceof VariableNode && indexExpressionNode != null) {
			String arrayName = ((VariableNode) arrayVariableBaseNode).resolveVariableName(elementManager);
			String key = indexExpressionNode.resolveName(elementManager);
			PhpVariable phpVariable = elementManager.getVariableFromFunctionScope(arrayName);
			
			if (phpVariable != null && phpVariable.getDataNode() instanceof ArrayNode) {
				ArrayNode arrayNode = (ArrayNode) phpVariable.getDataNode();
				DataNode value = arrayNode.getElement(key);
				if (value != null)
					return value;
			}
		}
		return new SymbolicNode(this);
	}

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.VariableBaseNode#createVariablePossiblyWithNull(servergraph.ElementManager)
	 */
	@Override
	public PhpVariable createVariablePossiblyWithNull(ElementManager elementManager) {
		// The following code is used from BabelRef to identify $_REQUEST['var'] or $sql_row['name'] variables 
		// BEGIN OF BABELREF CODE
		if (requestVariableListener != null || sqlTableColumnListener != null)
			babelrefCheckArrayAccessNode(elementManager);
		
		if (VariableNode.variableDeclListener != null && arrayVariableBaseNode instanceof VariableNode && !((VariableNode) arrayVariableBaseNode).resolveVariableName(elementManager).startsWith("_"))
			((VariableNode) arrayVariableBaseNode).variableDeclFound(elementManager);
		// END OF BABELREF CODE
		
		if (arrayVariableBaseNode instanceof VariableNode && indexExpressionNode != null) {
			String arrayName = ((VariableNode) arrayVariableBaseNode).resolveVariableName(elementManager);
			String key = indexExpressionNode.execute(elementManager).getApproximateStringValue();			
			return new PhpArrayElement(arrayName, key);			
		}
		else
			return null;
	}
	
	/*
	 * The following code is used from BabelRef to identify $_REQUEST['var'] or $sql_row['name'] variables 
	 */
	// BEGIN OF BABELREF CODE
	public interface IRequestVariableListener {
		public void requestVariableFound(DataNode dataNode);
	}
	
	public interface ISqlTableColumnListener {
		public void sqlTableColumnFound(DataNode dataNode, String scope);
	}
	
	public static IRequestVariableListener requestVariableListener = null;
	
	public static ISqlTableColumnListener sqlTableColumnListener = null;
	
	/**
	 * Checks if the ArrayAccess is a $_REQUEST['var'] or $sql_row['name'] variable.
	 */
	private void babelrefCheckArrayAccessNode(ElementManager elementManager) {
		if (!(arrayVariableBaseNode instanceof VariableNode))
			return;

		String variableName = ((VariableNode) arrayVariableBaseNode).resolveVariableName(elementManager);
			
		if (requestVariableListener != null 
			&& (variableName.equals("_REQUEST") || variableName.equals("_POST") || variableName.equals("_GET") || variableName.equals("_FILES"))
			&& indexExpressionNode != null) {
				DataNode dataNode;
				if (indexExpressionNode instanceof IdentifierNode)
					dataNode = new LiteralNode((IdentifierNode) indexExpressionNode);
				else 
					dataNode = indexExpressionNode.execute(elementManager);
				requestVariableListener.requestVariableFound(dataNode);
		}
		
		if (sqlTableColumnListener != null) {
			PhpVariable phpVariable = elementManager.getVariableFromFunctionScope(variableName);
			if (phpVariable != null) {
				String variableValue = phpVariable.getDataNode().getApproximateStringValue();
				if (variableValue.startsWith("mysql_query_") // @see php.nodes.FunctionInvocationNode.php_mysql_query(ArrayList<DataNode>, ElementManager, Object)
															 // and php.nodes.FunctionInvocationNode.php_mysql_fetch_array(ArrayList<DataNode>, ElementManager)
					&& indexExpressionNode != null) {
					DataNode dataNode;
					if (indexExpressionNode instanceof IdentifierNode)
						dataNode = new LiteralNode((IdentifierNode) indexExpressionNode);
					else 
						dataNode = indexExpressionNode.execute(elementManager);
					sqlTableColumnListener.sqlTableColumnFound(dataNode, variableValue);
				}
			}
		}
	}
	// END OF BABELREF CODE

}

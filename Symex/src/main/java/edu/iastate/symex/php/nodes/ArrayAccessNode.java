package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ArrayAccess;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.ArrayNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.php.elements.PhpArrayElement;
import edu.iastate.symex.php.elements.PhpVariable;

/**
 * 
 * @author HUNG
 *
 */
public class ArrayAccessNode extends VariableBaseNode {

	private VariableBaseNode name;
	private ExpressionNode index = null;
	
	/*
	Holds a variable and an index that point to array or hashtable 

	e.g. $a[],
	 $a[1],
	 $a[$b],
	 $a{'name'} 
	*/
	public ArrayAccessNode(ArrayAccess arrayAccess) {
		super(arrayAccess);
		name = VariableBaseNode.createInstance(arrayAccess.getName());
		if (arrayAccess.getIndex() != null)
			index = ExpressionNode.createInstance(arrayAccess.getIndex());
	}
	
	@Override
	public DataNode execute(Env env) {
		// The following code is used from BabelRef to identify $_REQUEST['var'] or $sql_row['name'] variables 
		// BEGIN OF BABELREF CODE
//		if (requestVariableListener != null || sqlTableColumnListener != null)
//			babelrefCheckArrayAccessNode(env);
//		
//		if (VariableNode.variableRefListener != null && name instanceof VariableNode && !((VariableNode) name).getResolveVariableNameOrNull(env).startsWith("_"))
//			((VariableNode) name).variableRefFound(env);
		// END OF BABELREF CODE
		
		if (name instanceof VariableNode && index != null) {
			String arrayName = ((VariableNode) name).getResolvedVariableNameOrNull(env);
			String key = index.getResolvedNameOrNull(env);
			PhpVariable phpVariable = env.readVariable(arrayName);
			
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
	 * @see servergraph.nodes.VariableBaseNode#createVariablePossiblyWithNull(servergraph.env)
	 */
	@Override
	public PhpVariable createVariablePossiblyWithNull(Env env) {
		// The following code is used from BabelRef to identify $_REQUEST['var'] or $sql_row['name'] variables 
		// BEGIN OF BABELREF CODE
//		if (requestVariableListener != null || sqlTableColumnListener != null)
//			babelrefCheckArrayAccessNode(env);
//		
//		if (VariableNode.variableDeclListener != null && name instanceof VariableNode && !((VariableNode) name).getResolveVariableNameOrNull(env).startsWith("_"))
//			((VariableNode) name).variableDeclFound(env);
		// END OF BABELREF CODE
		
		if (name instanceof VariableNode && index != null) {
			String arrayName = ((VariableNode) name).getResolvedVariableNameOrNull(env);
			String key = index.execute(env).getExactStringValueOrNull();
			if (arrayName != null && key != null)
				return new PhpArrayElement(arrayName, key);
			else
				return null;
		}
		else
			return null;
	}
	
	/*
	 * The following code is used from BabelRef to identify $_REQUEST['var'] or $sql_row['name'] variables 
	 */
	// BEGIN OF BABELREF CODE
//	public interface IRequestVariableListener {
//		public void requestVariableFound(DataNode dataNode);
//	}
//	
//	public interface ISqlTableColumnListener {
//		public void sqlTableColumnFound(DataNode dataNode, String scope);
//	}
//	
//	public static IRequestVariableListener requestVariableListener = null;
//	
//	public static ISqlTableColumnListener sqlTableColumnListener = null;
//	
//	/**
//	 * Checks if the ArrayAccess is a $_REQUEST['var'] or $sql_row['name'] variable.
//	 */
//	private void babelrefCheckArrayAccessNode(Env env) {
//		if (!(name instanceof VariableNode))
//			return;
//
//		String variableName = ((VariableNode) name).getResolveVariableNameOrNull(env);
//			
//		if (requestVariableListener != null 
//			&& (variableName.equals("_REQUEST") || variableName.equals("_POST") || variableName.equals("_GET") || variableName.equals("_FILES"))
//			&& index != null) {
//				DataNode dataNode;
//				if (index instanceof IdentifierNode)
//					dataNode = LiteralNodeFactory.createLiteralNode((IdentifierNode) index);
//				else 
//					dataNode = index.execute(env);
//				requestVariableListener.requestVariableFound(dataNode);
//		}
//		
//		if (sqlTableColumnListener != null) {
//			PhpVariable phpVariable = env.getVariableFromFunctionScope(variableName);
//			if (phpVariable != null) {
//				String variableValue = phpVariable.getDataNode().getApproximateStringValue();
//				if (variableValue.startsWith("mysql_query_") // @see edu.iastate.symex.php.nodes.FunctionInvocationNode.php_mysql_query(ArrayList<DataNode>, env, Object)
//															 // and edu.iastate.symex.php.nodes.FunctionInvocationNode.php_mysql_fetch_array(ArrayList<DataNode>, env)
//					&& index != null) {
//					DataNode dataNode;
//					if (index instanceof IdentifierNode)
//						dataNode = LiteralNodeFactory.createLiteralNode((IdentifierNode) index);
//					else 
//						dataNode = index.execute(env);
//					sqlTableColumnListener.sqlTableColumnFound(dataNode, variableValue);
//				}
//			}
//		}
//	}
	// END OF BABELREF CODE

}

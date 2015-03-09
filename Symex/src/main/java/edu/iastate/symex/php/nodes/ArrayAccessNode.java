package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ArrayAccess;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.ArrayNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.instrumentation.WebAnalysis;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 * 
 * @see edu.iastate.symex.php.nodes.FieldAccessNode
 *
 */
public class ArrayAccessNode extends VariableNode {

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
	public String getResolvedVariableNameOrNull(Env env) {
		MyLogger.log(MyLevel.USER_EXCEPTION, "In ArrayAccessNode.java: Method getResolvedVariableNameOrNull not yet implemented.");
		return super.getResolvedVariableNameOrNull(env);
	}
	
	@Override
	public DataNode execute(Env env) {
		DataNode dataNode = name.execute(env);
		DataNode indexNode = (index != null ? index.execute(env) : null);
		
		/*
		 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB ANALYSIS CODE
		if (WebAnalysis.isEnabled() && indexNode != null)
			WebAnalysis.onArrayAccessExecute((ArrayAccess) this.getAstNode(), this, dataNode, indexNode, env);
		// END OF WEB ANALYSIS CODE
		
		if (dataNode instanceof ArrayNode) {
			ArrayNode array = (ArrayNode) dataNode;
			String key = (indexNode != null ? indexNode.getExactStringValueOrNull() : null);
			
			if (key == null) {
				MyLogger.log(MyLevel.TODO, "In ArrayAccessNode.java: Not yet handled the case where key of array is null.");
				return DataNodeFactory.createSymbolicNode(this);
			}
			
			DataNode elementValue = array.getElementValue(key);
			return DataNodeFactory.createSymbolicNodeFromUnsetNodeIfPossible(elementValue, this);
		}
		else
			return DataNodeFactory.createSymbolicNode(this);
	}

	@Override
	public PhpVariable createVariablePossiblyWithNull(Env env) {
		DataNode dataNode = name.execute(env);
		
		// An array access can create a new array (e.g., $x[1] = 'a' creates a new array for $x)
		if (!(dataNode instanceof ArrayNode)) {
			PhpVariable phpArray = name.createVariablePossiblyWithNull(env);
			if (phpArray != null) {
				dataNode = DataNodeFactory.createArrayNode();
				env.writeVariable(phpArray, dataNode);
			}
		}
		
		if (dataNode instanceof ArrayNode) {
			ArrayNode array = (ArrayNode) dataNode;
			String key = (index != null ? index.execute(env).getExactStringValueOrNull() : null);
			
			// Handle the case when a value is appended to the array (e.g., $x[] = '1')
			if (key == null)
				key = array.generateNextKey();

			return env.getOrPutArrayElement(array, key);
		}
		else
			return null;
	}

}

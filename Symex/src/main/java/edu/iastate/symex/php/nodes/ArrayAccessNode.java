package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ArrayAccess;

import edu.iastate.symex.analysis.WebAnalysis;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpArrayElement;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.ArrayNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;

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
		/*
		 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB ANALYSIS CODE
		WebAnalysis.onArrayAccessExecute((ArrayAccess) this.getAstNode(), env);
		// END OF WEB ANALYSIS CODE
		
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
		return DataNodeFactory.createSymbolicNode(this);
	}

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.VariableBaseNode#createVariablePossiblyWithNull(servergraph.env)
	 */
	@Override
	public PhpVariable createVariablePossiblyWithNull(Env env) {
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

}

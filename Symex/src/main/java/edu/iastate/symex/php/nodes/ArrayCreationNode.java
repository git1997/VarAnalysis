package edu.iastate.symex.php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.ArrayCreation;
import org.eclipse.php.internal.core.ast.nodes.ArrayElement;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.ArrayNode;
import edu.iastate.symex.datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class ArrayCreationNode extends ExpressionNode {
	
	private ArrayList<ExpressionNode> keys = new ArrayList<ExpressionNode>();
	private ArrayList<ExpressionNode> values = new ArrayList<ExpressionNode>();
	
	/*
	Represents array creation 

	e.g. array(1,2,3,),
	 array('Dodo'=>'Golo','Dafna'=>'Dodidu')
	 array($a, $b=>foo(), 1=>$myClass->getFirst())
	*/ 
	public ArrayCreationNode(ArrayCreation arrayCreation) {
		super(arrayCreation);
		for (ArrayElement arrayElement : arrayCreation.elements()) {
			if (arrayElement.getKey() != null)
				keys.add(ExpressionNode.createInstance(arrayElement.getKey()));
			else
				keys.add(null);
			values.add(ExpressionNode.createInstance(arrayElement.getValue()));
		}
	}
	
	@Override
	public DataNode execute(Env env) {
		ArrayNode arrayNode = new ArrayNode();
		for (int i = 0; i < keys.size(); i++) {
			ExpressionNode key = keys.get(i);
			ExpressionNode value = values.get(i);
			
			String resolvedKey = (key != null ? key.execute(env).getExactStringValueOrNull() : Integer.toString(i));
			// TODO Handle resolvedKey == null
			DataNode resolvedValue = value.execute(env);
			
			arrayNode.setElement(resolvedKey, resolvedValue);
		}
		return arrayNode;
	}

}
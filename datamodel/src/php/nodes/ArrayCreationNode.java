package php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.ArrayCreation;
import org.eclipse.php.internal.core.ast.nodes.ArrayElement;

import php.ElementManager;

import datamodel.nodes.ArrayNode;
import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class ArrayCreationNode extends ExpressionNode {
	
	private ArrayList<ExpressionNode> keyExpressionNodes = new ArrayList<ExpressionNode>();
	private ArrayList<ExpressionNode> valueExpressionNodes = new ArrayList<ExpressionNode>();
	
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
				keyExpressionNodes.add(ExpressionNode.createInstance(arrayElement.getKey()));
			else
				keyExpressionNodes.add(null);
			valueExpressionNodes.add(ExpressionNode.createInstance(arrayElement.getValue()));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		ArrayNode arrayNode = new ArrayNode();
		for (int i = 0; i < keyExpressionNodes.size(); i++) {
			ExpressionNode keyExpressionNode = keyExpressionNodes.get(i);
			ExpressionNode valueExpressionNode = valueExpressionNodes.get(i);
			String key = (keyExpressionNode != null ? keyExpressionNode.execute(elementManager).getApproximateStringValue() : Integer.toString(i));
			DataNode dataNode = valueExpressionNode.execute(elementManager);
			arrayNode.setElement(key, dataNode);
		}
		return arrayNode;
	}

}
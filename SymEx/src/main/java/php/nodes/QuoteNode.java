package php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.Quote;

import php.ElementManager;


import datamodel.nodes.DataNode;
import datamodel.nodes.ConcatNode;

/**
 * 
 * @author HUNG
 *
 */
public class QuoteNode extends ExpressionNode {

	private ArrayList<ExpressionNode> expressionNodes = new ArrayList<ExpressionNode>();
	
	/*
	Represents complex qoute(i.e. qoute that includes string and variables). Also represents heredoc 

	e.g. 
	 "this is $a quote",
	 "'single ${$complex->quote()}'"
	 <<
	*/
	public QuoteNode(Quote quote) {
		super(quote);
		for (Expression expression : quote.expressions()) {
			ExpressionNode expressionNode = ExpressionNode.createInstance(expression);
			expressionNodes.add(expressionNode);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		ConcatNode concatNode = new ConcatNode();
		for (ExpressionNode expressionNode : expressionNodes)
			concatNode.appendChildNode(expressionNode.execute(elementManager));
		return concatNode;
	}

}

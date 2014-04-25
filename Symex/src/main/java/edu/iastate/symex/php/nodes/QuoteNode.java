package edu.iastate.symex.php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.Quote;


import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;

/**
 * 
 * @author HUNG
 *
 */
public class QuoteNode extends ExpressionNode {

	private ArrayList<ExpressionNode> expressions = new ArrayList<ExpressionNode>();
	
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
			expressions.add(expressionNode);
		}
	}
	
	@Override
	public DataNode execute(Env env) {
		ArrayList<DataNode> childNodes = new ArrayList<DataNode>();
		for (ExpressionNode expressionNode : expressions)
			childNodes.add(expressionNode.execute(env));
		
		return DataNodeFactory.createCompactConcatNode(childNodes);
	}

}

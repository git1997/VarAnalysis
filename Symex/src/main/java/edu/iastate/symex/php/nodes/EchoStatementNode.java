package edu.iastate.symex.php.nodes;

import java.util.ArrayList;
import org.eclipse.php.internal.core.ast.nodes.EchoStatement;
import org.eclipse.php.internal.core.ast.nodes.Expression;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;

/**
 * 
 * @author HUNG
 *
 */
public class EchoStatementNode extends StatementNode {

	private ArrayList<ExpressionNode> expressions = new ArrayList<ExpressionNode>();
	
	/*
	Represent a echo statement. 

	e.g. echo "hello",
	 echo "hello", "world"
	*/
	public EchoStatementNode(EchoStatement echoStatement) {
		super(echoStatement);
		for (Expression expression : echoStatement.expressions()) {
			ExpressionNode expressionNode = ExpressionNode.createInstance(expression);
			expressions.add(expressionNode);
		}
	}
	
	@Override
	public DataNode execute_(Env env) {
		ArrayList<DataNode> resolvedValues = new ArrayList<DataNode>();
		for (ExpressionNode expression : expressions) {
			DataNode resolvedValue = expression.execute(env);
			resolvedValues.add(resolvedValue);
		}
		env.appendOutput(resolvedValues);
		return SpecialNode.ControlNode.OK;
	}
	
}

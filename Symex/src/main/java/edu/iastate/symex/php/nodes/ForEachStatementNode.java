package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ForEachStatement;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.ArrayNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.SpecialNode;

/**
 * 
 * @author HUNG
 *
 */
public class ForEachStatementNode extends StatementNode {

	private ExpressionNode expressionNode;
	private ExpressionNode keyNode;
	private ExpressionNode valueNode;
	private StatementNode statementNode;	
	
	/*
	Represents a for each statement 

	e.g. foreach (array_expression as $value)
	   statement;
	     
	 foreach (array_expression as $key => $value) 
	   statement;
	 
	 foreach (array_expression as $key => $value): 
	   statement;
	   ...
	 endforeach;
	*/
	public ForEachStatementNode(ForEachStatement forEachStatement) {
		super(forEachStatement);
		expressionNode = ExpressionNode.createInstance(forEachStatement.getExpression());
		keyNode = (forEachStatement.getKey() != null ? ExpressionNode.createInstance(forEachStatement.getKey()) : null);
		valueNode = ExpressionNode.createInstance(forEachStatement.getValue());
		statementNode = StatementNode.createInstance(forEachStatement.getStatement());		
	}
	
	@Override
	public DataNode execute_(Env env) {
		DataNode expressionResult = expressionNode.execute(env);
		
		ArrayNode array = (expressionResult instanceof ArrayNode ? (ArrayNode) expressionResult : null);
		String keyVarName = (keyNode instanceof VariableNode ? ((VariableNode) keyNode).getVariableNameBeforeRunTimeOrNull() : null);
		String valueVarName = (valueNode instanceof VariableNode ? ((VariableNode) valueNode).getVariableNameBeforeRunTimeOrNull() : null);
		
		if (array != null && valueVarName != null) {
			DataNode control = SpecialNode.ControlNode.OK;
			for (String key : array.getKeys()) {
				DataNode value = array.getElementValue(key);
				if (keyVarName != null)
					env.getOrPutThenWriteVariable(keyVarName, DataNodeFactory.createLiteralNode(key));
				env.getOrPutThenWriteVariable(valueVarName, value);
				control = statementNode.execute(env);
			}
			return control; // TODO Revise this returned CONTROL value
		}
		else {
			Constraint constraint = ConstraintFactory.createAtomicConstraint(expressionNode.getSourceCode(), expressionNode.getLocation());
			
			return WhileStatementNode.execute(env, constraint, statementNode);
		}
	}

}

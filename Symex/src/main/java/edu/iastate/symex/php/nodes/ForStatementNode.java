package edu.iastate.symex.php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.ForStatement;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class ForStatementNode extends StatementNode {

	private ArrayList<ExpressionNode> initializers = new ArrayList<ExpressionNode>();
	private ArrayList<ExpressionNode> conditions = new ArrayList<ExpressionNode>();	
	private StatementNode statement;	
	
	/*
	Represents a for statement 

	e.g. for (expr1; expr2; expr3)
	 	 statement;
	 
	 for (expr1; expr2; expr3):
	 	 statement
	 	 ...
	 endfor;
	*/
	public ForStatementNode(ForStatement forStatement) {
		super(forStatement);
		for (Expression expression : forStatement.initializers()) {
			initializers.add(ExpressionNode.createInstance(expression));
		}
		for (Expression expression : forStatement.conditions()) {
			conditions.add(ExpressionNode.createInstance(expression));
		}
		statement = StatementNode.createInstance(forStatement.getBody());		
	}
	
	@Override
	public DataNode execute_(Env env) {
		/*
		 * The following code is used from BabelRef to identify PHP variable entities.
		 */
		// BEGIN OF BABELREF CODE
//		if (VariableNode.variableDeclListener != null) {
//			for (ExpressionNode expressionNode : initializers) {
//				if (expressionNode instanceof AssignmentNode) {
//					AssignmentNode assignmentNode = (AssignmentNode) expressionNode;
//					if (assignmentNode.getLeftHandSide() instanceof VariableNode)
//						((VariableNode) assignmentNode.getLeftHandSide()).variableDeclFound(env);
//				}
//			}
//		}
		// END OF BABELREF CODE
		
		// The initializers should not be executed; Otherwise, the loopNode will contain information about the
		// first iteration and cannot be generalized for other iterations.
		//for (ExpressionNode expressionNode : initializerNodes)
			//expressionNode.execute(env);
		for (ExpressionNode condition : conditions)
			condition.execute(env);
		
		PhpNode conditionNode = (conditions.isEmpty() ? this : conditions.get(0));
		Constraint constraint = ConstraintFactory.createAtomicConstraint(conditionNode.getSourceCode(), conditionNode.getLocation());

		return WhileStatementNode.execute(env, constraint, statement);
	}

}

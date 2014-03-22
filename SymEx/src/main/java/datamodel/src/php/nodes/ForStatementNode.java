package php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.ForStatement;

import php.ElementManager;

import datamodel.nodes.DataNode;
import datamodel.nodes.LiteralNode;

/**
 * 
 * @author HUNG
 *
 */
public class ForStatementNode extends StatementNode {

	private LiteralNode conditionString;
	private ArrayList<ExpressionNode> initializerNodes = new ArrayList<ExpressionNode>();
	private ArrayList<ExpressionNode> conditionNodes = new ArrayList<ExpressionNode>();	
	private StatementNode statementNode;	
	
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
		conditionString = (forStatement.conditions().isEmpty() ? new LiteralNode(forStatement) : new LiteralNode(forStatement.conditions().get(0)));
		for (Expression expression : forStatement.initializers()) {
			initializerNodes.add(ExpressionNode.createInstance(expression));
		}
		for (Expression expression : forStatement.conditions()) {
			conditionNodes.add(ExpressionNode.createInstance(expression));
		}	
		statementNode = StatementNode.createInstance(forStatement.getBody());		
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		/*
		 * The following code is used from BabelRef to identify PHP variable entities.
		 */
		// BEGIN OF BABELREF CODE
		if (VariableNode.variableDeclListener != null) {
			for (ExpressionNode expressionNode : initializerNodes) {
				if (expressionNode instanceof AssignmentNode) {
					AssignmentNode assignmentNode = (AssignmentNode) expressionNode;
					if (assignmentNode.getVariableBaseNode() instanceof VariableNode)
						((VariableNode) assignmentNode.getVariableBaseNode()).variableDeclFound(elementManager);
				}
			}
		}
		// END OF BABELREF CODE
		
		// The initializers should not be executed; Otherwise, the loopNode will contain information about the
		// first iteration and cannot be generalized for other iterations.
		//for (ExpressionNode expressionNode : initializerNodes)
			//expressionNode.execute(elementManager);
		for (ExpressionNode expressionNode : conditionNodes)
			expressionNode.execute(elementManager);
		ForStatementNode.execute(elementManager, conditionString, statementNode);
		return null;
	}
	
	/**
	 * Executes the loop and updates the elementManager accordingly.
	 */
	public static void execute(ElementManager elementManager, LiteralNode conditionString, StatementNode statementNode) {
		ElementManager loopElementManager = new ElementManager(elementManager, conditionString, true);
		statementNode.execute(loopElementManager);
		elementManager.updateWithLoop(conditionString, loopElementManager);
	}

}

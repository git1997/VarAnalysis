package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.IfStatement;

import php.ElementManager;

import datamodel.nodes.ConcatNode;
import datamodel.nodes.DataNode;
import datamodel.nodes.LiteralNode;
import datamodel.nodes.SelectNode;

/**
 * 
 * @author HUNG
 *
 */
public class IfStatementNode extends StatementNode {

	private LiteralNode conditionString;
	private ExpressionNode expressionNode;
	private StatementNode trueStatementNode;
	private StatementNode falseStatementNode;
	
	/*
	Represents if statement 

	e.g. 
	 if ($a > $b) {
	   echo "a is bigger than b";
	 } elseif ($a == $b) {
	   echo "a is equal to b";
	 } else {
	   echo "a is smaller than b";
	 },
	 
	 if ($a):
	   echo "a is bigger than b";
	   echo "a is NOT bigger than b";
	 endif;
	 */
	public IfStatementNode(IfStatement ifStatement) {
		this.conditionString = new LiteralNode(ifStatement.getCondition());
		this.expressionNode = ExpressionNode.createInstance(ifStatement.getCondition());
		this.trueStatementNode = StatementNode.createInstance(ifStatement.getTrueStatement());
		this.falseStatementNode = (ifStatement.getFalseStatement() != null ? StatementNode.createInstance(ifStatement.getFalseStatement()) : null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		IfStatementNode.execute(elementManager, expressionNode, conditionString, trueStatementNode, falseStatementNode);
		return null;
	}
	
	/**
	 * Executes different branches and updates the elementManager accordingly.
	 * @see {@link php.nodes.ConditionalExpressionNode#execute(ElementManager)}
	 */
	public static DataNode execute(ElementManager elementManager, ExpressionNode conditionNode, LiteralNode conditionString, PhpNode trueBranchNode, PhpNode falseBranchNode) {
		if (conditionNode != null) {
			DataNode dataNode = conditionNode.execute(elementManager);
			if ( // @see php.nodes.InfixExpressionNode.execute(ElementManager)
				dataNode instanceof ConcatNode && !dataNode.getApproximateStringValue().isEmpty()
				|| dataNode instanceof LiteralNode && 
					!((LiteralNode) dataNode).getStringValue().equals("FALSE") && !((LiteralNode) dataNode).getStringValue().isEmpty()) {
				if (trueBranchNode != null)
					return trueBranchNode.execute(elementManager);
				else 
					return new LiteralNode("");
			}
			else if (dataNode instanceof LiteralNode &&
					(dataNode.getApproximateStringValue().equals("FALSE") || dataNode.getApproximateStringValue().isEmpty())) {
				if (falseBranchNode != null)
					return falseBranchNode.execute(elementManager);
				else
					return new LiteralNode("");
			}
		}
			
		ElementManager trueBranchElementManager = null;
		ElementManager falseBranchElementManager = null;
		DataNode trueBranchValue = null;
		DataNode falseBranchValue = null;		
		
		// Execute the branches
		if (trueBranchNode != null) {
			trueBranchElementManager = new ElementManager(elementManager, conditionString, true);
			trueBranchValue = trueBranchNode.execute(trueBranchElementManager);
		}
		if (falseBranchNode != null) {
			falseBranchElementManager = new ElementManager(elementManager, conditionString, false);
			falseBranchValue = falseBranchNode.execute(falseBranchElementManager);
		}
		
		// Update the elementManager
		elementManager.updateWithBranches(conditionString, trueBranchElementManager, falseBranchElementManager);
		
		// Return a value (in case it is a ConditionalExpression)
		return new SelectNode(conditionString, trueBranchValue, falseBranchValue).compact();		
	}

}

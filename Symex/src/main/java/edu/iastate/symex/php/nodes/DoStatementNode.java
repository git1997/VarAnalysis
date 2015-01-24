package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.DoStatement;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class DoStatementNode extends StatementNode {

	private ExpressionNode condition;	
	private StatementNode body;
	
	/*
	Represent do while statement.
	
	e.g.
	 do {
	   echo $i;
	 } while ($i > 0);
	*/
	public DoStatementNode(DoStatement doStatement) {
		super(doStatement);
		condition = ExpressionNode.createInstance(doStatement.getCondition());
		body = StatementNode.createInstance(doStatement.getBody());
	}

	@Override
	public DataNode execute_(Env env) {
		body.execute(env); // TODO Consider the returned CONTROL value?
		condition.execute(env);
		
		Constraint constraint = ConstraintFactory.createAtomicConstraint(condition.getSourceCode(), condition.getLocation());

		return WhileStatementNode.execute(env, constraint, body);
	}
	
}

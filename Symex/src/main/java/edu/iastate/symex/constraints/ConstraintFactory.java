package edu.iastate.symex.constraints;

import org.eclipse.php.internal.core.ast.nodes.InfixExpression;

import edu.iastate.symex.php.nodes.ExpressionNode;
import edu.iastate.symex.php.nodes.InfixExpressionNode;
import edu.iastate.symex.position.CompositeRange;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;

/**
 * 
 * @author HUNG
 *
 */
public abstract class ConstraintFactory {
	
	private static ConstraintFactory factory = new BddConstraintFactory();
	
	public static Constraint createTrueConstraint() {
		return factory.createTrue();
	}
	
	public static Constraint createFalseConstraint() {
		return factory.createFalse();
	}
	
	public static Constraint createAtomicConstraint(String conditionString, PositionRange location) {
		Constraint constraint = factory.createAtomic(conditionString);
		constraint.setLocation(location);
		return constraint;
	}

	public static Constraint createAndConstraint(Constraint constraint1, Constraint constraint2) {
		Constraint constraint = factory.createAnd(constraint1, constraint2);
		constraint.setLocation(new CompositeRange(constraint1.getLocation(), constraint2.getLocation()));
		return constraint;
	}
	
	public static Constraint createOrConstraint(Constraint constraint1, Constraint constraint2) {
		Constraint constraint = factory.createOr(constraint1, constraint2);
		constraint.setLocation(new CompositeRange(constraint1.getLocation(), constraint2.getLocation()));
		return constraint;
	}
	
	public static Constraint createNotConstraint(Constraint oppositeConstraint) {
		Constraint constraint = factory.createNot(oppositeConstraint);
		constraint.setLocation(oppositeConstraint.getLocation());
		return constraint;
	}
	
	/**
	 * Creates a constraint from a condition expression.
	 */
	public static Constraint createConstraintFromCondition(ExpressionNode condition) {
		if (condition instanceof InfixExpressionNode) {
			switch (((InfixExpressionNode) condition).getOperator()) {
				// '=='
				case InfixExpression.OP_IS_EQUAL: 
					return ConstraintFactory.createEqualConstraint(((InfixExpressionNode) condition).getLeft(), ((InfixExpressionNode) condition).getRight());
				
				// '!='
				// TODO Temporarily comment out this code because it breaks the testLoop test case (which is an excerpt from the SchoolMate web app)
				/*
				case InfixExpression.OP_IS_NOT_EQUAL:
					return ConstraintFactory.createNotConstraint(
								ConstraintFactory.createEqualConstraint(((InfixExpressionNode) condition).getLeft(), ((InfixExpressionNode) condition).getRight()));
				*/
				
				// TODO Handle more cases here if necessary (e.g., condition = "a && b" => return ConstraintFactory.createAndConstraint)
				
				default:
					return ConstraintFactory.createAtomicConstraint(condition.getSourceCode(), condition.getLocation()); 
			}
		}
		else
			return ConstraintFactory.createAtomicConstraint(condition.getSourceCode(), condition.getLocation());
	}
	
	/**
	 * Creates a constraint from an "==" expression (e.g., "$x == some_value") 
	 * @param leftExpression
	 * @param rightExpression
	 */
	public static Constraint createEqualConstraint(ExpressionNode leftExpression, ExpressionNode rightExpression) {
		String conditionString = leftExpression.getSourceCode() + " == " + rightExpression.getSourceCode();
		PositionRange location = new CompositeRange(new CompositeRange(leftExpression.getLocation(), new Range(" == ".length())), rightExpression.getLocation());
		
		Constraint constraint = factory.createEqual(conditionString, leftExpression, rightExpression);
		constraint.setLocation(location);
		return constraint;
	}
	
	protected abstract Constraint createTrue();
	
	protected abstract Constraint createFalse();
	
	protected abstract Constraint createAtomic(String conditionString);
	
	protected abstract Constraint createAnd(Constraint constraint1, Constraint constraint2);
	
	protected abstract Constraint createOr(Constraint constraint1, Constraint constraint2);
	
	protected abstract Constraint createNot(Constraint oppositeConstraint);
	
	protected abstract Constraint createEqual(String conditionString, ExpressionNode leftExpression, ExpressionNode rightExpression);
	
}

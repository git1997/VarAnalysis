package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ArrayCreation;
import org.eclipse.php.internal.core.ast.nodes.Assignment;
import org.eclipse.php.internal.core.ast.nodes.CastExpression;
import org.eclipse.php.internal.core.ast.nodes.ConditionalExpression;
import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.Identifier;
import org.eclipse.php.internal.core.ast.nodes.IgnoreError;
import org.eclipse.php.internal.core.ast.nodes.Include;
import org.eclipse.php.internal.core.ast.nodes.InfixExpression;
import org.eclipse.php.internal.core.ast.nodes.ParenthesisExpression;
import org.eclipse.php.internal.core.ast.nodes.PostfixExpression;
import org.eclipse.php.internal.core.ast.nodes.PrefixExpression;
import org.eclipse.php.internal.core.ast.nodes.Quote;
import org.eclipse.php.internal.core.ast.nodes.Reference;
import org.eclipse.php.internal.core.ast.nodes.Scalar;
import org.eclipse.php.internal.core.ast.nodes.UnaryOperation;
import org.eclipse.php.internal.core.ast.nodes.VariableBase;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.util.ASTHelper;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public abstract class ExpressionNode extends PhpNode {
	
	/**
	 * Constructor
	 * @param expression
	 */
	public ExpressionNode(Expression expression) {
		super(expression);
	}
	
	/*
	Base class for all expressions in PHP
	 */
	public static ExpressionNode createInstance(Expression expression) {
		if (expression instanceof VariableBase)
			return VariableBaseNode.createInstance((VariableBase) expression);

		switch (expression.getType()) {
			case Expression.ARRAY_CREATION:				return new ArrayCreationNode((ArrayCreation) expression);
			case Expression.ASSIGNMENT: 				return new AssignmentNode((Assignment) expression);
			case Expression.CAST_EXPRESSION:			return new CastExpressionNode((CastExpression) expression);
			case Expression.CONDITIONAL_EXPRESSION:		return new ConditionalExpressionNode((ConditionalExpression) expression);
			case Expression.IDENTIFIER:					return new IdentifierNode((Identifier) expression);
			case Expression.IGNORE_ERROR:				return new IgnoreErrorNode((IgnoreError) expression);
			case Expression.INCLUDE:					return new IncludeNode((Include) expression);
			case Expression.INFIX_EXPRESSION:			return new InfixExpressionNode((InfixExpression) expression);
			case Expression.PARENTHESIS_EXPRESSION:		return new ParenthesisExpressionNode((ParenthesisExpression) expression);
			case Expression.POSTFIX_EXPRESSION:			return new PostfixExpressionNode((PostfixExpression) expression);
			case Expression.PREFIX_EXPRESSION:			return new PrefixExpressionNode((PrefixExpression) expression);
			case Expression.QUOTE:						return new QuoteNode((Quote) expression);
			case Expression.REFERENCE:					return new ReferenceNode((Reference) expression);
			case Expression.SCALAR:						return new ScalarNode((Scalar) expression);
			case Expression.UNARY_OPERATION:			return new UnaryOperationNode((UnaryOperation) expression);
			default:									MyLogger.log(MyLevel.TODO, "Expression (" + expression.getClass().getSimpleName() + ") unimplemented: " + ASTHelper.inst.getSourceCodeOfPhpASTNode(expression)); return new UnresolvedExpressionNode(expression);
		}
	}
	
	/**
	 * Executes the expression.
	 * The returned value must be either a non-CONTROL value, an EXIT value (from FunctionInvocation 'exit'),
	 * 	 or a multi-value in which concrete values are non-CONTROL or EXIT.
	 * @param env 
	 */
	public abstract DataNode execute(Env env);
	
}
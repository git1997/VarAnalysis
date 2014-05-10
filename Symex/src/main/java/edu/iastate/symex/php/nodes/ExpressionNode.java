package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ArrayCreation;
import org.eclipse.php.internal.core.ast.nodes.Assignment;
import org.eclipse.php.internal.core.ast.nodes.CastExpression;
import org.eclipse.php.internal.core.ast.nodes.ClassInstanceCreation;
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
import org.eclipse.php.internal.core.ast.nodes.Scalar;
import org.eclipse.php.internal.core.ast.nodes.UnaryOperation;
import org.eclipse.php.internal.core.ast.nodes.VariableBase;

import edu.iastate.symex.core.Env;

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
			case Expression.CLASS_INSTANCE_CREATION:	return new ClassInstanceCreationNode((ClassInstanceCreation) expression);
			case Expression.CONDITIONAL_EXPRESSION:		return new ConditionalExpressionNode((ConditionalExpression) expression);
			case Expression.IDENTIFIER:					return new IdentifierNode((Identifier) expression);
			case Expression.IGNORE_ERROR:				return new IgnoreErrorNode((IgnoreError) expression);
			case Expression.INCLUDE:					return new IncludeNode((Include) expression);
			case Expression.INFIX_EXPRESSION:			return new InfixExpressionNode((InfixExpression) expression);
			case Expression.PARENTHESIS_EXPRESSION:		return new ParenthesisExpressionNode((ParenthesisExpression) expression);
			case Expression.POSTFIX_EXPRESSION:			return new PostfixExpressionNode((PostfixExpression) expression);
			case Expression.PREFIX_EXPRESSION:			return new PrefixExpressionNode((PrefixExpression) expression);
			case Expression.QUOTE:						return new QuoteNode((Quote) expression);
			case Expression.SCALAR:						return new ScalarNode((Scalar) expression);
			case Expression.UNARY_OPERATION:			return new UnaryOperationNode((UnaryOperation) expression);
			default:									MyLogger.log(MyLevel.TODO, "Expression unimplemented: " + ASTHelper.inst.getSourceCodeOfPhpASTNode(expression)); return new UnresolvedExpressionNode(expression);
		}
	}
	
	/**
	 * Resolves the name of a variable/function/class from this expression. 
	 * Note that this information may not be available until run time.
	 * Returns null if the name cannot be resolved.
	 */
	public String getResolvedNameOrNull(Env env) {
		if (this instanceof IdentifierNode)
			return ((IdentifierNode) this).getName();
		
		else if (env != null)
			return this.execute(env).getExactStringValueOrNull();
		
		else
			return null;
	}
	
}
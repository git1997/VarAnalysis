package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Block;
import org.eclipse.php.internal.core.ast.nodes.BreakStatement;
import org.eclipse.php.internal.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.ast.nodes.ContinueStatement;
import org.eclipse.php.internal.core.ast.nodes.DoStatement;
import org.eclipse.php.internal.core.ast.nodes.EchoStatement;
import org.eclipse.php.internal.core.ast.nodes.EmptyStatement;
import org.eclipse.php.internal.core.ast.nodes.ExpressionStatement;
import org.eclipse.php.internal.core.ast.nodes.ForEachStatement;
import org.eclipse.php.internal.core.ast.nodes.ForStatement;
import org.eclipse.php.internal.core.ast.nodes.FunctionDeclaration;
import org.eclipse.php.internal.core.ast.nodes.GlobalStatement;
import org.eclipse.php.internal.core.ast.nodes.IfStatement;
import org.eclipse.php.internal.core.ast.nodes.InLineHtml;
import org.eclipse.php.internal.core.ast.nodes.ReturnStatement;
import org.eclipse.php.internal.core.ast.nodes.Statement;
import org.eclipse.php.internal.core.ast.nodes.SwitchCase;
import org.eclipse.php.internal.core.ast.nodes.SwitchStatement;
import org.eclipse.php.internal.core.ast.nodes.TryStatement;
import org.eclipse.php.internal.core.ast.nodes.WhileStatement;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.instrumentation.WebDebugger;
import edu.iastate.symex.util.ASTHelper;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public abstract class StatementNode extends PhpNode {

	/**
	 * Constructor
	 * @param statement
	 */
	public StatementNode(Statement statement) {
		super(statement);
	}
	
	/*
	This is the base class for all statements in the PHP AST tree 
	*/
	public static StatementNode createInstance(Statement statement) {
		switch (statement.getType()) {
			case Statement.BLOCK:					return new BlockNode((Block) statement);
			case Statement.BREAK_STATEMENT:			return new BreakStatementNode((BreakStatement) statement);
			case Statement.CLASS_DECLARATION:		return new ClassDeclarationNode((ClassDeclaration) statement);
			case Statement.CONTINUE_STATEMENT:		return new ContinueStatementNode((ContinueStatement) statement);
			case Statement.DO_STATEMENT:			return new DoStatementNode((DoStatement) statement);
			case Statement.ECHO_STATEMENT: 			return new EchoStatementNode((EchoStatement) statement);
			case Statement.EMPTY_STATEMENT:			return new EmptyStatementNode((EmptyStatement) statement);
			case Statement.EXPRESSION_STATEMENT: 	return new ExpressionStatementNode((ExpressionStatement) statement);
			case Statement.FOR_EACH_STATEMENT:		return new ForEachStatementNode((ForEachStatement) statement);
			case Statement.FOR_STATEMENT:			return new ForStatementNode((ForStatement) statement);
			case Statement.FUNCTION_DECLARATION:	return new FunctionDeclarationNode((FunctionDeclaration) statement);
			case Statement.GLOBAL_STATEMENT:		return new GlobalStatementNode((GlobalStatement) statement);
			case Statement.IF_STATEMENT:			return new IfStatementNode((IfStatement) statement);
			case Statement.IN_LINE_HTML:			return new InLineHtmlNode((InLineHtml) statement);
			case Statement.RETURN_STATEMENT:		return new ReturnStatementNode((ReturnStatement) statement);
			case Statement.SWITCH_CASE:				return new SwitchCaseNode((SwitchCase) statement);
			case Statement.SWITCH_STATEMENT:		return new SwitchStatementNode((SwitchStatement) statement);
			case Statement.TRY_STATEMENT:			return new TryStatementNode((TryStatement) statement);
			case Statement.WHILE_STATEMENT:			return new WhileStatementNode((WhileStatement) statement);			
			default: 								MyLogger.log(MyLevel.TODO, "Statement (" + statement.getClass().getSimpleName() + ") unimplemented: " + ASTHelper.inst.getSourceCodeOfPhpASTNode(statement)); return new UnresolvedStatementNode(statement);
		}
	}
	
	/**
	 * Executes the statement.
	 * The returned value must be a CONTROL value (or a multi-value of type CONTROL). 
	 * @param env
	 */
	public DataNode execute(Env env) {
		/*
		 * The following code is used for web debugger. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB DEBUGGER CODE
		if (WebDebugger.isEnabled())
			WebDebugger.onStatementExecuteStart(this, env);
		// END OF WEB DEBUGGER CODE
		  
		DataNode retValue = execute_(env);
		
		/*
		 * The following code is used for web debugger. Comment out/Uncomment out if necessary.
		 */
		// BEGIN OF WEB DEBUGGER CODE
		if (WebDebugger.isEnabled())
			WebDebugger.onStatementExecuteEnd(this, env);
		// END OF WEB DEBUGGER CODE
		
		return retValue;
	}
	
	/**
	 * Executes a statement.
	 * See edu.iastate.symex.php.nodes.StatementNode.execute(Env) for more details.
	 */
	public abstract DataNode execute_(Env env);
	
}
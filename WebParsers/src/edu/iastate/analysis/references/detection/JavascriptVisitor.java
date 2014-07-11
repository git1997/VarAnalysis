package edu.iastate.analysis.references.detection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.ASTVisitor;
import org.eclipse.wst.jsdt.core.dom.Expression;
import org.eclipse.wst.jsdt.core.dom.FieldAccess;
import org.eclipse.wst.jsdt.core.dom.ForInStatement;
import org.eclipse.wst.jsdt.core.dom.ForStatement;
import org.eclipse.wst.jsdt.core.dom.FunctionDeclaration;
import org.eclipse.wst.jsdt.core.dom.FunctionInvocation;
import org.eclipse.wst.jsdt.core.dom.IfStatement;
import org.eclipse.wst.jsdt.core.dom.SimpleName;
import org.eclipse.wst.jsdt.core.dom.SingleVariableDeclaration;
import org.eclipse.wst.jsdt.core.dom.Statement;
import org.eclipse.wst.jsdt.core.dom.StringLiteral;
import org.eclipse.wst.jsdt.core.dom.SwitchStatement;
import org.eclipse.wst.jsdt.core.dom.VariableDeclarationFragment;
import org.eclipse.wst.jsdt.core.dom.VariableDeclarationStatement;
import org.eclipse.wst.jsdt.core.dom.WhileStatement;

import edu.iastate.analysis.config.AnalysisConfig;
import edu.iastate.analysis.references.JsFunctionCall;
import edu.iastate.analysis.references.JsFunctionDecl;
import edu.iastate.analysis.references.JsRefToHtmlForm;
import edu.iastate.analysis.references.JsRefToHtmlId;
import edu.iastate.analysis.references.JsRefToHtmlInput;
import edu.iastate.analysis.references.JsVariableDecl;
import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.ReferenceManager;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.RelativeRange;

/**
 * JavascriptVisitor visits Javascript elements and detects entities.
 * 
 * @author HUNG
 *
 */
public class JavascriptVisitor extends ASTVisitor {
	
	private PositionRange javascriptLocation;
	private Constraint constraint;
	private ReferenceManager referenceManager;
	
	/**
	 * Constructor
	 */
	public JavascriptVisitor(PositionRange javascriptLocation, Constraint constraint, ReferenceManager referenceManager) {
		this.javascriptLocation = javascriptLocation;
		this.constraint = constraint;
		this.referenceManager = referenceManager;
	}
	
	/**
	 * Adds a reference.
	 * This method should be called instead of calling referenceManager.addReference directly.
	 */
	private void addReference(Reference reference) {
		reference.setConstraint(constraint);
		referenceManager.addReference(reference);
	}
	
	/**
	 * Visits a function declaration.
	 */
	public boolean visit(FunctionDeclaration functionDeclaration) {
		if (functionDeclaration.getName() != null) {
			SimpleName functionName = functionDeclaration.getName();
			
			// Add a JsFunctionDecl reference
			Reference reference = new JsFunctionDecl(functionName.getIdentifier(), getLocation(functionName));
			addReference(reference);
		}
		
		for (Object object : functionDeclaration.parameters()) {
			SingleVariableDeclaration singleVariableDeclaration = (SingleVariableDeclaration) object;
			singleVariableDeclaration.accept(this);
		}
		if (functionDeclaration.getBody() != null)
			functionDeclaration.getBody().accept(this);
		
		return false;
	}
	
	/**
	 * Visits a function invocation.
	 */
	public boolean visit(FunctionInvocation functionInvocation) {
		SimpleName functionName = functionInvocation.getName();
		
		if (functionName == null) {
			// Do nothing
		}
		else if (functionName.getIdentifier().equals("getElementById")) {
			// Add a JsRefToHtmlId reference
			if (functionInvocation.arguments().size() == 1 && functionInvocation.arguments().get(0) instanceof StringLiteral) {
				StringLiteral stringLiteral = (StringLiteral) functionInvocation.arguments().get(0);
				String id = stringLiteral.getEscapedValue();
				id = id.substring(1, id.length() - 1);
				
				Reference reference = new JsRefToHtmlId(id, getLocation(stringLiteral, 1));
				addReference(reference);
			}
		}
		else if (!isJavascriptKeyword(functionName.getIdentifier())) {
			// Add a JsFunctionCall reference
			Reference reference = new JsFunctionCall(functionName.getIdentifier(), getLocation(functionName));
			addReference(reference);
		}
		
		for (Object object : functionInvocation.arguments()) {
			Expression expression = (Expression) object;
			expression.accept(this);
		}
		
		if (functionInvocation.getExpression() != null) {
			functionInvocation.getExpression().accept(this);
		}
		
		return false;
	}
	
	/**
	 * Visits a field access.
	 */
	public boolean visit(FieldAccess fieldAccess) {
		Expression expression = fieldAccess.getExpression();
		SimpleName fieldName = fieldAccess.getName();
		
		if (isJavascriptKeyword(fieldName.getIdentifier())) {
			// Do nothing
		}
		else if (expression.toString().endsWith("document")) {
			// Add a JsRefToHtmlForm reference
			Reference reference = new JsRefToHtmlForm(fieldName.getIdentifier(), getLocation(fieldName));
			addReference(reference);
		}
		
		else if (expression instanceof FieldAccess && ((FieldAccess) expression).getExpression().toString().endsWith("document")) {
			// Add a JsRefToHtmlInput reference
			String formName = ((FieldAccess) expression).getName().getIdentifier();
			Reference reference = new JsRefToHtmlInput(fieldName.getIdentifier(), getLocation(fieldName), formName);
			addReference(reference);
		}
		
		expression.accept(this);
		
		return false;
	}
	
	/*
	 * Handle Javascript variables.
	 */
	
	/**
	 * Visits a variable declaration statement.
	 */
	public boolean visit(VariableDeclarationStatement variableDeclarationStatement) {
		variableDeclarationStatement.getType().accept(this);
		for (Object object : variableDeclarationStatement.fragments()) {
			VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) object;
			variableDeclarationFragment.getName().accept(this);
			if (variableDeclarationFragment.getInitializer() != null)
				variableDeclarationFragment.getInitializer().accept(this);
		}
		return false;
	}
	
	/**
	 * Visits a simple name.
	 */
	public boolean visit(SimpleName simpleName) {
		if (AnalysisConfig.DETECT_JS_VARIABLES) {
			if (!isJavascriptKeyword(simpleName.getIdentifier())) {
				// Add a JsVariable reference
				Reference reference = new JsVariableDecl(simpleName.getIdentifier(), getLocation(simpleName));
				addReference(reference);
			}
		}
		return false;
	}
	
	/*
	 * Handle branches.
	 */
	
	/**
	 * Visits an If-statement.
	 */
	public boolean visit(IfStatement ifStatement) {
		ifStatement.getExpression().accept(this);
		visitBranchingNode(ifStatement.getThenStatement());
		if (ifStatement.getElseStatement() != null) {
			visitBranchingNode(ifStatement.getElseStatement());
		}
		return false;
	}
	
	/**
	 * Visits a Switch-statement
	 */
	public boolean visit(SwitchStatement switchStatement) {
		switchStatement.getExpression().accept(this);
		for (Object object : switchStatement.statements()) {
			Statement statement = (Statement) object;
			if (statement.getNodeType() == Statement.SWITCH_CASE)
				statement.accept(this);
			else
				this.visitBranchingNode(statement);
		}
		return false;
	}
	
	/**
	 * Visits a For-statement
	 */
	public boolean visit(ForStatement forStatement) {
		for (Object object : forStatement.initializers()) {
			Expression expression = (Expression) object;
			expression.accept(this);
		}
		forStatement.getExpression().accept(this);
		ArrayList<ASTNode> astNodes = new ArrayList<ASTNode>();
		astNodes.add(forStatement.getBody());
		for (Object object : forStatement.updaters()) {
			Expression expression = (Expression) object;
			astNodes.add(expression);
		}
		this.visitBranchingNodes(astNodes);
		return false;
	}
	
	/**
	 * Visits a For-in-statement
	 */
	public boolean visit(ForInStatement forInStatement) {
		forInStatement.getCollection().accept(this);
		forInStatement.getIterationVariable().accept(this);
		this.visitBranchingNode(forInStatement.getBody());
		return false;
	}
	
	/**
	 * Visits a While-statement
	 */
	public boolean visit(WhileStatement whileStatement) {
		whileStatement.getExpression().accept(this);
		this.visitBranchingNode(whileStatement.getBody());
		return false;
	}
	
	/**
	 * Visits branching nodes (for example, the then-statement or else-statement in an If-statement).
	 */
	private void visitBranchingNodes(ArrayList<ASTNode> branchingNodes) {
		for (ASTNode astNode : branchingNodes)
			astNode.accept(this);
	}
	
	/**
	 * Visits a branching node (for example, the then-statement or else-statement in an If-statement).
	 */
	private void visitBranchingNode(ASTNode branchingNode) {
		ArrayList<ASTNode> branchingNodes = new ArrayList<ASTNode>();
		branchingNodes.add(branchingNode);
		visitBranchingNodes(branchingNodes);
	}
	
	/*
	 * Utility methods
	 */
	
	private PositionRange getLocation(ASTNode astNode) {
		return getLocation(astNode, 0);
	}
	
	private PositionRange getLocation(ASTNode astNode, int adjustedOffset) {
		return new RelativeRange(javascriptLocation, astNode.getStartPosition() + adjustedOffset, astNode.getLength());
	}
	
	/**
	 * List of Javascript keywords
	 */
	private static String[] keywords = {
		"alert", "Array", "attachEvent", "blur", "body", "checked", "childNodes", "clearTimeout", "close", "color", "confirm", "createElement",
		"disabled", "document", "domain", "elements", "escape", "eval", "FALSE", "firstChild", "focus", "forms",
		"getAttribute", "getElementById", "getElementsByTagName", "indexOf",
		"JavaScript", "javascript", "join", "lastIndexOf", "length", "Math", "name", "navigator", "open", "opener", "options",
		"parent", "parentNode",	"parseInt", "print", "replace", "round", 
		"select", "selectedIndex", "self", "send", "setAttribute", "setRequestHeader", "setTimeout", "split", "status", "style", "submit", "substr", "substring", 
		"title", "toLowerCase", "toUpperCase", "TRUE", "type", "unescape", "value", "window", "windows", "write", "writeln"
	};
	
	private static HashSet<String> javascriptKeywords = new HashSet<String>(Arrays.asList(keywords));
	
	public boolean isJavascriptKeyword(String name) {
		return javascriptKeywords.contains(name);
	}
	
}
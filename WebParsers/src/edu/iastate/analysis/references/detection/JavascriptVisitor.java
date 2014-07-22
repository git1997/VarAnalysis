package edu.iastate.analysis.references.detection;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.ASTVisitor;
import org.eclipse.wst.jsdt.core.dom.Assignment;
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

import edu.iastate.analysis.references.DeclaringReference;
import edu.iastate.analysis.references.JsFunctionCall;
import edu.iastate.analysis.references.JsFunctionDecl;
import edu.iastate.analysis.references.JsRefToHtmlForm;
import edu.iastate.analysis.references.JsRefToHtmlId;
import edu.iastate.analysis.references.JsRefToHtmlInput;
import edu.iastate.analysis.references.JsVariableDecl;
import edu.iastate.analysis.references.JsVariableRef;
import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.ReferenceManager;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
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
	private Constraint javascriptConstraint;
	
	private File entryFile;
	private ReferenceManager referenceManager;
	
	private Env env; // Used to detect data flows
	
	/**
	 * Constructor
	 */
	public JavascriptVisitor(PositionRange javascriptLocation, Constraint javascriptConstraint, File entryFile, ReferenceManager referenceManager) {
		this.javascriptLocation = javascriptLocation;
		this.javascriptConstraint = javascriptConstraint;
		
		this.entryFile = entryFile;
		this.referenceManager = referenceManager;
		
		this.env = new Env();
	}
	
	/**
	 * Adds a reference.
	 * This method should be called instead of calling referenceManager.addReference directly.
	 */
	private void addReference(Reference reference) {
		reference.setConstraint(ConstraintFactory.createAndConstraint(javascriptConstraint, env.getConstraint()));
		reference.setEntryFile(entryFile);
		referenceManager.addReference(reference);
	}
	
	/**
	 * Visits a function declaration.
	 */
	public boolean visit(FunctionDeclaration functionDeclaration) {
		Env prevEnv = env;
		
		env = new Env();
		
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
		
		env = prevEnv;
		
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
			if (functionInvocation.arguments().size() == 1 && functionInvocation.arguments().get(0) instanceof StringLiteral) {
				StringLiteral stringLiteral = (StringLiteral) functionInvocation.arguments().get(0);
				String id = stringLiteral.getEscapedValue();
				id = id.substring(1, id.length() - 1);
				PositionRange location = new RelativeRange(javascriptLocation, stringLiteral.getStartPosition() + 1, id.length());
				
				// Add a JsRefToHtmlId reference
				Reference reference = new JsRefToHtmlId(id, location);
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
			String formName = ((FieldAccess) expression).getName().getIdentifier();
			
			// Add a JsRefToHtmlInput reference
			Reference reference = new JsRefToHtmlInput(fieldName.getIdentifier(), getLocation(fieldName), formName);
			addReference(reference);
		}
		
		expression.accept(this);
		
		return false;
	}
	
	/*
	 * Handle JavaScript variables.
	 */
	
	/**
	 * Visits a variable declaration statement.
	 */
	public boolean visit(VariableDeclarationStatement variableDeclarationStatement) {
		variableDeclarationStatement.getType().accept(this);
		
		for (Object object : variableDeclarationStatement.fragments()) {
			VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) object;
			SimpleName simpleName = variableDeclarationFragment.getName();
			
			if (!isJavascriptKeyword(simpleName.getIdentifier()))
				foundVariableDecl(simpleName, variableDeclarationFragment.getInitializer());
			
			if (variableDeclarationFragment.getInitializer() != null)
				variableDeclarationFragment.getInitializer().accept(this);
		}
		
		return false;
	}
	
	/**
	 * Visits an assignment.
	 */
	public boolean visit(Assignment assignment) {
		Expression leftHandSide = assignment.getLeftHandSide();
		if (leftHandSide instanceof SimpleName) {
			SimpleName simpleName = (SimpleName) leftHandSide;
			
			if (!isJavascriptKeyword(simpleName.getIdentifier()))
				foundVariableDecl(simpleName, assignment.getRightHandSide());
		}
		else
			leftHandSide.accept(this);
		
		assignment.getRightHandSide().accept(this);
		
		return false;
	}
	
	/**
	 * Visits a simple name.
	 */
	public boolean visit(SimpleName simpleName) {
		if (!isJavascriptKeyword(simpleName.getIdentifier()))
			foundVariableRef(simpleName);

		return false;
	}
	
	private void foundVariableDecl(SimpleName variable, Expression rightHandSide) {
		// Add a JsVariableDecl
		JsVariableDecl jsVariableDecl = new JsVariableDecl(variable.getIdentifier(), getLocation(variable));
		addReference(jsVariableDecl);
		
		/*
		 * Record data flows
		 */
		HashSet<JsVariableDecl> decls = new HashSet<JsVariableDecl>();
		decls.add(jsVariableDecl);
		env.setVariableDeclarations(jsVariableDecl.getName(), decls);
		
		if (rightHandSide != null)
			referenceManager.getDataFlowManager().putMapDeclToRefLocations(jsVariableDecl, getLocation(rightHandSide));
	}
	
	private void foundVariableRef(SimpleName variable) {
		// Add a JsVariableRef
		JsVariableRef jsVariableRef = new JsVariableRef(variable.getIdentifier(), getLocation(variable));
		addReference(jsVariableRef);
		
		/*
		 * Record data flows
		 */
		HashSet<DeclaringReference> decls = new HashSet<DeclaringReference>(env.getVariableDeclarations(jsVariableRef.getName()));
		referenceManager.getDataFlowManager().putMapRefToDecls(jsVariableRef, decls);
	}
	
	/*
	 * Handle branches
	 */
	
	/**
	 * Visits an If-statement.
	 */
	public boolean visit(IfStatement ifStatement) {
		ifStatement.getExpression().accept(this);
		
		Constraint constraint = ConstraintFactory.createAtomicConstraint(ifStatement.getExpression().toString(), getLocation(ifStatement.getExpression()));
		visitBranches(constraint, ifStatement.getThenStatement(), ifStatement.getElseStatement());
		
		return false;
	}
	
	/**
	 * Visits a Switch-statement
	 */
	public boolean visit(SwitchStatement switchStatement) {
		// TODO Handle switch statements
		switchStatement.getExpression().accept(this);
		
		for (Object object : switchStatement.statements()) {
			Statement statement = (Statement) object;
			if (statement.getNodeType() == Statement.SWITCH_CASE)
				statement.accept(this);
			else
				statement.accept(this);
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
		
		Constraint constraint = ConstraintFactory.createAtomicConstraint(forStatement.getExpression().toString(), getLocation(forStatement.getExpression()));
		visitBranches(constraint, forStatement.getBody(), null);
		
		for (Object object : forStatement.updaters()) {
			Expression expression = (Expression) object;
			expression.accept(this);
		}
		
		return false;
	}
	
	/**
	 * Visits a For-in-statement
	 */
	public boolean visit(ForInStatement forInStatement) {
		forInStatement.getCollection().accept(this);
		forInStatement.getIterationVariable().accept(this);
		
		Constraint constraint = ConstraintFactory.createAtomicConstraint(forInStatement.getCollection().toString(), getLocation(forInStatement.getCollection()));
		visitBranches(constraint, forInStatement.getBody(), null);
		
		return false;
	}
	
	/**
	 * Visits a While-statement
	 */
	public boolean visit(WhileStatement whileStatement) {
		whileStatement.getExpression().accept(this);
		
		Constraint constraint = ConstraintFactory.createAtomicConstraint(whileStatement.getExpression().toString(), getLocation(whileStatement.getExpression()));
		visitBranches(constraint, whileStatement.getBody(), null);
		
		return false;
	}
	
	/**
	 * Visits branches. A branch can be null.
	 */
	private void visitBranches(Constraint constraint, Statement statement1, Statement statement2) {
		Env prevEnv = env;
		Constraint prevConstraint = env.getConstraint();

		env = new Env(prevEnv, ConstraintFactory.createAndConstraint(prevConstraint, constraint));
		if (statement1 != null)
			statement1.accept(this);
		Env env1 = env;
		
		env = new Env(prevEnv, ConstraintFactory.createAndConstraint(prevConstraint, ConstraintFactory.createNotConstraint(constraint)));
		if (statement2 != null)
			statement2.accept(this);
		Env env2 = env;
		
		env = prevEnv;
		
		/*
		 * Record data flows
		 */
		updateVariableDeclarations(env, env1, env2);
	}
	
	/**
	 * Updates variable declarations after visiting two branches env1 and env2
	 */
	private void updateVariableDeclarations(Env prevEnv, Env env1, Env env2) {
		HashSet<String> variableNames = env1.getVariablesInCurrentScope();
		variableNames.addAll(env2.getVariablesInCurrentScope());
		
		for (String variableName : variableNames) {
			HashSet<JsVariableDecl> variableDeclarations = env1.getVariableDeclarations(variableName);
			variableDeclarations.addAll(env2.getVariableDeclarations(variableName));
			prevEnv.setVariableDeclarations(variableName, variableDeclarations);
		}
	}
	
	/*
	 * Utility methods
	 */
	
	private PositionRange getLocation(ASTNode astNode) {
		return new RelativeRange(javascriptLocation, astNode.getStartPosition(), astNode.getLength());
	}
	
	/**
	 * List of JavaScript keywords
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
	
	/**
	 * Used to detect data flows
	 */
	private class Env {
		
		private Env outerScopeEnv;
		private Constraint constraint;
		
		private HashMap<String, HashSet<JsVariableDecl>> variableTable = new HashMap<String, HashSet<JsVariableDecl>>();
		
		public Env(Env outerScopeEnv, Constraint constraint) {
			this.outerScopeEnv = outerScopeEnv;
			this.constraint = constraint;
		}
		
		public Env() {
			this(null, Constraint.TRUE);
		}
		
		public Constraint getConstraint() {
			return constraint;
		}
		
		public HashSet<String> getVariablesInCurrentScope() {
			return new HashSet<String>(variableTable.keySet());
		}
		
		public HashSet<JsVariableDecl> getVariableDeclarations(String variableName) {
			if (variableTable.containsKey(variableName))
				return new HashSet<JsVariableDecl>(variableTable.get(variableName));
			else if (outerScopeEnv != null)
				return outerScopeEnv.getVariableDeclarations(variableName);
			else
				return new HashSet<JsVariableDecl>();
		}
		
		public void setVariableDeclarations(String variableName, HashSet<JsVariableDecl> variableDeclarations) {
			variableTable.put(variableName, variableDeclarations);
		}
		
	}
	
}
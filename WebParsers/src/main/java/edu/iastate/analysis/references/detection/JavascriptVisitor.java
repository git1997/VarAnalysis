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
import org.eclipse.wst.jsdt.core.dom.ReturnStatement;
import org.eclipse.wst.jsdt.core.dom.SimpleName;
import org.eclipse.wst.jsdt.core.dom.SingleVariableDeclaration;
import org.eclipse.wst.jsdt.core.dom.Statement;
import org.eclipse.wst.jsdt.core.dom.StringLiteral;
import org.eclipse.wst.jsdt.core.dom.SwitchStatement;
import org.eclipse.wst.jsdt.core.dom.VariableDeclarationFragment;
import org.eclipse.wst.jsdt.core.dom.VariableDeclarationStatement;
import org.eclipse.wst.jsdt.core.dom.WhileStatement;

import edu.iastate.analysis.references.DeclaringReference;
import edu.iastate.analysis.references.JsDeclOfHtmlInputValue;
import edu.iastate.analysis.references.JsFunctionCall;
import edu.iastate.analysis.references.JsFunctionDecl;
import edu.iastate.analysis.references.JsObjectFieldDecl;
import edu.iastate.analysis.references.JsObjectFieldRef;
import edu.iastate.analysis.references.JsRefToHtmlForm;
import edu.iastate.analysis.references.JsRefToHtmlId;
import edu.iastate.analysis.references.JsRefToHtmlInput;
import edu.iastate.analysis.references.JsRefToHtmlInputValue;
import edu.iastate.analysis.references.JsVariableDecl;
import edu.iastate.analysis.references.JsVariableRef;
import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.RegularReference;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.RelativeRange;

/**
 * JavascriptVisitor visits JavaScript elements and detects entities.
 * 
 * @author HUNG
 *
 */
public class JavascriptVisitor extends ASTVisitor {
	
	private PositionRange location; // Location of the JavaScript code
	
	private File entryFile;
	private Constraint constraint; // Could be updated during traversal
	
	private ReferenceManager referenceManager;
	
	/*
	 * Used to construct data flow
	 */
	private DataFlowManager dataFlowManager;
	private Env env;
	
	/**
	 * Constructor
	 */
	public JavascriptVisitor(PositionRange location, File entryFile, Constraint constraint, ReferenceManager referenceManager) {
		this.location = location;
		
		this.entryFile = entryFile;
		this.constraint = constraint;
		this.referenceManager = referenceManager;
		
		this.dataFlowManager = referenceManager.getDataFlowManager();
		this.env = new Env();
	}
	
	/**
	 * Adds a reference.
	 * This method should be called instead of calling referenceManager.addReference directly.
	 */
	private void addReference(Reference reference) {
		reference.setEntryFile(entryFile);
		reference.setConstraint(constraint);
		referenceManager.addReference(reference);
	}
	
	/*
	 * Handle variables
	 */
	
	/**
	 * Visits an assignment.
	 */
	public boolean visit(Assignment assignment) {
		Expression leftHandSide = assignment.getLeftHandSide();
		Expression rightHandSide = assignment.getRightHandSide();
		
		rightHandSide.accept(this);
		
		if (leftHandSide instanceof SimpleName) {
			SimpleName simpleName = (SimpleName) leftHandSide;
			
			// Found a JsVariableDecl
			if (!isJavascriptKeyword(simpleName.getIdentifier()))
				foundVariableDecl(simpleName, rightHandSide);
		}
		else if (leftHandSide instanceof FieldAccess) { 
			FieldAccess fieldAccess = (FieldAccess) leftHandSide;
			Expression expression = fieldAccess.getExpression();
			SimpleName name = fieldAccess.getName();
			
			expression.accept(this);
			JsVariableRef jsVariableRef = env.getVariableRef(expression);
			
			// Found a JsObjectFieldDecl
			if (jsVariableRef != null && !isJavascriptKeyword(name.getIdentifier()))
				foundJsObjectFieldDecl(fieldAccess, (RegularReference) jsVariableRef, rightHandSide);
		}
		else
			leftHandSide.accept(this);
		
		return false;
	}
	
	/**
	 * Visits a variable declaration statement.
	 */
	public boolean visit(VariableDeclarationStatement variableDeclarationStatement) {
		variableDeclarationStatement.getType().accept(this);
		
		for (Object object : variableDeclarationStatement.fragments()) {
			VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) object;
			SimpleName simpleName = variableDeclarationFragment.getName();
			Expression initializer = variableDeclarationFragment.getInitializer();
			
			if (initializer != null)
				initializer.accept(this);
			
			// Found a JsVariableDecl
			if (!isJavascriptKeyword(simpleName.getIdentifier()))
				foundVariableDecl(simpleName, variableDeclarationFragment.getInitializer());
		}
		
		return false;
	}
	
	/**
	 * Visits a simple name.
	 */
	public boolean visit(SimpleName simpleName) {
		// Found a JsVariableRef
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
		JsVariable jsVariable = env.getOrPutVariable(jsVariableDecl.getName());
		env.putVariableDecls(jsVariable, jsVariableDecl);
		if (rightHandSide != null) {
			HashSet<JsVariableRef> jsVariableRefs = env.getVariableRefs(rightHandSide);
			dataFlowManager.addDataFlow(new HashSet<RegularReference>(jsVariableRefs), jsVariableDecl);
		}
	}
	
	private void foundVariableRef(SimpleName variableNode) {
		// Add a JsVariableRef
		JsVariableRef jsVariableRef = new JsVariableRef(variableNode.getIdentifier(), getLocation(variableNode));
		addReference(jsVariableRef);
		
		/*
		 * Record data flows
		 */
		JsVariable jsVariable = env.getVariable(jsVariableRef.getName());
		env.putVariableRef(variableNode, jsVariableRef);
		if (jsVariable != null) {
			HashSet<JsVariableDecl> jsVariableDecls = env.getVariableDecls(jsVariable);
			dataFlowManager.addDataFlow(new HashSet<DeclaringReference>(jsVariableDecls), jsVariableRef);
		}
	}
	
	/*
	 * Handle object fields
	 */
	
	/**
	 * Visits a field access.
	 */
	public boolean visit(FieldAccess fieldAccess) {
 		Expression expression = fieldAccess.getExpression();
		SimpleName name = fieldAccess.getName();
		
		expression.accept(this);
		JsVariableRef jsVariableRef = env.getVariableRef(expression);
		
		// Found a JsObjectFieldRef
		if (jsVariableRef != null && !isJavascriptKeyword(name.getIdentifier()))
			foundJsObjectFieldRef(fieldAccess, (RegularReference) jsVariableRef);
		
		return false;
	}
	
	private void foundJsObjectFieldDecl(FieldAccess fieldAccess, RegularReference object, Expression rightHandSide) {
		String name = fieldAccess.getName().getIdentifier();
		PositionRange location = getLocation(fieldAccess.getName());
		
		JsObjectFieldDecl jsObjectFieldDecl;
		if (object instanceof JsRefToHtmlInput && name.equals("value")) {
			// Add a JsDeclOfHtmlInputValue
			jsObjectFieldDecl = new JsDeclOfHtmlInputValue(name, location, (JsRefToHtmlInput) object);
		}
		else {
			// Currently only handle objects with field name 'value'
			return;
		}
		addReference(jsObjectFieldDecl);
		
		/*
		 * Record data flows
		 */
		JsVariable jsVariable = env.getOrPutVariable(jsObjectFieldDecl.getFullyQualifiedName());
		env.putVariableDecls(jsVariable, jsObjectFieldDecl);
		if (rightHandSide != null) {
			HashSet<JsVariableRef> jsVariableRefs = env.getVariableRefs(rightHandSide);
			dataFlowManager.addDataFlow(new HashSet<RegularReference>(jsVariableRefs), jsObjectFieldDecl);
		}
	}
	
	private void foundJsObjectFieldRef(FieldAccess fieldAccess, RegularReference object) {
		String name = fieldAccess.getName().getIdentifier();
		PositionRange location = getLocation(fieldAccess.getName());
		
		JsObjectFieldRef jsObjectFieldRef;
		if (object.getName().equals("document")) {
			// Add a JsRefToHtmlForm
			jsObjectFieldRef = new JsRefToHtmlForm(name, location, object);
		}
		else if (object instanceof JsRefToHtmlForm) {
			// Add a JsRefToHtmlInput
			jsObjectFieldRef = new JsRefToHtmlInput(name, location, (JsRefToHtmlForm) object);
		}
		else if (object instanceof JsRefToHtmlInput && name.equals("value")) {
			// Add a JsRefToHtmlInputValue
			jsObjectFieldRef = new JsRefToHtmlInputValue(name, location, (JsRefToHtmlInput) object);
		}
		else {
			// Currently only handle objects with field name 'value'
			return;
		}
		addReference(jsObjectFieldRef);
		
		/*
		 * Record data flows
		 */
		JsVariable jsVariable = env.getVariable(jsObjectFieldRef.getFullyQualifiedName());
		env.putVariableRef(fieldAccess, jsObjectFieldRef);
		if (jsVariable != null) {
			HashSet<JsVariableDecl> jsVariableDecls = env.getVariableDecls(jsVariable);
			dataFlowManager.addDataFlow(new HashSet<DeclaringReference>(jsVariableDecls), jsObjectFieldRef);
		}
	}
	
	/*
	 * Handle functions & return statements
	 */
	
	/**
	 * Visits a function declaration.
	 */
	public boolean visit(FunctionDeclaration functionDeclaration) {
		SimpleName functionNameNode = functionDeclaration.getName();
		String functionName = (functionNameNode != null ? functionNameNode.getIdentifier() : "");
		
		Env prevEnv = env;
		env = new Env();
		
		if (!functionName.isEmpty()) {
			// Add a JsFunctionDecl reference
			JsFunctionDecl jsFunctionDecl = new JsFunctionDecl(functionName, getLocation(functionNameNode));
			addReference(jsFunctionDecl);
			
			env.setCurrentFunction(jsFunctionDecl);
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
		SimpleName functionNameNode = functionInvocation.getName();
		String functionName = (functionNameNode != null ? functionNameNode.getIdentifier() : "");
		
		if (functionName.isEmpty()) {
			// Do nothing
		}
		else if (functionName.equals("getElementById")) {
			if (functionInvocation.arguments().size() == 1 && functionInvocation.arguments().get(0) instanceof StringLiteral) {
				StringLiteral stringLiteral = (StringLiteral) functionInvocation.arguments().get(0);
				String id = stringLiteral.getEscapedValue();
				id = id.substring(1, id.length() - 1);
				PositionRange refLocation = new RelativeRange(location, stringLiteral.getStartPosition() + 1, id.length());
				
				// Add a JsRefToHtmlId reference
				JsRefToHtmlId jsRefToHtmlId = new JsRefToHtmlId(id, refLocation);
				addReference(jsRefToHtmlId);
			}
		}
		else if (!isJavascriptKeyword(functionName)) {
			// Add a JsFunctionCall reference
			JsFunctionCall jsFunctionCall = new JsFunctionCall(functionName, getLocation(functionNameNode));
			addReference(jsFunctionCall);
		}
		
		for (Object object : functionInvocation.arguments()) {
			Expression expression = (Expression) object;
			expression.accept(this);
		}
		
		if (functionInvocation.getExpression() != null) {
			functionInvocation.getExpression().accept(this);
		}
		
		/*
		 * Record data flows
		 */
		// Currently, connecting JavaScript functions is done by DataFlowManager
		// since a function declaration and a function invocation may appear in different code fragments.
		// @see edu.iastate.analysis.references.detection.DataFlowManager.resolveDataFlowsWithinJavaScriptCode(ArrayList<Reference>, HashMap<String, ArrayList<Reference>>)
		
		return false;
	}
	
	/**
	 * Visits a return statement.
	 */
	public boolean visit(ReturnStatement returnStatement) {
		if (returnStatement.getExpression() != null)
			returnStatement.getExpression().accept(this);
		
		/*
		 * Record data flows
		 */
		JsFunctionDecl jsFunctionDecl = env.getCurrentFunction();
		if (jsFunctionDecl != null) {
			dataFlowManager.addDataFlow(new HashSet<RegularReference>(env.getVariableRefs(returnStatement)), jsFunctionDecl);
		}
		
		return false;
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
	private void visitBranches(Constraint condition, Statement thenStatement, Statement elseStatement) {
		Env prevEnv = env;
		Constraint prevConstraint = constraint;

		env = new Env(prevEnv);
		constraint = ConstraintFactory.createAndConstraint(prevConstraint, condition);
		if (thenStatement != null)
			thenStatement.accept(this);
		Env env1 = env;
		
		env = new Env(prevEnv);
		constraint = ConstraintFactory.createAndConstraint(prevConstraint, ConstraintFactory.createNotConstraint(condition));
		if (elseStatement != null)
			elseStatement.accept(this);
		Env env2 = env;
		
		env = prevEnv;
		constraint = prevConstraint;
		
		/*
		 * Record data flows
		 */
		env.updateAfterBranchExecution(env1, env2);
	}
	
	/*
	 * Utility methods
	 */
	
	private PositionRange getLocation(ASTNode astNode) {
		return new RelativeRange(location, astNode.getStartPosition(), astNode.getLength());
	}
	
	/**
	 * List of JavaScript keywords
	 */
	private static String[] keywords = {
		"alert", "Array", "attachEvent", "blur", "body", "checked", "childNodes", "clearTimeout", "close", "color", "confirm", "createElement",
		"disabled", "domain", "elements", "escape", "eval", "FALSE", "firstChild", "focus", "forms",
		"getAttribute", "getElementById", "getElementsByTagName", "indexOf",
		"JavaScript", "javascript", "join", "lastIndexOf", "length", "Math", "name", "navigator", "open", "opener", "options",
		"parent", "parentNode",	"parseInt", "print", "replace", "round", 
		"select", "selectedIndex", "self", "send", "setAttribute", "setRequestHeader", "setTimeout", "split", "status", "style", "submit", "substr", "substring", 
		"title", "toLowerCase", "toUpperCase", "TRUE", "type", "unescape", "window", "windows", "write", "writeln"
	};
	
	private static HashSet<String> javascriptKeywords = new HashSet<String>(Arrays.asList(keywords));
	
	public boolean isJavascriptKeyword(String name) {
		return javascriptKeywords.contains(name);
	}
	
	/*
	 * Classes used for detecting data flows
	 */
	
	private static class JsVariable {
		
	}
	
	private static class Env {
		
		private Env outerScopeEnv;
		
		private JsFunctionDecl currentFunction;
		
		private HashMap<String, JsVariable> variableTable;	// Specific to each Env
		
		private HashMap<JsVariable, HashSet<JsVariableDecl>> declMap; // Specific to each Env
		
		private HashMap<ASTNode, JsVariableRef> refMap;

		/**
		 * Constructor
		 */
		public Env() {
			this.outerScopeEnv = null;
			this.currentFunction = null;
			this.variableTable = new HashMap<String, JsVariable>();
			this.declMap = new HashMap<JsVariable, HashSet<JsVariableDecl>>();
			this.refMap = new HashMap<ASTNode, JsVariableRef>();
		}
		
		/**
		 * Constructor
		 * @param outerScopeEnv
		 */
		public Env(Env outerScopeEnv) {
			this.outerScopeEnv = outerScopeEnv;
			this.currentFunction = outerScopeEnv.currentFunction;
			this.variableTable = new HashMap<String, JsVariable>();
			this.declMap = new HashMap<JsVariable, HashSet<JsVariableDecl>>();
			this.refMap = outerScopeEnv.refMap;
		}
		
		/*
		 * MANAGE FUNCTIONS
		 */
		
		public void setCurrentFunction(JsFunctionDecl currentFunction) {
			this.currentFunction = currentFunction;
		}
		
		public JsFunctionDecl getCurrentFunction() {
			return currentFunction;
		}
		
		/*
		 * MANAGE VARIABLES
		 */
		
		/**
		 * Creates a variable
		 */
		public JsVariable createVariable(String name) {
			return new JsVariable(); // name doesn't need to be used
		}
		
		/**
		 * Puts a variable in the CURRENT scope.
		 */
		private void putVariableInCurrentScope(String variableName, JsVariable jsVariable) {
			variableTable.put(variableName, jsVariable);
		}

		/**
		 * Gets a variable from the CURRENT scope.
		 */
		private JsVariable getVariableFromCurrentScope(String variableName) {
			return variableTable.get(variableName);
		}
		
		/**
		 * Puts a variable to the env.
		 */
		public void putVariable(String name, JsVariable jsVariable) {
			putVariableInCurrentScope(name, jsVariable);
		}
		
		/**
		 * Gets a variable from the env.
		 */
		public JsVariable getVariable(String name) {
			Env env = this;
			while (env != null) {
				JsVariable jsVariable = getVariableFromCurrentScope(name);
				if (jsVariable != null)
					return jsVariable;
				env = env.outerScopeEnv;
			}
			return null;
		}
		
		/**
		 * Get a variable or put one if it doesn't already exist.
		 */
		public JsVariable getOrPutVariable(String name) {
			JsVariable jsVariable = getVariable(name);
			if (jsVariable == null) {
				jsVariable = createVariable(name);
				putVariable(name, jsVariable);
			}
			return jsVariable;
		}
		
		/*
		 * MANAGE DECLS
		 */
		
		public void putVariableDecls(JsVariable jsVariable, JsVariableDecl jsVariableDecl) {
			declMap.put(jsVariable, new HashSet<JsVariableDecl>());
			declMap.get(jsVariable).add(jsVariableDecl);
		}
		
		public void putVariableDecls(JsVariable jsVariable, HashSet<JsVariableDecl> jsVariableDecls) {
			declMap.put(jsVariable, jsVariableDecls);
		}
		
		public HashSet<JsVariableDecl> getVariableDecls(JsVariable jsVariable) {
			Env env = this;
			while (env != null) {
				if (declMap.containsKey(jsVariable))
					return new HashSet<JsVariableDecl>(declMap.get(jsVariable));
				env = env.outerScopeEnv;
			}
			return new HashSet<JsVariableDecl>();
		}
		
		/*
		 * MANAGE REFS
		 */
		
		public void putVariableRef(ASTNode astNode, JsVariableRef jsVariableRef) {
			refMap.put(astNode, jsVariableRef);
		}
		
		/**
		 * Returns the JsVariableRef created at this AST node.
		 */
		public JsVariableRef getVariableRef(ASTNode astNode) {
			return refMap.get(astNode);
		}
		
		/**
		 * Returns JsVariableRefs created not just at but also under this AST node.
		 */
		public HashSet<JsVariableRef> getVariableRefs(ASTNode astNode) {
			final HashSet<JsVariableRef> jsVariableRefs = new HashSet<JsVariableRef>();
			
			astNode.accept(new ASTVisitor() {
				
				@Override
				public boolean visit(SimpleName simpleName) {
					JsVariableRef jsVariableRef = getVariableRef(simpleName);
					if (jsVariableRef != null)
						jsVariableRefs.add(jsVariableRef);
					
					return true;
				}
				
				@Override
				public boolean visit(FieldAccess fieldAccess) {
					JsVariableRef jsVariableRef = getVariableRef(fieldAccess);
					if (jsVariableRef != null)
						jsVariableRefs.add(jsVariableRef);
					
					return true;
				}
			});
			
			return jsVariableRefs;
		}
		
		/*
		 * MANAGE EXECUTION 
		 */
		
		/**
		 * Updates env after visiting two branches env1 and env2
		 */
		public void updateAfterBranchExecution(Env env1, Env env2) {
			HashSet<String> variableNames = new HashSet<String>();
			variableNames.addAll(env1.variableTable.keySet());
			variableNames.addAll(env2.variableTable.keySet());
			
			for (String variableName : variableNames) {
				// Update variableTable
				JsVariable jsVariable = this.getOrPutVariable(variableName);
				
				/*
				 * Record data flows
				 */
				HashSet<JsVariableDecl> jsVariableDecls = new HashSet<JsVariableDecl>();
				
				JsVariable jsVariable1 = env1.getVariable(variableName);
				if (jsVariable1 != null)
					jsVariableDecls.addAll(env1.getVariableDecls(jsVariable1));
				
				JsVariable jsVariable2 = env2.getVariable(variableName);
				if (jsVariable2 != null)
					jsVariableDecls.addAll(env2.getVariableDecls(jsVariable2));
				
				this.putVariableDecls(jsVariable, jsVariableDecls);
			}
		}
		
	}
	
}
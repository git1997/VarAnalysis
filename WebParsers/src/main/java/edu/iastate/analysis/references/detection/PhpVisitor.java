package edu.iastate.analysis.references.detection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.ArrayAccess;
import org.eclipse.php.internal.core.ast.nodes.Assignment;
import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.FormalParameter;
import org.eclipse.php.internal.core.ast.nodes.FunctionDeclaration;
import org.eclipse.php.internal.core.ast.nodes.FunctionInvocation;
import org.eclipse.php.internal.core.ast.nodes.Identifier;
import org.eclipse.php.internal.core.ast.nodes.ReturnStatement;
import org.eclipse.php.internal.core.ast.nodes.Variable;
import org.eclipse.php.internal.core.ast.visitor.AbstractVisitor;
import edu.iastate.analysis.references.DeclaringReference;
import edu.iastate.analysis.references.PhpFunctionCall;
import edu.iastate.analysis.references.PhpFunctionDecl;
import edu.iastate.analysis.references.PhpRefToHtml;
import edu.iastate.analysis.references.PhpRefToSqlTableColumn;
import edu.iastate.analysis.references.PhpVariableDecl;
import edu.iastate.analysis.references.PhpVariableRef;
import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.RegularReference;
import edu.iastate.analysis.references.SqlTableColumnDecl;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.ArrayNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.instrumentation.WebAnalysis;
import edu.iastate.symex.php.nodes.ArrayAccessNode;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;
import edu.iastate.symex.position.RelativeRange;
import edu.iastate.symex.util.ASTHelper;

/**
 * PhpVisitor visits PHP elements and detects entities.
 *  
 * @author HUNG
 *
 */
public class PhpVisitor implements WebAnalysis.IListener {
	
	private File entryFile;
	private ReferenceManager referenceManager;

	/*
	 * Used to construct data flow
	 */
	private DataFlowManager dataFlowManager;
	private HelperEnv helperEnv;
	
	/**
	 * Constructor
	 */
	public PhpVisitor(File entryFile, ReferenceManager referenceManager) {
		this.entryFile = entryFile;
		this.referenceManager = referenceManager;
		
		this.dataFlowManager = referenceManager.getDataFlowManager();
		this.helperEnv = new HelperEnv();
	}
	
	/**
	 * Adds a reference.
	 * This method should be called instead of calling referenceManager.addReference directly.
	 */
	private void addReference(Reference reference, ASTNode astNode, Env env) {
		reference.setEntryFile(entryFile);
		reference.setConstraint(env.getConjunctedConstraintUpToGlobalEnvScope());
		referenceManager.addReference(reference);
		
		/*
		 * Record data flows
		 */
		if (reference instanceof RegularReference) {
			helperEnv.putReference(astNode, (RegularReference) reference);
		}
	}
	
	/*
	 * Handle variables
	 */
	
	@Override
	public void onAssignmentExecute(Assignment assignment, PhpVariable phpVariable, Env env) {
		if (assignment.getLeftHandSide().getType() == ASTNode.VARIABLE) {
			Variable variable = (Variable) assignment.getLeftHandSide();
			String variableName = (variable.getName() instanceof Identifier ? ((Identifier) variable.getName()).getName() : null);
			
			// Found a PhpVariableDecl
			if (variableName != null && !isSpecialVariable(variableName))
				foundVariableDecl(variable, variableName, phpVariable, assignment.getRightHandSide(), env);
		}
		else if (assignment.getLeftHandSide().getType() == ASTNode.ARRAY_ACCESS) {
			// Handle ArrayAccess here
		}
	}
	
	@Override
	public void onVariableExecute(Variable variable, PhpVariable phpVariable, Env env) {
		String variableName = (variable.getName() instanceof Identifier ? ((Identifier) variable.getName()).getName() : null);

		// Found a PhpVariableRef
		if (variableName != null && !isSpecialVariable(variableName))
			foundVariableRef(variable, variableName, phpVariable, env);
	}
	
	private void foundVariableDecl(Variable variable, String variableName, PhpVariable phpVariable, Expression rightHandSide, Env env) {
		// Add a PhpVariableDecl
		PhpVariableDecl phpVariableDecl = new PhpVariableDecl(variableName, getLocation(variable));
		addReference(phpVariableDecl, variable, env);
		
		/*
		 * Record data flows
		 */
		helperEnv.putVariableDecls(phpVariable, phpVariableDecl);
		HashSet<RegularReference> references = helperEnv.getReferencesUnderExceptArguments(rightHandSide);
		dataFlowManager.addDataFlow(references, phpVariableDecl);
	}
	
	private void foundVariableRef(Variable variable, String variableName, PhpVariable phpVariable, Env env) {
		// Add a PhpVariableRef
		PhpVariableRef phpVariableRef = new PhpVariableRef(variableName, getLocation(variable));
		addReference(phpVariableRef, variable, env);
		
		/*
		 * Record data flows
		 */
		if (phpVariable != null) {
			HashSet<PhpVariableDecl> phpVariableDecls = helperEnv.getVariableDecls(phpVariable);
			dataFlowManager.addDataFlow(new HashSet<DeclaringReference>(phpVariableDecls), phpVariableRef);
		}
	}
	
	/*
	 * Handle array access
	 */
	
	@Override
	public void onArrayAccessExecute(ArrayAccess arrayAccess, ArrayAccessNode arrayAccessNode, DataNode arrayNode, DataNode keyNode, Env env) {
		/*
		 * Detect PHP request variables, e.g. $_GET['input1']
		 */
		String arrayVariableName = getArrayVariableNameOrNull(arrayAccess);
		if (arrayVariableName != null && isRequestVariable(arrayVariableName) && keyNode instanceof LiteralNode) {
			LiteralNode key = (LiteralNode) keyNode;
			
			// Add a PhpRefToHtml
			PhpRefToHtml phpRefToHtml = new PhpRefToHtml(key.getStringValue(), key.getLocation());
			addReference(phpRefToHtml, arrayAccess, env);
		}
			
		/*
		 * Detect SQL column access, e.g. $row['name']
		 */
		else if (arrayNode instanceof ArrayNode && keyNode instanceof LiteralNode) {
			ArrayNode array = (ArrayNode) arrayNode;
			LiteralNode key = (LiteralNode) keyNode;
			DataNode elementValue = array.getElementValue(key.getStringValue());
		
			if (elementValue instanceof SymbolicNode) {
				SqlTableColumnDecl sqlTableColumnDecl = helperEnv.getSqlTableColumnDecl((SymbolicNode) elementValue);
				
				if (sqlTableColumnDecl != null) {
					// Add a PhpRefToSqlTableColumn
					PhpRefToSqlTableColumn phpRefToSqlTableColumn = new PhpRefToSqlTableColumn(key.getStringValue(), key.getLocation());
					addReference(phpRefToSqlTableColumn, arrayAccess, env);
					
					/*
					 * Record data flows
					 */
					dataFlowManager.addDataFlow(sqlTableColumnDecl, phpRefToSqlTableColumn);
				}
			}
		}
	}
	
	/*
	 * Handle functions & return statements
	 */
	
	@Override
	public void onFunctionDeclarationExecute(FunctionDeclaration functionDeclaration, Env env) {
		String functionName = functionDeclaration.getFunctionName().getName();
		helperEnv.putFunction(functionName, functionDeclaration);
	}

	@Override
	public void onFunctionInvocationExecute(FunctionInvocation functionInvocation, Env env) {
		Expression functionInvocationNameNode = functionInvocation.getFunctionName().getName();
		String functionName = (functionInvocationNameNode instanceof Identifier ? ((Identifier) functionInvocationNameNode).getName() : null);
		
		if (functionName != null) {
			// Add a PhpFunctionCall
			PhpFunctionCall phpFunctionCall = new PhpFunctionCall(functionName, getLocation(functionInvocationNameNode));
			addReference(phpFunctionCall, functionInvocation, env);
			
			FunctionDeclaration functionDeclaration = helperEnv.getFunction(functionName);
			if (functionDeclaration != null) {
				Identifier functionDeclarationNameNode = functionDeclaration.getFunctionName();

				// Add a PhpFunctionDecl every time a function is called (to address the calling-context problem in program slicing)
				PhpFunctionDecl phpFunctionDecl = new PhpFunctionDecl(functionName, getLocation(functionDeclarationNameNode));
				addReference(phpFunctionDecl, functionDeclaration, env);
				
				/*
				 * Record data flows
				 */
				helperEnv.setCurrentFunction(phpFunctionDecl);
				dataFlowManager.addDataFlow(phpFunctionDecl, phpFunctionCall);
			}
		}
		
		helperEnv = new HelperEnv(helperEnv);
	}
	
	@Override
	public void onFunctionInvocationParameterPassing(FormalParameter parameter, PhpVariable phpVariable, Expression argument, Env env) {
		// Add a PhpVariableDecl
		PhpVariableDecl phpVariableDecl = new PhpVariableDecl(parameter.getParameterNameIdentifier().getName(), getLocation(parameter));
		addReference(phpVariableDecl, parameter, env);
		
		/*
		 * Record data flows
		 */
		helperEnv.putVariableDecls(phpVariable, phpVariableDecl);
		HashSet<RegularReference> references = helperEnv.getReferencesUnderExceptArguments(argument);
		dataFlowManager.addDataFlow(references, phpVariableDecl);
	}
	
	@Override
	public void onReturnStatementExecute(ReturnStatement returnStatement, Env env) {
		/*
		 * Record data flows
		 */
		PhpFunctionDecl phpFunctionDecl = helperEnv.getCurrentFunction();
		if (phpFunctionDecl != null) {
			dataFlowManager.addDataFlow(helperEnv.getReferencesUnderExceptArguments(returnStatement), phpFunctionDecl);
		}
	}
	
	@Override
	public void onFunctionInvocationFinished(HashSet<PhpVariable> nonLocalDirtyVariablesInFunction, Env env) {
		HelperEnv funcEnv = helperEnv;
		helperEnv = helperEnv.getOuterScopeEnv();
		
		/*
		 * Record data flows
		 */
		helperEnv.updateAfterFunctionExecution(nonLocalDirtyVariablesInFunction, funcEnv);
	}
	
	/*
	 * Handle SQL
	 */
	
	@Override
	public DataNode onMysqlQuery(FunctionInvocation functionInvocation, DataNode argumentValue, Env env) {
		Expression functionInvocationNameNode = functionInvocation.getFunctionName().getName();
		String functionName = ((Identifier) functionInvocationNameNode).getName(); // functionName should be not-null here.

		// Add a PhpFunctionCall for mysql_query
		PhpFunctionCall phpFunctionCall = new PhpFunctionCall(functionName, getLocation(functionInvocationNameNode));
		addReference(phpFunctionCall, functionInvocation, env);
					
		if (!(argumentValue instanceof LiteralNode))
			return DataNodeFactory.createSymbolicNode();
		
		// Parse the SQL code
		String sqlCode = ((LiteralNode) argumentValue).getStringValue(); // e.g. SELECT name FROM users
		PositionRange sqlLocation = ((LiteralNode) argumentValue).getLocation(); 
		ArrayList<LiteralNode> sqlTableColumns = extractSqlTableColumns(sqlCode, sqlLocation);
		
		// Create an array to represent the returned resource
		ArrayNode array = DataNodeFactory.createArrayNode();
		for (int i = 0; i < sqlTableColumns.size(); i++) {
			LiteralNode sqlTableColumn = sqlTableColumns.get(i);
			String key1 = Integer.toString(i);
			String key2 = sqlTableColumn.getStringValue();
			SymbolicNode value = DataNodeFactory.createSymbolicNode();
			
			env.getOrPutThenWriteArrayElement(array, key1, value);
			env.getOrPutThenWriteArrayElement(array, key2, value);
			
			// Add SqlTableColumnDecls (e.g. 'name' in mysql_query("SELECT name FROM products"))
			SqlTableColumnDecl sqlTableColumnDecl = new SqlTableColumnDecl(sqlTableColumn.getStringValue(), sqlTableColumn.getLocation());
			addReference(sqlTableColumnDecl, functionInvocation, env); // This might cause functionInvocation to be mapped to one of the declarations only, but it's Okay for now since inside addReference, it disregards DeclaringReference
			
			/*
			 * Record data flows
			 */
			helperEnv.putSqlTableColumnDecl(value, sqlTableColumnDecl);
		}
		return array;
	}
	
	/**
	 * Returns a array of LiteralNode describing the column extracted from the SqlCode.
	 */
	public ArrayList<LiteralNode> extractSqlTableColumns(String sqlCode, PositionRange sqlLocation) {
		ArrayList<LiteralNode> sqlTableColumns = new ArrayList<LiteralNode>();
		
		Pattern p = Pattern.compile("SELECT (\\s*\\w+\\s*,)*(\\s*\\w+\\s*) FROM", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(sqlCode);
		if (m.lookingAt()) {
			String selectPart = m.group().substring("SELECT ".length(), m.group().length() - " FROM".length());
			 
			String[] sqlTableCols = selectPart.split("\\s*,\\s*");
			for (String sqlTableCol : sqlTableCols) {
				int offset = sqlCode.indexOf(sqlTableCol);
				PositionRange location = new RelativeRange(sqlLocation, offset, sqlTableCol.length());
				 
				LiteralNode sqlTableColumn = DataNodeFactory.createLiteralNode(sqlTableCol, location);
				sqlTableColumns.add(sqlTableColumn);
			}
		}
		 
		return sqlTableColumns;
	}

	@Override
	public DataNode onMysqlFetchArray(FunctionInvocation functionInvocation, DataNode argumentValue, Env env) {
		Expression functionInvocationNameNode = functionInvocation.getFunctionName().getName();
		String functionName = ((Identifier) functionInvocationNameNode).getName(); // functionName should be not-null here.

		// Add a PhpFunctionCall for mysql_fetch_array
		PhpFunctionCall phpFunctionCall = new PhpFunctionCall(functionName, getLocation(functionInvocationNameNode));
		addReference(phpFunctionCall, functionInvocation, env);
		
		/*
		 * Propagate the SQL data, as in 
		 * 		mysql_query("SELECT name FROM products");
		 * 		$product = mysql_fetch_array($result);
		 * 		echo $product['name']
		 */
		return argumentValue;
	}

	/*
	 * Handle branches
	 */

	@Override
	public void onTrueBranchExecutionStarted(Env env) {
		HelperEnv trueBranchEnv = new HelperEnv(helperEnv);
		helperEnv.setTrueBranchEnv(trueBranchEnv);
		
		helperEnv = trueBranchEnv;
	}
	
	@Override
	public void onFalseBranchExecutionStarted(Env env) {
		helperEnv = helperEnv.getOuterScopeEnv();
		
		HelperEnv falseBranchEnv = new HelperEnv(helperEnv);
		helperEnv.setFalseBranchEnv(falseBranchEnv);
		
		helperEnv = falseBranchEnv;
	}
	
	@Override
	public void onBothBranchesExecutionFinished(HashSet<PhpVariable> dirtyVariablesInTrueBranch, HashSet<PhpVariable> dirtyVariablesInFalseBranch, Env env) {
		helperEnv = helperEnv.getOuterScopeEnv();
		
		/*
		 * Record data flows
		 */
		helperEnv.updateAfterBranchExecution(dirtyVariablesInTrueBranch, dirtyVariablesInFalseBranch);
	}

	/*
	 * Utility methods
	 */
	
	private PositionRange getLocation(ASTNode astNode) {
		File file = ASTHelper.inst.getSourceFileOfPhpASTNode(astNode);	
		return new Range(file, astNode.getStart(), astNode.getEnd() - astNode.getStart());
	}
	
	/**
	 * Returns the array name of an array access, or null if the array access is not a (simple) variable.  
	 */
	private String getArrayVariableNameOrNull(ArrayAccess arrayAccess) {
		if (arrayAccess.getName() instanceof Variable) {
			Variable arrayVariable = (Variable) arrayAccess.getName();
			
			if (arrayVariable.getName() instanceof Identifier) {
				Identifier identifier = (Identifier) arrayVariable.getName();
				return identifier.getName();
			}
		}
		return null;
	}
	
	private boolean isRequestVariable(String variableName) {
		return variableName.equals("_REQUEST") || variableName.equals("_POST") || variableName.equals("_GET") || variableName.equals("_FILES");
	}
	
	private boolean isSpecialVariable(String variableName) {
		return isRequestVariable(variableName) || variableName.equals("_SESSION");
	}
	
	/*
	 * Classes used for detecting data flows
	 */
	
	private static class HelperEnv {
		
		private HelperEnv outerScopeEnv;
		
		private PhpFunctionDecl currentFunction;
		
		private HashMap<String, FunctionDeclaration> functionMap;
		
		private HashMap<PhpVariable, HashSet<PhpVariableDecl>> declMap; // Specific to each HelperEnv
		
		private HashMap<ASTNode, RegularReference> refMap;
		
		private HashMap<SymbolicNode, SqlTableColumnDecl> sqlMap;
		
		private HelperEnv trueBranchEnv = null, falseBranchEnv = null;
		
		/**
		 * Constructor
		 */
		public HelperEnv() {
			this.outerScopeEnv = null;
			this.currentFunction = null;
			this.functionMap = new HashMap<String, FunctionDeclaration>();
			this.declMap = new HashMap<PhpVariable, HashSet<PhpVariableDecl>>();
			this.refMap = new HashMap<ASTNode, RegularReference>();
			this.sqlMap = new HashMap<SymbolicNode, SqlTableColumnDecl>();
		}
		
		/**
		 * Constructor
		 * @param outerScopeEnv
		 */
		public HelperEnv(HelperEnv outerScopeEnv) {
			this.outerScopeEnv = outerScopeEnv;
			this.currentFunction = outerScopeEnv.currentFunction;
			this.functionMap = outerScopeEnv.functionMap;
			this.declMap = new HashMap<PhpVariable, HashSet<PhpVariableDecl>>();
			this.refMap = outerScopeEnv.refMap;
			this.sqlMap = outerScopeEnv.sqlMap;
		}
		
		/*
		 * MANAGE SCOPES
		 */
		
		public HelperEnv getOuterScopeEnv() {
			return outerScopeEnv;
		}

		/*
		 * MANAGE FUNCTIONS
		 */
		
		public void setCurrentFunction(PhpFunctionDecl currentFunction) {
			this.currentFunction = currentFunction;
		}
		
		public PhpFunctionDecl getCurrentFunction() {
			return currentFunction;
		}
		
		public void putFunction(String functionName, FunctionDeclaration functionDeclaration) {
			functionMap.put(functionName, functionDeclaration);
		}
		
		public FunctionDeclaration getFunction(String functionName) {
			return functionMap.get(functionName);
		}
		
		/*
		 * MANAGE DECLS
		 */
		
		public void putVariableDecls(PhpVariable phpVariable, PhpVariableDecl phpVariableDecl) {
			declMap.put(phpVariable, new HashSet<PhpVariableDecl>());
			declMap.get(phpVariable).add(phpVariableDecl);
		}
		
		public void putVariableDecls(PhpVariable phpVariable, HashSet<PhpVariableDecl> phpVariableDecls) {
			declMap.put(phpVariable, phpVariableDecls);
		}
		
		public HashSet<PhpVariableDecl> getVariableDecls(PhpVariable phpVariable) {
			if (declMap.containsKey(phpVariable))
				return new HashSet<PhpVariableDecl>(declMap.get(phpVariable));
			else if (outerScopeEnv != null)
				return outerScopeEnv.getVariableDecls(phpVariable);
			else
				return new HashSet<PhpVariableDecl>();
		}
		
		/*
		 * MANAGE REFS
		 */
		
		public void putReference(ASTNode astNode, RegularReference reference) {
			refMap.put(astNode, reference);
		}
		
		/**
		 * Returns the RegularReference created at this AST node.
		 */
		public RegularReference getReferenceExactlyAt(ASTNode astNode) {
			return refMap.get(astNode);
		}
		
		/**
		 * Returns RegularReferences created not just at but also under this AST node, except arguments in function calls.
		 */
		public HashSet<RegularReference> getReferencesUnderExceptArguments(ASTNode astNode) {
			final HashSet<RegularReference> references = new HashSet<RegularReference>();
			
			astNode.accept(new AbstractVisitor() {
				
				@Override
				public boolean visit(Variable variable) {
					RegularReference reference = getReferenceExactlyAt(variable);
					if (reference != null)
						references.add(reference);
					
					return true;
				}
				
				@Override
				public boolean visit(ArrayAccess arrayAccess) {
					RegularReference reference = getReferenceExactlyAt(arrayAccess);
					if (reference != null)
						references.add(reference);
					
					return true;
				}
				
				@Override
				public boolean visit(FunctionInvocation functionInvocation) {
					RegularReference reference = getReferenceExactlyAt(functionInvocation);
					if (reference != null)
						references.add(reference);
						
					// [ADHOC CODE] If the function declaration is not found, we assume that the function's return value
					// depends on all input arguments. Therefore, we also return those arguments to record the data flow
					// to the left-hand side of an assignment (e.g., $x = hi($y) then we assume there's a flow from $y to $x).
					if (reference == null || functionMap.get(reference.getName()) == null)
						return true;
					// [END OF ADHOC CODE]
					
					return false;
				}
			});
			
			return references;
		}
		
		/*
		 * MANAGE EXECUTION 
		 */
		
		public void setTrueBranchEnv(HelperEnv trueBranchEnv) {
			this.trueBranchEnv = trueBranchEnv;
		}
		
		public void setFalseBranchEnv(HelperEnv falseBranchEnv) {
			this.falseBranchEnv = falseBranchEnv;
		}
		
		/**
		 * Updates env after visiting two branches trueBranchEnv and falseBranchEnv
		 */
		public void updateAfterBranchExecution(HashSet<PhpVariable> dirtyVariablesInTrueBranch, HashSet<PhpVariable> dirtyVariablesInFalseBranch) {
			HashSet<PhpVariable> dirtyVariables = new HashSet<PhpVariable>();
			dirtyVariables.addAll(dirtyVariablesInTrueBranch);
			dirtyVariables.addAll(dirtyVariablesInFalseBranch);
			
			for (PhpVariable dirtyVariable : dirtyVariables) {
				/*
				 * Record data flows
				 */
				HashSet<PhpVariableDecl> phpVariableDecls = new HashSet<PhpVariableDecl>();
				phpVariableDecls.addAll(trueBranchEnv.getVariableDecls(dirtyVariable));
				phpVariableDecls.addAll(falseBranchEnv.getVariableDecls(dirtyVariable));
				
				this.putVariableDecls(dirtyVariable, phpVariableDecls);
			}
		}
		
		/**
		 * Updates env after executing a function
		 */
		public void updateAfterFunctionExecution(HashSet<PhpVariable> nonLocalDirtyVariablesInFunction, HelperEnv funcEnv) {
			for (PhpVariable dirtyVariable : nonLocalDirtyVariablesInFunction) {
				/*
				 * Record data flows
				 */
				this.putVariableDecls(dirtyVariable, funcEnv.getVariableDecls(dirtyVariable));
			}
		}
		
		/*
		 * MANAGE SQL
		 */
		
		public void putSqlTableColumnDecl(SymbolicNode symbolicNode, SqlTableColumnDecl sqlTableColumnDecl) {
			sqlMap.put(symbolicNode, sqlTableColumnDecl);
		}
		
		public SqlTableColumnDecl getSqlTableColumnDecl(SymbolicNode symbolicNode) {
			return sqlMap.get(symbolicNode);
		}
		
	}
		
}

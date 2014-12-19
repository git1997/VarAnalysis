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
import edu.iastate.symex.analysis.WebAnalysis.IEntityDetectionListener;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.ArrayNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
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
public class PhpVisitor implements IEntityDetectionListener {
	
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
	private void addReference(Reference reference, Env env) {
		reference.setEntryFile(entryFile);
		reference.setConstraint(env.getConjunctedConstraintUpToPhpEnvScope());
		referenceManager.addReference(reference);
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
		addReference(phpVariableDecl, env);
		
		/*
		 * Record data flows
		 */
		helperEnv.putVariableDecls(phpVariable, phpVariableDecl);
		HashSet<PhpVariableRef> phpVariableRefs = helperEnv.getVariableRefs(rightHandSide);
		dataFlowManager.addDataFlow(new HashSet<RegularReference>(phpVariableRefs), phpVariableDecl);
	}
	
	private void foundVariableRef(Variable variableNode, String variableName, PhpVariable phpVariable, Env env) {
		// Add a PhpVariableRef
		PhpVariableRef phpVariableRef = new PhpVariableRef(variableName, getLocation(variableNode));
		addReference(phpVariableRef, env);
		
		/*
		 * Record data flows
		 */
		helperEnv.putVariableRef(variableNode, phpVariableRef);
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
			addReference(phpRefToHtml, env);
		}
			
		/*
		 * Detect SQL column access, e.g. $row['name']
		 */
		if (arrayNode instanceof ArrayNode && keyNode instanceof LiteralNode) {
			ArrayNode array = (ArrayNode) arrayNode;
			LiteralNode key = (LiteralNode) keyNode;
			DataNode elementValue = array.getElementValue(key.getStringValue());
		
			if (elementValue instanceof SymbolicNode) {
				SqlTableColumnDecl sqlTableColumnDecl = helperEnv.getSqlTableColumnDecl((SymbolicNode) elementValue);
				
				if (sqlTableColumnDecl != null) {
					// Add a PhpRefToSqlTableColumn
					PhpRefToSqlTableColumn phpRefToSqlTableColumn = new PhpRefToSqlTableColumn(key.getStringValue(), key.getLocation());
					addReference(phpRefToSqlTableColumn, env);
					
					/*
					 * Record data flows
					 */
					dataFlowManager.addDataFlow(sqlTableColumnDecl, phpRefToSqlTableColumn);
				}
			}
		}
	}
	
	/*
	 * Handle SQL
	 */
	
	@Override
	public DataNode onMysqlQuery(FunctionInvocation functionInvocation, DataNode argumentValue, Env env) {
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
			addReference(sqlTableColumnDecl, env);
			
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
		/*
		 * Propagate the SQL data, as in 
		 * 		mysql_query("SELECT name FROM products");
		 * 		$product = mysql_fetch_array($result);
		 * 		echo $product['name']
		 */
		return argumentValue;
	}
	
	/*
	 * Handle functions & return statements
	 */
	
	@Override
	public void onFunctionDeclarationExecute(FunctionDeclaration functionDeclaration, Env env) {
		Identifier functionNameNode = functionDeclaration.getFunctionName();
		String functionName = functionNameNode.getName();

		// Add a PhpFunctionDecl reference
		PhpFunctionDecl phpFunctionDecl = new PhpFunctionDecl(functionName, getLocation(functionNameNode));
		addReference(phpFunctionDecl, env);
		
		helperEnv.putFunction(functionName, phpFunctionDecl);
	}

	@Override
	public void onFunctionInvocationExecute(FunctionInvocation functionInvocation, Env env) {
		Expression functionNameNode = functionInvocation.getFunctionName().getName();
		String functionName = (functionNameNode instanceof Identifier ? ((Identifier) functionNameNode).getName() : "");
		
		if (!functionName.isEmpty()) {
			// Add a PhpFunctionCall reference
			PhpFunctionCall phpFunctionCall = new PhpFunctionCall(functionName, getLocation(functionNameNode));
			addReference(phpFunctionCall, env);
			
			/*
			 * Record data flows
			 */
			PhpFunctionDecl phpFunctionDecl = helperEnv.getFunction(functionName);
			if (phpFunctionDecl != null) {
				dataFlowManager.addDataFlow(phpFunctionDecl, phpFunctionCall);
			}
		}
	}
	
	@Override
	public void onReturnStatementExecute(ReturnStatement returnStatement, Env env) {
		/*
		 * Record data flows
		 */
		String functionName = env.peekFunctionFromStack();
		PhpFunctionDecl phpFunctionDecl = (functionName != null ? helperEnv.getFunction(functionName) : null);
		if (phpFunctionDecl != null) {
			dataFlowManager.addDataFlow(new HashSet<RegularReference>(helperEnv.getVariableRefs(returnStatement)), phpFunctionDecl);
		}
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
		
		private HashMap<String, PhpFunctionDecl> functionMap;
		
		private HashMap<PhpVariable, HashSet<PhpVariableDecl>> declMap; // Specific to each HelperEnv
		
		private HashMap<Variable, PhpVariableRef> refMap;
		
		private HashMap<SymbolicNode, SqlTableColumnDecl> sqlMap;
		
		private HelperEnv trueBranchEnv = null, falseBranchEnv = null;
		
		/**
		 * Constructor
		 */
		public HelperEnv() {
			this.outerScopeEnv = null;
			this.functionMap = new HashMap<String, PhpFunctionDecl>();
			this.declMap = new HashMap<PhpVariable, HashSet<PhpVariableDecl>>();
			this.refMap = new HashMap<Variable, PhpVariableRef>();
			this.sqlMap = new HashMap<SymbolicNode, SqlTableColumnDecl>();
		}
		
		/**
		 * Constructor
		 * @param outerScopeEnv
		 */
		public HelperEnv(HelperEnv outerScopeEnv) {
			this.outerScopeEnv = outerScopeEnv;
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
		
		public void putFunction(String functionName, PhpFunctionDecl phpFunctionDecl) {
			functionMap.put(functionName, phpFunctionDecl);
		}
		
		public PhpFunctionDecl getFunction(String functionName) {
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
		
		public void putVariableRef(Variable variable, PhpVariableRef phpVariableRef) {
			refMap.put(variable, phpVariableRef);
		}
		
		/**
		 * Returns the PhpVariableRef created at this AST node.
		 */
		public PhpVariableRef getVariableRef(Variable variable) {
			return refMap.get(variable);
		}
		
		/**
		 * Returns PhpVariableRefs created not just at but also under this AST node.
		 */
		public HashSet<PhpVariableRef> getVariableRefs(ASTNode astNode) {
			final HashSet<PhpVariableRef> phpVariableRefs = new HashSet<PhpVariableRef>();
			
			astNode.accept(new AbstractVisitor() {
				
				@Override
				public boolean visit(Variable variable) {
					PhpVariableRef phpVariableRef = getVariableRef(variable);
					if (phpVariableRef != null)
						phpVariableRefs.add(phpVariableRef);
					
					return true;
				}
			});
			
			return phpVariableRefs;
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

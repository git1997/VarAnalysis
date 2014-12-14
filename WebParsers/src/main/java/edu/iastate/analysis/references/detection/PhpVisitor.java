package edu.iastate.analysis.references.detection;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

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
import edu.iastate.analysis.references.ReferenceManager;
import edu.iastate.symex.analysis.WebAnalysis.IEntityDetectionListener;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.php.nodes.ExpressionNode;
import edu.iastate.symex.php.nodes.FunctionInvocationNode;
import edu.iastate.symex.php.nodes.VariableNode;
import edu.iastate.symex.position.CompositeRange;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;
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

	/**
	 * Used to detect data flows
	 */
	private HashMap<PhpVariable, HashSet<PhpVariableDecl>> variableTable = new HashMap<PhpVariable, HashSet<PhpVariableDecl>>();
	
	/**
	 * Constructor
	 */
	public PhpVisitor(File entryFile, ReferenceManager referenceManager) {
		this.entryFile = entryFile;
		this.referenceManager = referenceManager;
		
		this.variableTable = new HashMap<PhpVariable, HashSet<PhpVariableDecl>>();
	}
	
	/**
	 * Adds a reference.
	 * This method should be called instead of calling referenceManager.addReference directly.
	 */
	private void addReference(Reference reference, Env env) {
		reference.setConstraint(env.getConjunctedConstraintUpToPhpEnvScope());
		reference.setEntryFile(entryFile);
		referenceManager.addReference(reference);
	}
	
	@Override
	public void onAssignmentExecute(Assignment assignment, PhpVariable phpVariable, Env env) {
		if (!(assignment.getLeftHandSide() instanceof Variable))
			return;
		
		/*
		 * Detect PHP variable declarations, e.g. $x = 1
		 */
		Variable variableDecl = (Variable) assignment.getLeftHandSide();
		PhpVariableDecl phpVariableDecl = (PhpVariableDecl) createVariable(variableDecl, true, env);
		if (phpVariableDecl != null)
			addReference(phpVariableDecl, env);
		
		/*
		 * Record data flows
		 */
		if (phpVariableDecl != null) {
			HashSet<PhpVariableDecl> decls = new HashSet<PhpVariableDecl>();
			decls.add(phpVariableDecl);
			variableTable.put(phpVariable, decls);
		
			referenceManager.getDataFlowManager().putMapDeclToRefLocations(phpVariableDecl, getLocation(assignment.getRightHandSide()));
		}
	}
	
	@Override
	public void onVariableExecute(Variable variable, Env env) {
		/*
		 * Detect PHP variables, e.g. $x
		 */
		PhpVariableRef phpVariableRef = (PhpVariableRef) createVariable(variable, false, env);
		if (phpVariableRef != null)
			addReference(phpVariableRef, env);
		
		/*
		 * Record data flows
		 */
		if (phpVariableRef != null) {
			PhpVariable phpVariable = env.getVariable(phpVariableRef.getName());
			HashSet<DeclaringReference> decls = new HashSet<DeclaringReference>();
			if (variableTable.containsKey(phpVariable))
				decls.addAll(variableTable.get(phpVariable));
			referenceManager.getDataFlowManager().putMapRefToDecls(phpVariableRef, decls);
		}
	}
	
	private Reference createVariable(Variable variable, boolean isDeclared, Env env) {
		if (!(variable.getName() instanceof Identifier))
			return null;
		
		String variableName =  ((Identifier) variable.getName()).getName();
		if (isSpecialVariable(variableName)) // Ignore special variables
			return null;
		
		String name = variableName;
		Range location = new VariableNode(variable).getLocation();
		String scope;
		if (env.getFunctionStack().isEmpty() || env.getGlobalVariables().contains(name))
			scope = "GLOBAL_SCOPE";
		else
			scope = "FUNCTION_SCOPE_" + env.peekFunctionFromStack();
			
		Reference phpVariable = isDeclared ? new PhpVariableDecl(name, location, scope) : new PhpVariableRef(name, location, scope);
		
		return phpVariable;
	}

	@Override
	public void onArrayAccessExecute(ArrayAccess arrayAccess, Env env) {
		/*
		 * Detect array variables, e.g. $x[1]
		 */
		if (arrayAccess.getName() instanceof Variable)
			onVariableExecute((Variable) arrayAccess.getName(), env);
		
		/*
		 * Detect PHP request variables, e.g. $_GET['input1']
		 */
		PhpRefToHtml phpRefToHtml = createPhpRefToHtml(arrayAccess, env);
		if (phpRefToHtml != null)
			addReference(phpRefToHtml, env);
			
		/*
		 * Detect SQL column access, e.g. $row['name']
		 */
		PhpRefToSqlTableColumn phpRefToSql = createPhpRefToSqlTableColumn(arrayAccess, env);
		if (phpRefToSql != null)
			addReference(phpRefToSql, env);
	}
	
	private PhpRefToHtml createPhpRefToHtml(ArrayAccess arrayAccess, Env env) {
		String arrayVariableName = getArrayVariableNameOrNull(arrayAccess);
		
		if (arrayVariableName != null && isRequestVariable(arrayVariableName)) {
			LiteralNode arrayIndex = getArrayIndexOrNull(arrayAccess, env);
			
			if (arrayIndex != null) {
				String name = arrayIndex.getStringValue();
				PositionRange location = arrayIndex.getLocation();
				return new PhpRefToHtml(name, location);
			}
		}
		return null;
	}
	
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
	
	private LiteralNode getArrayIndexOrNull(ArrayAccess arrayAccess, Env env) {
		DataNode resolvedIndex = ExpressionNode.createInstance(arrayAccess.getIndex()).execute(env);
		if (resolvedIndex instanceof LiteralNode)
			return (LiteralNode) resolvedIndex;
		else
			return null;
	}

	@Override
	public DataNode onMysqlQuery(FunctionInvocation functionInvocation, DataNode argumentValue, Env env) {
		/*
		 * Detect SqlTableColumn declarations, e.g. 'name' in mysql_query("SELECT name FROM products");
		 */
		String scope = "mysql_query_" + functionInvocation.hashCode();
		Range location = new FunctionInvocationNode(functionInvocation).getLocation();
		LiteralNode retValue = DataNodeFactory.createLiteralNode(location, scope);
		
		DataNode dataNode = argumentValue;
		while (dataNode instanceof ConcatNode && !((ConcatNode) dataNode).getChildNodes().isEmpty()) {
			dataNode = ((ConcatNode) dataNode).getChildNodes().get(0); 
		}
		
		if (!(dataNode instanceof LiteralNode))
			return retValue;
		
		String sqlCode = ((LiteralNode) dataNode).getStringValue(); // e.g. SELECT name FROM users
		PositionRange sqlLocation = ((LiteralNode) dataNode).getLocation(); 
		
		ReferenceDetector.findReferencesInSqlCode(sqlCode, sqlLocation, scope, entryFile, referenceManager);
		return retValue;
	}

	@Override
	public DataNode onMysqlFetchArray(FunctionInvocation functionInvocation, DataNode argumentValue, Env env) {
		/*
		 * Used to identify the propagation of SQL table columns, as in 
		 * 		mysql_query("SELECT name FROM products");
		 * 		$product = mysql_fetch_array($result);
		 * 		echo $product['name']
		 */
		return argumentValue;
	}
	
	private PhpRefToSqlTableColumn createPhpRefToSqlTableColumn(ArrayAccess arrayAccess, Env env) {
		String arrayVariableName = getArrayVariableNameOrNull(arrayAccess);
		if (arrayVariableName != null) {
			PhpVariable phpVariable = env.getVariable(arrayVariableName);
			
			if (phpVariable != null) {
				String variableValue = phpVariable.getValue().getExactStringValueOrNull();
				
				if (variableValue != null && variableValue.startsWith("mysql_query_")) {
					LiteralNode arrayIndex = getArrayIndexOrNull(arrayAccess, env);
					
					if (arrayIndex != null) {
						String name = arrayIndex.getStringValue();
						PositionRange location = arrayIndex.getLocation();
						String scope = variableValue;
						
						if (!name.matches("[0-9]+")) // Ignore numbers, e.g. $sql_row[1]
							return new PhpRefToSqlTableColumn(name, location, scope);
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public void onFunctionDeclarationExecute(FunctionDeclaration functionDeclaration, Env env) {
		/*
		 * Detect function declaration
		 */
		final PhpFunctionDecl phpFunctionDecl = new PhpFunctionDecl(functionDeclaration.getFunctionName().getName(), getLocation(functionDeclaration.getFunctionName()));
		addReference(phpFunctionDecl, env);
		
		/*
		 * Record data flows
		 */
		functionDeclaration.accept(new AbstractVisitor() {
			
			@Override
			public boolean visit(ReturnStatement returnStatement) {
				if (returnStatement.getExpression() != null) {
					PositionRange range1 = referenceManager.getDataFlowManager().getRefLocationsOfDecl(phpFunctionDecl);
					PositionRange range2 = getLocation(returnStatement.getExpression());
					PositionRange newRange = range1 != null ? new CompositeRange(range1, range2) : range2;
				
					referenceManager.getDataFlowManager().putMapDeclToRefLocations(phpFunctionDecl, newRange);
				}
				return false;
			}
		});
	}

	@Override
	public void onFunctionInvocationExecute(FunctionInvocation functionInvocation, Env env) {
		/*
		 * Detect function call
		 */
		Expression name = functionInvocation.getFunctionName().getName();
		if (name instanceof Identifier) {
			Reference reference = new PhpFunctionCall(((Identifier) name).getName(), getLocation(name));
			addReference(reference, env);
		}
	}

	/*
	 * Handle branches
	 */

	@Override
	public void onEnvUpdateWithBranches(PhpVariable phpVariable, PhpVariable phpVariableInTrueBranch, PhpVariable phpVariableInFalseBranch) {
		variableTable.put(phpVariable, new HashSet<PhpVariableDecl>());
		if (variableTable.containsKey(phpVariableInTrueBranch))
			variableTable.get(phpVariable).addAll(variableTable.get(phpVariableInTrueBranch));
		if (variableTable.containsKey(phpVariableInFalseBranch))
			variableTable.get(phpVariable).addAll(variableTable.get(phpVariableInFalseBranch));
	}
	
	/*
	 * Utility methods
	 */
	
	private PositionRange getLocation(ASTNode astNode) {
		File file = ASTHelper.inst.getSourceFileOfPhpASTNode(astNode);	
		return new Range(file, astNode.getStart(), astNode.getEnd() - astNode.getStart());
	}
	
	private boolean isRequestVariable(String variableName) {
		return variableName.equals("_REQUEST") || variableName.equals("_POST") || variableName.equals("_GET") || variableName.equals("_FILES");
	}
	
	private boolean isSpecialVariable(String variableName) {
		return isRequestVariable(variableName) || variableName.equals("_SESSION");
	}
		
}

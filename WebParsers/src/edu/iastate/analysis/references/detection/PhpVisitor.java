package edu.iastate.analysis.references.detection;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.php.internal.core.ast.nodes.ArrayAccess;
import org.eclipse.php.internal.core.ast.nodes.Assignment;
import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.FunctionInvocation;
import org.eclipse.php.internal.core.ast.nodes.Identifier;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.php.internal.core.ast.nodes.Scalar;
import org.eclipse.php.internal.core.ast.nodes.Variable;
import org.eclipse.php.internal.core.ast.visitor.AbstractVisitor;

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
import edu.iastate.symex.php.nodes.FunctionInvocationNode;
import edu.iastate.symex.php.nodes.ScalarNode;
import edu.iastate.symex.php.nodes.VariableNode;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;

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
	 * These maps are used to record data flows
	 */
	
	private HashMap<Variable, Reference> mapVariableToReference = new HashMap<Variable, Reference>();
	
	private HashMap<Variable, HashSet<Variable>> mapDataflowLeftToRight = new HashMap<Variable, HashSet<Variable>>();
	private HashMap<PhpVariable, HashSet<PhpVariableDecl>> mapDataflowBottomToTop = new HashMap<PhpVariable, HashSet<PhpVariableDecl>>();
	
	/**
	 * Constructor
	 */
	public PhpVisitor(File entryFile, ReferenceManager referenceManager) {
		this.entryFile = entryFile;
		this.referenceManager = referenceManager;
	}
	
	/**
	 * Adds a reference.
	 * This method should be called instead of calling referenceManager.addReference directly.
	 */
	private void addReference(Reference reference) {
		reference.setEntryFile(entryFile);
		referenceManager.addReference(reference);
	}
	
	@Override
	public void onProgramExecute(Program program) {
		program.accept(new AbstractVisitor() {
			
			@Override
			public boolean visit(Variable variable) {
				if (variable.getParent() instanceof Assignment 
						&& ((Assignment) variable.getParent()).getLeftHandSide() == variable) {
					recordDataflow(variable, ((Assignment) variable.getParent()).getRightHandSide());
				}
				return true;
			}
			
			@Override
			public boolean visit(ArrayAccess arrayAccess) {
				if (arrayAccess.getParent() instanceof Assignment
						&& ((Assignment) arrayAccess.getParent()).getLeftHandSide() == arrayAccess) {
					recordDataflow(arrayAccess, ((Assignment) arrayAccess.getParent()).getRightHandSide());
				}
				return true;
			}
		});
	}
	
	private void recordDataflow(final Variable variableDecl, Expression rightHandSide) {
		mapDataflowLeftToRight.put(variableDecl, new HashSet<Variable>());
		rightHandSide.accept(new AbstractVisitor() {
			@Override
			public boolean visit(Variable variable) {
				mapDataflowLeftToRight.get(variableDecl).add(variable);
				return true;
			}
			@Override
			public boolean visit(ArrayAccess arrayAccess) {
				mapDataflowLeftToRight.get(variableDecl).add(arrayAccess);
				return true;
			}
		});
	}
	
	@Override
	public void onAssignmentExecute(Assignment assignment, PhpVariable phpVariable, Env env) {
		if (!(assignment.getLeftHandSide() instanceof Variable))
			return;
		Variable variableDecl = (Variable) assignment.getLeftHandSide();
		
		/*
		 * Detect PHP variable declarations, e.g. $x = 1
		 */
		PhpVariableDecl phpVariableDecl = (PhpVariableDecl) createVariable(variableDecl, true, env);
		if (phpVariableDecl != null) {
			addReference(phpVariableDecl);
			mapVariableToReference.put(variableDecl, phpVariableDecl);
		}
		
		/*
		 * Record data flow
		 */
		if (phpVariableDecl != null && mapDataflowLeftToRight.containsKey(variableDecl)) {
			for (Variable variableRef : mapDataflowLeftToRight.get(variableDecl)) {
				Reference phpVariableRef =  mapVariableToReference.get(variableRef);
				if (phpVariableRef != null)
					phpVariableDecl.addDataflowFromReference(phpVariableRef);
			}
		}
		
		if (phpVariableDecl != null) {
			mapDataflowBottomToTop.put(phpVariable, new HashSet<PhpVariableDecl>());
			mapDataflowBottomToTop.get(phpVariable).add(phpVariableDecl);
		}
	}
	
	@Override
	public void onVariableExecute(Variable variable, Env env) {
		/*
		 * Detect PHP variables, e.g. $x
		 */
		PhpVariableRef phpVariableRef = (PhpVariableRef) createVariable(variable, false, env);
		if (phpVariableRef != null) {
			addReference(phpVariableRef);
			mapVariableToReference.put(variable, phpVariableRef);
		}
		
		/*
		 * Record data flow
		 */
		if (phpVariableRef != null) {
			PhpVariable phpVariable = env.readVariable(phpVariableRef.getName());
			if (mapDataflowBottomToTop.containsKey(phpVariable))
				for (PhpVariableDecl phpVariableDecl : mapDataflowBottomToTop.get(phpVariable))
						phpVariableRef.addDataflowFromReference(phpVariableDecl);
		}
	}
	
	private Reference createVariable(Variable variable, boolean isDeclared, Env env) {
		if (!(variable.getName() instanceof Identifier))
			return null;
		
		String variableName =  ((Identifier) variable.getName()).getName();
		if (isRequestVariable(variableName)) // Ignore request variables
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
		if (phpRefToHtml != null) {
			addReference(phpRefToHtml);
			mapVariableToReference.put(arrayAccess, phpRefToHtml);
		}
			
		/*
		 * Detect SQL column access, e.g. $row['name']
		 */
		PhpRefToSqlTableColumn phpRefToSql = createPhpRefToSqlTableColumn(arrayAccess, env);
		if (phpRefToSql != null) {
			addReference(phpRefToSql);
			mapVariableToReference.put(arrayAccess, phpRefToSql);
		}
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
		if (arrayAccess.getIndex() instanceof Scalar) {
			Scalar arrayIndex = (Scalar) arrayAccess.getIndex();
			DataNode resolvedIndex = new ScalarNode(arrayIndex).execute(env);
			if (resolvedIndex instanceof LiteralNode)
				return (LiteralNode) resolvedIndex;
		}
		return null;
	}
	
	private boolean isRequestVariable(String variableName) {
		return variableName.equals("_REQUEST") || variableName.equals("_POST") || variableName.equals("_GET") || variableName.equals("_FILES");
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
		
		ReferenceDetector.findReferencesInSqlCode(sqlCode, sqlLocation, scope, referenceManager);
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
			PhpVariable phpVariable = env.readVariable(arrayVariableName);
			
			if (phpVariable != null) {
				String variableValue = phpVariable.getDataNode().getExactStringValueOrNull();
				
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
	public void onEnvUpdateWithBranches(PhpVariable phpVariable, PhpVariable phpVariableInTrueBranch, PhpVariable phpVariableInFalseBranch) {
		mapDataflowBottomToTop.put(phpVariable, new HashSet<PhpVariableDecl>());
		if (mapDataflowBottomToTop.containsKey(phpVariableInTrueBranch))
			mapDataflowBottomToTop.get(phpVariable).addAll(mapDataflowBottomToTop.get(phpVariableInTrueBranch));
		if (mapDataflowBottomToTop.containsKey(phpVariableInFalseBranch))
			mapDataflowBottomToTop.get(phpVariable).addAll(mapDataflowBottomToTop.get(phpVariableInFalseBranch));
	}
		
}

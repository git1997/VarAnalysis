package references.detection;

import java.util.ArrayList;

import constraints.AndConstraint;
import constraints.AtomicConstraint;
import constraints.Constraint;
import constraints.NotConstraint;

import php.nodes.IdentifierNode;
import references.PhpRefToHtmlEntity;
import references.PhpRefToSqlTableColumn;
import references.PhpVariableDecl;
import references.PhpVariableRef;
import references.Reference;
import references.ReferenceManager;
import datamodel.nodes.ConcatNode;
import datamodel.nodes.LiteralNode;
import datamodel.nodes.ext.SelectNode;

/**
 * PhpVisitor visits PHP elements and detects entities.
 *  
 * @author HUNG
 *
 */
public class PhpVisitor {
	
	private String relativeFilePath;
	private ReferenceManager referenceManager;
	
	/**
	 * Constructor
	 */
	public PhpVisitor(String relativeFilePath, ReferenceManager referenceManager) {
		this.relativeFilePath = relativeFilePath;
		this.referenceManager = referenceManager;
	}

	/**
	 * Visits a PHP $_REQUEST, $_POST, or $_GET variable.
	 */
	public void visitRequestVariable(datamodel.nodes.DataNode dataNode) {
		if (dataNode instanceof LiteralNode) {
			LiteralNode literalNode = (LiteralNode) dataNode;
			String htmlInputName = literalNode.getStringValue(); // e.g. 'user' in $_GET['user']
			
			Reference reference = new PhpRefToHtmlEntity(htmlInputName, literalNode.getLocation(), relativeFilePath);
			//TODO Comment out the statement below to stop detecting PhpRefToHtml entities.
			//referenceManager.addReference(reference);
		}
	}
	
	/**
	 * Visits a PHP mysql_query statement.
	 */
	public void visitMysqlQueryStatement(datamodel.nodes.DataNode dataNode, String scope) {
		while (dataNode instanceof ConcatNode && !((ConcatNode) dataNode).getChildNodes().isEmpty()) {
			dataNode = ((ConcatNode) dataNode).getChildNodes().get(0); 
		}
		
		if (dataNode instanceof LiteralNode) {
			LiteralNode literalNode = (LiteralNode) dataNode;
			String sqlCode = literalNode.getStringValue(); // e.g. SELECT name FROM users
			ReferenceDetector.findReferencesInSqlCode(sqlCode, literalNode.getLocation(), scope, referenceManager);
		}
	}
	
	/**
	 * Visits a PHP the table column embedded in a PHP array access (e.g. 'name' in $sql_row['name']).
	 */
	public void visitSqlTableColumn(datamodel.nodes.DataNode dataNode, String scope) {
		if (dataNode instanceof LiteralNode) {
			LiteralNode literalNode = (LiteralNode) dataNode;
			String sqlTableColumnName = literalNode.getStringValue(); // e.g. 'name' in $sql_row['name']
			if (sqlTableColumnName.matches("[0-9]+")) // Ignore numbers, e.g. $sql_row[1]
				return;
			
			Reference reference = new PhpRefToSqlTableColumn(sqlTableColumnName, literalNode.getLocation(), scope);
			referenceManager.addReference(reference);
		}
	}
	
	/**
	 * Visits a PHP variable declarion (e.g. $user = "user").
	 */
	public void visitVariableDecl(IdentifierNode variableName, ArrayList<php.Constraint> constraints, String scope) {
		Reference reference = new PhpVariableDecl(variableName.getName(), variableName.getLocation(), scope);
		reference.setConstraint(createConstraintFromPhpConstraints(constraints));
		referenceManager.addReference(reference);
	}
	
	/**
	 * Visits a PHP variable reference (e.g. echo $user).
	 */
	public void visitVariableRef(IdentifierNode variableName, ArrayList<php.Constraint> constraints, String scope) {
		Reference reference = new PhpVariableRef(variableName.getName(), variableName.getLocation(), scope);
		reference.setConstraint(createConstraintFromPhpConstraints(constraints));
		referenceManager.addReference(reference);
	}

	/**
	 * Creates an AndConstraint combining all the PHP constraints.
	 */
	public static Constraint createConstraintFromPhpConstraints(ArrayList<php.Constraint> phpConstraints) {
		Constraint constraint = null;
		
		for (php.Constraint phpConstraint : phpConstraints) {
			SelectNode selectNode = new SelectNode(new datamodel.nodes.SelectNode(phpConstraint.getConditionString(), null, null));
			Constraint newConstraint = (phpConstraint.isTrueBranch() ? new AtomicConstraint(selectNode) : new NotConstraint(new AtomicConstraint(selectNode)));
			if (constraint == null)
				constraint = newConstraint;
			else
				constraint = new AndConstraint(constraint, newConstraint);
		}
		
		return constraint;
	}
	
}

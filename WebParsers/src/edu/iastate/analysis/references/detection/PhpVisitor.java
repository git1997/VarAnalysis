package edu.iastate.analysis.references.detection;

import java.util.ArrayList;

import edu.iastate.analysis.references.PhpRefToHtmlEntity;
import edu.iastate.analysis.references.PhpRefToSqlTableColumn;
import edu.iastate.analysis.references.PhpVariableDecl;
import edu.iastate.analysis.references.PhpVariableRef;
import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.ReferenceManager;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.php.nodes.IdentifierNode;

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
	public void visitRequestVariable(DataNode dataNode) {
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
	public void visitMysqlQueryStatement(DataNode dataNode, String scope) {
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
	public void visitSqlTableColumn(DataNode dataNode, String scope) {
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
	 * Visits a PHP variable declaration (e.g. $user = "user").
	 */
	public void visitVariableDecl(IdentifierNode variableName, ArrayList<Constraint> constraints, String scope) {
		Reference reference = new PhpVariableDecl(variableName.getName(), variableName.getLocation(), scope);
		reference.setConstraint(createConstraintFromPhpConstraints(constraints));
		referenceManager.addReference(reference);
	}
	
	/**
	 * Visits a PHP variable reference (e.g. echo $user).
	 */
	public void visitVariableRef(IdentifierNode variableName, ArrayList<Constraint> constraints, String scope) {
		Reference reference = new PhpVariableRef(variableName.getName(), variableName.getLocation(), scope);
		reference.setConstraint(createConstraintFromPhpConstraints(constraints));
		referenceManager.addReference(reference);
	}

	/**
	 * Creates an AndConstraint combining all the PHP constraints.
	 */
	public static Constraint createConstraintFromPhpConstraints(ArrayList<Constraint> phpConstraints) {
		Constraint constraint = null;
		
		for (Constraint phpConstraint : phpConstraints) {
			if (constraint == null)
				constraint = phpConstraint;
			else
				constraint = ConstraintFactory.createAndConstraint(constraint, phpConstraint);
		}
		
		return constraint;
	}
	
}

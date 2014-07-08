package edu.iastate.analysis.references.detection;

import org.eclipse.wst.jsdt.core.dom.AST;
import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.ASTParser;

import edu.iastate.analysis.references.ReferenceManager;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.PositionRange;


/**
 * 
 * @author HUNG
 *
 */
public class ReferenceDetector {
	
	/**
	 * Finds references in PHP code (looking at PHP request variables and database access)
	 */
	public static void findReferencesInPhpCode(String relativeFilePath, ReferenceManager referenceManager) {
//		final PhpVisitor phpVisitor = new PhpVisitor(relativeFilePath, referenceManager);
//		
//		ArrayAccessNode.requestVariableListener = new ArrayAccessNode.IRequestVariableListener() {
//			
//			@Override
//			public void requestVariableFound(datamodel.nodes.DataNode dataNode) {
//				phpVisitor.visitRequestVariable(dataNode);
//			}
//		};
//		
//		FunctionInvocationNode.mysqlQueryStatementListener = new FunctionInvocationNode.IMysqlQueryStatementListener() {
//			
//			@Override
//			public void mysqlQueryStatementFound(datamodel.nodes.DataNode dataNode, String scope) {
//				phpVisitor.visitMysqlQueryStatement(dataNode, scope);
//			}
//		};
//		
//		ArrayAccessNode.sqlTableColumnListener = new ArrayAccessNode.ISqlTableColumnListener() {
//			
//			@Override
//			public void sqlTableColumnFound(datamodel.nodes.DataNode dataNode, String scope) {
//				phpVisitor.visitSqlTableColumn(dataNode, scope);
//			}
//		};
//		
//		VariableNode.variableDeclListener = new VariableNode.IVariableDeclListener() {
//			
//			@Override
//			public void variableDeclFound(IdentifierNode variableName, ArrayList<php.Constraint> constraints, String scope) {
//				phpVisitor.visitVariableDecl(variableName, constraints, scope);
//			}
//		};
//		
//		VariableNode.variableRefListener = new VariableNode.IVariableRefListener() {
//			
//			@Override
//			public void variableRefFound(IdentifierNode variableName, ArrayList<php.Constraint> constraints, String scope) {
//				phpVisitor.visitVariableRef(variableName, constraints, scope);
//			}
//		};
//		
//		FunctionDeclarationNode.formalParameterListener = new FunctionDeclarationNode.IFormalParameterListener() {
//			
//			@Override
//			public void formalParameterFound(IdentifierNode variableName, String scope) {
//				phpVisitor.visitVariableDecl(variableName, new ArrayList<php.Constraint>(), scope);
//			}
//		};
	}
	
	public static void findReferencesInPhpCodeFinished() {
//		ArrayAccessNode.requestVariableListener = null;
//		FunctionInvocationNode.mysqlQueryStatementListener = null;
//		ArrayAccessNode.sqlTableColumnListener = null;
//		VariableNode.variableDeclListener = null;
//		VariableNode.variableRefListener = null;
//		FunctionDeclarationNode.formalParameterListener = null;
	}
	
	/**
	 * Finds references in SQL code
	 */
	public static void findReferencesInSqlCode(String sqlCode, PositionRange sqlLocation, String scope, ReferenceManager referenceManager) {				
//        SqlVisitor visitor = new SqlVisitor(sqlCode, sqlLocation, scope, referenceManager);
//        visitor.visit();
	}
	
	/**
	 * Finds references in an HtmlDocument
	 */
	public static void findReferencesInHtmlDocument(HtmlDocument htmlDocument, ReferenceManager referenceManager) {    
        HtmlVisitor visitor = new HtmlVisitor(referenceManager);
       	visitor.visitDocument(htmlDocument);
	}
	
	/**
	 * Finds references in JavaScript code
	 */
	public static void findReferencesInJavascriptCode(String javascriptCode, PositionRange javascriptLocation, Constraint constraint, ReferenceManager referenceManager) {				
		ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(javascriptCode.toCharArray());
        ASTNode rootNode = parser.createAST(null);
        
        JavascriptVisitor visitor = new JavascriptVisitor(javascriptLocation, constraint, referenceManager);
        rootNode.accept(visitor);
	}

}

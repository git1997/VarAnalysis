package references.detection;


import java.util.ArrayList;

import org.eclipse.wst.jsdt.core.dom.AST;
import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.ASTParser;
import constraints.Constraint;

import datamodel.DataModel;
import datamodel.nodes.ext.DataNode;
import deprecated.html.elements.HtmlElement;
import edu.iastate.parsers.html.htmlparser.HtmlDomParser;

import php.nodes.ArrayAccessNode;
import php.nodes.FunctionDeclarationNode;
import php.nodes.FunctionInvocationNode;
import php.nodes.IdentifierNode;
import php.nodes.VariableNode;

import references.ReferenceManager;
import sourcetracing.Location;

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
		final PhpVisitor phpVisitor = new PhpVisitor(relativeFilePath, referenceManager);
		
		ArrayAccessNode.requestVariableListener = new ArrayAccessNode.IRequestVariableListener() {
			
			@Override
			public void requestVariableFound(datamodel.nodes.DataNode dataNode) {
				phpVisitor.visitRequestVariable(dataNode);
			}
		};
		
		FunctionInvocationNode.mysqlQueryStatementListener = new FunctionInvocationNode.IMysqlQueryStatementListener() {
			
			@Override
			public void mysqlQueryStatementFound(datamodel.nodes.DataNode dataNode, String scope) {
				phpVisitor.visitMysqlQueryStatement(dataNode, scope);
			}
		};
		
		ArrayAccessNode.sqlTableColumnListener = new ArrayAccessNode.ISqlTableColumnListener() {
			
			@Override
			public void sqlTableColumnFound(datamodel.nodes.DataNode dataNode, String scope) {
				phpVisitor.visitSqlTableColumn(dataNode, scope);
			}
		};
		
		VariableNode.variableDeclListener = new VariableNode.IVariableDeclListener() {
			
			@Override
			public void variableDeclFound(IdentifierNode variableName, ArrayList<php.Constraint> constraints, String scope) {
				phpVisitor.visitVariableDecl(variableName, constraints, scope);
			}
		};
		
		VariableNode.variableRefListener = new VariableNode.IVariableRefListener() {
			
			@Override
			public void variableRefFound(IdentifierNode variableName, ArrayList<php.Constraint> constraints, String scope) {
				phpVisitor.visitVariableRef(variableName, constraints, scope);
			}
		};
		
		FunctionDeclarationNode.formalParameterListener = new FunctionDeclarationNode.IFormalParameterListener() {
			
			@Override
			public void formalParameterFound(IdentifierNode variableName, String scope) {
				phpVisitor.visitVariableDecl(variableName, new ArrayList<php.Constraint>(), scope);
			}
		};
	}
	
	public static void findReferencesInPhpCodeFinished() {
		ArrayAccessNode.requestVariableListener = null;
		FunctionInvocationNode.mysqlQueryStatementListener = null;
		ArrayAccessNode.sqlTableColumnListener = null;
		VariableNode.variableDeclListener = null;
		VariableNode.variableRefListener = null;
		FunctionDeclarationNode.formalParameterListener = null;
	}
	
	/**
	 * Finds references in SQL code
	 */
	public static void findReferencesInSqlCode(String sqlCode, Location sqlLocation, String scope, ReferenceManager referenceManager) {				
        SqlVisitor visitor = new SqlVisitor(sqlCode, sqlLocation, scope, referenceManager);
        visitor.visit();
	}
	
	/**
	 * Finds references in a Data Model
	 */
	public static void findReferencesInDataModel(DataModel dataModel, ReferenceManager referenceManager, String projectFolder) {
		DataNode outputDataNode = DataNode.createInstance(dataModel.getOutputDataNode());
		DataModelVisitor dataModelVisitor = new DataModelVisitor(projectFolder, referenceManager);
		dataModelVisitor.visit(outputDataNode);
	}
	
	/**
	 * Finds references in HTML code
	 */
	public static void findReferencesInHtmlCode(ExtendedParsingState parsingState, String htmlCode, Location htmlLocation, ReferenceManager referenceManager, String projectFolder) {
		HtmlDomParser parser = new HtmlDomParser(parsingState, htmlCode, htmlLocation);
        ArrayList<HtmlElement> htmlElements = parser.parse();
        
        HtmlVisitor visitor = new HtmlVisitor(projectFolder, parsingState.getConstraint(), referenceManager);
        for (HtmlElement htmlElement : htmlElements)
        	visitor.visit(htmlElement);
	}
	
	/**
	 * Finds references in Javascript code
	 */
	public static void findReferencesInJavascriptCode(String javascriptCode, Location javascriptLocation, Constraint constraint, ReferenceManager referenceManager) {				
		ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(javascriptCode.toCharArray());
        ASTNode rootNode = parser.createAST(null);
        
        JavascriptVisitor visitor = new JavascriptVisitor(javascriptLocation, constraint, referenceManager);
        rootNode.accept(visitor);
	}

}

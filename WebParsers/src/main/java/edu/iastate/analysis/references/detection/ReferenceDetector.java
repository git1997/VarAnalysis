package edu.iastate.analysis.references.detection;

import java.io.File;

import org.eclipse.wst.jsdt.core.dom.AST;
import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.ASTParser;

import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.symex.analysis.WebAnalysis;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class ReferenceDetector {
	
	/**
	 * Finds references in PHP code
	 */
	public static void findReferencesInPhpCode(File entryFile, ReferenceManager referenceManager) {
		PhpVisitor phpVisitor = new PhpVisitor(entryFile, referenceManager);
		WebAnalysis.setListener(phpVisitor);
	};
	
	public static void findReferencesInPhpCodeFinished() {
		WebAnalysis.setListener(null);
	}
	
	/**
	 * Finds references in an HtmlDocument
	 */
	public static void findReferencesInHtmlDocument(HtmlDocument htmlDocument, File entryFile, ReferenceManager referenceManager) {    
        HtmlVisitor visitor = new HtmlVisitor(entryFile, referenceManager);
       	visitor.visitDocument(htmlDocument);
	}
	
	/**
	 * Finds references in JavaScript code
	 */
	public static void findReferencesInJavascriptCode(String javascriptCode, PositionRange javascriptLocation, Constraint javascriptConstraint, File entryFile, ReferenceManager referenceManager) {				
		ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(javascriptCode.toCharArray());
        ASTNode rootNode = parser.createAST(null);
        
        JavascriptVisitor visitor = new JavascriptVisitor(javascriptLocation, entryFile, javascriptConstraint, referenceManager);
        rootNode.accept(visitor);
	}

}

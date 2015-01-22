package edu.iastate.symex.util;

import java.io.File;
import java.util.HashMap;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.Program;

/**
 * Purpose: Return the original file and the file's content that contains a given AST node.
 * @author HUNG
 *
 */
public class ASTHelper {
	
	public static ASTHelper inst = new ASTHelper();

	/*
	 * Mappings
	 */	
	private HashMap<Program, File> phpProgramToSourceFile = new HashMap<Program, File>();
	private HashMap<Program, String> phpProgramToSourceCode = new HashMap<Program, String>();	
	
	/*
	 * Set properties
	 */
	
	public void setSourceFileForPhpProgram(Program program, File sourceFile) {
		phpProgramToSourceFile.put(program, sourceFile);
	}
	
	public void setSourceCodeForPhpProgram(Program program, String sourceCode) {
		phpProgramToSourceCode.put(program, sourceCode);
	}
	
	/*
	 * Get properties
	 */
	
	/**
	 * Returns the source file that contains the PHP AST node.
	 */
	public File getSourceFileOfPhpASTNode(ASTNode astNode) {
		return phpProgramToSourceFile.get(astNode.getProgramRoot());
	}

	/**
	 * Returns the source code that contains the PHP AST node.
	 */
	public String getSourceCodeOfPhpASTNode(ASTNode astNode) {
		String sourceCode = phpProgramToSourceCode.get(astNode.getProgramRoot());
		return sourceCode.substring(astNode.getStart(), astNode.getStart() + astNode.getLength());
	}
	
}

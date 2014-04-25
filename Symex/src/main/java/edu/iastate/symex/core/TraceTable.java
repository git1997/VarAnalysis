package edu.iastate.symex.core;

import java.io.File;
import java.util.HashMap;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.Program;

/**
 * 
 * @author HUNG
 *
 */
public class TraceTable {
	
	/*
	 * Trace tables
	 */
	
	private static HashMap<Program, char[]> phpProgramToSourceCode;	
	private static HashMap<Program, File> phpProgramToSourceFile;	
	
	/**
	 * Resets the static fields every time the main program is executed to save memory space and prevent caching.
	 */
	public static void resetStaticFields() {
		phpProgramToSourceCode = new HashMap<Program, char[]>();
		phpProgramToSourceFile = new HashMap<Program, File>();
	}
	
	/*
	 * Set properties
	 */
	
	public static void setSourceCodeForPhpProgram(Program program, char[] sourceCode) {
		TraceTable.phpProgramToSourceCode.put(program, sourceCode);
	}
	
	public static void setSourceFileForPhpProgram(Program program, File sourceFile) {
		TraceTable.phpProgramToSourceFile.put(program, sourceFile);
	}
	
	/*
	 * Get properties
	 */

	/**
	 * Returns the source code that contains the PHP AST node.
	 */
	public static String getSourceCodeOfPhpASTNode(ASTNode astNode) {
		char[] sourceCode = phpProgramToSourceCode.get(astNode.getProgramRoot());
		return new String(sourceCode, astNode.getStart(), astNode.getLength());
	}
	
	/**
	 * Returns the current source file that contains the PHP AST node.
	 */
	public static File getCurrentSourceFileRelativePathOfPhpASTNode(ASTNode astNode) {
		return phpProgramToSourceFile.get(astNode.getProgramRoot());
	}
	
}

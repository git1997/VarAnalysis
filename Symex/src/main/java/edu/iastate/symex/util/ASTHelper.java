package edu.iastate.symex.util;

import java.io.File;
import java.util.HashMap;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.Program;

/**
 * Purpose: Managing the relation between a PHP file and its AST.
 * @author HUNG
 *
 */
public class ASTHelper {
	
	public static ASTHelper inst = new ASTHelper();

	/*
	 * Mappings
	 */	
	private HashMap<File, Program> phpFileToPhpProgram = new HashMap<File, Program>();
	private HashMap<Program, File> phpProgramToPhpFile = new HashMap<Program, File>();
	private HashMap<Program, String> phpProgramToSourceCode = new HashMap<Program, String>();	
	
	/*
	 * Set properties
	 */
	
	public void setPhpFileForPhpProgram(Program program, File file, String sourceCode) {
		phpFileToPhpProgram.put(file, program);
		phpProgramToPhpFile.put(program, file);
		phpProgramToSourceCode.put(program, sourceCode);
	}
	
	/*
	 * Get properties
	 */
	
	/**
	 * Returns the root AST node of a PHP file
	 * @param file
	 */
	public Program getPhpProgramOfPhpFile(File file) {
		return phpFileToPhpProgram.get(file);
	}
	
	/**
	 * Returns the PHP file that contains the PHP AST node.
	 */
	public File getPhpFileOfPhpASTNode(ASTNode astNode) {
		return phpProgramToPhpFile.get(astNode.getProgramRoot());
	}

	/**
	 * Returns the source code that corresponds to the PHP AST node.
	 */
	public String getSourceCodeOfPhpASTNode(ASTNode astNode) {
		String sourceCode = phpProgramToSourceCode.get(astNode.getProgramRoot());
		return sourceCode.substring(astNode.getStart(), astNode.getStart() + astNode.getLength());
	}
	
}

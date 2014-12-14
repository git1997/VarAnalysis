package edu.iastate.symex.php.nodes;

import java.io.File;

import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.ASTParser;
import org.eclipse.php.internal.core.ast.nodes.Program;

import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.util.ASTHelper;
import edu.iastate.symex.util.FileIO;

/**
 * 
 * @author HUNG
 *
 */
public class FileNode {

	private final File file;
	private ProgramNode programNode = null;	// The AST node representing a PHP program
		
	/**
	 * Constructor: Creates a FileNode representing a PHP source file.
	 * @param file	The PHP file to be parsed
	 * @see {@link edu.iastate.symex.php.nodes.IncludeNode#execute(Env)}
	 */
	public FileNode(File file) {
		this.file=file;
		
		/*
		 * Prepare to parse the source file
		 */
		ASTParser parser = ASTParser.newParser(PHPVersion.PHP5, true);
		char[] source = FileIO.readStringFromFile(file).toCharArray();
		
		/*
		 * Parse the source file
		 */
		Program program = null;
		try {
			parser.setSource(source);
			program = parser.createAST(null);
		} catch (Exception e) {
			MyLogger.log(MyLevel.JAVA_EXCEPTION, "In FileNode.java: Error parsing " + file + " (" + e.getMessage() + ")");
		}
		
		/*
		 * Create the ProgramNode
		 */
		if (program != null) {
			ASTHelper.inst.setSourceFileForPhpProgram(program, file);
			ASTHelper.inst.setSourceCodeForPhpProgram(program, source);
			this.programNode = new ProgramNode(program);
		}
	}
	
	/**
	 * @see {@link edu.iastate.symex.php.nodes.PhpNode#execute(Env)}
	 */
	public DataNode execute(Env env) {
		env.putFile(file, this);
		
		env.pushFileToStack(file);
		DataNode retValue = null;
		if (programNode != null)
			retValue = programNode.execute(env);
		env.popFileFromStack();
		
		return retValue;
	}

}
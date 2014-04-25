package edu.iastate.symex.php.nodes;

import java.io.File;

import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.ASTParser;
import org.eclipse.php.internal.core.ast.nodes.Program;

import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.TraceTable;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.php.elements.PhpFile;
import edu.iastate.symex.util.FileIO;

/**
 * 
 * @author HUNG
 *
 */
public class FileNode {

	private ProgramNode programNode = null;	// The AST node representing a PHP program
	private final File file;
		
	/**
	 * Constructor: Creates a FileNode representing a PHP source file.
	 * @param phpFileRelativePath	The PHP file to be parsed
	 * @see {@link edu.iastate.symex.php.nodes.IncludeNode#execute(Env)}
	 */
	public FileNode(File file) {
		this.file=file;
		
		// Parse the source file
		try {
			ASTParser parser = ASTParser.newParser(PHPVersion.PHP5, true);
			char[] source = FileIO.readStringFromFile(file).toCharArray();
			parser.setSource(source);
			Program program = parser.createAST(null);
			TraceTable.setSourceCodeForPhpProgram(program, source);
			TraceTable.setSourceFileForPhpProgram(program, file);			
			this.programNode = new ProgramNode(program);
		} catch (Exception e) {
			MyLogger.log(MyLevel.JAVA_EXCEPTION, "In FileNode.java: Error parsing " + file + " (" + e.getMessage() + ")");
		}
	}
	
	public DataNode execute(Env env) {
		env.putFile(file, new PhpFile(this));
		
		env.pushFileToStack(file);
		if (programNode != null)
			programNode.execute(env);
		env.popFileFromStack();
		
		return null;
	}

}
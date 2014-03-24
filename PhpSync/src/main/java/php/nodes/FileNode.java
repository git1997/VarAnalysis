package php.nodes;

import java.io.File;

import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.ASTParser;
import org.eclipse.php.internal.core.ast.nodes.Program;

import php.ElementManager;
import php.TraceTable;
import php.elements.PhpFile;
import util.FileIO;
import util.StringUtils;
import util.logging.MyLevel;
import util.logging.MyLogger;
import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class FileNode extends PhpNode {

	private ProgramNode programNode = null;	// The AST node representing a PHP program
	private final File file;
		
	/**
	 * Constructor: Creates a FileNode representing a PHP source file.
	 * @param phpFileRelativePath	The PHP file to be parsed
	 * @see {@link php.nodes.IncludeNode#execute(ElementManager)}
	 */
	public FileNode(File file, File workingDirectory) {
		// Standardize the file path to OS format first
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
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		elementManager.putFile(file, new PhpFile(this));
		
		elementManager.pushFileToStack(file);
		if (programNode != null)
			programNode.execute(elementManager);
		elementManager.popFileFromStack();
		
		return null;
	}

}
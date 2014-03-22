package php.nodes;

import logging.MyLevel;
import logging.MyLogger;

import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.ASTParser;
import org.eclipse.php.internal.core.ast.nodes.Program;

import php.ElementManager;
import php.TraceTable;
import php.elements.PhpFile;
import util.FileIO;
import util.StringUtils;
import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class FileNode extends PhpNode {

	private String phpFileRelativePath;
	private ProgramNode programNode = null;	// The AST node representing a PHP program
		
	/**
	 * Constructor: Creates a FileNode representing a PHP source file.
	 * @param phpFileRelativePath	The PHP file to be parsed
	 * @see {@link php.nodes.IncludeNode#execute(ElementManager)}
	 */
	public FileNode(String projectFolder, String phpFileRelativePath) {
		// Standardize the file path to OS format first
		projectFolder = projectFolder.replace("\\", StringUtils.getFileSystemSlash()).replace("/", StringUtils.getFileSystemSlash());
		phpFileRelativePath = phpFileRelativePath.replace("\\", StringUtils.getFileSystemSlash()).replace("/", StringUtils.getFileSystemSlash());
		String phpFileAbsolutePath = projectFolder + StringUtils.getFileSystemSlash() + phpFileRelativePath;
		
		this.phpFileRelativePath = phpFileRelativePath;
		
		// Parse the source file
		try {
			ASTParser parser = ASTParser.newParser(PHPVersion.PHP5, true);
			char[] source = FileIO.readStringFromFile(phpFileAbsolutePath).toCharArray();
			parser.setSource(source);
			Program program = parser.createAST(null);
			TraceTable.setSourceCodeForPhpProgram(program, source);
			TraceTable.setSourceFileForPhpProgram(program, phpFileRelativePath);			
			this.programNode = new ProgramNode(program);
		} catch (Exception e) {
			MyLogger.log(MyLevel.JAVA_EXCEPTION, "In FileNode.java: Error parsing " + phpFileRelativePath + " (" + e.getMessage() + ")");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		elementManager.putFile(phpFileRelativePath, new PhpFile(this));
		
		elementManager.pushFileToStack(phpFileRelativePath);
		if (programNode != null)
			programNode.execute(elementManager);
		elementManager.popFileFromStack();
		
		return null;
	}

}
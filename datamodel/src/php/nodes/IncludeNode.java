package php.nodes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import logging.MyLevel;
import logging.MyLogger;

import org.eclipse.php.internal.core.ast.nodes.Include;

import php.ElementManager;
import php.elements.PhpFile;
import php.elements.PhpVariable;
import util.StringUtils;
import datamodel.nodes.DataNode;
import datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class IncludeNode extends ExpressionNode {
	
	private int includeType;
	
	private ExpressionNode includedFileExpressionNode;
	
	/*
	Represents include, include_once, require and require_once expressions 
	
	e.g. include('myFile.php'),
	 include_once($myFile),
	 require($myClass->getFileName()),
	 require_once(A::FILE_NAME)
	*/
	public IncludeNode(Include include) {
		super(include);
		this.includeType = include.getIncludeType();
		this.includedFileExpressionNode = ExpressionNode.createInstance(include.getExpression());
	}
	
	/**
	 * Resolves the absolute path of the included file.
	 */
	private String resolveIncludedFileAbsolutePath(ElementManager elementManager) {
		String includedFileRelativePath = includedFileExpressionNode.execute(elementManager).getApproximateStringValue().replace("\\", StringUtils.getFileSystemSlash()).replace("/", StringUtils.getFileSystemSlash());
		
		// Some systems specify absolute paths in include statements. In such cases, return its path right away.
		if (new File(includedFileRelativePath).isFile())
			return includedFileRelativePath;
		
		String projectFolder = elementManager.getProjectFolder();
		ArrayList<String> fileStack = elementManager.getFileStack();
		
		String includedFileAbsolutePath = null;
		for (String invokedFile : fileStack) {
			File includedFile = new File(new File(projectFolder + StringUtils.getFileSystemSlash() + invokedFile).getParent(), includedFileRelativePath);
			if (includedFile.isFile()) { 
				try {
					includedFileAbsolutePath = includedFile.getCanonicalPath(); // getCanonicalPath() is better than getAbsolutePath() because it cancels the recursive ..\something\..\something\..
					includedFileAbsolutePath = includedFileAbsolutePath.replace("\\", StringUtils.getFileSystemSlash()).replace("/", StringUtils.getFileSystemSlash()); // Standardize the file path to OS format
					break;
				} catch (IOException e) {
				}
			}
		}
		
		if (includedFileAbsolutePath != null)
			return includedFileAbsolutePath;
		else {
			String currentFilePath = fileStack.get(fileStack.size() - 1);
			String originalFilePath = fileStack.get(0);
			MyLogger.log(MyLevel.USER_EXCEPTION, "In IncludeNode.java: Unable to resolve the included file " + includedFileRelativePath + ". Current file: " + currentFilePath + ". Original file: " + originalFilePath + ".");
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		/*
		 * Resolve the included file
		 */
		String includedFileAbsolutePath = resolveIncludedFileAbsolutePath(elementManager);
		if (includedFileAbsolutePath == null)
			return new SymbolicNode(this);
		
		String projectFolder = elementManager.getProjectFolder();	
		String includedFileRelativePath = includedFileAbsolutePath.substring(projectFolder.length() + 1);
		
		/*
		 * Check for include_once and require_once.
		 */
		if ((includeType == Include.IT_INCLUDE_ONCE || includeType == Include.IT_REQUIRE_ONCE)
			&& elementManager.getInvokedFiles().contains(includedFileRelativePath)) {
			return new SymbolicNode(this);
		}
		// Avoid recursive file calling
		if (elementManager.containsFileInStack(includedFileRelativePath))
			return new SymbolicNode(this);
		
		/*
		 * Prepare to execute the file
		 */
		
		// Before executing the file, do some backup with the current return value 
		PhpVariable backupPhpReturn = elementManager.getReturnValue();
		elementManager.removeReturnValue();
		
		// Print the trace of included files
		StringBuilder fileTrace = new StringBuilder();
		for (String invokedFile : elementManager.getFileStack())
			fileTrace.append(invokedFile + " -> ");
		fileTrace.append(includedFileRelativePath);
		MyLogger.log(MyLevel.PROGRESS, "Executing " + fileTrace);
		
		/*
		 * Execute the file
		 */
		PhpFile phpFile = elementManager.getFile(includedFileRelativePath);
		FileNode fileNode;
		if (phpFile != null)
			fileNode = phpFile.getFileNode();
		else
			fileNode = new FileNode(projectFolder, includedFileRelativePath);
		fileNode.execute(elementManager);
		
		/*
		 * Finish up
		 */
		//MyLogger.log(MyLevel.PROGRESS, "Done with " + fileTrace + ".");
		
		// Save the new return value and restore the backup return value
		PhpVariable newPhpReturn = elementManager.getReturnValue();
		elementManager.removeReturnValue();
		if (backupPhpReturn != null) {
			elementManager.addReturnValue(backupPhpReturn.getDataNode());
		}
		
		// Get the return value after executing the source file.
		if (newPhpReturn != null)
			return newPhpReturn.getDataNode();
		else
			return new SymbolicNode(this);
	}

}
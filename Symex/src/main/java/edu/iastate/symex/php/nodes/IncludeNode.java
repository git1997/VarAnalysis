package edu.iastate.symex.php.nodes;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Include;

import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.php.elements.PhpVariable;

/**
 * 
 * @author HUNG
 * 
 */
public class IncludeNode extends ExpressionNode {

	private int includeType;
	private ExpressionNode expression;

	/*
	 * Represents include, include_once, require and require_once expressions
	 * 
	 * e.g. include('myFile.php'), include_once($myFile),
	 * require($myClass->getFileName()), require_once(A::FILE_NAME)
	 */
	public IncludeNode(Include include) {
		super(include);
		this.includeType = include.getIncludeType();
		this.expression = ExpressionNode.createInstance(include.getExpression());
	}

	@Override
	public DataNode execute(Env env) {
		/*
		 * Resolve the included file
		 */
		File includedFile = resolveIncludedFile(env);
		
		if (includedFile == null) {
			ArrayList<File> fileStack = env.getFileStack();
			File currentFile = fileStack.get(fileStack.size() - 1);
			File originalFile = fileStack.get(0);
			MyLogger.log(MyLevel.USER_EXCEPTION, "In IncludeNode.java: Unable to resolve the included file " + expression.getSourceCode() + ". "
						+ "Current file: " + currentFile + ". "
						+ "Original file: " + originalFile + ".");
			return DataNodeFactory.createSymbolicNode(this);
		}

		/*
		 * Check for include_once and require_once.
		 */
		if ((includeType == Include.IT_INCLUDE_ONCE || includeType == Include.IT_REQUIRE_ONCE)
				&& env.getInvokedFiles().contains(includedFile)) {
			return DataNodeFactory.createSymbolicNode(this);
		}
		// Avoid recursive file calling
		if (env.containsFileInStack(includedFile))
			return DataNodeFactory.createSymbolicNode(this);

		/*
		 * Prepare to execute the file
		 */

		// Before executing the file, do some backup with the current return value
		PhpVariable backupPhpReturn = env.getReturnValue();
		env.removeReturnValue();

		// Print the trace of included files
		StringBuilder fileTrace = new StringBuilder();
		for (File invokedFile : env.getFileStack())
			fileTrace.append(invokedFile + " -> ");
		fileTrace.append(includedFile);
		MyLogger.log(MyLevel.PROGRESS, "Executing " + fileTrace);

		/*
		 * Execute the file
		 */
		FileNode fileNode = env.getFile(includedFile);
		if (fileNode == null)
			fileNode = new FileNode(includedFile);
		fileNode.execute(env);

		/*
		 * Finish up
		 */
		// MyLogger.log(MyLevel.PROGRESS, "Done with " + fileTrace + ".");

		// Save the new return value and restore the backup return value
		PhpVariable newPhpReturn = env.getReturnValue();
		env.removeReturnValue();
		if (backupPhpReturn != null) {
			env.addReturnValue(backupPhpReturn.getDataNode());
		}

		// Get the return value after executing the source file.
		if (newPhpReturn != null)
			return newPhpReturn.getDataNode();
		else
			return DataNodeFactory.createSymbolicNode(this);
	}

	/**
	 * Resolves the included file.
	 * Returns null if the file cannot be resolved.
	 */
	private File resolveIncludedFile(Env env) {
		String includedFilePath = expression.execute(env).getExactStringValueOrNull();
		if (includedFilePath == null)
			return null;
		
		includedFilePath = includedFilePath.replace('\\', File.separatorChar).replace('/', File.separatorChar);
		File includedFile = new File(includedFilePath);
		
		// Some systems specify absolute paths in include statements.
		// In such cases, the file is found right away.
		if (includedFile.isFile())
			return includedFile;

		// Others use relative paths, therefore do a search.
		for (File invokedFile : env.getFileStack()) {
			includedFile = new File(invokedFile.getParent() + File.separatorChar + includedFilePath);
			if (includedFile.isFile())
				return includedFile;
		}
		return null;
	}

}
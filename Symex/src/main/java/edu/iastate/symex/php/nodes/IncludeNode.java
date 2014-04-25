package edu.iastate.symex.php.nodes;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Include;

import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.php.elements.PhpFile;
import edu.iastate.symex.php.elements.PhpVariable;
import edu.iastate.symex.util.StringUtils;

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
		File includedFile = resolveIncludedFileAbsolutePath(env);
		if (includedFile == null || !includedFile.isFile())
			return new SymbolicNode(this);

		/*
		 * Check for include_once and require_once.
		 */
		if ((includeType == Include.IT_INCLUDE_ONCE || includeType == Include.IT_REQUIRE_ONCE)
				&& env.getInvokedFiles().contains(includedFile)) {
			return new SymbolicNode(this);
		}
		// Avoid recursive file calling
		if (env.containsFileInStack(includedFile))
			return new SymbolicNode(this);

		/*
		 * Prepare to execute the file
		 */

		// Before executing the file, do some backup with the current return
		// value
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
		PhpFile phpFile = env.getFile(includedFile);
		FileNode fileNode;
		if (phpFile != null)
			fileNode = phpFile.getFileNode();
		else
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
			return new SymbolicNode(this);
	}

	/**
	 * Resolves the absolute path of the included file.
	 */
	private File resolveIncludedFileAbsolutePath(Env env) {
		String includedFileRelativePath = expression
				.execute(env).getApproximateStringValue()
				.replace("\\", StringUtils.getFileSystemSlash())
				.replace("/", StringUtils.getFileSystemSlash());
		File includedFile = new File(includedFileRelativePath);

		// Some systems specify absolute paths in include statements. In such
		// cases, return its path right away.
		if (includedFile.isFile())
			return includedFile;

		ArrayList<File> fileStack = env.getFileStack();

		for (File invokedFile : fileStack) {
			includedFile = new File(invokedFile.getParent(),
					includedFileRelativePath);
			if (includedFile.isFile())
				return includedFile;
		}

		// error
		File currentFilePath = fileStack.get(fileStack.size() - 1);
		File originalFilePath = fileStack.get(0);
		MyLogger.log(MyLevel.USER_EXCEPTION,
				"In IncludeNode.java: Unable to resolve the included file "
						+ includedFileRelativePath + ". Current file: "
						+ currentFilePath + ". Original file: "
						+ originalFilePath + ".");
		return null;
	}

}
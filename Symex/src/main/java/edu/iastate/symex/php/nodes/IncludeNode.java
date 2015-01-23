package edu.iastate.symex.php.nodes;

import java.io.File;

import org.eclipse.php.internal.core.ast.nodes.Include;

import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.SpecialNode.UnsetNode;

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
		DataNode value = expression.execute(env);
		File includedFile = env.resolveFile(value);
		
		if (includedFile == null) {
			File currentFile = env.peekFileFromStack();
			File originalFile = env.getFileStack().get(0);
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
		
		/*
		 * Avoid recursive file calling
		 */
		if (env.containsFileInStack(includedFile))
			return DataNodeFactory.createSymbolicNode(this);

		/*
		 * Prepare to execute the file
		 */

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
		
		DataNode retValue = fileNode.execute(env);
		if (retValue == UnsetNode.UNSET)
			retValue = DataNodeFactory.createSymbolicNode(this);

		/*
		 * Finish up
		 */
		// MyLogger.log(MyLevel.PROGRESS, "Done with " + fileTrace + ".");

		return retValue;
	}

}
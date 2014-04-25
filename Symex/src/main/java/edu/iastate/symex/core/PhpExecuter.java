package edu.iastate.symex.core;

import java.io.File;

import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.php.nodes.FileNode;

/**
 * 
 * @author HUNG
 *
 */
public class PhpExecuter {
	
	/**
	 * Executes PHP code and returns a data model describing its output.
	 * @param file The file to be execute
	 * @param workingDirectory Optional
	 */
	public DataModel execute(File file, File workingDirectory) {
		Env.resetStaticFields(); // TODO: [Optional] Reset the static fields of env to prevent caching (e.g. some server code content has changed)
		TraceTable.resetStaticFields(); // Reset the static fields of TraceTable
		
		Env env = new Env();
		env.setWorkingDirectory(workingDirectory);
		
		FileNode fileNode = new FileNode(file);
		fileNode.execute(env);
		env.addCurrentOutputToFinalOutput(); // The final output is a selection of outputs from the normal flows and exit flows
		
		DataNode output = env.getFinalOutput() != null ? env.getFinalOutput().getDataNode() : null;
		return new DataModel(output);
	}
	
}
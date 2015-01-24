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
	 * Executes PHP code and returns a DataModel describing its output
	 * @param file The file to be executed
	 */
	public DataModel execute(File file) {
		GlobalEnv env = new GlobalEnv();
		FileNode fileNode = new FileNode(file);

		fileNode.execute(env); // TODO Consider the returned CONTROL value?
		env.finishExecution();
		
		DataNode output = env.getCurrentOutput();
		return new DataModel(output);
	}
	
}
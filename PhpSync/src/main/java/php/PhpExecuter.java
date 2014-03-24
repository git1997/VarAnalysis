package php;

import java.io.File;

import php.nodes.FileNode;

/**
 * 
 * @author HUNG
 *
 */
public class PhpExecuter {
	
	/**
	 * Executes the server code and returns elementManager.
	 */
	public ElementManager execute(File file, File workingDirectory) {
		ElementManager.resetStaticFields(); // TODO: [Optional] Reset the static fields of ElementManager to prevent caching (e.g. some server code content has changed)
		TraceTable.resetStaticFields(); // Reset the static fields of TraceTable
		
		ElementManager elementManager = new ElementManager();
		elementManager.setWorkingDirectory(workingDirectory);
		
		FileNode fileNode = new FileNode(file, workingDirectory);
		fileNode.execute(elementManager);
		elementManager.addCurrentOutputToFinalOutput(); // The final output is a selection of outputs from the normal flows and exit flows
		
		return elementManager;
	}
	
}
package edu.iastate.symex.php.elements;

import edu.iastate.symex.php.nodes.FileNode;

/**
 * 
 * @author HUNG
 *
 */
public class PhpFile extends PhpElement {

	private FileNode fileNode;
	
	/**
	 * Constructor
	 * @param fileNode
	 */
	public PhpFile(FileNode fileNode) {
		this.fileNode = fileNode;
	}
	
	/*
	 * Get properties
	 */
	
	public FileNode getFileNode() {
		return fileNode;
	}
	
}

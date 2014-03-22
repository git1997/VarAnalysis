package php.elements;

import php.nodes.FileNode;

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

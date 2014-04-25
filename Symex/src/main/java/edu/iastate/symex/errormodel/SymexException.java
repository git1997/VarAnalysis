package edu.iastate.symex.errormodel;

import java.io.File;

/**
 * 
 * @author HUNG
 * Copied from Christian's code.
 *
 */
public class SymexException extends Exception {

	private static final long serialVersionUID = 1L;

	private final File file;
	private final int line;
	private final int offset;

	public SymexException(String msg, File file, int line, int offset) {
		super(msg);
		this.file = file;
		this.line = line;
		this.offset = offset;
	}

	public File getFile() {
		return file;
	}
	
	public int getLine() {
		return line;
	}

	public int getOffset() {
		return offset;
	}
	
}

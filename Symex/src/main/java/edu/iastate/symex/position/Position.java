package edu.iastate.symex.position;

import java.io.File;

import edu.iastate.symex.util.FileIO;

/**
 * 
 * @author HUNG
 *
 */
public class Position {
	
	public static Position UNDEFINED = new Position(null, -1);
	
	private File file;
	private int offset;
	
	private int line = -2; 	// -2 means line has not been computed, 
							// -1 means line has been computed and is unresolved,
							// 0+ means line has been computed and is resolved.
	
	/**
	 * Constructor.
	 */	
	public Position(File file, int offset) {
		this.file = file;
		this.offset = offset;
	}
	
	public File getFile() {
		return file;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public boolean isUndefined() {
		return this == UNDEFINED;
	}
	
	public int getLine() {
		if (line == -2) {
			if (isUndefined())
				line = -1;
			else
				line = FileIO.getLineFromOffsetInFile(file, offset);
		}
		
		return line;
	}
	
	/**
	 * Returns true if the two positions are identical
	 */
	public boolean sameAs(Position position) {
		return this.getFile().equals(position.getFile()) && this.getOffset() == position.getOffset();
	}
	
}

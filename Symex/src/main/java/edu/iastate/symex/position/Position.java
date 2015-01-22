package edu.iastate.symex.position;

import java.io.File;

import edu.iastate.symex.util.FileIO;

/**
 * 
 * @author HUNG
 *
 */
public class Position {
	
	public static final Position UNDEFINED = new Position();
	
	private File file;		// null means the position is undefined
	private int offset;		// -1 means the position is undefined
	
	private int line = -2; 	// -2 means line has not been computed, 
							// -1 means line has been computed and is undefined,
							// 0+ means line has been computed and is defined.
	
	/**
	 * Constructor
	 */	
	public Position(File file, int offset) {
		this.file = file;
		this.offset = offset;
		
		//this.line = getLine(); // Uncomment this when debugging to get line information
	}
	
	/**
	 * Private constructor for an undefined Position
	 */
	private Position() {
		this.file = null;
		this.offset = -1;
	}
	
	/**
	 * Returns the file, or null if the position is UNDEFINED
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns the offset, or -1 if the position is UNDEFINED
	 */
	public int getOffset() {
		return offset;
	}
	
	/**
	 * Returns true if the position is UNDEFINED
	 */
	public boolean isUndefined() {
		return this == UNDEFINED;
	}
	
	/**
	 * Returns true if the two positions are identical
	 */
	public boolean sameAs(Position position) {
		if (this.isUndefined() || position.isUndefined())
			return false;
		else
			return this.getFile().equals(position.getFile()) && this.getOffset() == position.getOffset();
	}
	
	/**
	 * Returns the absolute path of the file, or null if the position is UNDEFINED
	 */
	public String getFilePath() {
		return (!isUndefined() ? file.getAbsolutePath() : null);
	}

	/**
	 * Returns the simple name of the file, or null if the position is UNDEFINED
	 */
	public String getFileName() {
		return (!isUndefined() ? file.getName() : null);
	}

	/**
	 * Returns the line, or -1 if the position is UNDEFINED
	 */
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
	 * Returns a string that uniquely identifies the position, or "UNDEFINED" if the position is UNDEFINED
	 */
	public String getSignature() {
		if (isUndefined())
			return "UNDEFINED";
		else
			return getFilePath() + "@" + getOffset();
	}
	
	public String toDebugString() {
		if (isUndefined())
			return "UNDEFINED";
		else
			return getFileName() + ":Line" + getLine() + ":Offset" + getOffset();
	}
	
}

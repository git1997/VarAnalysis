package edu.iastate.symex.position;

import java.io.File;

import edu.iastate.symex.util.FileIO;

/**
 * 
 * @author HUNG
 *
 */
public class Position {
	
	public static final Position UNDEFINED = new Position(null, -1);
	
	private File file;		// null means the position is not defined
	private int offset;		// -1 means the position is not defined
	
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
	
	/**
	 * Returns the file, or null if the position is UNDEFINED
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns the offset, or -1 if the position is UNDEFINED
	 * @return
	 */
	public int getOffset() {
		return offset;
	}
	
	public boolean isUndefined() {
		return this == UNDEFINED;
	}

	/**
	 * Returns the lines, or -1 if the position is UNDEFINED
	 * @return
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
	 * Returns the absolute path of the file, or null if the position is UNDEFINED
	 */
	public String getFilePath() {
		return (file != null ? file.getAbsolutePath() : null);
	}

	/**
	 * Returns the (simple) name of the file, or null if the position is UNDEFINED
	 * @return
	 */
	public String getFileName() {
		return (file != null ? file.getName() : null);
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
	 * Returns a string that uniquely identifies the position, or "UNDEFINED" if the position is UNDEFINED
	 */
	public String getSignature() {
		if (this.isUndefined())
			return "UNDEFINED";
		else
			return getFilePath() + "@" + getOffset();
	}
	
	public String toDebugString() {
		if (this.isUndefined())
			return "UNDEFINED";
		else
			return getFileName() + ":Line" + getLine() + ":Offset" + getOffset();
	}
	
}

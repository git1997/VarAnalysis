package edu.iastate.symex.position;

import java.io.File;
import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 * There are 3 types of ranges:
 * 	 + Type 1: Defined Range, e.g. (file: "index.php", offset: 1, length: 2)
 *   + Type 2: Undefined Range with defined length, e.g. (file: null, offset: -1, length: 2)
 *   + Type 3: Undefined Range with undefined length, e.g. (file: null, offset: -1, length -1)
 */
public class Range extends PositionRange {
	
	public static final Range UNDEFINED = new Range(); // Undefined Range with undefined length (type 3)
	
	private Position position;	// can be defined (type 1) or undefined (type 2 or 3)
	private int length;			// -1 means length is undefined (type 3)
	
	/**
	 * Constructor for a defined Range (Type 1)
	 */
	public Range(Position position, int length) {
		this.position = position;
		this.length = length;
	}
	
	/**
	 * Constructor for a defined Range (Type 1)
	 */
	public Range(File file, int offset, int length) {
		this.position = new Position(file, offset);
		this.length = length;
	}
	
	/**
	 * Constructor for an undefined Range with a defined length (Type 2)
	 */
	public Range(int length) {
		this.position = Position.UNDEFINED;
		this.length = length;
	}
	
	/**
	 * Private constructor for an undefined Range with an undefined length (Type 3)
	 */
	private Range() {
		this.position = Position.UNDEFINED;
		this.length = -1;
	}
	
	/**
	 * Returns the position, could be UNDEFINED
	 */
	public Position getPosition() {
		return position;
	}
	
	/**
	 * Returns the file, or null if the range is UNDEFINED
	 */
	public File getFile() {
		return position.getFile();
	}

	/**
	 * Returns the offset, or -1 if the range is UNDEFINED
	 */
	public int getOffset() {
		return position.getOffset();
	}
	
	/**
	 * Returns the length, or -1 if the length is UNDEFINED
	 */
	@Override
	public int getLength() {
		return length;
	}
	
	/**
	 * Returns true if the range is UNDEFINED
	 */
	public boolean isUndefined() {
		return position.isUndefined();
	}
	
	/**
	 * Returns the absolute path of the file, or null if the range is UNDEFINED
	 */
	public String getFilePath() {
		return position.getFilePath();
	}

	/**
	 * Returns the simple name of the file, or null if the range is UNDEFINED
	 */
	public String getFileName() {
		return position.getFileName();
	}
	
	@Override
	public ArrayList<Range> getRanges() {
		ArrayList<Range> ranges = new ArrayList<Range>(1);
		ranges.add(this);
		return ranges;
	}
	
	@Override
	public Position getPositionAtRelativeOffset(int relOffset) {
		return (isUndefined() ? Position.UNDEFINED : new Position(position.getFile(), position.getOffset() + relOffset));
	}
	
}

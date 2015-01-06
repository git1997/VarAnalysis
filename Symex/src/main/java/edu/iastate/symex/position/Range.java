package edu.iastate.symex.position;

import java.io.File;
import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 * There are 3 types of ranges:
 * 	 + Defined Range, e.g. (file: "index.php", offset: 1, length: 2)
 *   + Undefined Range with defined length, e.g. (file: null, offset: -1, length: 2)
 *   + Undefined Range with undefined length, e.g. (file: null, offset: -1, length -1)
 */
public class Range extends PositionRange {
	
	public static final Range UNDEFINED = new Range();
	
	private File file;		// null means the range is undefined (can still have length)
	private int offset;		// -1 means the range is undefined (can still have length)
	private int length;		// -1 means the range is undefined (and length is also undefined)
	
	/**
	 * Constructor
	 */
	public Range(File file, int offset, int length) {
		this.file = file;
		this.offset = offset;
		this.length = length;
	}
	
	/**
	 * Constructor for an undefined Range with a defined length
	 */
	public Range(int length) {
		this.file = null;
		this.offset = -1;
		this.length = length;
	}
	
	/**
	 * Private constructor for an undefined Range with an undefined length
	 */
	private Range() {
		this.file = null;
		this.offset = -1;
		this.length = -1;
	}
	
	/**
	 * Returns the file, or null if the range is UNDEFINED
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns the offset, or -1 if the range is UNDEFINED
	 */
	public int getOffset() {
		return offset;
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
		return file == null;
	}
	
	/**
	 * Returns the absolute path of the file, or null if the range is UNDEFINED
	 */
	public String getFilePath() {
		return (!isUndefined() ? file.getAbsolutePath() : null);
	}

	/**
	 * Returns the simple name of the file, or null if the range is UNDEFINED
	 */
	public String getFileName() {
		return (!isUndefined() ? file.getName() : null);
	}
	
	@Override
	public ArrayList<Range> getRanges() {
		ArrayList<Range> ranges = new ArrayList<Range>(1);
		ranges.add(this);
		return ranges;
	}
	
	@Override
	public Position getPositionAtRelativeOffset(int relOffset) {
		return (isUndefined() ? Position.UNDEFINED : new Position(file, offset + relOffset));
	}
	
}

package edu.iastate.symex.position;

import java.io.File;
import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 */
public class Range extends PositionRange {

	private File file;
	private int offset;
	private int length;
	
	/**
	 * Constructor
	 */
	public Range(File file, int offset, int length) {
		this.file = file;
		this.offset = offset;
		this.length = length;
	}
	
	/**
	 * Constructor
	 */
	public Range(Position startPosition, int length) {
		this(startPosition.getFile(), startPosition.getOffset(), length);
	}
	
	public File getFile() {
		return file;
	}
	
	public String getFilePath() {
		return file.getAbsolutePath();
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public Position getPositionAtRelativeOffset(int relOffset) {
		return new Position(file, offset + relOffset);
	}
	
	@Override
	public ArrayList<Range> getRangesAtRelativeOffset(int relOffset, int length) {
		ArrayList<Range> ranges = new ArrayList<Range>(1);
		ranges.add(new Range(file, offset + relOffset, length));
		return ranges;
	}
	
	@Override
	public ArrayList<Range> getRanges() {
		ArrayList<Range> ranges = new ArrayList<Range>(1);
		ranges.add(this);
		return ranges;
	}
	
	/**
	 * Returns a string that uniquely identifies the range.
	 */
	public String getSignature() {
		return getFilePath() + "@" + getOffset() + ":" + getLength();
	}
	
}

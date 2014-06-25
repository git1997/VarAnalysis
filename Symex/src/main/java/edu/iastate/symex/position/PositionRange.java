package edu.iastate.symex.position;

import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 */
public abstract class PositionRange {
	
	public static final UndefinedRange UNDEFINED = new UndefinedRange();
	
	/**
	 * Returns the length of the position range
	 */
	public abstract int getLength();
	
	/**
	 * Returns the position at a relative offset to the start position of the position range
	 */
	public abstract Position getPositionAtRelativeOffset(int relOffset);
	
	/**
	 * Returns the (absolute) ranges at a relative offset to the start position of the position range
	 */
	public ArrayList<Range> getRangesAtRelativeOffset(int relOffset, int length) {
		return new RelativeRange(this, relOffset, length).getRanges();
	}
	
	/**
	 * Returns the (absolute) ranges in this position range
	 */
	public abstract ArrayList<Range> getRanges();
	
	/**
	 * Returns the start position of the position range
	 */
	public Position getStartPosition() {
		return getPositionAtRelativeOffset(0);
	}
	
	/**
	 * Returns the end position of the position range
	 */
	public Position getEndPosition() {
		return getPositionAtRelativeOffset(getLength());
	}
	
}

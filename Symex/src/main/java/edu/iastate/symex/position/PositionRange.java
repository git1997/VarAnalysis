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
	 * Returns the position at a relative offset to the start position of the position range
	 */
	public abstract Position getPositionAtRelativeOffset(int relOffset);
	
	/**
	 * Returns the length of the position range
	 */
	public abstract int getLength();
	
	/**
	 * Returns ranges
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

package edu.iastate.symex.position;

import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 */
public abstract class PositionRange {
	
	/**
	 * Returns the length of the positionRange, or -1 if the length is undefined
	 */
	public abstract int getLength();
	
	/**
	 * Returns the (atomic) ranges in this positionRange
	 */
	public abstract ArrayList<Range> getRanges();
	
	/**
	 * Returns the position at a relative offset to the start position of the positionRange (could return UNDEFINED)
	 */
	public abstract Position getPositionAtRelativeOffset(int relOffset);
	
	/**
	 * Returns the start position of the positionRange (could return UNDEFINED)
	 */
	public Position getStartPosition() {
		return getPositionAtRelativeOffset(0);
	}
	
	/**
	 * Returns the end position of the positionRange, exclusive (could return UNDEFINED)
	 */
	public Position getEndPosition() {
		int length = getLength();
		return (length == -1 ? Position.UNDEFINED : getPositionAtRelativeOffset(length));
	}
	
}

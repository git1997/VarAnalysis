package edu.iastate.symex.position;

import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 */
public abstract class PositionRange {
	
	/**
	 * Returns the position at a relative offset to the start position of the positionRange.
	 */
	public abstract Position getPositionAtRelativeOffset(int relOffset);
	
	/**
	 * Returns atomic position ranges
	 */
	public abstract ArrayList<AtomicPositionRange> getAtomicPositionRanges();
	
	/**
	 * Returns the length of the positionRange.
	 */
	public abstract int getLength();
	
	/**
	 * Returns the start position of the positionRange.
	 */
	public Position getStartPosition() {
		return getPositionAtRelativeOffset(0);
	}
	
	/**
	 * Returns the end position of the positionRange.
	 */
	public Position getEndPosition() {
		return getPositionAtRelativeOffset(getLength());
	}
	
}

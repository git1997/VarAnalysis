package edu.iastate.symex.position;

import java.io.File;
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
	 * Returns the ranges in this positionRange, containing at least 1 range
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
	
	/*
	 * Convenience methods
	 * @see edu.iastate.symex.position.Position
	 */
	
	/**
	 * Returns the file of the start position, or null if the start position is UNDEFINED
	 */
	public File getFile() {
		return getStartPosition().getFile();
	}

	/**
	 * Returns the offset of the start position, or -1 if the start position is UNDEFINED
	 */
	public int getOffset() {
		return getStartPosition().getOffset();
	}
	
	/**
	 * Returns true if the start position is UNDEFINED
	 */
	public boolean isUndefined() {
		return getStartPosition().isUndefined();
	}
	
	/**
	 * Returns the absolute file path of the start position, or null if the start position is UNDEFINED
	 */
	public String getFilePath() {
		return getStartPosition().getFilePath();
	}
	
}

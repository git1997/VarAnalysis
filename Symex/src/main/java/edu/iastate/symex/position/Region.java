package edu.iastate.symex.position;

import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 */
public abstract class Region {
	
	public static final UndefinedRegion UNDEFINED = new UndefinedRegion();
	
	/**
	 * Returns the position at a relative offset to the start position of the region
	 */
	public abstract Position getPositionAtRelativeOffset(int relOffset);
	
	/**
	 * Returns continuous regions
	 */
	public abstract ArrayList<ContinuousRegion> getContinuousRegions();
	
	/**
	 * Returns the length of the region
	 */
	public abstract int getLength();
	
	/**
	 * Returns the start position of the region
	 */
	public Position getStartPosition() {
		return getPositionAtRelativeOffset(0);
	}
	
	/**
	 * Returns the end position of the region
	 */
	public Position getEndPosition() {
		return getPositionAtRelativeOffset(getLength());
	}
	
}

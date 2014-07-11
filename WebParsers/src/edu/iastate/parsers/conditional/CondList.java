package edu.iastate.parsers.conditional;

import java.util.ArrayList;

/**
 * CondList represents a list with conditional items.
 * 
 * @author HUNG
 *
 * @param <T>
 */
public abstract class CondList<T> {
	
	/**
	 * @return The left-most items in this list, can be many because the list can start with a Select.
	 */
	public abstract ArrayList<T> getLeftMostItems();
	
	/**
	 * Used for debugging
	 */
	public abstract String toDebugString();

}

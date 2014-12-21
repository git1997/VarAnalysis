package edu.iastate.parsers.conditional;

import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 * @param <T>
 */
public class CondListEmpty<T> extends CondList<T> {

	/**
	 * Protected constructor, called from CondListFactory only.
	 */
	protected CondListEmpty() {
	}

	@Override
	public ArrayList<T> getLeftMostItems() {
		return new ArrayList<T>();
	}

	@Override
	public String toDebugString() {
		return "";
	}
	
}

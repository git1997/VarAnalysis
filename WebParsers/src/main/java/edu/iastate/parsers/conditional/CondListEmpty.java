package edu.iastate.parsers.conditional;

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
	public String toDebugString() {
		return "";
	}
	
}

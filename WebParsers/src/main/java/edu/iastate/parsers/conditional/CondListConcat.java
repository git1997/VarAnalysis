package edu.iastate.parsers.conditional;

import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 * @param <T>
 */
public class CondListConcat<T> extends CondList<T> {

	private ArrayList<CondList<T>> childNodes = new ArrayList<CondList<T>>();
	
	/**
	 * Protected constructor, called from CondListFactory only.
	 * @param childNodes childNodes must be compact (no child nodes of type CondListConcat)
	 */
	protected CondListConcat(ArrayList<CondList<T>> childNodes) {
		this.childNodes = childNodes;
	}
	
	public ArrayList<CondList<T>> getChildNodes() {
		return new ArrayList<CondList<T>>(childNodes);
	}

	@Override
	public ArrayList<T> getLeftMostItems() {
		return childNodes.get(0).getLeftMostItems();
	}

	@Override
	public String toDebugString() {
		StringBuilder str = new StringBuilder();
		for (CondList<T> child : childNodes)
			str.append(child.toDebugString() + System.lineSeparator());
		return str.toString();
	}
	
}

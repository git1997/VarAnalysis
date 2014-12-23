package edu.iastate.parsers.conditional;

import java.util.ArrayList;

import edu.iastate.symex.constraints.Constraint;

/**
 * 
 * @author HUNG
 *
 */
public class CondListFactory<T> {
	
	/**
	 * Creates a CondListConcat
	 * @param childNodes childNodes can be empty (but must not be null)
	 */
	public CondList<T> createCompactConcat(ArrayList<CondList<T>> childNodes) {
		ArrayList<CondList<T>> compactChildNodes = new ArrayList<CondList<T>>();
		appendNodes(compactChildNodes, childNodes);
		
		if (compactChildNodes.size() == 0)
			return createEmptyCondList();
		else if (compactChildNodes.size() == 1)
			return compactChildNodes.get(0);
		else
			return new CondListConcat<T>(compactChildNodes);
	}

	private void appendNodes(ArrayList<CondList<T>> compactNodes, ArrayList<CondList<T>> nodes) {
		for (CondList<T> node : nodes)
			appendNode(compactNodes, node);
	}
	
	private void appendNode(ArrayList<CondList<T>> compactNodes, CondList<T> node) {
		if (node instanceof CondListConcat<?>)
			appendNodes(compactNodes, ((CondListConcat<T>) node).getChildNodes());
		else if (!(node instanceof CondListEmpty))
			compactNodes.add(node);
	}
	
	/**
	 * Creates a CondListSelect
	 */
	public CondList<T> createCompactSelect(Constraint constraint, CondList<T> trueBranchNode, CondList<T> falseBranchNode) {
		return new CondListSelect<T>(constraint, trueBranchNode, falseBranchNode);
	}
	
	/**
	 * Creates a CondListItem
	 */
	public CondListItem<T> createCondListItem(T item) {
		return new CondListItem<T>(item);
	}
	
	/**
	 * Creates a CondListEmpty
	 */
	public CondList<T> createEmptyCondList() {
		return new CondListEmpty<T>();
	}
	
	/**
	 * Creates a CondList from a list of items
	 */
	public CondList<T> createCondList(ArrayList<T> itemList) {
		ArrayList<CondList<T>> items = new ArrayList<CondList<T>>();
		for (T item : itemList)
			items.add(createCondListItem(item));
		return createCompactConcat(items);
	}
	
}

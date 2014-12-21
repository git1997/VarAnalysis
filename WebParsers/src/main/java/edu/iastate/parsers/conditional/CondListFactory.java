package edu.iastate.parsers.conditional;

import java.util.ArrayList;

import edu.iastate.symex.constraints.Constraint;

/**
 * 
 * @author HUNG
 *
 */
public class CondListFactory<T> {
	
	/*
	 * Concat
	 */
	
	public CondList<T> createCompactConcat(ArrayList<CondList<T>> nodes) {
		ArrayList<CondList<T>> compactNodes = new ArrayList<CondList<T>>();
		appendNodes(compactNodes, nodes);
		
		if (compactNodes.size() == 0)
			return createEmptyCondList();
		else if (compactNodes.size() == 1)
			return compactNodes.get(0);
		else
			return new CondListConcat<T>(compactNodes);
	}

	public CondList<T> createCompactConcat(CondList<T> node1, CondList<T> node2) {
		ArrayList<CondList<T>> nodes = new ArrayList<CondList<T>>();
		nodes.add(node1);
		nodes.add(node2);
		return createCompactConcat(nodes);
	}
	
	private void appendNodes(ArrayList<CondList<T>> compactNodes, ArrayList<CondList<T>> nodes) {
		for (CondList<T> node : nodes)
			appendNode(compactNodes, node);
	}
	
	private void appendNode(ArrayList<CondList<T>> compactNodes, CondList<T> node) {
		if (node instanceof CondListConcat<?>)
			appendNodes(compactNodes, ((CondListConcat<T>) node).getChildNodes());
		else if (node != null)
			compactNodes.add(node);
	}
	
	/*
	 * Select
	 */
	
	public CondList<T> createCompactSelect(Constraint constraint, CondList<T> trueBranchNode, CondList<T> falseBranchNode) {
		if (trueBranchNode == null && falseBranchNode == null)
			return null;
		else
			return new CondListSelect<T>(constraint, trueBranchNode, falseBranchNode);
	}
	
	/*
	 * Other methods
	 */
	
	public CondList<T> createCondList(ArrayList<T> list) {
		ArrayList<CondList<T>> nodes = new ArrayList<CondList<T>>();
		for (T item : list)
			nodes.add(new CondListItem<T>(item));
		return createCompactConcat(nodes);
	}
	
	public CondList<T> createEmptyCondList() {
		return new CondListEmpty<T>();
	}
	
}

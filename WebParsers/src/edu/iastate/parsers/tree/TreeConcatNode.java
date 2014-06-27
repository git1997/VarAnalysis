package edu.iastate.parsers.tree;

import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 * @param <T>
 */
public class TreeConcatNode<T> extends TreeNode<T> {

	private ArrayList<TreeNode<T>> childNodes = new ArrayList<TreeNode<T>>();
	
	public TreeConcatNode(ArrayList<TreeNode<T>> childNodes) {
		addChildNodes(childNodes);
	}
	
	public void addChildNodes(ArrayList<TreeNode<T>> childNodes) {
		for (TreeNode<T> childNode : childNodes)
			addChildNode(childNode);
	}
	
	public void addChildNode(TreeNode<T> childNode) {
		if (childNode instanceof TreeConcatNode<?>)
			addChildNodes(((TreeConcatNode<T>) childNode).childNodes);
		else
			childNodes.add(childNode);
	}
	
	public ArrayList<TreeNode<T>> getChildNodes() {
		return new ArrayList<TreeNode<T>>(childNodes);
	}

	@Override
	public String toDebugString() {
		StringBuilder str = new StringBuilder();
		for (TreeNode<T> child : childNodes)
			str.append(child.toDebugString() + System.lineSeparator());
		return str.toString();
	}
	
}

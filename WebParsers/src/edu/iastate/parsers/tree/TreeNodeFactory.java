package edu.iastate.parsers.tree;

import java.util.ArrayList;

import edu.iastate.symex.constraints.Constraint;

/**
 * 
 * @author HUNG
 *
 */
public class TreeNodeFactory<T> {
	
	public TreeNode<T> createInstanceFromNodes(ArrayList<T> list) {
		ArrayList<TreeNode<T>> nodes = new ArrayList<TreeNode<T>>();
		for (T item : list)
			nodes.add(new TreeLeafNode<T>(item));
		
		return createCompactConcatNode(nodes);
	}
	
	public TreeNode<T> createInstanceFromBranchingNodes(Constraint constraint, ArrayList<T> listInTrueBranch, ArrayList<T> listInFalseBranch) {
		TreeNode<T> treeInTrueBranch = createInstanceFromNodes(listInTrueBranch);
		TreeNode<T> treeInFalseBranch = createInstanceFromNodes(listInFalseBranch);
		TreeNode<T> selectNode = createCompactSelectNode(constraint, treeInTrueBranch, treeInFalseBranch);
		return selectNode;
	}
	
	public TreeNode<T> createCompactConcatNode(TreeNode<T> node1, TreeNode<T> node2) {
		ArrayList<TreeNode<T>> nodes = new ArrayList<TreeNode<T>>();
		nodes.add(node1);
		nodes.add(node2);
		return createCompactConcatNode(nodes);
	}
		
	public TreeNode<T> createCompactConcatNode(ArrayList<TreeNode<T>> nodes) {
		ArrayList<TreeNode<T>> compactNodes = new ArrayList<TreeNode<T>>();
		appendNodes(compactNodes, nodes);
		
		if (compactNodes.size() == 0)
			return null;
		else if (compactNodes.size() == 1)
			return compactNodes.get(0);
		else
			return new TreeConcatNode<T>(compactNodes);
	}
	
	private void appendNodes(ArrayList<TreeNode<T>> compactNodes, ArrayList<TreeNode<T>> nodes) {
		for (TreeNode<T> node : nodes)
			appendNode(compactNodes, node);
	}
	
	private void appendNode(ArrayList<TreeNode<T>> compactNodes, TreeNode<T> node) {
		if (node instanceof TreeConcatNode<?>)
			appendNodes(compactNodes, ((TreeConcatNode<T>) node).getChildNodes());
		else if (node != null)
			compactNodes.add(node);
	}
	
	public TreeNode<T> createCompactSelectNode(Constraint constraint, TreeNode<T> trueBranchNode, TreeNode<T> falseBranchNode) {
		if (trueBranchNode == null && falseBranchNode == null)
			return null;
		else
			return new TreeSelectNode<T>(constraint, trueBranchNode, falseBranchNode);
	}
	
	public T getRightMostNode(TreeNode<T> treeNode) {
		if (treeNode instanceof TreeConcatNode<?>) {
			ArrayList<TreeNode<T>> childNodes = ((TreeConcatNode<T>) treeNode).getChildNodes();
			return getRightMostNode(childNodes.get(childNodes.size() - 1));
		}
		else if (treeNode instanceof TreeSelectNode<?>) {
			return null;
		}
		else {
			return ((TreeLeafNode<T>) treeNode).getNode();
		}
	}
	
	public TreeNode<T> removeRightMostNode(TreeNode<T> treeNode) {
		if (treeNode instanceof TreeConcatNode<?>) {
			ArrayList<TreeNode<T>> childNodes = ((TreeConcatNode<T>) treeNode).getChildNodes();
			TreeNode<T> lastNode = childNodes.get(childNodes.size() - 1);
			childNodes.remove(childNodes.size() - 1);
			
			lastNode = removeRightMostNode(lastNode);
			if (lastNode != null)
				childNodes.add(lastNode);
			return new TreeConcatNode<T>(childNodes);
		}
		else if (treeNode instanceof TreeSelectNode<?>) {
			return null;
		}
		else {
			return null;
		}
	}

}

package edu.iastate.parsers.tree;

import java.util.ArrayList;

import edu.iastate.symex.constraints.Constraint;

/**
 * 
 * @author HUNG
 *
 */
public class TreeNodeFactory<T> {
	
	public TreeNode<T> createInstanceFromNewNodes(TreeNode<T> currentTree, ArrayList<T> list) {
		ArrayList<TreeNode<T>> leafNodes = new ArrayList<TreeNode<T>>();
		if (currentTree instanceof TreeConcatNode) {
			for (TreeNode<T> item : ((TreeConcatNode<T>) currentTree).getChildNodes())
				leafNodes.add(item);
		}
		
		for (T item : list)
			leafNodes.add(new TreeLeafNode<T>(item));
		
		return createInstanceFromList(leafNodes);
	}
	
	public TreeNode<T> createInstanceFromNewBranchingNodes(TreeNode<T> currentTree, Constraint constraint, ArrayList<T> listInTrueBranch, ArrayList<T> listInFalseBranch) {
		TreeNode<T> treeInTrueBranch = createInstanceFromNewNodes(null, listInTrueBranch);
		TreeNode<T> treeInFalseBranch = createInstanceFromNewNodes(null, listInFalseBranch);
		TreeSelectNode<T> selectNode = new TreeSelectNode<T>(constraint, treeInTrueBranch, treeInFalseBranch);
		
		if (currentTree == null)
			return selectNode;
		else {
			ArrayList<TreeNode<T>> childNodes = new ArrayList<TreeNode<T>>();
			childNodes.add(currentTree);
			childNodes.add(selectNode);
			TreeConcatNode<T> concatNode = new TreeConcatNode<T>(childNodes);
			
			return concatNode;
		}
	}
		
	private TreeNode<T> createInstanceFromList(ArrayList<TreeNode<T>> list) {
		if (list.size() == 0)
			return null;
		
		else if (list.size() == 1)
			return list.get(0);
		
		else {
			TreeConcatNode<T> concatNode = new TreeConcatNode<T>(list);
			return concatNode;
		}
	}

}

package edu.iastate.symex.ui.notused;

import java.util.ArrayList;

import edu.iastate.symex.position.Position;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.symex.datamodel.nodes.SelectNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class MyTreeNode {
	
	private DataNode dataNode;
	
	private MyTreeNode parent;
	
	private MyTreeNode[] children;
	
	public MyTreeNode(DataNode dataNode, MyTreeNode parent) {
		this.dataNode = dataNode;
		this.parent = parent;
		
		ArrayList<DataNode> childNodes = new ArrayList<DataNode>();
		if (dataNode instanceof ConcatNode) {
			ConcatNode concatNode = (ConcatNode) dataNode;
			childNodes = concatNode.getChildNodes();
		}
		else if (dataNode instanceof SelectNode) {
			SelectNode selectNode = (SelectNode) dataNode;
			childNodes.add(selectNode.getNodeInTrueBranch());
			childNodes.add(selectNode.getNodeInFalseBranch());
		}
		else if (dataNode instanceof RepeatNode) {
			RepeatNode repeatNode = (RepeatNode) dataNode;
			childNodes.add(repeatNode.getChildNode());
		}
		// else do nothing
		
		children = new MyTreeNode[childNodes.size()];
		for (int i = 0; i < childNodes.size(); i++) {
			children[i] = new MyTreeNode(childNodes.get(i), this);
		}
	}
	
	public String getNodeName() {
		return dataNode.getClass().getSimpleName();
	}
	
	public String getLocation() {
		if (dataNode instanceof ConcatNode) {
			return "";
		}
		else if (dataNode instanceof SelectNode) {
			SelectNode selectNode = (SelectNode) dataNode;
			return "";
		}
		else if (dataNode instanceof RepeatNode) {
			RepeatNode repeatNode = (RepeatNode) dataNode;
			return "";
		}
		else if (dataNode instanceof LiteralNode) {
			LiteralNode literalNode = (LiteralNode) dataNode;
			Position position = literalNode.getLocation().getStartPosition();
			return position.getFile().getPath() + " @ Line " + position.getLine(); 
		}
		else { // if (dataNode instanceof SymbolicNode) {
			SymbolicNode symbolicNode = (SymbolicNode) dataNode;
			return "";
		}
	}
	
	public String getText() {
		if (dataNode instanceof ConcatNode) {
			return "Concat";
		}
		else if (dataNode instanceof SelectNode) {
			SelectNode selectNode = (SelectNode) dataNode;
			return "Select(" + (selectNode.getConstraint() != null ? selectNode.getConstraint().toString() : "") + ")";
		}
		else if (dataNode instanceof RepeatNode) {
			RepeatNode repeatNode = (RepeatNode) dataNode;
			return "Repeat";
		}
		else if (dataNode instanceof LiteralNode) {
			LiteralNode literalNode = (LiteralNode) dataNode;
			return literalNode.getStringValue();
		}
		else { // if (dataNode instanceof SymbolicNode) {
			SymbolicNode symbolicNode = (SymbolicNode) dataNode;
			return "Symbolic";
		}
	}
	
	public MyTreeNode getParent() {
		return parent;
	}
	
	public MyTreeNode[] getChildren() {
		return children;
	}
	
	public boolean hasChildren() {
		return children.length > 0;
	}
	
}

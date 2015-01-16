package edu.iastate.symex.debug;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

import edu.iastate.symex.php.nodes.StatementNode;
import edu.iastate.symex.util.StringUtils;

/**
 * 
 * @author HUNG
 *
 */
public class Trace {
	
	private DefaultMutableTreeNode pseudoRoot;
	private DefaultMutableTreeNode currentNode;
	
	/**
	 * Protected constructor, called from Debugger only.
	 */
	protected Trace() {
		this.pseudoRoot = new DefaultMutableTreeNode(null);
		this.currentNode = pseudoRoot;
	}
	
	/**
	 * Protected method, called from Debugger only.
	 * @param data
	 */
	protected void push(Object data) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(data);
		currentNode.add(node);
		currentNode = node;
	}
	
	/**
	 * Protected method, called from Debugger only.
	 */
	protected void pop() {
		currentNode = (DefaultMutableTreeNode) currentNode.getParent();
	}
	
	/**
	 * Returns the pseudoRoot
	 */
	public DefaultMutableTreeNode getPseudoRoot() {
		return pseudoRoot;
	}
	
	/**
	 * Returns a string describing the trace
	 */
	public String printTraceToString() {
		StringBuilder strBuilder = new StringBuilder();

		Enumeration<?> en = pseudoRoot.preorderEnumeration();
		en.nextElement(); // Skip the pseudoRoot
		
		while (en.hasMoreElements()) {
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) en.nextElement();
			strBuilder.append(StringUtils.getIndentedTabs(treeNode.getLevel() - 1) + printData(treeNode.getUserObject()) + System.lineSeparator());
		}
		
		return strBuilder.toString();
	}
	
	private String printData(Object data) {
		return ((StatementNode) data).getLocation().getStartPosition().toDebugString();
	}
	
}
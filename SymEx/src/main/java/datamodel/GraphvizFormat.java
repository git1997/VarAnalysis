package datamodel;

import php.elements.PhpElement;
import util.Graphviz;
import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class GraphvizFormat {

	/*
	 * NODES
	 */
	
	public static String printNode(DataNode dataNode) {
		return dataNode.getGraphvizNodeId() + " [label=" + Graphviz.getStandardizedGraphvizLabel(dataNode.getGraphvizLabel()) + dataNode.getGraphvizAttributes() + "];\r\n";
	}
	
	public static String printElement(PhpElement phpElement) {
		return phpElement.getGraphvizNodeId() + " [label=" + Graphviz.getStandardizedGraphvizLabel(phpElement.getGraphvizLabel()) + phpElement.getGraphvizAttributes() + "];\r\n";
	}
	
	/*
	 * EDGES
	 */
	
	public static String printEdge(PhpElement phpElement, DataNode dataNode) {
		return phpElement.getGraphvizNodeId() + " -> " + dataNode.getGraphvizNodeId() + ";\r\n";
	}
	
	public static String printEdge(DataNode node1, DataNode node2) {
		return node1.getGraphvizNodeId() + " -> " + node2.getGraphvizNodeId() + ";\r\n";
	}
	
	public static String printEdge(DataNode node1, DataNode node2, String label) {
		return node1.getGraphvizNodeId() + " -> " + node2.getGraphvizNodeId() + " [label=" + Graphviz.getStandardizedGraphvizLabel(label) +"];\r\n";
	}
	
}

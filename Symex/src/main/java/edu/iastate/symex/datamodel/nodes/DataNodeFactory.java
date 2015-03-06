package edu.iastate.symex.datamodel.nodes;

import java.util.ArrayList;

import edu.iastate.symex.php.nodes.ClassDeclarationNode;
import edu.iastate.symex.php.nodes.PhpNode;
import edu.iastate.symex.position.Range;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.SpecialNode.ReferenceNode;

/**
 * 
 * @author HUNG
 *
 */
public class DataNodeFactory {
	
	/**
	 * Creates an ArrayNode.
	 */
	public static ArrayNode createArrayNode() {
		return new ArrayNode();
	}
	
	/**
	 * Creates a (compact) ConcatNode from childNodes
	 * @param childNodes childNodes must contain at least 1 element
	 */
	public static DataNode createCompactConcatNode(ArrayList<DataNode> childNodes) {
		ArrayList<DataNode> compactChildNodes = new ArrayList<DataNode>();
		appendChildNodes(compactChildNodes, childNodes);
		
		if (compactChildNodes.size() == 1)
			return compactChildNodes.get(0);
		else
			return new ConcatNode(compactChildNodes);
	}
	
	/**
	 * Creates a (compact) ConcatNode from 2 childNodes
	 * @param childNode1
	 * @param childNode2
	 */
	public static DataNode createCompactConcatNode(DataNode childNode1, DataNode childNode2) {
		ArrayList<DataNode> childNodes = new ArrayList<DataNode>(2);
		childNodes.add(childNode1);
		childNodes.add(childNode2);
		return createCompactConcatNode(childNodes);
	}
	
	/**
	 * Appends childNodes such that the ConcatNode is compact
	 */
	private static void appendChildNodes(ArrayList<DataNode> compactChildNodes, ArrayList<DataNode> childNodes) {
		for (DataNode childNode : childNodes)
			appendChildNode(compactChildNodes, childNode);
	}

	/**
	 * Appends a childNode such that the ConcatNode is compact
	 * @param childNode
	 */
	private static void appendChildNode(ArrayList<DataNode> compactChildNodes, DataNode childNode) {
		if (childNode instanceof ConcatNode) {
			appendChildNodes(compactChildNodes, ((ConcatNode) childNode).getChildNodes());
		}
		else if (childNode instanceof LiteralNode 
				&& !compactChildNodes.isEmpty() && compactChildNodes.get(compactChildNodes.size() - 1) instanceof LiteralNode) {
			LiteralNode node1 = (LiteralNode) compactChildNodes.get(compactChildNodes.size() - 1);
			LiteralNode node2 = (LiteralNode) childNode;

			// Normally, we should not combine consecutive literal nodes because new literal nodes in branches or loops would be combined
			// 	 with those before the branches or loops, making it difficult to merge the results after the branches/loops.
			// However, if the locations of the two literal nodes are adjacent, we can combine them.
			if (node1.getLocation() instanceof Range && node2.getLocation() instanceof Range
					&& node1.getLocation().getEndPosition().sameAs(node2.getLocation().getStartPosition())) {
				Range range = new Range(node1.getLocation().getStartPosition().getFile(), node1.getLocation().getStartPosition().getOffset(), node1.getLocation().getLength() + node2.getLocation().getLength());
				String stringValue = node1.getStringValue() + node2.getStringValue();

				LiteralNode combinedLiteralNode = createLiteralNode(stringValue, range);
				compactChildNodes.set(compactChildNodes.size() - 1, combinedLiteralNode);
			}
			else
				compactChildNodes.add(childNode);
		}
		else
			compactChildNodes.add(childNode);
	}	
	
	/*
	 * Create Literal Nodes
	 */
	
	/**
	 * Creates a LiteralNode
	 * Note: stringValue.length() must be equal to location.getLength() to ensure correct location tracking.
	 */
	public static LiteralNode createLiteralNode(String stringValue, PositionRange location) {
		return new LiteralNode(stringValue, location);
	}

	public static LiteralNode createLiteralNode(PhpNode phpNode) {
		return createLiteralNode(phpNode.getSourceCode(), phpNode.getLocation());
	}

	public static LiteralNode createLiteralNode(String stringValue) {
		// The value is dynamically generated and cannot be traced back to the source code.
		return createLiteralNode(stringValue, new Range(stringValue.length())); 
	}
	
	/**
	 * Creates an ObjectNode.
	 */
	public static ObjectNode createObjectNode(ClassDeclarationNode classDeclarationNode) {
		return new ObjectNode(classDeclarationNode);
	}
	
	/**
	 * Creates a RepeatNode.
	 */
	public static RepeatNode createRepeatNode(Constraint constraint, DataNode dataNode) {
		return new RepeatNode(constraint, dataNode);
	}
	
	/**
	 * Creates a (compact) SelectNode
	 */
	public static DataNode createCompactSelectNode(Constraint constraint, DataNode nodeInTrueBranch, DataNode nodeInFalseBranch) {
		// Handle the trivial case where two branches are identical
		if (nodeInTrueBranch == nodeInFalseBranch)
			return nodeInTrueBranch;
		
		// Attempt to compact the SelectNode only if the branches are Concat/LiteralNodes
		if (!((nodeInTrueBranch instanceof ConcatNode || nodeInTrueBranch instanceof LiteralNode) && (nodeInFalseBranch instanceof ConcatNode || nodeInFalseBranch instanceof LiteralNode)))
			return new SelectNode(constraint, nodeInTrueBranch, nodeInFalseBranch);

		// Get the nodes in the two branches (turn ConcatNode into a list of DataNodes)
		ArrayList<DataNode> nodesInTrueBranch = new ArrayList<DataNode>();
		if (nodeInTrueBranch instanceof LiteralNode)
			nodesInTrueBranch.add(nodeInTrueBranch);
		else
			nodesInTrueBranch.addAll(((ConcatNode) nodeInTrueBranch).getChildNodes());
		ArrayList<DataNode> nodesInFalseBranch = new ArrayList<DataNode>();
		if (nodeInFalseBranch instanceof LiteralNode)
			nodesInFalseBranch.add(nodeInFalseBranch);
		else
			nodesInFalseBranch.addAll(((ConcatNode) nodeInFalseBranch).getChildNodes());

		// Get the common nodes
		int commonNodesBefore = 0;
		for (int i = 0; i < nodesInTrueBranch.size(); i++) {
			if (i < nodesInFalseBranch.size() && nodesInTrueBranch.get(i) == nodesInFalseBranch.get(i))
				commonNodesBefore++;
			else
				break;
		}
		int commonNodesAfter = 0;
		for (int i = 1; i <= nodesInTrueBranch.size() - commonNodesBefore; i++) {
			if (nodesInFalseBranch.size() - i >= 0 && nodesInTrueBranch.get(nodesInTrueBranch.size() - i) == nodesInFalseBranch.get(nodesInFalseBranch.size() - i))
				commonNodesAfter++;
			else
				break;
		}

		// Attempt to compact only if the branches share some common nodes
		if (commonNodesBefore == 0 && commonNodesAfter == 0)
			return new SelectNode(constraint, nodeInTrueBranch, nodeInFalseBranch);

		// Extract the common nodes on the left hand side
		ArrayList<DataNode> childNodesOfConcat = new ArrayList<DataNode>();
		for (int i = 0; i < commonNodesBefore; i++)
			childNodesOfConcat.add(nodesInTrueBranch.get(i));

		// Create a selection node for the alternative parts in the middle
		DataNode diffNodesInTrueBranch;
		if (commonNodesBefore + commonNodesAfter == nodesInTrueBranch.size())
			diffNodesInTrueBranch = SpecialNode.UnsetNode.UNSET;
		else if (commonNodesBefore + commonNodesAfter + 1 == nodesInTrueBranch.size())
			diffNodesInTrueBranch = nodesInTrueBranch.get(commonNodesBefore);
		else {
			ArrayList<DataNode> childNodesTemp1 = new ArrayList<DataNode>();
			for (int i = commonNodesBefore; i < nodesInTrueBranch.size() - commonNodesAfter; i++)
				childNodesTemp1.add(nodesInTrueBranch.get(i));
			diffNodesInTrueBranch = createCompactConcatNode(childNodesTemp1);
		}

		DataNode diffNodesInFalseBranch;
		if (commonNodesBefore + commonNodesAfter == nodesInFalseBranch.size())
			diffNodesInFalseBranch = SpecialNode.UnsetNode.UNSET;
		else if (commonNodesBefore + commonNodesAfter + 1 == nodesInFalseBranch.size())
			diffNodesInFalseBranch = nodesInFalseBranch.get(commonNodesBefore);
		else {
			ArrayList<DataNode> childNodesTemp2 = new ArrayList<DataNode>();
			for (int i = commonNodesBefore; i < nodesInFalseBranch.size() - commonNodesAfter; i++)
				childNodesTemp2.add(nodesInFalseBranch.get(i));
			diffNodesInFalseBranch = createCompactConcatNode(childNodesTemp2);
		}

		if (diffNodesInTrueBranch != SpecialNode.UnsetNode.UNSET || diffNodesInFalseBranch != SpecialNode.UnsetNode.UNSET) {
			DataNode middleNode = createCompactSelectNode(constraint, diffNodesInTrueBranch, diffNodesInFalseBranch);
			childNodesOfConcat.add(middleNode);
		}

		// Extract the common nodes on the right hand side
		for (int i = 1; i <= commonNodesAfter; i++)
			childNodesOfConcat.add(nodesInTrueBranch.get(nodesInTrueBranch.size() - i));

		// Return the compact DataNode that represents the same value as the SelectNode
		return createCompactConcatNode(childNodesOfConcat);
	}
	
	/*
	 * Create SymbolicNodes
	 */

	/**
	 * @param phpNode The PhpNode which has unresolved value, can be null.
	 * @param parentNode To support the tracing of unresolved values, can be null.
	 */
	public static SymbolicNode createSymbolicNode(PhpNode phpNode, SymbolicNode parentNode) {
		return new SymbolicNode(phpNode, parentNode);
	}
	
	/**
	 * @param phpNode The PhpNode which has unresolved value, can be null.
	 */
	public static SymbolicNode createSymbolicNode(PhpNode phpNode) {
		return createSymbolicNode(phpNode, null);
	}
	
	public static SymbolicNode createSymbolicNode() {
		return createSymbolicNode(null, null);
	}
	
	/**
	 * Checks the dataNode, if it is UNSET then create a SYMBOLIC node, otherwise just return the dataNode
	 */
	public static DataNode createSymbolicNodeFromUnsetNodeIfPossible(DataNode dataNode, PhpNode phpNode) {
		if (dataNode == SpecialNode.UnsetNode.UNSET)
			return DataNodeFactory.createSymbolicNode(phpNode);
		else if (dataNode instanceof SymbolicNode)
			return DataNodeFactory.createSymbolicNode(phpNode, (SymbolicNode) dataNode);
		else
			return dataNode;
	}
	
	/*
	 * Create ReferenceNodes
	 */
	
	public static ReferenceNode createReferenceNode(PhpVariable phpVariable) {
		return new ReferenceNode(phpVariable);
	}

}

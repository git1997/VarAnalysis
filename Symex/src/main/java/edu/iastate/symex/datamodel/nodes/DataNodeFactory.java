package edu.iastate.symex.datamodel.nodes;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.SwitchCase;
import org.eclipse.php.internal.core.ast.nodes.SwitchStatement;

import edu.iastate.symex.php.nodes.IdentifierNode;
import edu.iastate.symex.php.nodes.InLineHtmlNode;
import edu.iastate.symex.php.nodes.PhpNode;
import edu.iastate.symex.php.nodes.ScalarNode;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.ScatteredPositionRange;
import edu.iastate.symex.position.AtomicPositionRange;
import edu.iastate.symex.position.UndefinedPositionRange;
import edu.iastate.symex.util.StringUtils;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.util.sourcetracing.Location;
import edu.iastate.symex.util.sourcetracing.ScatteredLocation;
import edu.iastate.symex.util.sourcetracing.SourceCodeLocation;
import edu.iastate.symex.util.sourcetracing.UndefinedLocation;
import edu.iastate.symex.config.SymexConfig;
import edu.iastate.symex.core.TraceTable;

/**
 * 
 * @author HUNG
 *
 */
public class DataNodeFactory {
	
	/*
	 * Special objects to indicate the returned values of statements.
	 */
	public static SymbolicNode RETURN = new SymbolicNode();
	public static SymbolicNode BREAK = new SymbolicNode();
	
	/**
	 * Creates a (compact) ConcatNode from childNodes
	 * @param childNodes
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
		ArrayList<DataNode> childNodes = new ArrayList<DataNode>(Arrays.asList(new DataNode[]{childNode1, childNode2}));
		return createCompactConcatNode(childNodes);
	}
	
	/**
	 * Appends childNodes such that the ConcatNode is compact
	 * @param childNode
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
			if (SymexConfig.COMBINING_CONSECUTIVE_LITERAL_NODES
				|| node1.getPositionRange().getEndPosition().sameAs(node2.getPositionRange().getStartPosition())) {
				LiteralNode combinedLiteralNode = createLiteralNode(node1, node2);
				compactChildNodes.set(compactChildNodes.size() - 1, combinedLiteralNode);
			}
			else {
				compactChildNodes.add(childNode);
			}
		}
		else {
			compactChildNodes.add(childNode);
		}
	}	
	
	/**
	 * Creates a (compact) SelectNode
	 */
	public static DataNode createCompactSelectNode(LiteralNode conditionString, DataNode nodeInTrueBranch, DataNode nodeInFalseBranch) {
		// Attempt to compact the SelectNode if the branches are Concat/LiteralNodes
		if (!((nodeInTrueBranch instanceof ConcatNode || nodeInTrueBranch instanceof LiteralNode) && (nodeInFalseBranch instanceof ConcatNode || nodeInFalseBranch instanceof LiteralNode)))
			return new SelectNode(conditionString, nodeInTrueBranch, nodeInFalseBranch);

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

		// Only attempt compacting if the branches share some common nodes
		if (commonNodesBefore == 0 && commonNodesAfter == 0)
			return new SelectNode(conditionString, nodeInTrueBranch, nodeInFalseBranch);

		// Extract the common nodes on the left hand side
		ArrayList<DataNode> childNodesOfConcat = new ArrayList<DataNode>();
		for (int i = 0; i < commonNodesBefore; i++)
			childNodesOfConcat.add(nodesInTrueBranch.get(i));

		// Create a selection node for the alternative parts in the middle
		DataNode diffNodesInTrueBranch;
		if (commonNodesBefore + commonNodesAfter == nodesInTrueBranch.size())
			diffNodesInTrueBranch = null;
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
			diffNodesInFalseBranch = null;
		else if (commonNodesBefore + commonNodesAfter + 1 == nodesInFalseBranch.size())
			diffNodesInFalseBranch = nodesInFalseBranch.get(commonNodesBefore);
		else {
			ArrayList<DataNode> childNodesTemp2 = new ArrayList<DataNode>();
			for (int i = commonNodesBefore; i < nodesInFalseBranch.size() - commonNodesAfter; i++)
				childNodesTemp2.add(nodesInFalseBranch.get(i));
			diffNodesInFalseBranch = createCompactConcatNode(childNodesTemp2);
		}

		if (diffNodesInTrueBranch != null || diffNodesInFalseBranch != null) {
			DataNode middleNode = createCompactSelectNode(conditionString, diffNodesInTrueBranch, diffNodesInFalseBranch);
			childNodesOfConcat.add(middleNode);
		}

		// Extract the common nodes on the right hand side
		for (int i = 1; i <= commonNodesAfter; i++)
			childNodesOfConcat.add(nodesInTrueBranch.get(nodesInTrueBranch.size() - i));

		// Return the compact DataNode that represents the same value as the SelectNode
		return createCompactConcatNode(childNodesOfConcat);
	}
	
	/*
	 * Create Literal Nodes
	 */
	
	public static LiteralNode createLiteralNode(LiteralNode node1, LiteralNode node2) {
		PositionRange positionRange = new ScatteredPositionRange(node1.getPositionRange(), node2.getPositionRange());
		String stringValue = node1.getStringValue() + node2.getStringValue();
		return new LiteralNode(positionRange, stringValue);
	}

	public static LiteralNode createLiteralNode(PhpNode phpNode) {
		return new LiteralNode(phpNode.getPositionRange(), phpNode.getSourceCode());
	}

	public static LiteralNode createLiteralNode(String stringValue) {
		// The value is dynamically generated and cannot be traced back to the source code.
		return new LiteralNode(UndefinedPositionRange.inst, stringValue); 
	}
	
	
	
	
	
	
	
	
	
	
	
	
//	
//	public static LiteralNode createLiteralNode(SymbolicNode symbolicNode) {
//		SinglePositionRange positionRange;
//		String type;
//		String stringValue;
//		String unescapedStringValue;
//
//		positionRange = null; // FIXME UndefinedPositionRange.inst;
//		type = LiteralNode.LITERAL_UNDEFINED;
//		stringValue = symbolicNode.getSymbolicValue();
//		unescapedStringValue = stringValue;
//
//		return new LiteralNode(positionRange, type, stringValue, unescapedStringValue);
//	}
//
//	public static LiteralNode createLiteralNode(SelectNode selectionNode) {
//		SinglePositionRange positionRange;
//		String type;
//		String stringValue;
//		String unescapedStringValue;
//
//		positionRange = null; // FIXME = UndefinedLocation.inst;
//		type = LiteralNode.LITERAL_UNDEFINED;
//		stringValue = selectionNode.getSymbolicValue();
//		unescapedStringValue = stringValue;
//
//		return new LiteralNode(positionRange, type, stringValue, unescapedStringValue);
//	}
//
//	public static LiteralNode createLiteralNode(SwitchCase switchCase) {
//		LiteralNode lit = createLiteralNode((ASTNode) switchCase);
//		lit.stringValue = "case "
//				+ (switchCase.getParent().getParent() instanceof SwitchStatement ? TraceTable
//						.getSourceCodeOfPhpASTNode(((SwitchStatement) switchCase
//								.getParent().getParent()).getExpression())
//						: "?")
//				+ " == "
//				+ (switchCase.getValue() != null ? TraceTable
//						.getSourceCodeOfPhpASTNode(switchCase.getValue())
//						: TraceTable.getSourceCodeOfPhpASTNode(switchCase));
//		lit.unescapedStringValue = lit.stringValue;
//		return lit;
//	}
//
//	public static LiteralNode createLiteralNode(String stringValue, SinglePositionRange positionRange) {
//		String type;
//		String unescapedStringValue;
//
//		// The value is dynamically generated from an AST
//		// node (e.g. function invocation)
//		type = LiteralNode.LITERAL_UNDEFINED;
//		unescapedStringValue = stringValue;
//
//		return new LiteralNode(positionRange, type, stringValue, unescapedStringValue);
//	}
//
//	/*
//	 * Escape/unescape string values
//	 */
//
//	public static String getUnescapedStringValue(String stringValue,
//			String stringType) {
//		//stringValue = stringValue.replace(" ", "c"); // Fix the copyright
//														// character (can't be
//														// printed in XML
//														// format)
//		if (stringType.equals(LiteralNode.LITERAL_QUOTES)) {
//			HashMap<Character, String> mapTable = new HashMap<Character, String>();
//			mapTable.put('t', "\t");
//			mapTable.put('r', "\r");
//			mapTable.put('n', "\n");
//			mapTable.put('\\', "\\");
//			mapTable.put('"', "\"");
//			mapTable.put('\'', "\\\'"); // Keep \' as \' in quotes
//			// mapTable.put('u', "\\u"); // Fix a bug with the escape character
//			// \\u
//			return StringUtils.unescape(stringValue, mapTable);
//		} else if (stringType.equals(LiteralNode.LITERAL_APOSTROPHES)) {
//			HashMap<Character, String> mapTable = new HashMap<Character, String>();
//			mapTable.put('t', "\t");
//			mapTable.put('r', "\r");
//			mapTable.put('n', "\n");
//			mapTable.put('\\', "\\");
//			mapTable.put('\'', "\'");
//			mapTable.put('\"', "\\\""); // Keep \" as \" in apostrophes
//			return StringUtils.unescape(stringValue, mapTable);
//		} else if (stringType.equals(LiteralNode.LITERAL_CONSTANT)) {
//			// Do nothing for CONSTANT
//			return stringValue;
//		} else if (stringType.equals(LiteralNode.LITERAL_INLINE)) {
//			// Do nothing for INLINE
//			return stringValue;
//		} else {
//			// Don't know how to handle UNDEFINED
//			MyLogger.log(
//					MyLevel.TODO,
//					"In LiteralNode.getUnescapedStringValue: Don't know how to handle UNDEFINED string type of LiteralNode: "
//							+ stringValue);
//			return stringValue;
//		}
//	}
//
//	public static String getUnescapedStringValuePreservingLength(
//			String stringValue, String stringType) {
//		//stringValue = stringValue.replace(" ", "c"); // Fix the copyright
//														// character (can't be
//														// printed in XML
//														// format)
//		if (stringType.equals(LiteralNode.LITERAL_QUOTES)) {
//			HashMap<Character, String> mapTable = new HashMap<Character, String>();
//			mapTable.put('t', " \t");
//			mapTable.put('r', " \r");
//			mapTable.put('n', "\n "); // Put the space after so that \r\n ->
//										// _[\r][\n]_
//			mapTable.put('\\', " \\");
//			mapTable.put('"', "\" "); // Put the space after so that \\\" ->
//										// _\"_
//			mapTable.put('\'', "\\\'"); // Keep \' as \' in quotes
//			// mapTable.put('u', "\\u"); // Fix a bug with the escape character
//			// \\u
//			return StringUtils.unescape(stringValue, mapTable);
//		} else if (stringType.equals(LiteralNode.LITERAL_APOSTROPHES)) {
//			HashMap<Character, String> mapTable = new HashMap<Character, String>();
//			mapTable.put('t', " \t");
//			mapTable.put('r', " \r");
//			mapTable.put('n', "\n "); // Put the space after so that \r\n ->
//										// _[\r][\n]_
//			mapTable.put('\\', " \\");
//			mapTable.put('\'', "\' "); // Put the space after so that \\\' ->
//										// _\'_
//			mapTable.put('\"', "\\\""); // Keep \" as \" in apostrophes
//			return StringUtils.unescape(stringValue, mapTable);
//		} else if (stringType.equals(LiteralNode.LITERAL_CONSTANT)) {
//			// Do nothing for CONSTANT
//			return stringValue;
//		} else if (stringType.equals(LiteralNode.LITERAL_INLINE)) {
//			// Do nothing for INLINE
//			return stringValue;
//		} else {
//			// Don't know how to handle UNDEFINED
//			MyLogger.log(
//					MyLevel.TODO,
//					"In LiteralNode.getUnescapedStringValuePreservingLength: Don't know how to handle UNDEFINED string type of LiteralNode: "
//							+ stringValue);
//			return stringValue;
//		}
//	}
//
//	public static String getEscapedStringValue(String stringValue,
//			String stringType) {
//		if (stringType.equals(LiteralNode.LITERAL_QUOTES)) {
//			HashMap<Character, String> mapTable = new HashMap<Character, String>();
//			mapTable.put('\\', "\\\\");
//			mapTable.put('"', "\\\"");
//			return StringUtils.escape(stringValue, mapTable);
//		} else if (stringType.equals(LiteralNode.LITERAL_APOSTROPHES)) {
//			HashMap<Character, String> mapTable = new HashMap<Character, String>();
//			mapTable.put('\\', "\\\\");
//			mapTable.put('\'', "\\\'");
//			return StringUtils.escape(stringValue, mapTable);
//		} else if (stringType.equals(LiteralNode.LITERAL_CONSTANT)) {
//			// CONSTANT needs a special treatment. For example, if we are to
//			// replace a numeric value 10 with string a"bc, then 10 => "a\"bc"
//			if (StringUtils.isNumeric(stringValue))
//				return stringValue;
//			else
//				return "\""
//						+ getEscapedStringValue(stringValue,
//								LiteralNode.LITERAL_QUOTES) + "\"";
//		} else if (stringType.equals(LiteralNode.LITERAL_INLINE)) {
//			// Do nothing for INLINE
//			return stringValue;
//		} else {
//			// Don't know how to handle UNDEFINED
//			return stringValue;
//		}
//	}

}

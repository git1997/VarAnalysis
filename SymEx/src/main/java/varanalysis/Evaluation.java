//package varanalysis;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
//
//import org.eclipse.php.internal.core.ast.nodes.ASTNode;
//import org.eclipse.php.internal.core.ast.nodes.Scalar;
//
//import util.sourcetracing.SourceCodeLocation;
//import datamodel.DataModel;
//import datamodel.nodes.ArrayNode;
//import datamodel.nodes.ConcatNode;
//import datamodel.nodes.DataNode;
//import datamodel.nodes.LiteralNode;
//import datamodel.nodes.ObjectNode;
//import datamodel.nodes.RepeatNode;
//import datamodel.nodes.SelectNode;
//import datamodel.nodes.SymbolicNode;
//
///**
// * 
// * @author HUNG
// *
// */
//public class Evaluation {
//	
//	/*
//	 * Count statements
//	 */
//	public static int numStatements = 0;
//	
//	/*
//	 * Size of D-Model
//	 */
//	public static int numDModelChars = 0;
//	public static int numDModelConditions = 0;
//	
//	/*
//	 * Count output chars
//	 */
//	public static HashMap<String, LiteralNode> mapLocationToStringValue = new HashMap<String, LiteralNode>();
//	public static int numOutputChars = 0;
//	public static int numOutputCharsOnEcho = 0;
//	
//	/*
//	 * Start & finish
//	 */
//	
//	public static void start() {
//	}
//	
//	public static void finish() {
//		System.out.format("Number of statements created: %d\n", numStatements);
//		
//		System.out.format("Number of D-Model chars: %d\n", numDModelChars);
//		System.out.format("Number of D-Model conditions: %d\n", numDModelConditions);
//		
//		System.out.format("Number of output chars: %d\n", numOutputChars);
//		System.out.format("Number of output chars on Echo: %d\n", numOutputCharsOnEcho);
//		
//		System.out.format("%% output chars on echo: %d%%\n", numOutputCharsOnEcho * 100 / numOutputChars);
//	}
//	
//	/*
//	 * Count statements
//	 */
//	
//	public static void statementNodeCreated() {
//		numStatements++;
//	}
//	
//	/*
//	 * Size of D-Model
//	 */
//	
//	public static void dataModelCreated(DataModel dataModel) {
//		DataNode outputDataNode = dataModel.getOutputDataNode();
//		numDModelChars = countChars(outputDataNode);
//		numDModelConditions = countConditions(outputDataNode);
//	}
//	
//	private static int countChars(DataNode dataNode) {
//		if (dataNode instanceof ArrayNode) {
//			return 0;
//		}
//		
//		else if (dataNode instanceof ObjectNode) {
//			return 0;
//		}
//		
//		else if (dataNode instanceof ConcatNode) {
//	    	int cnt = 0;
//	    	for (DataNode child : ((ConcatNode) dataNode).getChildNodes()) {
//	    		cnt += countChars(child);
//    		}
//    		return cnt;
//		}
//		
//		else if (dataNode instanceof LiteralNode) {
//			String stringValue = ((LiteralNode) dataNode).getUnescapedStringValue();
//			return stringValue.length();
//		}
//		
//		else if (dataNode instanceof RepeatNode) {
//			return countChars(((RepeatNode) dataNode).getChildNode()); 
//		}
//		
//		else if (dataNode instanceof SelectNode) {
//			return countChars(((SelectNode) dataNode).getNodeInTrueBranch()) + countChars(((SelectNode) dataNode).getNodeInFalseBranch());
//		}
//		
//		else if (dataNode instanceof SymbolicNode) {
//			return 0;
//		}
//		
//	    return 0; // Should not reach here
//    }
//	
//	private static int countConditions(DataNode dataNode) {
//		if (dataNode instanceof ArrayNode) {
//			return 0;
//		}
//		
//		else if (dataNode instanceof ObjectNode) {
//			return 0;
//		}
//		
//		else if (dataNode instanceof ConcatNode) {
//	    	int cnt = 0;
//	    	for (DataNode child : ((ConcatNode) dataNode).getChildNodes()) {
//	    		cnt += countConditions(child);
//    		}
//    		return cnt;
//		}
//		
//		else if (dataNode instanceof LiteralNode) {
//			return 0;
//		}
//		
//		else if (dataNode instanceof RepeatNode) {
//			return countConditions(((RepeatNode) dataNode).getChildNode()); 
//		}
//		
//		else if (dataNode instanceof SelectNode) {
//			return 1 + countConditions(((SelectNode) dataNode).getNodeInTrueBranch()) + countConditions(((SelectNode) dataNode).getNodeInFalseBranch());
//		}
//		
//		else if (dataNode instanceof SymbolicNode) {
//			return 0;
//		}
//		
//	    return 0; // Should not reach here
//    }
//	
//	/*
//	 * Count output chars
//	 */
//	
//	public static void dataModelCreated2(DataModel dataModel) {
//		DataNode outputDataNode = dataModel.getOutputDataNode();
//		numOutputChars = countOutputChars(outputDataNode, mapLocationToStringValue);
//		numOutputCharsOnEcho = countOutputCharsOnEcho(mapLocationToStringValue);
//	}
//	
//	private static int countOutputChars(DataNode outputDataNode, HashMap<String, LiteralNode> map) {
//		getOutputChars(outputDataNode, map);
//		return countCharsFromStrings(map.values());
//	}
//	
//	public static void getOutputChars(DataNode dataNode, HashMap<String, LiteralNode> map) {
//		if (dataNode instanceof ArrayNode) {
//			// Do nothing
//		}
//		
//		else if (dataNode instanceof ObjectNode) {
//			// Do nothing
//		}
//		
//		else if (dataNode instanceof ConcatNode) {
//	    	for (DataNode child : ((ConcatNode) dataNode).getChildNodes()) {
//	    		getOutputChars(child, map);
//    		}
//		}
//		
//		else if (dataNode instanceof LiteralNode) {
//			String stringValue = ((LiteralNode) dataNode).getUnescapedStringValue();
//			SourceCodeLocation location = ((LiteralNode) dataNode).getLocation().getLocationAtOffset(0);
//			String locationString = location.getFilePath() + location.getPosition();
//			
//			if (stringValue.contains("<"))
//				map.put(locationString, (LiteralNode) dataNode);
//		}
//		
//		else if (dataNode instanceof RepeatNode) {
//			getOutputChars(((RepeatNode) dataNode).getChildNode(), map); 
//		}
//		
//		else if (dataNode instanceof SelectNode) {
//			getOutputChars(((SelectNode) dataNode).getNodeInTrueBranch(), map);
//			getOutputChars(((SelectNode) dataNode).getNodeInFalseBranch(), map);
//		}
//		
//		else if (dataNode instanceof SymbolicNode) {
//			// Do nothing
//		}
//		
//	    // Should not reach here
//    }
//	
//	public static int countCharsFromStrings(Collection<LiteralNode> literalNodes) {
//		int cnt = 0;
//		for (LiteralNode node : literalNodes)
//			cnt += 1; //node.getUnescapedStringValue().length();
//		return cnt;
//	}
//	
//	private static int countOutputCharsOnEcho(HashMap<String, LiteralNode> map) {
//		int cnt = 0;
//		for (LiteralNode node : map.values())
//			if (node.getType().equals("I"))
//				cnt += 1; //node.getUnescapedStringValue().length();
//		return cnt;
//	}
//	
//	public static String convertEchoToInlineType(Scalar scalar, String defaultType) {
//		ASTNode node = scalar.getParent();
//		while (node != null) {
//			if (node instanceof org.eclipse.php.internal.core.ast.nodes.EchoStatement) {
//				return LiteralNode.LITERAL_INLINE;
//			}
//			if (node instanceof org.eclipse.php.internal.core.ast.nodes.FunctionInvocation) {
//				org.eclipse.php.internal.core.ast.nodes.FunctionInvocation func = (org.eclipse.php.internal.core.ast.nodes.FunctionInvocation) node;
//				if (func.getFunctionName().getName() instanceof org.eclipse.php.internal.core.ast.nodes.Identifier) {
//					org.eclipse.php.internal.core.ast.nodes.Identifier id = (org.eclipse.php.internal.core.ast.nodes.Identifier) func.getFunctionName().getName();
//					if (id.getName().equals("print")) {
//						return LiteralNode.LITERAL_INLINE;
//					}
//				}
//			}
//			node = node.getParent();
//		}
//		return defaultType;
//	}
//	
//}

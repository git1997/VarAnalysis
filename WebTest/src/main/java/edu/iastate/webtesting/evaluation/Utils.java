package edu.iastate.webtesting.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.DataModelVisitor;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.webtesting.util_clone.CodeLocation;
import edu.iastate.webtesting.util_clone.XmlReadWrite;
import edu.iastate.webtesting.values_clone.Concat;
import edu.iastate.webtesting.values_clone.CondValue;
import edu.iastate.webtesting.values_clone.Literal;

/**
 * 
 * @author HUNG
 *
 */
public class Utils {

	/**
	 * Flattens a CondValue:
	 * 	- If it is a Concat, flatten into a list of CondValues where no values are Concat (either Literal or NullValue).
	 *  - Otherwise, return a list of 1 element which is the given CondValue itself. 
	 */
	public static List<CondValue> flattenConcat(CondValue condValue) {
		if (condValue instanceof Concat) {
			return ((Concat) condValue).getChildValues();
		}
		else {
			List<CondValue> list = new ArrayList<CondValue>();
			list.add(condValue);
			return list;
		}
	}
	
	public static List<LiteralNode> getSortedLiteralNodesInDataModel(DataModel dataModel) {
		final List<LiteralNode> literalNodes = new ArrayList<LiteralNode>();
		dataModel.getRoot().accept(new DataModelVisitor() {
			
			@Override
			public boolean visitLiteralNode(LiteralNode literalNode) {
				literalNodes.add(literalNode);
				return true;
			}
		});
		return literalNodes;
	}
	
	public static int countStringLengthOfLiteralNodes(Collection<LiteralNode> literalNodes) {
		int length = 0;
		for (LiteralNode literalNode : literalNodes)
			length += literalNode.getStringValue().length();
		return length;
	}
	
	public static int countStringLengthOfLiterals(Collection<Literal> literals) {
		int length = 0;
		for (Literal literal : literals)
			length += literal.getStringValue().length();
		return length;
	}
	
	public static String codeLocationToString(CodeLocation codeLocation) {
		return XmlReadWrite.codeLocationToString(codeLocation);
	}
	
	public static String listToString(List<String> list) {
		StringBuilder string = new StringBuilder();
		for (String str : list)
			string.append(str + System.lineSeparator());
		return string.toString();
	}
}

package edu.iastate.symex.datamodel;

import edu.iastate.symex.datamodel.nodes.ArrayNode;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.ObjectNode;
import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.symex.datamodel.nodes.SelectNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class WriteDataModelToIfDefs extends DataModelVisitor {
	
	private StringBuilder strBuilder;
	
	/**
	 * Converts a DataModel to #ifdefs format.
	 */
	public static String convert(DataModel dataModel) {
		WriteDataModelToIfDefs visitor = new WriteDataModelToIfDefs();
		dataModel.getRoot().accept(visitor);
		return visitor.getResults();
	}
	
	public WriteDataModelToIfDefs() {
		this.strBuilder = new StringBuilder();
	}
	
	public String getResults() {
		return strBuilder.toString();
	}
	
	@Override
	public boolean visitArrayNode(ArrayNode arrayNode) {
		strBuilder.append("[Array]");
		return true;
	}

	@Override
	public boolean visitLiteralNode(LiteralNode literalNode) {
		strBuilder.append(literalNode.getStringValue());
		return true;
	}

	@Override
	public boolean visitObjectNode(ObjectNode objectNode) {
		strBuilder.append("[Object]");
		return true;
	}

	@Override
	public boolean visitRepeatNode(RepeatNode repeatNode) {
		strBuilder.append(System.lineSeparator() + "#repeat (" + repeatNode.getConstraint().toDebugString() + ")" + System.lineSeparator());
		repeatNode.getChildNode().accept(this);
		strBuilder.append(System.lineSeparator() + "#endrepeat" + System.lineSeparator());
		return false;
	}

	@Override
	public boolean visitSelectNode(SelectNode selectNode) {
		strBuilder.append(System.lineSeparator() + "#if (" + selectNode.getConstraint().toDebugString() + ")" + System.lineSeparator());
		selectNode.getNodeInTrueBranch().accept(this);
		strBuilder.append(System.lineSeparator() + "#else" + System.lineSeparator());
		selectNode.getNodeInFalseBranch().accept(this);
		strBuilder.append(System.lineSeparator() + "#endif" + System.lineSeparator());
		return false;
	}
	
	@Override
	public boolean visitSpecialNode(SpecialNode specialNode) {
		strBuilder.append("[Special]");
		return true;
	}

	@Override
	public boolean visitSymbolicNode(SymbolicNode symbolicNode) {
		strBuilder.append("[Symbolic:" + (symbolicNode.getPhpNode() != null ? symbolicNode.getPhpNode().getSourceCode() : "") + "]");
	    return true;
    }
	
}
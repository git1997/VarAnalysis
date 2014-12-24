package edu.iastate.symex.datamodel;

import edu.iastate.symex.datamodel.nodes.ArrayNode;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.ObjectNode;
import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.symex.datamodel.nodes.SelectNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 * Copied from Christian's code.
 *
 */
public class DataModelVisitor {

	public boolean visitArrayNode(ArrayNode arrayNode) {
		return true;
	}

	public boolean visitConcatNode(ConcatNode concatNode) {
		return true;
	}

	public boolean visitLiteralNode(LiteralNode literalNode) {
		return true;
	}

	public boolean visitObjectNode(ObjectNode objectNode) {
		return true;
	}

	public boolean visitRepeatNode(RepeatNode repeatNode) {
		return true;
	}

	public boolean visitSelectNode(SelectNode selectNode) {
		return true;
	}
	
	public boolean visitSpecialNode(SpecialNode specialNode) {
		return true;
	}

	public boolean visitSymbolicNode(SymbolicNode symbolicNode) {
		return true;
	}

}

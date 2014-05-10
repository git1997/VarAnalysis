package edu.iastate.symex.datamodel;

import edu.iastate.symex.constraints.AtomicConstraint;
import edu.iastate.symex.datamodel.nodes.ArrayNode;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
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
public class WriteDataModelToIfDefs {
	
	/**
	 * Converts a DataModel to #ifdefs format.
	 */
	public static String convert(DataModel dataModel) {
		return convert(dataModel.getRoot());
	}
	
	/**
	 * Converts a DataNode to #ifdefs format.
	 */
	private static String convert(DataNode dataNode) {
		if (dataNode instanceof ArrayNode) {
			return "[Array]";
		}
		
		else if (dataNode instanceof ConcatNode) {
	    	StringBuilder str = new StringBuilder();
	    	for (DataNode child : ((ConcatNode) dataNode).getChildNodes()) {
	    		String childValue = convert(child);
    			str.append(childValue);
    		}
    		return str.toString();
		}
		
		else if (dataNode instanceof LiteralNode) {
			String stringValue = ((LiteralNode) dataNode).getStringValue();
			
//			Position location = ((LiteralNode) dataNode).getPositionRange().getStartPosition();
//			String locationInfo = "[Unresolved Location]";
//			if (!(location.isUndefined())) {
//				locationInfo = " (Location: " + location.getFile() + " @ Line " + location.getLine() + ") ";
//			}

			return stringValue;
		}
		
		else if (dataNode instanceof ObjectNode) {
			return "[Object]";
		}
		
		else if (dataNode instanceof RepeatNode) {
			return "[RepeatBegin]\n" + convert(((RepeatNode) dataNode).getChildNode()) + "\n[RepeatEnd]"; 
		}
		
		else if (dataNode instanceof SelectNode) {
			String constraint = ((SelectNode) dataNode).getConstraint() != null ? ((SelectNode) dataNode).getConstraint().toString() : "[Unresolved Constraint]";
			
			String trueBranch = convert(((SelectNode) dataNode).getNodeInTrueBranch());
			String falseBranch = convert(((SelectNode) dataNode).getNodeInFalseBranch());
		
			String retString = "\n#if (" + constraint + ")\n"
					+ trueBranch + "\n"
					+ "#else" + "\n"
					+ falseBranch + "\n"
					+ "#endif" + "\n";
			
			return retString;
		}
		
		else if (dataNode instanceof SpecialNode) {
			return "[Special]";
		}
		
		else if (dataNode instanceof SymbolicNode) {
			return "[Symbolic:" + (((SymbolicNode) dataNode).getPhpNode() != null ? ((SymbolicNode) dataNode).getPhpNode().getSourceCode() : "") + "]";
		}
		
	    return ""; // Should not reach here
    }
	
}
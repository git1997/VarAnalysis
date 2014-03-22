package datamodel.nodes.ext;

/**
 * 
 * @author HUNG
 *
 */
public class DataNodeVisitor {
	
	/*
	 * Visit general nodes
	 */
	
	/**
	 * Pre-visits a DataNode
	 */
	public void preVisit(DataNode node) {
	}
	
	/**
	 * Post-visits a DataNode
	 */
	public void postVisit(DataNode node) {		
	}
	
	/*
	 * Visit specific nodes
	 */
	
	/**
	 * Visits a ConcatNode
	 */
	public boolean visit(ConcatNode node) {
		return true;
	}
	
	/**
	 * Visits a SelectNode
	 */
	public boolean visit(SelectNode node) {
		return true;
	}
	
	/**
	 * Visits a RepeatNode
	 */
	public boolean visit(RepeatNode node) {
		return true;
	}
	
	/**
	 * Visits a LiteralNode
	 */
	public boolean visit(LiteralNode node) {
		return true;
	}
	
	/**
	 * Visits a SymbolicNode
	 */
	public boolean visit(SymbolicNode node) {
		return true;
	}

}

package edu.iastate.symex.datamodel.nodes;

import edu.iastate.symex.datamodel.DataModelVisitor;

/**
 * Used for special values (e.g., Boolean values or returned codes of statements).
 * @author HUNG
 * 
 */
public class SpecialNode extends DataNode {
	
	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitSpecialNode(this);
		
	}
	
	public static class BooleanNode extends SpecialNode {
		public static BooleanNode TRUE		= new BooleanNode();
		public static BooleanNode FALSE		= new BooleanNode();
		public static BooleanNode UNKNOWN	= new BooleanNode();
		
		private BooleanNode() {
		}
		
		public boolean isTrueValue() {
			return this == TRUE;
		}
		
		public boolean isFalseValue() {
			return this == FALSE;
		}
		
		public boolean isUnknownValue() {
			return this == UNKNOWN;
		}
		
		/**
		 * Implements operator '!'
		 */
		public BooleanNode negate() {
			if (isTrueValue())
				return FALSE;
			else if (isFalseValue())
				return TRUE;
			else
				return UNKNOWN;
		}
		
		@Override
		public BooleanNode isEqualTo(DataNode dataNode) {
			if (isUnknownValue())
				return UNKNOWN;
			
			return super.isEqualTo(dataNode);
		}
		
		@Override
		public BooleanNode isIdenticalTo(DataNode dataNode) {
			if (isUnknownValue())
				return UNKNOWN;
			
			return super.isEqualTo(dataNode);
		}
	}
	
	public static class ControlNode extends SpecialNode {
		public static ControlNode RETURN 	= new ControlNode();
		public static ControlNode BREAK 	= new ControlNode();
	}

}

package edu.iastate.symex.datamodel.nodes;

import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.DataModelVisitor;

/**
 * Used for special values (e.g., Boolean values or returned codes of statements).
 * @author HUNG
 * 
 */
public abstract class SpecialNode extends DataNode {
	
	@Override
	public void accept(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitSpecialNode(this);
	}
	
	/**
	 * Represents Boolean values.
	 */
	public static class BooleanNode extends SpecialNode {
		
		public static BooleanNode TRUE		= new BooleanNode();
		public static BooleanNode FALSE		= new BooleanNode();
		public static BooleanNode UNKNOWN	= new BooleanNode();
		
		/**
		 * Private constructor.
		 */
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
		
		/**
		 * Implements operator '=='
		 * @see edu.iastate.symex.datamodel.nodes.DataNode.isEqualTo(DataNode)
		 */
		public BooleanNode isEqualTo(BooleanNode booleanNode) {
			if (isUnknownValue() || booleanNode.isUnknownValue())
				return UNKNOWN;
			
			if (isTrueValue() && booleanNode.isTrueValue()
					|| isFalseValue() && booleanNode.isFalseValue())
				return TRUE;
			else
				return FALSE;
		}
		
		/**
		 * Implements operator '==='
		 * @see edu.iastate.symex.datamodel.nodes.DataNode.isIdenticalTo(DataNode)
		 */
		public BooleanNode isIdenticalTo(BooleanNode booleanNode) {
			return isEqualTo(booleanNode);
		}

	}
	
	/**
	 * Represents a reference to a PhpVariable (e.g., $x = &$y)
	 */
	public static class ReferenceNode extends SpecialNode {
	
		private PhpVariable phpVariable;
		
		/**
		 * Protected constructor, called from DataNodeFactory only.
		 */
		protected ReferenceNode(PhpVariable phpVariable) {
			this.phpVariable = phpVariable;
		}
		
		public PhpVariable getPhpVariable() {
			return phpVariable;
		}
		
	}
	
	/**
	 * Represents UNSET values.
	 */
	public static class UnsetNode extends SpecialNode {
		
		public static UnsetNode UNSET	= new UnsetNode();	// Values of uninitialized variables
		
		/**
		 * Private constructor.
		 */
		private UnsetNode() {
		}
		
	}
	
	/**
	 * Represents returned codes of statements
	 */
	public static class ControlNode extends SpecialNode {
		
		public static ControlNode OK		= new ControlNode(); // Represents the normal case
		public static ControlNode EXIT		= new ControlNode();
		public static ControlNode RETURN 	= new ControlNode();
		public static ControlNode BREAK 	= new ControlNode();
		public static ControlNode CONTINUE 	= new ControlNode();
		
		/**
		 * Private constructor.
		 */
		private ControlNode() {
		}
		
	}

}

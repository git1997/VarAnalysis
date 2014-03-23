package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Assignment;

import php.ElementManager;
import php.elements.PhpArrayElement;
import php.elements.PhpVariable;
import util.logging.MyLevel;
import util.logging.MyLogger;
import datamodel.nodes.ArrayNode;
import datamodel.nodes.ConcatNode;
import datamodel.nodes.DataNode;
import datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class AssignmentNode extends ExpressionNode {
	
	private int operator;
	private VariableBaseNode variableBaseNode;
	private ExpressionNode expressionNode;
	
	/*
	Represents an assignment statement. 
	
	e.g. $a = 5,
	 $a += 5,
	 $a .= $b,
	*/
	public AssignmentNode(Assignment assignment) {
		super(assignment);
		this.operator = assignment.getOperator();
		this.variableBaseNode = VariableBaseNode.createInstance(assignment.getLeftHandSide());
		this.expressionNode = ExpressionNode.createInstance(assignment.getRightHandSide());
	}
	
	/*
	 * Get properties
	 */
	
	public VariableBaseNode getVariableBaseNode() {
		return variableBaseNode;
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		PhpVariable phpVariable = variableBaseNode.createVariablePossiblyWithNull(elementManager);
		DataNode rightHandSideValue = expressionNode.execute(elementManager);
		
		/*
		 * Handle array assignment, e.g. $x[1] = abc.
		 */
		if (phpVariable instanceof PhpArrayElement) {
			String arrayName = ((PhpArrayElement) phpVariable).getName();
			String key = ((PhpArrayElement) phpVariable).getKey();
			
			PhpVariable newPhpArray = new PhpVariable(arrayName);
			PhpVariable oldPhpArray = elementManager.getVariableFromFunctionScope(arrayName);
			if (oldPhpArray != null && oldPhpArray.getDataNode() instanceof ArrayNode)
				newPhpArray.setDataNode(oldPhpArray.getDataNode().clone());	// TODO: Get a clone because we don't want to modify the ArrayNode of the oldPhpArray
			else
				newPhpArray.setDataNode(new ArrayNode());
			ArrayNode arrayNode = (ArrayNode) newPhpArray.getDataNode();
			
			switch (operator) {
				// '='
				case Assignment.OP_EQUAL:
					arrayNode.setElement(key, rightHandSideValue);					
					break;
				
				// '.='		
				case Assignment.OP_CONCAT_EQUAL:
					if (arrayNode.getElement(key) != null)
						arrayNode.setElement(key, new ConcatNode(arrayNode.getElement(key), rightHandSideValue));
					else
						arrayNode.setElement(key, rightHandSideValue);
					break;
					
				default:				
					MyLogger.log(MyLevel.TODO, "In AssignmentNode.java: Assignment Operator " + Assignment.getOperator(operator) + " not yet implemented.");
					arrayNode.setElement(key, new SymbolicNode(this));
					break;
			}
			elementManager.putVariableInCurrentScope(newPhpArray);
		}
		
		/*
		 * Handle regualar variable assignment, e.g. $x = abc.
		 */
		else if (phpVariable != null) {
			switch (operator) {
				// '='
				case Assignment.OP_EQUAL:
					phpVariable.setDataNode(rightHandSideValue);					
					break;
					
				// '.='	
				case Assignment.OP_CONCAT_EQUAL:
					PhpVariable oldVariable = elementManager.getVariableFromFunctionScope(phpVariable.getName());
					if (oldVariable != null)
						phpVariable.setDataNode(new ConcatNode(oldVariable.getDataNode(), rightHandSideValue));
					else
						phpVariable.setDataNode(rightHandSideValue);
					break;
					
				// '+='
				case Assignment.OP_PLUS_EQUAL:
					phpVariable.setDataNode(new SymbolicNode(this));
					break;
					
				// '*='		
				case Assignment.OP_MUL_EQUAL:
					phpVariable.setDataNode(new SymbolicNode(this));
					break;
					
				default:	
					MyLogger.log(MyLevel.TODO, "In AssignmentNode.java: Assignment Operator " + Assignment.getOperator(operator) + " not yet implemented.");
					phpVariable.setDataNode(new SymbolicNode(this));
					break;
			}
			elementManager.putVariableInCurrentScope(phpVariable);
		}
		
		return rightHandSideValue;
	}
	
}
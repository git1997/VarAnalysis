package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Assignment;

import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpArrayElement;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.ArrayNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;

/**
 * 
 * @author HUNG
 *
 */
public class AssignmentNode extends ExpressionNode {
	
	private int operator;
	private VariableBaseNode leftHandSide;
	private ExpressionNode rightHandSide;
	
	/*
	Represents an assignment statement. 
	
	e.g. $a = 5,
	 $a += 5,
	 $a .= $b,
	*/
	public AssignmentNode(Assignment assignment) {
		super(assignment);
		this.operator = assignment.getOperator();
		this.leftHandSide = VariableBaseNode.createInstance(assignment.getLeftHandSide());
		this.rightHandSide = ExpressionNode.createInstance(assignment.getRightHandSide());
	}
	
	/*
	 * Get properties
	 */
	
	public VariableBaseNode getLeftHandSide() {
		return leftHandSide;
	}
	
	@Override
	public DataNode execute(Env env) {
		PhpVariable phpVariable = leftHandSide.createVariablePossiblyWithNull(env);
		DataNode rightHandSideValue = rightHandSide.execute(env);
		
		/*
		 * Handle array assignment, e.g. $x[1] = abc.
		 */
		if (phpVariable instanceof PhpArrayElement) {
			String arrayName = ((PhpArrayElement) phpVariable).getName();
			String key = ((PhpArrayElement) phpVariable).getKey();
			
			PhpVariable newPhpArray = new PhpVariable(arrayName);
			PhpVariable oldPhpArray = env.readVariable(arrayName);
			if (oldPhpArray != null && oldPhpArray.getDataNode() instanceof ArrayNode)
				newPhpArray.setDataNode(oldPhpArray.getDataNode());	// TODO: Should we get a clone? (because we don't want to modify the ArrayNode of the oldPhpArray)
			else
				newPhpArray.setDataNode(DataNodeFactory.createArrayNode());
			ArrayNode arrayNode = (ArrayNode) newPhpArray.getDataNode();
			
			switch (operator) {
				// '='
				case Assignment.OP_EQUAL:
					arrayNode.setElement(key, rightHandSideValue);					
					break;
				
				// '.='		
				case Assignment.OP_CONCAT_EQUAL:
					if (arrayNode.getElement(key) != null)
						arrayNode.setElement(key, DataNodeFactory.createCompactConcatNode(arrayNode.getElement(key), rightHandSideValue));
					else
						arrayNode.setElement(key, rightHandSideValue);
					break;
					
				default:				
					MyLogger.log(MyLevel.TODO, "In AssignmentNode.java: Assignment Operator " + Assignment.getOperator(operator) + " not yet implemented.");
					arrayNode.setElement(key, DataNodeFactory.createSymbolicNode(this));
					break;
			}
			env.writeVariable(newPhpArray);
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
					PhpVariable oldVariable = env.readVariable(phpVariable.getName());
					if (oldVariable != null)
						phpVariable.setDataNode(DataNodeFactory.createCompactConcatNode(oldVariable.getDataNode(), rightHandSideValue));
					else
						phpVariable.setDataNode(rightHandSideValue);
					break;
					
				// '+='
				case Assignment.OP_PLUS_EQUAL:
					phpVariable.setDataNode(DataNodeFactory.createSymbolicNode(this));
					break;
					
				// '*='		
				case Assignment.OP_MUL_EQUAL:
					phpVariable.setDataNode(DataNodeFactory.createSymbolicNode(this));
					break;
					
				default:	
					MyLogger.log(MyLevel.TODO, "In AssignmentNode.java: Assignment Operator " + Assignment.getOperator(operator) + " not yet implemented.");
					phpVariable.setDataNode(DataNodeFactory.createSymbolicNode(this));
					break;
			}
			env.writeVariable(phpVariable);
		}
		
		return rightHandSideValue;
	}
	
}
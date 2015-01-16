package edu.iastate.symex.php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Assignment;

import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpListVariable;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.ArrayNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.SpecialNode;
import edu.iastate.symex.instrumentation.WebAnalysis;

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
	
	@Override
	public DataNode execute(Env env) {
		DataNode rightHandSideValue = rightHandSide.execute(env);
		PhpVariable phpVariable = leftHandSide.createVariablePossiblyWithNull(env);
		
		/*
		 * Handle reference variables, e.g., $x = &$y
		 */
		if (rightHandSideValue instanceof edu.iastate.symex.datamodel.nodes.SpecialNode.ReferenceNode) {
			if (leftHandSide instanceof VariableNode) {
				String variableName = ((VariableNode) leftHandSide).getResolvedVariableNameOrNull(env); // Causing re-evaluation of leftHandSide, but it's probably Okay
				if (variableName != null) {
					phpVariable = ((edu.iastate.symex.datamodel.nodes.SpecialNode.ReferenceNode) rightHandSideValue).getPhpVariable();
					env.putVariable(variableName, phpVariable);
				}
			}
		}
		/*
		 * Handle list assignment, e.g., list($a, $b) = array(1, 2)
		 */
		else if (phpVariable instanceof PhpListVariable) {
			if (rightHandSideValue instanceof ArrayNode) {
				ArrayList<PhpVariable> phpVariables = ((PhpListVariable) phpVariable).getVariables();
				ArrayList<DataNode> values = ((ArrayNode) rightHandSideValue).getElementValues();
				
				if (phpVariables.size() == values.size()) {
					for (int i = 0; i < phpVariables.size(); i++)
						if (phpVariables.get(i) != null)
							assign(phpVariables.get(i), values.get(i), env);
				}
			}
		}
		/*
		 * Handle a regular assignment
		 */
		else if (phpVariable != null) {
			assign(phpVariable, rightHandSideValue, env);
			
			/*
			 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
			 */
			// BEGIN OF WEB ANALYSIS CODE
			if (WebAnalysis.isEnabled())
				WebAnalysis.onAssignmentExecute((Assignment) this.getAstNode(), phpVariable, env);
			// END OF WEB ANALYSIS CODE
		}
		
		return rightHandSideValue;
	}
	
	private void assign(PhpVariable phpVariable, DataNode rightHandSideValue, Env env) {
		DataNode oldValue = phpVariable.getValue();
		DataNode newValue;
		
		if (oldValue == SpecialNode.UnsetNode.UNSET)
			newValue = rightHandSideValue;
		else {
			switch (operator) {
				// '='
				case Assignment.OP_EQUAL:
					newValue = rightHandSideValue;
					break;
					
				// '.='	
				case Assignment.OP_CONCAT_EQUAL:
					newValue = DataNodeFactory.createCompactConcatNode(oldValue, rightHandSideValue);
					break;
					
				// '+='
				case Assignment.OP_PLUS_EQUAL:
					newValue = DataNodeFactory.createSymbolicNode(this);
					break;
					
				// '*='		
				case Assignment.OP_MUL_EQUAL:
					newValue = DataNodeFactory.createSymbolicNode(this);
					break;
					
				default:	
					MyLogger.log(MyLevel.TODO, "In AssignmentNode.java: Assignment Operator " + Assignment.getOperator(operator) + " not yet implemented.");
					newValue = DataNodeFactory.createSymbolicNode(this);
					break;
			}
		}
		
		// Update env
		env.writeVariable(phpVariable, newValue);
	}
	
}
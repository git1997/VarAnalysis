package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.SingleFieldDeclaration;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class SingleFieldDeclarationNode extends PhpNode {
	
	private VariableNode name;
	private ExpressionNode value = null; // Can be null
	
	/*
	Represents a fields declaration
	
	e.g.
	 var $a, $b;
	 public $a = 3;
	 final private static $var;
	*/
	public SingleFieldDeclarationNode(SingleFieldDeclaration singleFieldDeclaration) {
		super(singleFieldDeclaration);
		name = new VariableNode(singleFieldDeclaration.getName());
		if (singleFieldDeclaration.getValue() != null)
			value = ExpressionNode.createInstance(singleFieldDeclaration.getValue());
	}
	
	/**
	 * Returns the name of the SingleFieldDeclaration before run time.
	 * @see edu.iastate.symex.php.nodes.FormalParameterNode.getParameterNameBeforeRunTimeOrNull() 
	 */
	public String getFieldNameBeforeRunTimeOrNull() {
		return name.getVariableNameBeforeRunTimeOrNull();
	}
	
	/**
	 * Returns the value of the SingleFieldDeclaration, can be NULL.
	 */
	public ExpressionNode getValue() {
		return value;
	}
	
	@Override
	public DataNode execute(Env env) {
		MyLogger.log(MyLevel.USER_EXCEPTION, "In SingleFieldDeclaration.java: This should not get executed.");
		return SpecialNode.ControlNode.OK;
	}

}
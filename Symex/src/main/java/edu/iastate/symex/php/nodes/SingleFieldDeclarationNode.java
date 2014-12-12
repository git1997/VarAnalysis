package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.SingleFieldDeclaration;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class SingleFieldDeclarationNode extends PhpNode {
	
	private VariableNode name;
	private ExpressionNode value;
	
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
		value = ExpressionNode.createInstance(singleFieldDeclaration.getValue());
	}
	
	public VariableNode getName() {
		return name;
	}
	
	public ExpressionNode getValue() {
		return value;
	}
	
	@Override
	public DataNode execute(Env env) {
		MyLogger.log(MyLevel.USER_EXCEPTION, "In SingleFieldDeclaration.java: This should not get executed.");
		return null;
	}

}
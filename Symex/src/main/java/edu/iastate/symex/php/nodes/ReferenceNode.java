package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Reference;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class ReferenceNode extends ExpressionNode {
	
	private ExpressionNode expression;
	
	/*
	Represents an reference to a variable or class instantiation.
	
	e.g.
	 &$a,
	  &new MyClass()
	  &foo()
	*/
	public ReferenceNode(Reference reference) {
		super(reference);
		expression = ExpressionNode.createInstance(reference.getExpression());
	}
	
	@Override
	public DataNode execute(Env env) {
		if (expression instanceof VariableBaseNode) {
			PhpVariable phpVariable = ((VariableBaseNode) expression).createVariablePossiblyWithNull(env);
			if (phpVariable != null)
				return DataNodeFactory.createReferenceNode(phpVariable);
			else
				return DataNodeFactory.createSymbolicNode(this);
		}
		
		MyLogger.log(MyLevel.TODO, "In ReferenceNode.java: Expression not yet implemented: " + this.getSourceCode());
		return DataNodeFactory.createSymbolicNode(this);
	}
	
}
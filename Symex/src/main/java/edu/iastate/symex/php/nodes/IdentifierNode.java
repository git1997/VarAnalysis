package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Identifier;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class IdentifierNode extends ExpressionNode {
	
	private String name;	// The name of the identifier
	
	/*
	Holds an identifier.
	uses for variable name, function name and class name. 

	e.g.  $variableName - variableName is the identifier,
	 foo() - foo is the identifier,
	 $myClass->fun() - myClass and fun are identifiers  
	*/
	public IdentifierNode(Identifier identifer) {
		super(identifer);
		name = identifer.getName();
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public DataNode execute(Env env) {
		// Generally, this node should not be executed.
		// However, it can be executed in cases like $_GET[input1] where input1 is an Identifier.
		MyLogger.log(MyLevel.USER_EXCEPTION, "In IdentifierNode.java: IdentifierNode + " + this.getSourceCode() + " should not get executed.");

		return DataNodeFactory.createLiteralNode(this);
	}
	
}
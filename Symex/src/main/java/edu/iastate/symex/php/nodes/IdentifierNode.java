package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.Identifier;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;

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
		return DataNodeFactory.createLiteralNode(this);
	}
	
}
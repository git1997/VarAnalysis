package php.nodes;

import logging.MyLevel;
import logging.MyLogger;

import org.eclipse.php.internal.core.ast.nodes.Identifier;

import php.ElementManager;

import datamodel.nodes.DataNode;
import datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class IdentifierNode extends ExpressionNode {
	
	private String name;		// The name of the identifer.
	
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
	
	/*
	 * Get properties
	 */
	
	public String getName() {
		return name;
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		MyLogger.log(MyLevel.USER_EXCEPTION, "In IdentifierNode.java: IdentiferNode + " + this.name + " should not get executed.");
		return new SymbolicNode(this);
	}
	
}
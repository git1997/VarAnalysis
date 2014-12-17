package edu.iastate.symex.core;

import java.util.ArrayList;

import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 * 
 * A PhpListVariable corresponds to the PHP list construct and represents a list of variables.
 * (e.g., list($a, $b) = array(1, 2))
 *
 */
public class PhpListVariable extends PhpVariable {
	
	private ArrayList<PhpVariable> variables;
	
	/**
	 * Constructor
	 */
	public PhpListVariable(ArrayList<PhpVariable> variables) {
		super(""); // The arguments to this super call is not important
		this.variables = variables;
	}
	
	public ArrayList<PhpVariable> getVariables() {
		return new ArrayList<PhpVariable>(variables);
	}
	
	@Override
	public String getName() {
		MyLogger.log(MyLevel.ALL, "In PhpListVariable.java: PhpListVariable.getName() should not get executed.");
		return super.getName();
	}
	
	@Override
	public DataNode getValue() {
		MyLogger.log(MyLevel.ALL, "In PhpListVariable.java: PhpListVariable.getValue() should not get executed.");
		return super.getValue();
	}

	@Override
	protected void setValue(DataNode value) {
		MyLogger.log(MyLevel.ALL, "In PhpListVariable.java: PhpListVariable.setValue() should not get executed.");
		super.setValue(value);
	}
	
}

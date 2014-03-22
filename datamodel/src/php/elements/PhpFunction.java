package php.elements;

import java.util.HashSet;

import php.ElementManager;
import php.nodes.FunctionDeclarationNode;

import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class PhpFunction extends PhpElement {
	
	private FunctionDeclarationNode functionDeclarationNode;
	
	/**
	 * Constructor
	 * @param functionDeclarationNode
	 */
	public PhpFunction(FunctionDeclarationNode functionDeclarationNode) {
		this.functionDeclarationNode = functionDeclarationNode;
	}
	
	/*
	 * Get properties
	 */
	
	public FunctionDeclarationNode getFunctionDeclarationNode() {
		return functionDeclarationNode;
	}
	
	/*
	 * Provide formatting for Graphviz.
	 */

	@Override
	public String printGraphToGraphvizFormat(HashSet<DataNode> setOfPrintedNodes) {
		// Execute the function first
		ElementManager functionElementManager = new ElementManager();
		functionDeclarationNode.getBodyNode().execute(functionElementManager);
		
		// Then show the values of the variables
		StringBuilder string = new StringBuilder();
		string.append("subgraph cluster_" + functionDeclarationNode.getFunctionName() + " {\r\n");
		string.append("label=" + functionDeclarationNode.getFunctionName() + "\r\n");
		for (PhpVariable phpVariable : functionElementManager.getAllVariables()) {
			string.append(phpVariable.printGraphToGraphvizFormat(setOfPrintedNodes));
		}
		string.append("}\r\n");
		return string.toString();
	}
	
}

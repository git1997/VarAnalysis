package php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.FormalParameter;
import org.eclipse.php.internal.core.ast.nodes.FunctionDeclaration;

import php.ElementManager;
import php.elements.PhpFunction;
import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class FunctionDeclarationNode extends StatementNode {
	
	private String functionName;	// The name of the function	
	
	private ArrayList<FormalParameterNode> formalParameterNodes = new ArrayList<FormalParameterNode>();	// The parameters of the function 
	
	private BlockNode bodyNode;	// The body of the function
	
	/*
	Represents a function declaration 
	
	e.g. function foo() {}
	 
	 function &foo() {}
	 
	 function foo($a, int $b, $c = 5, int $d = 6) {}
	 
	 function foo(); -abstract function in class declaration
	*/
	public FunctionDeclarationNode(FunctionDeclaration functionDeclaration) {
		this.functionName = functionDeclaration.getFunctionName().getName();
		for (FormalParameter formalParameter : functionDeclaration.formalParameters()) {
			this.formalParameterNodes.add(new FormalParameterNode(formalParameter));
		}
		this.bodyNode = new BlockNode(functionDeclaration.getBody());
	}
	
	/*
	 * Get properties
	 */
	
	public String getFunctionName() {
		return functionName;
	}
	
	public ArrayList<FormalParameterNode> getFormalParameterNodes() {
		return new ArrayList<FormalParameterNode>(formalParameterNodes);
	}
	
	public BlockNode getBodyNode() {
		return bodyNode;
	}
	
	/*
	 * The following code is used from BabelRef to identify PHP variable entities.
	 */
	// BEGIN OF BABELREF CODE
	public interface IFormalParameterListener {
		public void formalParameterFound(IdentifierNode variableName, String scope);
	}
	
	public static IFormalParameterListener formalParameterListener = null;
	// END OF BABELREF CODE

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		/*
		 * The following code is used from BabelRef to identify PHP variable entities.
		 */
		// BEGIN OF BABELREF CODE
		if (formalParameterListener != null) {
			for (FormalParameterNode formalParameterNode : formalParameterNodes) {
				if (formalParameterNode.getParameterNameExpressionNode() instanceof IdentifierNode) {
					IdentifierNode parameterName = (IdentifierNode) formalParameterNode.getParameterNameExpressionNode();
					String scope = "FUNCTION_SCOPE_" + getFunctionName(); // @see php.nodes.VariableNode.getFunctionScope(ElementManager)
					formalParameterListener.formalParameterFound(parameterName, scope);
				}
			}
		}
		// END OF BABELREF CODE
			
		elementManager.putFunction(this.getFunctionName(), new PhpFunction(this));
		return null;
	}

}
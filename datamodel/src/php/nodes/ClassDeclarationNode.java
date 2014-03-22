package php.nodes;

import java.util.HashMap;

import org.eclipse.php.internal.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.ast.nodes.FieldsDeclaration;
import org.eclipse.php.internal.core.ast.nodes.MethodDeclaration;
import org.eclipse.php.internal.core.ast.nodes.Statement;

import php.ElementManager;
import php.elements.PhpClass;

import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class ClassDeclarationNode extends StatementNode {

	private String className;	// The name of this class
	
	private HashMap<String, FunctionDeclarationNode> functionDeclarationNodes;		// The functions declared in this class
	
	/*
	Represents a class declaration 

	 e.g. 
	 class MyClass { },
	 class MyClass extends SuperClass implements Interface1, Interface2 { 
	   const MY_CONSTANT = 3; 
	   public static final $myVar = 5, $yourVar; 
	   var $anotherOne; 
	   private function myFunction($a) { }
	 }
	*/
	public ClassDeclarationNode(ClassDeclaration classDeclaration) {
		className = classDeclaration.getName().getName();
		functionDeclarationNodes = new HashMap<String, FunctionDeclarationNode>();
		for (Statement statement : classDeclaration.getBody().statements()) {
			if (statement instanceof FieldsDeclaration) {
				//FieldsDeclaration fieldsDeclaration = (FieldsDeclaration) statement;
			}
			if (statement instanceof MethodDeclaration) {
				FunctionDeclarationNode functionDeclarationNode = new FunctionDeclarationNode(((MethodDeclaration) statement).getFunction());
				functionDeclarationNodes.put(functionDeclarationNode.getFunctionName(), functionDeclarationNode);
			}
		}
	}
	
	/*
	 * Get properties
	 */
	
	public String getClassName() {
		return className;
	}
	
	public FunctionDeclarationNode getFunction(String functionName) {
		return functionDeclarationNodes.get(functionName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		elementManager.putClass(this.getClassName(), new PhpClass(this));
		return null;
	}

}

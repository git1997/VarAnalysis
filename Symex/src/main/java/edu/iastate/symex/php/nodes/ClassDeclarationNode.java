package edu.iastate.symex.php.nodes;

import java.util.HashMap;

import org.eclipse.php.internal.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.ast.nodes.FieldsDeclaration;
import org.eclipse.php.internal.core.ast.nodes.MethodDeclaration;
import org.eclipse.php.internal.core.ast.nodes.Statement;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.php.elements.PhpClass;

/**
 * 
 * @author HUNG
 *
 */
public class ClassDeclarationNode extends StatementNode {

	private String name;	// The name of this class
	
	private HashMap<String, FunctionDeclarationNode> functionDeclarations;	// The functions declared in this class
	
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
		super(classDeclaration);
		name = classDeclaration.getName().getName();
		functionDeclarations = new HashMap<String, FunctionDeclarationNode>();
		for (Statement statement : classDeclaration.getBody().statements()) {
			if (statement instanceof FieldsDeclaration) {
				//FieldsDeclaration fieldsDeclaration = (FieldsDeclaration) statement;
			}
			else if (statement instanceof MethodDeclaration) {
				FunctionDeclarationNode functionDeclarationNode = new FunctionDeclarationNode(((MethodDeclaration) statement).getFunction());
				functionDeclarations.put(functionDeclarationNode.getName(), functionDeclarationNode);
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public FunctionDeclarationNode getFunction(String functionName) {
		return functionDeclarations.get(functionName);
	}
	
	@Override
	public DataNode execute(Env env) {
		env.putClass(this.getName(), this);
		return null;
	}

}

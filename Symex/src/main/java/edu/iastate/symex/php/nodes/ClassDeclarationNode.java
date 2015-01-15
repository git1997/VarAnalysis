package edu.iastate.symex.php.nodes;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.php.internal.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.ast.nodes.FieldsDeclaration;
import org.eclipse.php.internal.core.ast.nodes.MethodDeclaration;
import org.eclipse.php.internal.core.ast.nodes.SingleFieldDeclaration;
import org.eclipse.php.internal.core.ast.nodes.Statement;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;
import edu.iastate.symex.util.ASTHelper;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class ClassDeclarationNode extends StatementNode {

	private String name;	// The name of this class
	
	private HashMap<String, SingleFieldDeclarationNode> fieldDeclarations;	// The fields declared in this class
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
		fieldDeclarations = new HashMap<String, SingleFieldDeclarationNode>();
		functionDeclarations = new HashMap<String, FunctionDeclarationNode>();
		
		for (Statement statement : classDeclaration.getBody().statements()) {
			if (statement instanceof FieldsDeclaration) {
				for (SingleFieldDeclaration field : ((FieldsDeclaration) statement).fields()) {
					SingleFieldDeclarationNode fieldDeclarationNode = new SingleFieldDeclarationNode(field);
					String name = fieldDeclarationNode.getFieldNameBeforeRunTimeOrNull();
					if (name != null)
						fieldDeclarations.put(name, fieldDeclarationNode);
				}
			}
			else if (statement instanceof MethodDeclaration) {
				FunctionDeclarationNode functionDeclarationNode = new FunctionDeclarationNode(((MethodDeclaration) statement).getFunction());
				functionDeclarations.put(functionDeclarationNode.getName(), functionDeclarationNode);
			}
			else {
				MyLogger.log(MyLevel.TODO, "In ClassDeclarationNode.java: Statement unimplemented " + ASTHelper.inst.getSourceCodeOfPhpASTNode(statement));
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public SingleFieldDeclarationNode getField(String fieldName) {
		return fieldDeclarations.get(fieldName);
	}
	
	public ArrayList<SingleFieldDeclarationNode> getFields() {
		return new ArrayList<SingleFieldDeclarationNode>(fieldDeclarations.values());
	}
	
	public FunctionDeclarationNode getFunction(String functionName) {
		return functionDeclarations.get(functionName);
	}
	
	@Override
	public DataNode execute_(Env env) {
		env.putClass(this.getName(), this);
		return SpecialNode.ControlNode.OK;
	}

}

package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ClassInstanceCreation;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.ObjectNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.php.elements.PhpClass;

/**
 * 
 * @author HUNG
 *
 */
public class ClassInstanceCreationNode extends ExpressionNode {

	private ClassNameNode className;
	
	/*
	Represents a class instantiation. This class holds the class name as an expression and array of constructor parameters 

	e.g. new MyClass(),
	 new $a('start'),
	 new foo()(1, $a)
	*/
	public ClassInstanceCreationNode(ClassInstanceCreation classInstanceCreation) {
		super(classInstanceCreation);
		className = new ClassNameNode(classInstanceCreation.getClassName());
		//classInstanceCreation.ctorParams();
	}
	
	@Override
	public DataNode execute(Env env) {
		// Get the class name
		String resolvedClassName = className.getResolvedNameOrNull(env);	
		
		// Get the PHP class
		PhpClass phpClass = env.getClass(resolvedClassName);
		
		// Return an object, or a SymbolicNode if the class name is not found
		if (phpClass != null)
			return new ObjectNode(phpClass.getClassDeclarationNode());
		else
			return new SymbolicNode(this);
	}

}

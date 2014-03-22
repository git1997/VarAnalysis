package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ClassInstanceCreation;

import php.ElementManager;
import php.elements.PhpClass;

import datamodel.nodes.DataNode;
import datamodel.nodes.ObjectNode;
import datamodel.nodes.SymbolicNode;

/**
 * 
 * @author HUNG
 *
 */
public class ClassInstanceCreationNode extends ExpressionNode {

	private ClassNameNode classNameNode;
	
	/*
	Represents a class instantiation. This class holds the class name as an expression and array of constructor parameters 

	e.g. new MyClass(),
	 new $a('start'),
	 new foo()(1, $a)
	*/
	public ClassInstanceCreationNode(ClassInstanceCreation classInstanceCreation) {
		super(classInstanceCreation);
		classNameNode = new ClassNameNode(classInstanceCreation.getClassName());
		//classInstanceCreation.ctorParams();
	}
	
	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		// Get the class name
		String className = classNameNode.resolveClassName(elementManager);	
		
		// Get the PHP class
		PhpClass phpClass = elementManager.getClass(className);
		
		// Return an object, or a SymbolicNode if the class name is not found
		if (phpClass != null)
			return new ObjectNode(phpClass.getClassDeclarationNode());
		else
			return new SymbolicNode(this);
	}

}

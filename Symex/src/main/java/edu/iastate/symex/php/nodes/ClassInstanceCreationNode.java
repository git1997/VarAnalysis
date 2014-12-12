package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ClassInstanceCreation;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.core.PhpVariable;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.ObjectNode;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class ClassInstanceCreationNode extends VariableBaseNode {

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
		// TODO Implement constructor with parameters
		//classInstanceCreation.ctorParams();
	}
	
	@Override
	public DataNode execute(Env env) {
		// Get the class name
		String resolvedClassName = className.getResolvedNameOrNull(env);	
		
		// Get the PHP class
		ClassDeclarationNode phpClass = (resolvedClassName != null ? env.getClass(resolvedClassName) : null);
		
		if (phpClass != null) {
			ObjectNode object = DataNodeFactory.createObjectNode(phpClass);
			
			// Initialize the object's fields
			for (SingleFieldDeclarationNode field : phpClass.getFields()) {
				VariableNode nameNode = field.getName();
				ExpressionNode valueNode = field.getValue(); // TODO Can valueNode be null?
				
				String name = nameNode.getResolvedVariableNameOrNull(env);
				DataNode value = valueNode.execute(env);
				if (name != null)
					object.putFieldValue(name, value);
			}
			
			return object;
		}
		else // Return a SymbolicNode if the class name is not found
			return DataNodeFactory.createSymbolicNode(this);
	}

	@Override
	public PhpVariable createVariablePossiblyWithNull(Env env) {
		MyLogger.log(MyLevel.TODO, "In ClassInstanceCreationNode.java: Don't know how to create a variable from a classInstanceCreation.");
		return null;
	}

}

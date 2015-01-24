package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ClassName;

import edu.iastate.symex.core.Env;

/**
 * 
 * @author HUNG
 *
 */
public class ClassNameNode extends PhpNode {

	private ExpressionNode name;	// The name of the class
	
	/*
	Holds a class name. note that the class name can be expression, 

	e.g. MyClass,
	 getClassName() - the function getClassName return a class name
	 $className - the variable $a holds the class name
	*/
	public ClassNameNode(ClassName className) {
		super(className);
		name = ExpressionNode.createInstance(className.getName());
	}
	
	/**
	 * Resolves the name of the class.
	 */
	public String getResolvedClassNameOrNull(Env env) {
		return name.execute(env).getExactStringValueOrNull();
	}

}

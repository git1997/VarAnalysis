package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ClassName;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

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
	public String getResolvedNameOrNull(Env env) {
		return name.getResolvedNameOrNull(env);
	}

	@Override
	public DataNode execute(Env env) {
		MyLogger.log(MyLevel.USER_EXCEPTION, "In ClassNameNode.java: ClassNameNode + " + this.getSourceCode() + " should not get executed.");
		return null;
	}
	
}

package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ClassName;

import php.ElementManager;

import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class ClassNameNode extends PhpNode {

	private ExpressionNode classNameExpressionNode;	// The name of the class
	private String className = null;
	
	/*
	Holds a class name. note that the class name can be expression, 

	e.g. MyClass,
	 getClassName() - the function getClassName return a class name
	 $className - the variable $a holds the class name
	*/
	public ClassNameNode(ClassName className) {
		classNameExpressionNode = ExpressionNode.createInstance(className.getName());
	}
	
	/**
	 * Resolves the name of the class.
	 */
	public String resolveClassName(ElementManager elementManager) {
		if (className == null)
			className = classNameExpressionNode.resolveName(elementManager);
		return className;
	}

	/*
	 * (non-Javadoc)
	 * @see servergraph.nodes.PhpNode#execute(servergraph.ElementManager)
	 */
	@Override
	public DataNode execute(ElementManager elementManager) {
		return null;	// This function should not be called
	}
	
}

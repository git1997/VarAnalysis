package php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;

import php.ElementManager;
import php.TraceTable;

import sourcetracing.Location;
import sourcetracing.SourceCodeLocation;
import sourcetracing.UndefinedLocation;
import datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public abstract class PhpNode {
	
	private Location location;	// Its location in the source code.
	
	private String stringValue;	// The source code of the PhpNode
	
	/**
	 * Empty constructor
	 */
	public PhpNode() {
		location = UndefinedLocation.inst;
		stringValue = "";
	}
	
	/**
	 * Constructor
	 * @param astNode
	 */
	public PhpNode(ASTNode astNode) {
		String filePath = TraceTable.getCurrentSourceFileRelativePathOfPhpASTNode(astNode);	
		int position = astNode.getStart();
		
		location = new SourceCodeLocation(filePath, position);
		stringValue = TraceTable.getSourceCodeOfPhpASTNode(astNode);
	}
	
	/**
	 * Returns the location of the PhpNode in the source code.
	 * Note that the location may not be available (can be undefined).
	 */
	public Location getLocation() {
		return location;
	}
	
	/**
	 * Returns the source code of the PhpNode.
	 * Note that the source code may not be available (can be empty).
	 */
	public String getStringValue() {
		return stringValue;
	}
		
	/**
	 * Executes the given server node and updates elementManager along the way.
	 * @param elementManager contains PHP elements such as variables and functions during the execution.
	 * @return The client node describing the value of an expression, if the given server node is an expression.
	 */
	public abstract DataNode execute(ElementManager elementManager);
	
}

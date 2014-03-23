package datamodel.nodes;

import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import php.nodes.ClassDeclarationNode;


/**
 * 
 * @author HUNG
 *
 */
public class ObjectNode extends DataNode {
	
	private ClassDeclarationNode classDeclarationNode;
	
	/**
	 * Constructor
	 * @param classDeclarationNode
	 */
	public ObjectNode(ClassDeclarationNode classDeclarationNode) {
		this.classDeclarationNode = classDeclarationNode;
	}

	@Override
	public DataNode clone() {
		return new ObjectNode(classDeclarationNode);
	}

	/*
	 * Get properties
	 */
	
	public ClassDeclarationNode getClassDeclarationNode() {
		return classDeclarationNode;
	}
	
	public String getSymbolicValue() {
		return "__OBJECT_" + this.hashCode() + "__";
	}
	
	public static String getSymbolicValueRegularExpression() {
		return "__OBJECT_\\d+__";
	}
	
	@Override
	public String getApproximateStringValue() {
		return this.getSymbolicValue();
	}

	/*
	 * Provide formatting for XML.
	 */
	
	@Override
	public Element printGraphToXmlFormat(Document document,	HashSet<DataNode> parentNodes) {
		LiteralNode literalNode = new LiteralNode(this.getApproximateStringValue());
		return literalNode.printGraphToXmlFormat(document, parentNodes);
	}
	
	/*
	 * Provide formatting for Graphviz.
	 */
	
	@Override
	public String printGraphToGraphvizFormat(HashSet<DataNode> setOfPrintedNodes) {
		LiteralNode literalNode = new LiteralNode(this.getApproximateStringValue());
		return literalNode.printGraphToGraphvizFormat(setOfPrintedNodes);
	}

	@Override
	public void visit(DataModelVisitor dataModelVisitor) {
		dataModelVisitor.visitObjectNode(this);
	}

}

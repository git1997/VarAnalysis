package references.detection;

import java.util.Stack;

import constraints.Constraint;
import datamodel.nodes.ext.ConcatNode;
import datamodel.nodes.ext.DataNode;
import datamodel.nodes.ext.LiteralNode;
import datamodel.nodes.ext.RepeatNode;
import datamodel.nodes.ext.SelectNode;
import datamodel.nodes.ext.SymbolicNode;
import references.ReferenceManager;
import sourcetracing.Location;
import sourcetracing.UndefinedLocation;

/**
 * DataModelVisitor visits DataModel nodes and detects entities.
 * 
 * @author HUNG
 *
 */
public class DataModelVisitor {
	
	private String projectFolder;
	private ReferenceManager referenceManager;
	private ExtendedParsingState parsingState;
	
	private Stack<String> nodeStack = new Stack<String>();	// Used for debugging
	private static int stackMaxDepth = 1;					// Used for debugging
	
	/**
	 * Constructor
	 */
	public DataModelVisitor(String projectFolder, ReferenceManager referenceManager) {
		this.projectFolder = projectFolder;
		this.referenceManager = referenceManager;
		this.parsingState = new ExtendedParsingState();
	}
	
	/**
	 * Visits a general DataNode
	 * @param dataNode
	 */
	public void visit(DataNode dataNode) {
		/*===== BEGIN OF DEBUG CODE =====*/
		if (dataNode != null) {
			nodeStack.push(dataNode.getClass().getSimpleName());
			if (nodeStack.size() <= stackMaxDepth)
				logging.MyLogger.log(logging.MyLevel.PROGRESS, "Visiting " + nodeStack + "...");
		}
		/*===== END OF DEBUG CODE =====*/
		
		if (dataNode instanceof ConcatNode)
			visit((ConcatNode) dataNode);
		
		else if (dataNode instanceof SelectNode)
			visit((SelectNode) dataNode);
		
		else if (dataNode instanceof RepeatNode)
			visit((RepeatNode) dataNode);
		
		else if (dataNode instanceof SymbolicNode)
			visit((SymbolicNode) dataNode);
		
		else if (dataNode instanceof LiteralNode)
			visit((LiteralNode) dataNode);
		
		/*===== BEGIN OF DEBUG CODE =====*/
		if (dataNode != null) {
			//if (nodeStack.size() <= stackMaxDepth)
			//	logging.MyLogger.log(logging.MyLevel.PROGRESS, "DoneWith " + nodeStack + ".");
			nodeStack.pop();
		}
		/*===== END OF DEBUG CODE =====*/
	}
	
	/**
	 * Visits a ConcatNode
	 * @param concatNode
	 */
	private void visit(ConcatNode concatNode) {
		concatNode.compact();
		for (DataNode childNode : concatNode.getChildNodes())
			visit(childNode);	
	}
	
	/**
	 * Visits a SelectNode
	 * @param selectNode
	 */
	private void visit(SelectNode selectNode) {
		if (parsingState.isInJavascriptCode()) {
			if (selectNode.getNodeInTrueBranch() != null)
				visit(selectNode.getNodeInTrueBranch());
			else if (selectNode.getNodeInFalseBranch() != null)
				visit(selectNode.getNodeInFalseBranch());
		}
		else {
			if (selectNode.getNodeInFalseBranch() != null) {
				ExtendedParsingState savedState = parsingState.save();
				
				if (selectNode.getConditionString() != null && selectNode.getConditionString().getStringValue().contains("case")) {
					// @see datamodel.nodes.LiteralNode.LiteralNode(SwitchCase)
					// If the condition is part of a switch-case statement, then don't add the constraint.
					// The purpose is to reduce the path constraints from NOT case 1 && NOT case 2 && case 3 => case 3. 
				}
				else {
					parsingState.addConstraint(selectNode, false);
				}
				visit(selectNode.getNodeInFalseBranch());
				
				parsingState = savedState;
			}
			if (selectNode.getNodeInTrueBranch() != null) {
				Constraint savedConstraint = parsingState.getConstraint();
				
				parsingState.addConstraint(selectNode, true);
				visit(selectNode.getNodeInTrueBranch());
				
				parsingState.setConstraint(savedConstraint);
			}
		}
	}
	
	/**
	 * Visits a RepeatNode
	 * @param repeatNode
	 */
	private void visit(RepeatNode repeatNode) {
		visit(repeatNode.getDataNode());
	}
	
	/**
	 * Visits a SymbolicNode
	 * @param symbolicNode
	 */
	private void visit(SymbolicNode symbolicNode) {
		String htmlCode = "1"; // Use '1' to replace the symbolic value.
		Location htmlLocation = UndefinedLocation.inst;
		ReferenceDetector.findReferencesInHtmlCode(parsingState, htmlCode, htmlLocation , referenceManager, projectFolder);
	}

	/**
	 * Visits a LiteralNode
	 * @param literalNode
	 */
	private void visit(LiteralNode literalNode) {
		String htmlCode = literalNode.getStringValue();
		Location htmlLocation = literalNode.getLocation();
		ReferenceDetector.findReferencesInHtmlCode(parsingState, htmlCode, htmlLocation , referenceManager, projectFolder);
	}
	
}

package edu.iastate.symex.ui.views;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.symex.datamodel.nodes.SelectNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.ui.UIHelper;

/**
 * 
 * @author HUNG
 *
 */
public class DataModelTreeViewer extends GenericTreeViewer {

	public DataModelTreeViewer(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	public Object[] getRootNodes(Object input) {
		DataModel dataModel = (DataModel) input;
		DataNode outputDataNode = dataModel.getRoot();
		if (outputDataNode == null)
			outputDataNode = DataNodeFactory.createSymbolicNode();
		return new Object[]{outputDataNode};
	}

	@Override
	public Object[] getChildren(Object element) {
		ArrayList<Object> children = new ArrayList<Object>();
		
		if (element instanceof ConcatNode) {
			children.addAll(((ConcatNode) element).getChildNodes());
		}
		
		else if (element instanceof SelectNode) {
			SelectNode selectNode = (SelectNode) element;
			if (selectNode.getNodeInTrueBranch() != null)
				children.add(new SelectChildNode(selectNode.getNodeInTrueBranch(), true));
			if (selectNode.getNodeInFalseBranch() != null)
				children.add(new SelectChildNode(selectNode.getNodeInFalseBranch(), false));
		}
		
		else if (element instanceof SelectChildNode) {
			children.add(((SelectChildNode) element).getChildNode());
		}
		
		else if (element instanceof RepeatNode) {
			children.add(((RepeatNode) element).getChildNode());
		}
		
		else if (element instanceof SymbolicNode) {
			if (((SymbolicNode) element).getParentNode() != null)
				children.add(((SymbolicNode) element).getParentNode());
		}
		
		else {
			// LiteralNode has no children.
		}
		
		return children.toArray(new Object[]{});
	}
	
	/**
	 * SelectChildNode represents a branch of a SelectNode.
	 */
	private class SelectChildNode {
		
		private Object childNode;
		private boolean isTrueBranch;
		
		public SelectChildNode(Object childNode, boolean isTrueBranch) {
			this.childNode = childNode;
			this.isTrueBranch = isTrueBranch;
		}
		
		public Object getChildNode() {
			return childNode;
		}
		
		public boolean isTrueBranch() {
			return isTrueBranch;
		}

	}
	
	@Override
	public String getTreeNodeLabel(Object element) {
		if (element instanceof SelectChildNode)
			return (((SelectChildNode) element).isTrueBranch() ? "True" : "False");
		else
			return element.getClass().getSimpleName().replace("Node", "");
	}
	
	@Override
	public Image getTreeNodeIcon(Object element) {
		// http://shinych.blogspot.com/2007/05/eclipse-shared-images.html
		String imageID;
		
		if (element instanceof ConcatNode)
			imageID = ISharedImages.IMG_OBJ_FOLDER;
		
		else if (element instanceof SelectNode)
			imageID = ISharedImages.IMG_TOOL_CUT;
		
		else if (element instanceof SelectChildNode)
			imageID = ISharedImages.IMG_TOOL_FORWARD;
		
		else if (element instanceof RepeatNode)
			imageID = ISharedImages.IMG_TOOL_REDO;
		
		else if (element instanceof SymbolicNode)
			imageID = ISharedImages.IMG_OBJS_WARN_TSK;
		
		else
			imageID = ISharedImages.IMG_OBJ_FILE;
		
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageID);
	}
	
	@Override
	public String getTreeNodeDescription(Object element) {
		if (element instanceof SelectNode)
			return (((SelectNode) element).getConstraint() != null ? ((SelectNode) element).getConstraint().toDebugString() : "");
		
		else if (element instanceof SymbolicNode)
			return (((SymbolicNode) element).getPhpNode() != null ? ((SymbolicNode) element).getPhpNode().getSourceCode() : "");
		
		else if (element instanceof LiteralNode)
			return UIHelper.standardizeText(((LiteralNode) element).getStringValue());
		
		else
			return "";
	}
	
	@Override
	public PositionRange getTreeNodePositionRange(Object element) {
		if (element instanceof SelectNode)
			return (((SelectNode) element).getConstraint() != null ? ((SelectNode) element).getConstraint().getLocation() : PositionRange.UNDEFINED);
		
		else if (element instanceof SymbolicNode)
			return (((SymbolicNode) element).getPhpNode() != null ? ((SymbolicNode) element).getPhpNode().getLocation() : PositionRange.UNDEFINED);
		
		else if (element instanceof LiteralNode)
			return ((LiteralNode) element).getLocation();
		
		else
			return PositionRange.UNDEFINED;
	}
	
}
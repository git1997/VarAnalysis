package edu.iastate.webtesting.ui.views;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.symex.datamodel.nodes.SelectNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;
import edu.iastate.symex.ui.UIHelper;
import edu.iastate.ui.views.ITreeViewer;
import edu.iastate.webtesting.outputcoverage.DataModelCoverage;

/**
 * 
 * @author HUNG
 *
 */
public class WebTestingTreeViewer implements ITreeViewer {
	
	private Map<DataNode, Float> coverageMap;
	
	@Override
	public Object[] getRootNodes(Object input) {
		DataModel dataModel = ((DataModelCoverage) input).getDataModel();
		coverageMap = ((DataModelCoverage) input).getCoverageMap();
		return new Object[]{dataModel.getRoot()};
	}

	@Override
	public Object[] getChildren(Object element) {
		ArrayList<Object> children = new ArrayList<Object>();
		
		if (element instanceof ConcatNode) {
			children.addAll(((ConcatNode) element).getChildNodes());
		}
		
		else if (element instanceof SelectNode) {
			SelectNode selectNode = (SelectNode) element;
			children.add(new SelectChildNode(selectNode.getNodeInTrueBranch(), true));
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
			// Other nodes have no children.
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
			return (((SelectChildNode) element).isTrueBranch() ? "True" : "False") + getCoverage((DataNode) ((SelectChildNode) element).getChildNode());
		else
			return element.getClass().getSimpleName().replace("Node", "") + getCoverage((DataNode) element);
	}
	
	private String getCoverage(DataNode dataNode) {
		if (coverageMap.containsKey(dataNode))
			return " " + (int) (coverageMap.get(dataNode) * 100) + "%";
		else
			return "";
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public Image getTreeNodeIcon(Object element) {
		// http://shinych.blogspot.com/2007/05/eclipse-shared-images.html
		String imageID;
		
		if (coverageMap.containsKey(element))
			imageID = ISharedImages.IMG_OBJS_TASK_TSK;
		
		else if (element instanceof ConcatNode)
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
		
		else if (element instanceof RepeatNode)
			return (((RepeatNode) element).getConstraint() != null ? ((RepeatNode) element).getConstraint().toDebugString() : "");
		
		else if (element instanceof SymbolicNode)
			return (((SymbolicNode) element).getPhpNode() != null ? ((SymbolicNode) element).getPhpNode().getSourceCode() : "");
		
		else if (element instanceof LiteralNode)
			return UIHelper.standardizeText(((LiteralNode) element).getStringValue());
		
		else
			return "";
	}
	
	@Override
	public PositionRange getTreeNodeLocation(Object element) {
		if (element instanceof SelectNode)
			return (((SelectNode) element).getConstraint() != null ? ((SelectNode) element).getConstraint().getLocation() : Range.UNDEFINED);
		
		else if (element instanceof RepeatNode)
			return (((RepeatNode) element).getConstraint() != null ? ((RepeatNode) element).getConstraint().getLocation() : Range.UNDEFINED);
		
		else if (element instanceof SymbolicNode)
			return (((SymbolicNode) element).getPhpNode() != null ? ((SymbolicNode) element).getPhpNode().getLocation() : Range.UNDEFINED);
		
		else if (element instanceof LiteralNode)
			return ((LiteralNode) element).getLocation();
		
		else
			return Range.UNDEFINED;
	}
	
}

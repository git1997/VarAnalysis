package edu.iastate.parsers.ui.views;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.iastate.parsers.html.dom.nodes.HtmlConcat;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.dom.nodes.HtmlSelect;
import edu.iastate.parsers.html.dom.nodes.HtmlText;
import edu.iastate.parsers.ui.UIHelper;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.ui.views.GenericTreeViewer;

/**
 * 
 * @author HUNG
 *
 */
public class DomResultTreeViewer extends GenericTreeViewer {

	public DomResultTreeViewer(Composite parent, int style) {
		super(parent, style);
	}
	
	@Override
	public Object[] getRootNodes(Object input) {
		return ((HtmlDocument)input).getTopNodes().toArray(new Object[]{});
	}

	@Override
	public Object[] getChildren(Object element) {
		ArrayList<Object> children = new ArrayList<Object>();
		
		if (element instanceof HtmlElement) {
			//children.add("1");
			//children.add("2");
			children.addAll(((HtmlElement) element).getChildNodes());
		}
		
		if (element instanceof HtmlConcat) {
			children.addAll(((HtmlConcat) element).getChildNodes());
		}
		
		else if (element instanceof HtmlSelect) {
			HtmlSelect selectNode = (HtmlSelect) element;
			if (selectNode.getTrueBranchNode() != null)
				children.add(new SelectChildNode(selectNode.getTrueBranchNode(), true));
			if (selectNode.getFalseBranchNode() != null)
				children.add(new SelectChildNode(selectNode.getFalseBranchNode(), false));
		}
		
		else if (element instanceof SelectChildNode) {
			children.add(((SelectChildNode) element).getChildNode());
		}
		
		else {
			// Other nodes have no children.
		}
		
		Object[] objects = children.toArray(new Object[]{});
		return objects;
	}

	@Override
	public String getTreeNodeLabel(Object element) {
		if (element instanceof SelectChildNode)
			return (((SelectChildNode) element).isTrueBranch() ? "True" : "False");
		else
			return element.getClass().getSimpleName();
	}

	@Override
	public Image getTreeNodeIcon(Object element) {
		// http://shinych.blogspot.com/2007/05/eclipse-shared-images.html
		String imageID;
		
		if (element instanceof HtmlConcat)
			imageID = ISharedImages.IMG_OBJ_FOLDER;
		
		else if (element instanceof HtmlSelect)
			imageID = ISharedImages.IMG_TOOL_CUT;
		
		else if (element instanceof SelectChildNode)
			imageID = ISharedImages.IMG_TOOL_FORWARD;
		
		else
			imageID = ISharedImages.IMG_OBJ_FILE;
		
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageID);
	}

	@Override
	public String getTreeNodeDescription(Object element) {
		if (element instanceof HtmlSelect) {
			Constraint constraint = ((HtmlSelect) element).getConstraint();
			return constraint.toDebugString();
		}
		
		else if (element instanceof HtmlElement)
			return ((HtmlElement) element).getOpenTag().toDebugString();
		
		else if (element instanceof HtmlText) {
			return UIHelper.standardizeText(((HtmlText) element).toDebugString());
		}
		
		else
			return "";
	}

	@Override
	public PositionRange getTreeNodePositionRange(Object element) {
		if (element instanceof HtmlElement)
			return ((HtmlElement) element).getOpenTag().getLocation();
		else
			return PositionRange.UNDEFINED;
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
	
}

package edu.iastate.parsers.ui.views;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.iastate.parsers.conditional.CondListConcat;
import edu.iastate.parsers.conditional.CondListItem;
import edu.iastate.parsers.conditional.CondListSelect;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.ui.UIHelper;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.ui.views.GenericTreeViewer;

/**
 * 
 * @author HUNG
 *
 */
public class LexResultTreeViewer extends GenericTreeViewer {
	
	public LexResultTreeViewer(Composite parent, int style) {
		super(parent, style);
	}
	
	@Override
	public Object[] getRootNodes(Object input) {
		return new Object[]{ ((GenericTreeViewer.TreeInput)input).getRoot() };
	}

	@Override
	public Object[] getChildren(Object element) {
		ArrayList<Object> children = new ArrayList<Object>();
		
		if (element instanceof CondListConcat<?>) {
			children.addAll(((CondListConcat<?>) element).getChildNodes());
		}
		
		else if (element instanceof CondListSelect<?>) {
			CondListSelect<?> selectNode = (CondListSelect<?>) element;
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
		
		if (element instanceof CondListConcat<?>)
			imageID = ISharedImages.IMG_OBJ_FOLDER;
		
		else if (element instanceof CondListSelect<?>)
			imageID = ISharedImages.IMG_TOOL_CUT;
		
		else if (element instanceof SelectChildNode)
			imageID = ISharedImages.IMG_TOOL_FORWARD;
		
		else
			imageID = ISharedImages.IMG_OBJ_FILE;
		
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageID);
	}

	@Override
	public String getTreeNodeDescription(Object element) {
		if (element instanceof CondListSelect<?>)
			return ((CondListSelect<?>) element).getConstraint().toDebugString();
		
		else if (element instanceof CondListItem<?>) {
			Object item = ((CondListItem<?>) element).getItem();
			String text;
			if (item instanceof HtmlToken)
				text = ((HtmlToken) item).toDebugString();
			else
				text = "";
			
			return UIHelper.standardizeText(text);
		}
		
		else
			return "";
	}

	@Override
	public PositionRange getTreeNodePositionRange(Object element) {
		if (element instanceof CondListItem<?>) {
			HtmlToken item = (HtmlToken) ((CondListItem<?>) element).getItem();
			return item.getLocation();
		}
		else if (element instanceof CondListSelect<?>) {
			Constraint constraint = ((CondListSelect<?>) element).getConstraint();
			return constraint.getLocation();
		}
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

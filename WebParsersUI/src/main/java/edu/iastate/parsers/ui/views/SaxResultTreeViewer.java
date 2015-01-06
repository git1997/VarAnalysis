package edu.iastate.parsers.ui.views;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.iastate.parsers.conditional.CondListConcat;
import edu.iastate.parsers.conditional.CondListItem;
import edu.iastate.parsers.conditional.CondListSelect;
import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.dom.nodes.HtmlAttributeValue;
import edu.iastate.parsers.html.sax.nodes.HOpenTag;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.parsers.ui.UIHelper;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;
import edu.iastate.symex.ui.views.GenericTreeViewer;

/**
 * 
 * @author HUNG
 *
 */
public class SaxResultTreeViewer extends GenericTreeViewer {

	public SaxResultTreeViewer(Composite parent, int style) {
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
		
		else if (element instanceof CondListItem<?>) {
			CondListItem<?> leafNode = (CondListItem<?>) element;
			HtmlSaxNode saxNode =  (HtmlSaxNode) leafNode.getItem();
			if (saxNode instanceof HOpenTag) {
				HOpenTag tag = (HOpenTag) saxNode;
				children.addAll(tag.getAttributes());
			}
		}
		
		else if (element instanceof HtmlAttribute) {
			children.add(((HtmlAttribute) element).getAttributeValue());
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
			HtmlSaxNode node = (HtmlSaxNode) ((CondListItem<?>) element).getItem();
			return UIHelper.standardizeText(node.toDebugString());
		}
		
		else if (element instanceof HtmlAttribute) {
			String name = ((HtmlAttribute) element).getName();
			Constraint constraint = ((HtmlAttribute) element).getConstraint(); 
			if (constraint.isTautology())
				return name;
			else
				return name + " [" + constraint.toDebugString() + "]";
		}
		
		else if (element instanceof HtmlAttributeValue) {
			String value = ((HtmlAttributeValue) element).getStringValue();
			return UIHelper.standardizeText(value);
		}
		
		else
			return "";
	}

	@Override
	public PositionRange getTreeNodePositionRange(Object element) {
		if (element instanceof CondListItem<?>) {
			HtmlSaxNode node = (HtmlSaxNode) ((CondListItem<?>) element).getItem();
			return node.getLocation();
		}
		else if (element instanceof CondListSelect<?>) {
			Constraint constraint = ((CondListSelect<?>) element).getConstraint();
			return constraint.getLocation();
		}
		else if (element instanceof HtmlAttribute) {
			return ((HtmlAttribute) element).getLocation();
		}
		else if (element instanceof HtmlAttributeValue) {
			return ((HtmlAttributeValue) element).getLocation();
		}
		else
			return Range.UNDEFINED;
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

package edu.iastate.parsers.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.StructuralPropertyDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.iastate.parsers.ui.UIHelper;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;
import edu.iastate.symex.ui.views.GenericTreeViewer;

/**
 * 
 * @author HUNG
 *
 */
public class PhpAstTreeViewer extends GenericTreeViewer {

	public PhpAstTreeViewer(Composite parent, int style) {
		super(parent, style);
	}
	
	@Override
	public Object[] getRootNodes(Object input) {
		return new Object[]{ ((GenericTreeViewer.TreeInput) input).getRoot() };
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object[] getChildren(Object element) {
		ArrayList<Object> children = new ArrayList<Object>();
		
		if (element instanceof ASTNode) {
			ASTNode node = (ASTNode) element;
			for (Object object : node.structuralPropertiesForType()) {
				StructuralPropertyDescriptor desc = (StructuralPropertyDescriptor) object;
				Descriptor descriptor = new Descriptor(node, desc);
				if (getChildren(descriptor).length > 0)
					children.add(descriptor);
			}
		}
		
		else if (element instanceof Descriptor) {
			Descriptor descriptor = (Descriptor) element;
			ASTNode node = descriptor.getASTNode();
			StructuralPropertyDescriptor desc = descriptor.getDescriptor();
			Object property = node.getStructuralProperty(desc);
			
			if (desc.isChildListProperty()) {
				children.addAll((List) property);
			}
			else if (property != null)
				children.add(property);
		}
		
		else {
			// Other nodes have no children.
		}
		
		Object[] objects = children.toArray(new Object[]{});
		return objects;
	}

	@Override
	public String getTreeNodeLabel(Object element) {
		if (element instanceof Descriptor)
			return ((Descriptor) element).getDescriptor().getClass().getSimpleName();
		else
			return element.getClass().getSimpleName();
	}

	@Override
	public Image getTreeNodeIcon(Object element) {
		// http://shinych.blogspot.com/2007/05/eclipse-shared-images.html
		String imageID;
		
		if (element instanceof ASTNode)
			imageID = ISharedImages.IMG_OBJ_FOLDER;
		
		else if (element instanceof Descriptor)
			imageID = ISharedImages.IMG_TOOL_FORWARD;
		
		else
			imageID = ISharedImages.IMG_OBJ_FILE;
		
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageID);
	}

	@Override
	public String getTreeNodeDescription(Object element) {
		if (element instanceof ASTNode) {
			return "";
		}
		else if (element instanceof Descriptor) {
			StructuralPropertyDescriptor descriptor = ((Descriptor) element).getDescriptor();
			return descriptor.getId();
		}
		else
			return element.toString();
	}

	@Override
	public PositionRange getTreeNodePositionRange(Object element) {
		if (element instanceof ASTNode) {
			return new Range(UIHelper.iFileToFile(UIHelper.getActiveEditorFile()), ((ASTNode) element).getStart(), ((ASTNode) element).getLength());
		}
		else
			return PositionRange.UNDEFINED;
	}
	
	private class Descriptor {
		
		private ASTNode node;
		private StructuralPropertyDescriptor descriptor;
		
		public Descriptor(ASTNode node, StructuralPropertyDescriptor descriptor) {
			this.node = node;
			this.descriptor = descriptor;
		}
		
		public ASTNode getASTNode() {
			return node;
		}
		
		public StructuralPropertyDescriptor getDescriptor() {
			return descriptor;
		}

	}
	
}

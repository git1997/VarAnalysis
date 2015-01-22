package edu.iastate.symex.ui.views;

import javax.swing.tree.DefaultMutableTreeNode;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.iastate.symex.debug.Trace;
import edu.iastate.symex.php.nodes.StatementNode;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.ui.UIHelper;
import edu.iastate.ui.views.ITreeViewer;

/**
 * 
 * @author HUNG
 *
 */
public class DebuggerTreeViewer implements ITreeViewer {
	
	@Override
	public Object[] getRootNodes(Object input) {
		Trace trace = (Trace) input;
		return getChildren(trace.getPseudoRoot());
	}

	@Override
	public Object[] getChildren(Object element) {
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) element;
		Object[] children = new Object[treeNode.getChildCount()];
		
		for (int i = 0; i < treeNode.getChildCount(); i++)
			children[i] = treeNode.getChildAt(i);
		
		return children;
	}
	
	@Override
	public String getTreeNodeLabel(Object element) {
		StatementNode statement = (StatementNode) ((DefaultMutableTreeNode) element).getUserObject();
		return statement.getClass().getSimpleName();
	}
	
	@Override
	public Image getTreeNodeIcon(Object element) {
		// http://shinych.blogspot.com/2007/05/eclipse-shared-images.html
		String imageID = ISharedImages.IMG_OBJ_FILE;
		
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageID);
	}
	
	@Override
	public String getTreeNodeDescription(Object element) {
		StatementNode statement = (StatementNode) ((DefaultMutableTreeNode) element).getUserObject();
		return UIHelper.standardizeText(statement.getSourceCode());
	}
	
	@Override
	public PositionRange getTreeNodeLocation(Object element) {
		StatementNode statement = (StatementNode) ((DefaultMutableTreeNode) element).getUserObject();
		return statement.getLocation();
	}
	
}

package edu.iastate.symex.ui.views;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import edu.iastate.symex.php.nodes.StatementNode;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.ui.UIHelper;
import edu.iastate.symex.debug.DebugInfo;
import edu.iastate.symex.debug.Debugger;
import edu.iastate.symex.debug.Trace;
import edu.iastate.ui.views.GenericTreeViewer;
import edu.iastate.ui.views.GenericView;
import edu.iastate.ui.views.ITreeViewer;

/**
 * 
 * @author HUNG
 *
 */
public class DebuggerView extends GenericView implements ITreeViewer {

	private GenericTreeViewer treeViewer;
	private StyledText styledText;
	
	/**
	 * Constructor
	 */
	public DebuggerView() {
		super(1, 2);
	}
	
	@Override
	public Button createButton(Composite parent, int buttonNumber) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Debug Symex");
		return button;
	}
	
	@Override
	public Control createTabControl(TabFolder tabFolder, TabItem tabItem, int tabNumber) {
		if (tabNumber == 0) {
			tabItem.setText("Tree");
			treeViewer = new GenericTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION, this);
			return treeViewer.getControl();
		}
		else {
			tabItem.setText("Text");
			styledText = new StyledText(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			return styledText;
		}
	}
	
	@Override
	public void buttonClicked(File file, int buttonNumber) {
		DebugInfo debugInfo = new Debugger().debug(file);
		
		treeViewer.setInput(debugInfo.getTrace());
		treeViewer.expandToLevel(1);
		styledText.setText(debugInfo.getTrace().printTraceToString());
	}
	
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
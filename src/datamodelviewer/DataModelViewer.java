package datamodelviewer;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * @author HUNG
 *
 */
public class DataModelViewer extends ViewPart {

	public static final String ID = "TreeView"; //$NON-NLS-1$
	private TreeViewer treeViewer;
	private Combo combo;
	private Text text_1;

	public DataModelViewer() {
	}
	
	/**
	 * Main method to test the user interface.
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(1000, 600);
		DataModelViewer mappingView = new DataModelViewer();
		mappingView.createPartControl(shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		container.setLayout(new GridLayout(2, false));
		{
			combo = new Combo(container, SWT.NONE);
			combo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					refreshTree(combo.getText());
				}
			});
			combo.setItems(new String[] {
					"/Work/To-do/JSVarex Project/Study/Output/addressbookv6.2.12/data_model.xml",
			});
			combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			combo.select(0);
		}
		{
			Button btnNewButton = new Button(container, SWT.NONE);
			btnNewButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					refreshTree(combo.getText());
				}
			});
			btnNewButton.setText("Refresh");
		}
		{
			text_1 = new Text(container, SWT.BORDER);
			text_1.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					searchTreeForText(text_1.getText());
				}
			});
			text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		}
		{
			Button btnSearch = new Button(container, SWT.NONE);
			btnSearch.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					searchTreeForText(text_1.getText());
				}
			});
			btnSearch.setText("Search");
		}
		{
			treeViewer = new TreeViewer(container, SWT.BORDER);
			Tree tree = treeViewer.getTree();
			tree.setHeaderVisible(true);
			GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
			gd_tree.widthHint = 643;
			tree.setLayoutData(gd_tree);
			{
				TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
				TreeColumn trclmnTree = treeViewerColumn.getColumn();
				trclmnTree.setWidth(500);
				trclmnTree.setText("Tree");
				treeViewerColumn.setLabelProvider(new ColumnLabelProvider() {

					@Override
					public String getText(Object element) {
						return shorten(((MyTreeNode) element).getText());
					}
				});
				
				treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
				TreeColumn trclmnInfo = treeViewerColumn.getColumn();
				trclmnInfo.setWidth(300);
				trclmnInfo.setText("");
				treeViewerColumn.setLabelProvider(new ColumnLabelProvider() {

					@Override
					public String getText(Object element) {
						return shorten(((MyTreeNode) element).getLocation());
					}
				});
			}
			treeViewer.setContentProvider(new TreeContentProvider());
			treeViewer.setInput(null);
		}

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
//		IToolBarManager toolbarManager = getViewSite().getActionBars()
//				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
//		IMenuManager menuManager = getViewSite().getActionBars()
//				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
		//treeViewer.getControl().setFocus();
	}
	
	private void refreshTree(String xmlFile) {
		MyTree tree = Xml2MyTree.createTreeFromXml(xmlFile);
		
		treeViewer.setInput(tree);
	}
	
	private void searchTreeForText(String text) {
		System.out.println("Searching for \"" + text + "\" ...");
		
//		for (TreeNode node : ((MyTree) treeViewer.getTree().getData()).getRoot().getChildren()) {
//			searchTreeNodeForText(node, text);
//		}
	}
	
	private void searchTreeNodeForText(MyTreeNode node, String text) {
		if (node.getText().toLowerCase().contains(text.toLowerCase())) {
			ArrayList<MyTreeNode> path = new ArrayList<MyTreeNode>();
			MyTreeNode i = node;
			while (i != null) {
				path.add(i);
				i = i.getParent();
			}
			
			for (int j = path.size() - 2; j >= 1; j--) {
				System.out.print(path.get(j).getText() + " => "); 
			}
			System.out.println(path.get(0).getText());
		}
		
		for (MyTreeNode child : node.getChildren()) {
			searchTreeNodeForText(child, text);
		}
	}
	
	/**
	 * Content provider for the tree.
	 */
	private class TreeContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return new Object[]{((MyTree) inputElement).getRoot()};
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return ((MyTreeNode) parentElement).getChildren();
		}

		@Override
		public Object getParent(Object element) {
			return ((MyTreeNode) element).getParent();
		}

		@Override
		public boolean hasChildren(Object element) {
			return ((MyTreeNode) element).hasChildren();
		}
		
	}
	
	/*
	 * Utility methods
	 */
	
	/**
	 * Shortens a string
	 * @param string
	 */
	private static String shorten(String string) {
		int beginLen = 30;
		String middleStr = ".....";
		int endLen = 30;
		
		if (string.length() < beginLen + middleStr.length() + endLen)
			return string;
		else
			return string.substring(0, beginLen) + middleStr + string.substring(string.length() - endLen);
	}

}
